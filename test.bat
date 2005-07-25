@echo off
pushd testsrc
del html\*.html
java -classpath ..\bin\oxdoc.jar OxDoc *.ox
:*.h *.ox
move *.html html
if not exist oxdoc.css copy ..\oxdoc.css html\
popd
