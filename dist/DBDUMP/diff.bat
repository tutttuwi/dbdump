@echo off
setlocal
echo ‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘
echo ‘@
echo ‘@DB DUMP FILE DIFF@Jn
echo ‘@
echo ‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘

@echo off

rem δr³fBNgέθ
if "%1" EQU "" (
  set srcDir=./diff/before
) else (
  set srcDir=%1
)
rem δrζfBNgέθ
if "%2" EQU "" (
  set dstDir=./diff/after
) else (
  set dstDir=%2
)
rem e[uL[έθt@C
set tableKeyFile=.\resources\prop\tablekey.conf
rem δrΞΫOJέθt@C
set ignoreColumnFile=.\resources\prop\ignoreColumn.conf
rem όΝt@CΆGR[fBO
set inputFileEncode=UTF-8

rem JAVA_HOMEέθ
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ΰΎwθψ
rem ------------------------------
rem srcDir       :δr³fBNg
rem dstDir       :δrζfBNg
rem tableKeyFile :e[uPKέθt@C
rem diffFileDir  :δrΚt@CoΝfBNg
rem inputFileEncode  :όΝt@CΆGR[fBO
rem ==============================
echo ==================================================
echo έθl
echo --------------------------------------------------
echo δr³fBNg :  %srcDir%
echo δrζfBNg :  %dstDir%
echo e[uL[t@C :  %tableKeyFile%
echo δrΞΫOJt@C :  %ignoreColumnFile%
echo όΝt@CΆGR[fBO :  %inputFileEncode%
echo ==================================================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=%srcDir% dstDir=%dstDir% ^
  tableKeyFile=%tableKeyFile% diffFileDir=./ ^
  ignoreColumnFile=%ignoreColumnFile% ^
  inputFileEncode=%inputFileEncode%


echo ‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘
echo ‘@
echo ‘@DB DUMP FILE DIFF@IΉ
echo ‘@
echo ‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘‘

pause

