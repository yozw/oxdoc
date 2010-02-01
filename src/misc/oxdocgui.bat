@echo off

rem The following code should automatically retrieve the directory
rem in which this file resides, and find oxdoc.jar accordingly
rem If, for some reason, this does not work, feel free to edit the
rem line below
set oxdocjar=%~dp0oxdoc.jar


              
rem ---------------- Do not edit the code below ------------------ 
if not exist "%oxdocjar%" goto oxdoc_jar_not_found

java -jar "%oxdocjar%" -gui
goto end

:oxdoc_jar_not_found
echo * The file %oxdocjar% could not be found.
echo * Please edit the location specified in the oxdoc.bat file.

:end
