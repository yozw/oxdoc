;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;General

  ;Name and file
  Name "oxdoc 0.95alpha"
  OutFile "setup.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\oxdoc"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\oxdoc" ""

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "..\license"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  Page custom LatexDir ValidateLatexDir
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

  ReserveFile "latexdir.ini"
  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Program files" SecProgram

  SetOutPath "$INSTDIR\bin"

  SetOverwrite on
  File ..\bin\oxdoc.jar
  File ..\lib\jregex.jar

  SetOutPath "$INSTDIR\css"
  File ..\css\*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\oxdoc" "" $INSTDIR

  !insertmacro MUI_INSTALLOPTIONS_READ $R0 "latexdir.ini" "Field 2" "State"  
  
  # BAD BAD NSIS !! This code is for removing a trailing backslash
  Push $R0      # directory to remove trailing backslash from.         Stack: $0(with backslash)
  Exch $EXEDIR  # exchange with a built-in dir var - exedir will do.
                # NSIS automatically removes the trailing backslash.   Stack: $EXEDIR(original)
  Exch $EXEDIR  # restore original dir var.                            Stack: $0(without backslash)
  Pop $R0       # and pop the directory without the backslash.         Stack: <clean> 

  WriteRegStr HKCU "Software\oxdoc" "latex" $R0

  SetOutPath "$INSTDIR"
  FileOpen $9 oxdoc.bat w ;Opens a Empty File an fills it
  FileWrite $9 "@java -classpath $\"$INSTDIR\bin\oxdoc.jar;$INSTDIR\bin\jregex.jar$\" OxDocCmd %1 %2 %3 %4 %5 %6 %7 %8 %9$\r$\n"
  FileClose $9 ;Closes the filled file 

  FileOpen $9 oxdocgui.bat w ;Opens a Empty File an fills it
  FileWrite $9 "@start /b javaw -classpath $\"$INSTDIR\bin\oxdoc.jar;$INSTDIR\bin\jregex.jar$\" OxDocGui %1 %2 %3 %4 %5 %6 %7 %8 %9$\r$\n"
  FileClose $9 ;Closes the filled file 

  CopyFiles `oxdoc.bat` `$WINDIR`

  SetOutPath "$INSTDIR\bin"
  FileOpen $9 oxdoc.xml w ;Opens a Empty File an fills it
  FileWrite $9 "<oxdoc>$\r$\n"
  FileWrite $9 "<option name=$\"latex$\" value=$\"$R0\latex.exe$\" />$\r$\n"
  FileWrite $9 "<option name=$\"dvipng$\" value=$\"$R0\dvipng.exe$\" />$\r$\n"
  FileWrite $9 "</oxdoc>$\r$\n"
  FileClose $9 ;Closes the filled file 

  CreateDirectory "$SMPROGRAMS\oxdoc"
  CreateShortCut "$SMPROGRAMS\oxdoc\oxdoc Gui.lnk" "javaw.exe" "-classpath $\"$INSTDIR\bin\oxdoc.jar;$INSTDIR\bin\jregex.jar$\" oxdocGui %1 %2 %3 %4 %5 %6 %7 %8 %9$\r$\n"
  CreateShortCut "$SMPROGRAMS\oxdoc\Manual.lnk" "$INSTDIR\manual\manual.pdf" 
  CreateShortCut "$SMPROGRAMS\oxdoc\Uninstall oxdoc.lnk" "$INSTDIR\uninstall.exe" 

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Examples" SecExamples

  SetOutPath "$INSTDIR\example"

  SetOverwrite off
  File ..\example\*

SectionEnd

Section "Manual" SecManual

  SetOutPath "$INSTDIR\manual"

  SetOverwrite on
  File ..\manual\manual.pdf

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecProgram ${LANG_ENGLISH} "oxdoc program files"
  LangString DESC_SecExamples ${LANG_ENGLISH} "Some examples"
  LangString DESC_SecManual ${LANG_ENGLISH} "Manual in HTML format"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecProgram} $(DESC_SecProgram)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecExamples} $(DESC_SecExamples)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecManual} $(DESC_SecManual)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Custom page for asking Latex folder section

Function .onInit

  ;Extract InstallOptions INI files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "latexdir.ini"
  
FunctionEnd

LangString TEXT_IO_TITLE ${LANG_ENGLISH} "Latex folder"
LangString TEXT_IO_SUBTITLE ${LANG_ENGLISH} "Select the folder in which your Latex installation resides."

Function LatexDir

  !insertmacro MUI_HEADER_TEXT "$(TEXT_IO_TITLE)" "$(TEXT_IO_SUBTITLE)"

  Push $0
  ReadRegStr $0 HKCU "Software\oxdoc" "latex"
  StrCmp $0 "" done
  !insertmacro MUI_INSTALLOPTIONS_WRITE	"latexdir.ini" "Field 2" "State" $0

done:
  Pop $0
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "latexdir.ini"

FunctionEnd

Function ValidateLatexDir

  !insertmacro MUI_INSTALLOPTIONS_READ $R0 "latexdir.ini" "Field 2" "State"  

  # BAD BAD NSIS !! This code is for removing a trailing backslash
  Push $R0      # directory to remove trailing backslash from.         Stack: $0(with backslash)
  Exch $EXEDIR  # exchange with a built-in dir var - exedir will do.
                # NSIS automatically removes the trailing backslash.   Stack: $EXEDIR(original)
  Exch $EXEDIR  # restore original dir var.                            Stack: $0(without backslash)
  Pop $R0       # and pop the directory without the backslash.         Stack: <clean> 

  IfFileExists "$R0\latex.exe" 0 NotFound
  IfFileExists "$R0\dvipng.exe" 0 NotFound
  Goto Done

NotFound:

  MessageBox MB_ICONEXCLAMATION|MB_OKCANCEL "Latex executable $R0\latex.exe and/or $R0\dvipng.exe not found. Click OK to install oxdoc without Latex support (you can still enable Latex support later by editing oxdoc.xml) or click Cancel to select the folder that contains latex.exe and dvipng.exe." IDOK Done
  Abort

Done:

FunctionEnd

;--------------------------------
;Uninstall section

Section "Uninstall"

  Delete "$INSTDIR\oxdoc.bat"
  Delete "$INSTDIR\oxdocgui.bat"
  Delete "$INSTDIR\bin\*"
  Delete "$INSTDIR\css\*"
  Delete "$INSTDIR\example\*"
  Delete "$INSTDIR\manual\*"

  Delete "$INSTDIR\Uninstall.exe"


  RMDir "$INSTDIR\bin"
  RMDir "$INSTDIR\css"
  RMDir "$INSTDIR\example"
  RMDir "$INSTDIR\manual"
  RMDir "$INSTDIR"


  Delete "$SMPROGRAMS\oxdoc\*"
  RMDir "$SMPROGRAMS\oxdoc"


  DeleteRegKey HKCU "Software\oxdoc"

SectionEnd