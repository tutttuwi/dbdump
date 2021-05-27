@echo off
setlocal
echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP CREATE TOC処理　開始
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

@echo off

rem ディレクトリ設定
if "%1" EQU "" (
  echo ---------------------------------------------------------------
  echo.
  echo データ格納ディレクトリが指定されていません。処理を終了します。
  echo.
  echo ---------------------------------------------------------------
  pause
  exit
) else (
  set srcDir=%1
)
rem 入力ファイル文字エンコーディング
set inputFileEncode=UTF-8

rem JAVA_HOME設定
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ＜説明＞指定引数
rem ------------------------------
rem srcDir       :データ格納ディレクトリ
rem tocFileDir   :ファイル出力ディレクトリ
rem inputFileEncode  :入力ファイル文字エンコーディング
rem ==============================
echo ==================================================
echo ＜設定値＞
echo --------------------------------------------------
echo 比較元ディレクトリ :  %srcDir%
echo 比較先ディレクトリ :  %tocFileDir%
echo テーブルキーファイル :  %inputFileEncode%
echo ==================================================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0030.AppConfig0030 createTocJob ^
  srcDir=%srcDir% tocFileDir ^
  inputFileEncode=%inputFileEncode%


echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP CREATE TOC処理　終了
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

pause

