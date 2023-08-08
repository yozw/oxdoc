# Oxdoc 1.1

oxdoc is a tool for generating API documentation in HTML format from comments in Ox source code, inspired by Sun Microsystems' Javadoc. Ox is an object-oriented statistical matrix programming language.

See http://yozw.github.io/oxdoc/ for examples and a user manual.

### Installation instructions (binaries)

The latest compiled version is available at https://github.com/yozw/oxdoc/releases.

#### Windows
* Unzip the binary zip file
* Use `oxdoc.bat` to run `oxdoc`
* You may want to copy `oxdoc.bat` to a directory in your path, e.g. 
  `c:\windows\system32`.  In that case, copy `oxdoc.jar` to the same
  directory.


#### Linux/OS X
* Unzip the binary zip file
* Activate execute permission for the oxdoc script in the `bin/` directory
* Edit `bin/oxdoc.xml`
* Run `bin/oxdoc`
* Alternatively, you can place oxdoc and oxdoc.jar in a directory in your path,
  e.g. `~/bin`  or  `/usr/local/bin`



### Compilation instructions
* Install ant; see https://ant.apache.org/.
* Run `ant build` to build Oxdoc.
* Run `ant dist` -- this will make zip files containing source and
  binary distributions in the `dist/` subdirectory


### License

The MIT License (MIT)

Copyright (c) 2005-2015 Y. Zwols

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
