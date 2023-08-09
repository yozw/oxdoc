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

## Version history

### August 2023 (oxdoc 1.2)
* Added support for `...varname` variable-length arguments.
* Added support for multiple variable declarations, e.g. `decl [a, b] = {1, 2}`.
* Allow `in` as a variable name.
* Added support for multiplicative matrix declaration, e.g. `enum{Vleng=3}; static decl zerovector = < [Vleng] *0>;`
* Added support for "raw string literals", i.e., multi-line strings surrounded by backtics.
* Added support for `.last` indexing in arrays.

### June 2017 (oxdoc 1.1)
* Updated `foreach` to allow for more complicated expressions
* Added `parallel for` and `serial`
* Updated case statement to allow for characters and strings

### March 4th, 2014
* Did a lot of refactoring and performance optimizations
* Oxdoc now automatically reads the "<filename>.oxdoc" file before processing "<filename>.ox" (if it exists).
Note that this happens only for main ".ox" files, and not for files that are included using the `#include` directive.
* Added an argument `-csspath` to specify where to find/write the css files.

### February 23rd, 2014
* Migrated to github
* Added a few unit tests
* Added support for new Ox 7 features: lambda expressions, the `foreach` keyword, and 
  default function argument values.
* Did some major refactoring and code cleaning
* Code now uses generics, so Java 5.0 or higher is required.

### April 4th, 2012
* Removed setup utility: not really necessary anymore because 
  Mathjax is default, and latex and dvipdfm are found on search path
* Refactored HTML output refactoring
* Added global variable support
* Enumeration elements are now in the index
* Removed AsciiMathML support
* Fixed GUI
* Beautified java code


### March 23rd, 2012
* Fixed the enumeration declaration problem (`enum { N=1, M =N+1}` is now parsed properly)
* Completed support for enumerations. Enumerations have no name in Ox, so they are 
  automatically called "Anonymous enumeration #1", etc. To suppress this behavior, 
  you can add an oxdoc comment before any enumeration, and use the `@name` tag to give 
  it a proper name. For example:

```
   /** Enumeration for feasilbility. 
       @name Feasibility **/
  enum { FEASIBLE, INFEASIBLE }
```

  The usual other `@` tags should work as well.

* Implemented indexing as per Chris Ferrall's suggestion: any overridden methods are
  placed on the same line.

* Added document to the output: the hierarchy tree. Notice that you need to update your 
  `oxdoc.css` to get this to work

* Changed the default oxdoc.css so that the output looks a bit prettier. At the same
  time, the HTML output is slightly different. The icons and texts in H1, H2, H3 headers
  are now inside SPANs.

* Implemented math display through Mathjax, as per suggestion of Michael Massman 
  (see http://www.mathjax.org/). Mathjax is superior to LaTeX image generation, but 
  it requires an internet connection. Since everybody these days has an internet connection, 
  this now the default behavior. You can still use LaTeX to generate formulas by specifying
  the option "-formulas latex". 

* Implemented automatic writing of auxiliary icons (for e.g. the tree, but also for 
  prettier output). If required icon files are not available, they are written. They are 
  never overwritten though. Icons are also on by default now. This can be disable by
  specifying `-noicons`

* Added an option `-uplevel` to enable the "up level" link.

* Implemented a `@sortkey` tag in all places. Every object has a sort key, which by default is
  the name of object (e.g. function name or class name), but which can be overridden by the 
  `@sortkey` tag. Objects are sorted according to their `@sortkey`s. In particular, this works
  for files, as you requested.

* The index now distinguishes between constructors, destructors, and methods.

* Default settings for Latex/dvipng executables now depend on the operating system.

* The `#import` command now includes appropriate header file
* The following code is no longer rejected:
  `[ a[1], a[2] ] = ... `
* Members are now listed in documentation too
* Ordering of private/protected/public in class definition is not fixed anymore
* Ordering of cases in switch should not be fixed (default comes last now)
* Add option to list members/methods as internal using `@internal`
* Member modifiers (e.g. static functions/fields, const fields) are documented as well now 
* Icons are now of the `icon` class in the output HTML
* Latex formulas are now of the `latex` class in the output HTML
* Improved css files / added Chris Ferrall's css file
* Added goto statement / labels to Ox syntax
* Added option for specifying include path 
* Added icon for fields / enumerations
* In PcLire, `Lire_Object` and `Lire_SimsSolver` have no icon in the index (fixed)


