[![Build Status](https://travis-ci.org/testingtony/test-tools.svg?branch=master)](https://travis-ci.org/testingtony/test-tools)
[![Dependency Status](https://www.versioneye.com/user/projects/593ab09e822da00069eff977/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/593ab09e822da00069eff977)

Test Tools
==========

The latest release is available for [download here](https://github.com/testingtony/test-tools/releases/latest)

Building
--------

Build with

```
mvnw clean package
```

from the top level directory.

Running
-------

This provides a command line wrapper around the tools libraries. When built, a shaded jar is created in
`build/libs/test-tools.jar` To run use the command
```
java -jar test-tools.jar command [options]
```
### Commands
#### counterstring [-n] [-s separator] [-t terminator] length
Produces a string which can easily be used to see if an input gets truncated.
e.g. `^3^5^7^9^12^15^18*` each terminator is at the position identified by the preceding number.

Ripped off from [James Bach's Perlclip](http://www.satisfice.com/tools.shtml) to create a counterstring of
length _length._

* `-n` output without a carriage return.
* `-s separator` the character to use between values in the counterstring
* `-t terminator` the character to use at the end of the counterstring

#### encoding -g file | -v encoding file | -r input_encoding input_file output_encoding output_file
Guess, check or recode the charset encoding of the file. This is not definitive since for lots of texts will produce the
same file with different encodings.

* `-g file` try to guess character encoding of a file.
* `-v encoding file` verify that encoding could be the charset of the file
* `-r input_encoding input_file output_encoding output_file` re-encode the input_file to the output_file from the
input_encoding to output_encoding.

#### jdbc -d | -D | -r \<filename> | -w \<filename> | -s [-p \<password>] [-u \<user>] url [table|sql]
Query databases over jdbc (jdbc drivers not supplied) 

* `-d` Describe the _table_ layout.
* `-D` Dump the _table_ to the screen.
* `-r` Read the CSV file _filename_ into _table_
* `-w` Write the results of _sql_ to CSV file _filename_ 
* `-s` Run general SQL _sql_ and display results
