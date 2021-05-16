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

rem JAVA_HOME設定
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ＜説明＞指定引数
rem ------------------------------
rem outputDir            :デフォルトで現在日付+現在時刻(YYYYMMDD_HHMMSS)
rem charSplitConma       :カラム内のコンマ(,)文字列をどのような文字に変換するかを指定。デフォルトはドキュメント参照
rem charSplitDoubleQuoted:カラム内のダブルクォーテーション(")文字列をどのような文字に変換するかを指定。デフォルトはドキュメント参照
rem charSplitCr          :カラム内のCR(\r)文字列をどのような文字に変換するかを指定。デフォルトはドキュメント参照  NONEを指定すると変換しない。
rem charSplitLf          :カラム内のLF(\n)文字列をどのような文字に変換するかを指定。デフォルトはドキュメント参照  NONEを指定すると変換しない。
rem charSplitCrLf        :カラム内のCRLF(\r\n)文字列をどのような文字に変換するかを指定。デフォルトはドキュメント参照  NONEを指定すると変換しない。
rem encode               :SQLファイル及び出力ファイルの文字コードを指定する。SJISで表現できない文字列が格納されていた場合エラーになってしまうため、デフォルトはUTF-8。SQLファイルもUTF-8で作成する必要あり。
rem ==============================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0010.AppConfig0010 dbDumpJob ^
  outputDir=%date_str%_%time_str%  execSqlList=.\resources\sql\execSqlList.txt ^
  charSplitConma= charSplitDoubleQuoted= ^
  charSplitCr=NONE charSplitLf=NONE charSplitCrLf=NONE ^
  encode=UTF-8


echo ■■■■■■■■■■■■■■■■■■■■
echo ■　
echo ■　DB DUMP処理　終了
echo ■　
echo ■■■■■■■■■■■■■■■■■■■■

pause

