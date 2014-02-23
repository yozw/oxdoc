# Oxdoc

oxdoc 1.1-beta [23/02/2014] (c) Copyright 2005-2014 by Y. Zwols

oxdoc is free software and comes with ABSOLUTELY NO WARRANTY.
You are welcome to redistribute it under certain conditions.
See the LICENSE file for distribution details.




## Installation instructions (binaries)

### Windows
* Unzip the binary zip file
* Use oxdoc.bat to run oxdoc
* You may want to copy oxdoc.bat to a directory in your path, e.g. 
  c:\windows\system32 .  In that case, copy oxdoc.jar to the same
  directory.


### Linux/OS X
* Unzip the binary zip file
* Activate execute permission for the oxdoc script in the bin/ directory
* Edit bin/oxdoc.xml
* Run bin/oxdoc
* Alternatively, you can place oxdoc and oxdoc.jar in a directory in your path,
  e.g. ~/bin  or  /usr/local/bin



## Compilation instructions
* Install JavaCC
* Copy build.properties.template to build.properties and edit it
* Run 'ant zips' -- this will make zip files containing source and
  binary distributions in the dist/ subdirectory
