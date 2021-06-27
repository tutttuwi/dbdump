@echo off
setlocal
echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP FILE DIFF処理　開始
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

@echo off

rem 比較元ディレクトリ設定
if "%1" EQU "" (
  set srcDir=./diff/before
) else (
  set srcDir=%1
)
rem 比較先ディレクトリ設定
if "%2" EQU "" (
  set dstDir=./diff/after
) else (
  set dstDir=%2
)
rem テーブルキー設定ファイル
set tableKeyFile=.\resources\prop\tablekey.conf
rem 比較対象外カラム設定ファイル
set ignoreColumnFile=.\resources\prop\ignoreColumn.conf
rem 入力ファイル文字エンコーディング
set inputFileEncode=UTF-8

rem JAVA_HOME設定
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ＜説明＞指定引数
rem ------------------------------
rem srcDir       :比較元ディレクトリ
rem dstDir       :比較先ディレクトリ
rem tableKeyFile :テーブルPK設定ファイル
rem diffFileDir  :比較結果ファイル出力ディレクトリ
rem inputFileEncode  :入力ファイル文字エンコーディング
rem ==============================
echo ==================================================
echo ＜設定値＞
echo --------------------------------------------------
echo 比較元ディレクトリ :  %srcDir%
echo 比較先ディレクトリ :  %dstDir%
echo テーブルキーファイル :  %tableKeyFile%
echo 比較対象外カラムファイル :  %ignoreColumnFile%
echo 入力ファイル文字エンコーディング :  %inputFileEncode%
echo ==================================================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=%srcDir% dstDir=%dstDir% ^
  tableKeyFile=%tableKeyFile% diffFileDir=./ ^
  ignoreColumnFile=%ignoreColumnFile% ^
  inputFileEncode=%inputFileEncode%


echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP FILE DIFF処理　終了
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

pause

