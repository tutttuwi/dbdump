@echo off
setlocal
echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP FILE DIFF処理　開始
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

@echo off

rem ==============================
rem ＜説明＞指定引数
rem ------------------------------
rem srcDir       :比較元ディレクトリ
rem dstDir       :比較先ディレクトリ
rem tableKeyFile :テーブルPK設定ファイル
rem diffFileDir  :比較結果ファイル出力ディレクトリ
rem ==============================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=./diff/before dstDir=./diff/after ^
  tableKeyFile=.\resources\prop\tablekey.conf diffFileDir=./ 


echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP FILE DIFF処理　終了
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

pause

