/*
 * MIT License
 *
 * Copyright (c) 2020 Udo Borkowski, (ub@abego.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.abego.yaml.sourcemap.internal;

import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment;
import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind;
import org.abego.yaml.sourcemap.YAMLSourceMapException;
import org.eclipse.jdt.annotation.NonNull;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.ALIAS_AS_MAP_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.ALIAS_AS_SEQUENCE_ITEM;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.MAP_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SCALAR_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SEQUENCE_ITEM;
import static org.abego.yaml.sourcemap.internal.Utils.last;

/**
 * Provides the {@link org.abego.yaml.sourcemap.YAMLSourceMap.Fragment}s for
 * a YAML document.
 *
 * <p>For details regarding fragments and how they relate to the YAML Source Map
 * see {@link org.abego.yaml.sourcemap.YAMLSourceMap}.</p>
 */
final class FragmentsProvider {

    /**
     * The list of the fragments of this source map.
     */
    private final List<Fragment> fragments;
    /**
     * The (SnakeYaml) parser to read the YAML documents, used to create the
     * fragments for the source map.
     */
    private final Parser parser;
    /**
     * The Mark to the start of the stream/document.
     */
    private final Mark streamStartMark;
    /**
     * Use to build the JSON Pointers for the fragments.
     */
    private final JSONPointerBuilder jsonPointerBuilder = new JSONPointerBuilder();
    /**
     * A stack of fragment kinds.
     */
    private final Stack<Kind> fragmentKindStack = new Stack<>();
    /**
     * When the top exists and is not null it holds the index of the next
     * sequence item to create when composeNode is called.
     */
    private final Stack<Integer> sequenceIndexStack = new Stack<>();

    /**
     * Creates a FragmentsProvider of the YAML document read
     * from the {@code yamlTextReader}.
     */
    private FragmentsProvider(Reader yamlTextReader) {
        fragments = new ArrayList<>();
        parser = new ParserImpl(new StreamReader(yamlTextReader));
        streamStartMark = eventStartMark();

        parseStream();
    }

    /**
     * Reads the YAML document from {@code reader} and returns the document's
     * fragments.
     */
    public static List<Fragment> readFragments(Reader reader) {
        return ((new FragmentsProvider(reader)).fragments);
    }

    // ========================================================================
    // === Parsing ============================================================
    //
    // A recursive descent parser for YAML documents, collecting information
    // to construct the fragments of a YAML document's source map.
    //
    // ========================================================================

    private void parseStream() {
        consumeEvent(Event.ID.StreamStart);

        if (eventId() == Event.ID.DocumentStart) {
            parseDocument();
        }

        if (eventId() == Event.ID.DocumentStart) {
            throw new YAMLSourceMapException("Only one document supported");
        }
    }

    private void parseDocument() {
        pushFragmentKind(Kind.DOCUMENT_START);

        consumeEvent(Event.ID.DocumentStart);

        while (eventId() != Event.ID.DocumentEnd) {
            parseNode();
        }

        consumeEvent(Event.ID.DocumentEnd);
        addFragment(Kind.DOCUMENT_END);
    }

    /**
     * Parses the next YAML node and returns the value of the scalar or alias or
     * an empty String when a sequence or map was parsed.
     */
    private String parseNode() {
        addFinishFragment();

        String result = "";
        if (checkEvent(Event.ID.Alias)) {
            addFragment(aliasKind());
            result = "*" + ((AliasEvent) peekEvent()).getAnchor();
            consumeEvent(Event.ID.Alias);

        } else {
            if (checkEvent(Event.ID.Scalar)) {
                result = parseScalar();
            } else if (checkEvent(Event.ID.SequenceStart)) {
                parseSequence();
            } else {
                parseMap();
            }
        }
        return result;
    }

    /**
     * Parses the next YAML node as a scalar and returns the scalar's value.
     */
    private String parseScalar() {
        Kind newFragmentKind = currentFragmentKind() == Kind.DOCUMENT_START
                ? SCALAR_VALUE : currentFragmentKind();
        addFragment(newFragmentKind);
        @NonNull
        String value = ((ScalarEvent) peekEvent()).getValue();
        consumeEvent(Event.ID.Scalar);
        return value;
    }

    /**
     * Parses the next YAML node as a sequence and returns the value of the
     * last scalar encountered.
     */
    private void parseSequence() {
        addFragment(eventEndMark(), Kind.SEQUENCE);
        pushFragmentKind(SEQUENCE_ITEM);
        consumeEvent(Event.ID.SequenceStart);

        startSequenceIndexing();
        while (peekEvent().getEventId() != Event.ID.SequenceEnd) {

            pushToJsonPointer(actSequenceIndex());
            parseNode();
            popFromJsonPointer();

            incrementSequenceIndex();
        }
        endSequenceIndexing();

        popFragmentKind();
        addFragment(eventEndMark(), Kind.SEQUENCE);
        consumeEvent(Event.ID.SequenceEnd);
    }

    private void parseMap() {
        addFragment(Kind.MAP);
        pushFragmentKind(Kind.MAP);
        consumeEvent(Event.ID.MappingStart);

        while (!checkEvent(Event.ID.MappingEnd)) {
            // parse map entry key

            // Remember the index of the first fragment of this map entry so we
            // can later set the 'correct' JSON pointer for all fragments of
            // the entry once we know it (i.e. have read the key)
            int indexOfFirstFragmentOfMap = fragments.size();
            pushFragmentKind(Kind.MAP_KEY);
            String key = parseNode();

            pushToJsonPointer(key);
            // We now know the key and just updated the jsonPointer.
            // We can now update the previous fragments of this map entry
            setFragmentsJsonPointers(indexOfFirstFragmentOfMap);

            popFragmentKind(); // the "mapKey" part is done.

            // parse map entry value
            pushFragmentKind(MAP_VALUE);
            parseNode();
            popFragmentKind(); // the "mapValue" part is done.

            popFromJsonPointer(); // this entry's jsonPointer is processed
        }

        popFragmentKind();
        addFragment(eventEndMark(), Kind.MAP);
        consumeEvent(Event.ID.MappingEnd);
    }

    // ========================================================================
    // === Fragments ==========================================================
    // ========================================================================

    /**
     * Creates a new {@link YAMLFragment} of the given
     * {@code kind} and the current jsonPointer, starting immediately after
     * the last fragment and ending at {@code endMark} and adds it to the
     * fragments list.
     *
     * <p>If this is the first fragment in the list it starts at the the
     * document.</p>
     *
     * <p>When the fragment would be empty, i.e. its start and end are equal,
     * no fragment is created/added.</p>
     *
     * <p>When the fragment has the same kind and jsonPointer as the previous
     * fragment the previous fragment's end is extended to endMark and no
     * new fragment is created/added. In other words, the fragments are
     * "merged".</p>
     */
    private void addFragment(Mark endMark, Kind kind) {
        // don't add empty fragments
        if (nextFragmentStartMark().getIndex() == endMark.getIndex())
            return;

        String jsonPointer = jsonPointer();
        if (!extendPreviousFragment(endMark, kind, jsonPointer)) {
            // we cannot extend the previous fragment so we create new one
            // and add it to fragments.
            YAMLFragment fragment = new YAMLFragment(
                    nextFragmentStartMark(), endMark, kind,
                    jsonPointer);
            fragments.add(fragment);
        }
    }

    /**
     * Creates a new {@link YAMLFragment} of the given
     * {@code kind} and the current jsonPointer, starting
     * immediately after the last fragment and ending at the end of the
     * current event.
     *
     * <p>For details see {@link #addFragment(Mark, Kind)}</p>
     */
    private void addFragment(Kind kind) {
        addFragment(eventEndMark(), kind);
    }

    /**
     * Extends the previous fragment's end to {@code endMark} and returns true
     * when the previous fragment has the given {@code kind} and
     * {@code jsonPointer}; return false otherwise.
     */
    private boolean extendPreviousFragment(Mark endMark, Kind kind, String jsonPointer) {
        if (!fragments.isEmpty()) {
            YAMLFragment prev = (YAMLFragment) last(fragments);
            if (prev.getKind() == kind
                    && prev.getJSONPointer().equals(jsonPointer)) {
                // We can merge, i.e. set the previous fragment's end to our
                // new end.
                prev.setEndMark(endMark);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a fragment with the current {@link #baseKind()} that ends at the
     * the start of the current event.
     *
     * <p>Typically used when the new event would trigger a baseKind switch,
     * to define a fragment for the range from the end of the last fragment
     * up to the start of the new fragment.</p>
     *
     * <p>Will do nothing when there is no undefined range, i.e. the end of
     * the last fragment is also the start of the new fragment.</p>
     */
    private void addFinishFragment() {
        addFragment(eventStartMark(), baseKind());
    }

    /**
     * Returns the start position for the next fragment to add.
     *
     * <p>The start position is behind the last
     * {@link YAMLFragment} in {@link FragmentsProvider#fragments}
     * or the start of the document when {@code fragments} is empty.
     */
    private Mark nextFragmentStartMark() {
        return fragments.isEmpty()
                ? streamStartMark
                : ((YAMLFragment) last(fragments)).getEndMark();
    }

    /**
     * Returns the base kind of the current state.
     *
     * <p>The kindStack always holds the most-specific kind of the current
     * fragment (e.g. mapKey). This methods returns the corresponding base kind,
     * i.e. one of scalar, sequence or map.</p>
     */
    private Kind baseKind() {
        Kind k = currentFragmentKind();
        switch (k) {
            case SCALAR_VALUE:
                return Kind.SCALAR;
            case SEQUENCE_ITEM:
                return Kind.SEQUENCE;
            case MAP_KEY:
            case MAP_VALUE:
                return Kind.MAP;
            default:
                return k;
        }
    }

    /**
     * Returns the kind of fragment to use when we encounter an alias in the
     * current state.
     *
     * <p>One of {@link Kind#ALIAS_AS_SEQUENCE_ITEM}</p>,
     * {@link Kind#ALIAS_AS_MAP_KEY}, {@link Kind#ALIAS_AS_MAP_VALUE}
     */
    private Kind aliasKind() {
        Kind kind = baseKind();
        if (kind == Kind.MAP) {
            return currentFragmentKind() == MAP_VALUE
                    ? ALIAS_AS_MAP_VALUE : Kind.ALIAS_AS_MAP_KEY;
        }

        return ALIAS_AS_SEQUENCE_ITEM;
    }

    // === JSON Pointer  stuff ==========================================

    /**
     * Returns the current jsonPointer.
     */
    private String jsonPointer() {
        return jsonPointerBuilder.toString();
    }

    /**
     * Pushes the given tag to the jsonPointer.
     */
    private void pushToJsonPointer(String tag) {
        jsonPointerBuilder.push(tag);
    }

    /**
     * Pops the topmost tag from the jsonPointer.
     */
    private void popFromJsonPointer() {
        jsonPointerBuilder.pop();
    }

    /**
     * Sets the jsonPointer of all fragments from the given {@code startIndex}
     * to the end of the fragments list to the current jsonPointer.
     */
    private void setFragmentsJsonPointers(int startIndex) {
        for (int i = startIndex; i < fragments.size(); i++) {
            ((YAMLFragment) fragments.get(i)).setJSONPointer(jsonPointer());
        }
    }

    // === Sequence Indexing stuff ==========================================

    /**
     * Starts the index handling for a new sequence, to provide proper
     * information for the jsonPointer.
     *
     * <p>When done with the sequence call {@link #endSequenceIndexing()}.</p>
     */
    private void startSequenceIndexing() {
        sequenceIndexStack.push(0);
    }

    /**
     * Ends the index handling for a sequence.
     *
     * <p>Must be balanced with a previous call to
     * {@link #startSequenceIndexing()} ()}.</p>
     */
    private void endSequenceIndexing() {
        sequenceIndexStack.pop();
    }

    /**
     * Returns the current index in the outermost sequence, as a {@link String}.
     */
    private String actSequenceIndex() {
        return String.valueOf(sequenceIndexStack.top());
    }

    /**
     * Increments the index in the outermost sequence.
     */
    private void incrementSequenceIndex() {
        sequenceIndexStack.push(sequenceIndexStack.pop() + 1);
    }

    // === Fragment Kind Stack stuff ==========================================

    /**
     * Pushes the {@code kind} to the fragmentKindStack, making this kind the
     * {@link #currentFragmentKind()}.
     */
    private void pushFragmentKind(Kind kind) {
        fragmentKindStack.push(kind);
    }

    /**
     * Pops the topmost item from  the fragmentKindStack, making the previous
     * kind the {@link #currentFragmentKind()}.
     */
    private void popFragmentKind() {
        fragmentKindStack.pop();
    }

    /**
     * Returns the currentFragmentKind, used as a default when creating new
     * fragments.
     */
    private Kind currentFragmentKind() {
        return fragmentKindStack.top();
    }

    // ========================================================================
    // === SnakeYaml Parser and Parser Event stuff ============================
    // ========================================================================

    /**
     * Returns {@code true} when the next event has the {@code expectedID};
     * Returns {@code false} otherwise.
     */
    private boolean checkEvent(Event.ID expectedID) {
        return parser.checkEvent(expectedID);
    }

    /**
     * Returns the next event, but does not consumes it.
     */
    private Event peekEvent() {
        return parser.peekEvent();
    }

    /**
     * Consumes the next event when that event has the {@code expectedID};
     * throws an {@link YAMLSourceMapException} otherwise.
     */
    void consumeEvent(Event.ID expectedID) {
        // check
        if (peekEvent().getEventId() != expectedID) {
            throw new YAMLSourceMapException(
                    String.format("Expected event of type %s, got %s",
                            expectedID, peekEvent().getEventId()));
        }

        // consume
        parser.getEvent();
    }

    /**
     * Returns the {@link Event.ID} of the next event.
     */
    private Event.ID eventId() {
        return peekEvent().getEventId();
    }

    /**
     * Returns the startMark of the next event.
     */
    private Mark eventStartMark() {
        return peekEvent().getStartMark();
    }

    /**
     * Returns the endMark of the next event.
     */
    private Mark eventEndMark() {
        return peekEvent().getEndMark();
    }

}
