# Changes

## February 23rd, 2014
* Migrated to github
* Added a few unit tests
* Added support for new Ox 7 features: lambda expressions, the foreach keyword, and 
  default function argument values.
* Did some major refactoring and code cleaning

## April 4th, 2012
* Removed setup utility => not really necessary anymore because 
  Mathjax is default, and latex and dvipdfm are searced for on path
* Refactored HTML output refactoring
* Added global variable support
* Enumeration elements are now in the index
* Removed AsciiMathML support
* Fixed GUI
* Beautified java code


## March 23rd, 2012:
* fixed the enumeration declaration problem (enum { N=1, M =N+1} is now parsed properly)
* completed support for enumerations. Enumerations have no name in Ox, so they are 
  automatically called "Anonymous enumeration #1", etc. To suppress this behavior, 
  you can add an oxdoc comment before any enumeration, and use the @name tag to give 
  it a proper name. For example:

   /** Enumeration for feasilbility. 
       @name Feasibility **/
  enum { FEASIBLE, INFEASIBLE }

  The usual other @ tags should work as well.

* implemented indexing as per Chris Ferrall's suggestion: any overridden methods are
  placed on the same line.

* added document to the output: the hierarchy tree. Notice that you need to update your 
  oxdoc.css to get this to work

* changed the default oxdoc.css so that the output looks a bit prettier. At the same
  time, the HTML output is slightly different. The icons and texts in H1, H2, H3 headers
  are now inside SPANs.

* implemented math display through Mathjax, as per suggestion of Michael Massman 
  (see http://www.mathjax.org/). Mathjax is superior to LaTeX image generation, but 
  it requires an internet connection. Since everybody these days has an internet connection, 
  this now the default behavior. You can still use LaTeX to generate formulas by specifying
  the option "-formulas latex". 

* implemented automatic writing of auxiliary icons (for e.g. the tree, but also for 
  prettier output). If required icon files are not available, they are written. They are 
  never overwritten though. Icons are also on by default now. This can be disable by
  specifying "-noicons"

* added an option "-uplevel" to enable the "up level" link.

* implemented a @sortkey tag in all places. Every object has a sort key, which by default is
  the name of object (e.g. function name or class name), but which can be overridden by the 
  @sortkey tag. Objects are sorted according to their @sortkeys. In particular, this works
  for files, as you requested.

* The index now distinguishes between constructors, destructors, and methods.

* Default settings for Latex/dvipng executables now depend on the operating system.

* #import command now includes appropriate header file
* the following code is no longer rejected:
   [ a[1], a[2] ] = ... 
* members are now listed in documentation too
* ordering of private/protected/public in class definition is not fixed anymore
* ordering of cases in switch should not be fixed (default comes last now)
* add option to list members/methods as internal using @internal
* member modifiers (e.g. static functions/fields, const fields) are documented as well now 
* icons are now of the 'icon' class in the output HTML
* latex formulas are now of the 'latex' class in the output HTML
* improved css files / added Chris Ferrall's css file
* added goto statement / labels to Ox syntax
* added option for specifying include path 
* add icon for fields / enumerations
* in PcLire, Lire_Object and Lire_SimsSolver have no icon in the index (fixed)

