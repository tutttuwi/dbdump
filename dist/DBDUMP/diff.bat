@echo off
setlocal
echo ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
echo ¡@
echo ¡@DB DUMP FILE DIFF@Jn
echo ¡@
echo ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡

@echo off

rem ==============================
rem à¾wèø
rem ------------------------------
rem srcDir       :är³fBNg
rem dstDir       :äræfBNg
rem tableKeyFile :e[uPKÝèt@C
rem diffFileDir  :ärÊt@CoÍfBNg
rem ==============================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=./diff/before dstDir=./diff/after ^
  tableKeyFile=.\resources\prop\tablekey.conf diffFileDir=./ 


echo ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡
echo ¡@
echo ¡@DB DUMP FILE DIFF@I¹
echo ¡@
echo ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡

pause

