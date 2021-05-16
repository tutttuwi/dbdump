@echo off
setlocal
echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP処理　開始
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

@echo off
rem 現在日付（YYYYMMDD）の取得
set date_str=%date:~-10,4%%date:~-5,2%%date:~-2,2%
rem 現在時刻（HHMMSS）の取得
set time_str=%time: =0%
set time_str=%time_str:~0,2%%time_str:~3,2%%time_str:~6,2%

rem 現在ディレクトリの取得
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem JAVA_HOME設定
set path

java -cp %current_dir%resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0010.AppConfig0010 dbDumpJob ^
  outputDir=%date_str%_%time_str%  execSqlList=.\resources\sql\execSqlList.txt ^
  charSplitConma= charSplitDoubleQuoted= ^
  charSplitCr=NONE charSplitLf=NONE charSplitCrLf=NONE


echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP処理　終了
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

pause

