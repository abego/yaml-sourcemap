# YAMLSourceMap
__Map YAML/JSON document texts to data values and vice versa, in 
Java.__

## Overview

The YAMLSourceMap provides a mapping between locations in a YAML document 
(the source) and the data values created from this document.

![Mapping between YAML document text and Data (JSON pointer)
](abego-yaml-sourcemap-core/src/main/javadoc/org/abego/yaml/sourcemap/doc-files/mapping.png)

The mapping works in both directions.

### YAML document text -> Data (JSON pointer)
        
If you have a location in the YAML document, the source map tells you the 
address (JSON pointer) of the data value this location relates to.
        
### Data (JSON pointer) -> YAML document text

If you have a JSON pointer for some data created from the YAML document 
the source tells you the locations in the YAML document that created the data.

## JSON and YAML

As YAML is a superset of JSON the YAMLSourceMap can also be used to create 
source maps for JSON documents.

## Usage

### Creating a YAMLSourceMap

The central type of this module is the YAMLSourceMap. 
You create a YAMLSourceMap for a specific YAML document using the YAMLSourceMapAPI.
Either you specify a Reader to read the YAML text from:

    Reader reader = ...;
    YAMLSourceMap srcMap = YAMLSourceMapAPI.createYAMLSourceMap(reader);
 
or directly pass in the YAML text:

    String yamlText = "foo: 123\nbar: 456\n";
    YAMLSourceMap srcMap = YAMLSourceMapAPI.createYAMLSourceMap(yamlText);
 
### Find the data for a YAML/JSON document text location

Once you have the YAMLSourceMap you can pass in a location in the YAML document 
text and the source map gives you the address of the data (value) the text 
at the given location in the YAML document created.
 
The data address is given as a JSON Pointer [1], a standard format to identify 
a specific value in a JSON document.

You can either specify the location in the YAML text as an offset to the start
of the text:

    YAMLSourceMap srcMap =...;

    int offset = 42;
    String jsonPointer = srcMap.jsonPointerAtOffset(offset); // return e.g. "/bill-to/address"
 
or give the location by line and column. E.g. to get the JSON Pointer for the
text of column 14 of the third line you would write:

    YAMLSourceMap srcMap =...;

    String jsonPointer = srcMap.jsonPointerAtLocation(3, 14); // return e.g. "/bill-to/address"
 
### Find the YAML/JSON document text that created a data value

To get from some data value to the corresponding YAML document text use 
`YAMLSourceMap.sourceOfValueOfJsonPointer(java.lang.String)`.
Pass in a JSON Pointer and the method gives you the range in the text of 
YAML/JSON document that created the data value identified by the JSON Pointer:

    YAMLSourceMap srcMap =...;

    String jsonPointer = "/bill-to/address";
    YAMLRange range = srcMap.sourceOfValueOfJsonPointer(jsonPointer)
 
If you interested not just in the text range that created the data value 
but would like to know the larger range in the YAML text related to the 
data value (including surrounding whitespace or comments, or special characters 
like ":", "[" etc.) you can use the method `YAMLSourceMap.sourceOfJsonPointer(...)` instead:

    YAMLSourceMap srcMap =...;

    String jsonPointer = "/bill-to/address";
    YAMLRange range = srcMap.sourceOfJsonPointer(jsonPointer)

The following picture demonstrates the difference between 
`sourceOfJsonPointer` and `sourceOfValueOfJsonPointer` for the example
JSON Pointer `/bill-to/address`. 

![Difference between sourceOfJsonPointer and sourceOfValueOfJsonPointer
](abego-yaml-sourcemap-core/src/main/javadoc/org/abego/yaml/sourcemap/doc-files/value-etc.png)


If you ask `sourceOfValueOfJsonPointer` for the source of `/bill-to/address` 
it will return the darker orange range. However `sourceOfJsonPointer` will return
the darker orange range _plus_ the light orange range, i.e. it will also 
include the white spaces and the map item's key `address:`.

### Examples

Have a look at the module `abego-yaml-sourcemap-examples` for some examples how
the YAMLSourceMap can be used in an application.

E.g. the "Breadcrumbs" application demonstrates how to use the YAMLSourceMap to
implement a "Breadcrumbs bar" (/Navigation bar), e.g. to view YAML/JSON documents.

![Mapping between YAML document text and Data (JSON pointer)
](abego-yaml-sourcemap-core/src/main/javadoc/org/abego/yaml/sourcemap/doc-files/breadcrumbs-demo.png)

That application also is also a use case for "bidirectional mapping": 

- After a click in the YAML text (the source) the source map is used to find the
address of the data created by the text at the click location. This address/JSON
Pointer is then used to update the Breadcrumbs bar. _(YAML Text -> Data)_
- Clicking a breadcrumb in the Breadcrumbs bar navigates the text cursor in the
YAML text to the location corresponding to that breadcrumb. (Every breadcrumb 
actually is a JSON Pointer). The source map provides the proper location for
every given JSON Pointer/breadcrumb.  _(Data -> YAML Text)_
 
[1]: https://tools.ietf.org/html/rfc6901

## Development

You may check out the source code from the [GitHub repository](https://github.com/abego/yaml-sourcemap).

## Links

- Sources: https://github.com/abego/yaml-sourcemap
- Twitter: @abego (e.g. for announcements of new releases)

## License

YAMLSourceMap is available under a business friendly [MIT license](https://www.abego-software.de/legal/mit-license.html).


