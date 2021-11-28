@echo off
setlocal
mode 100,5
color 18
echo ■■■■■　CREATE TOC処理(VBA)　開始　■■■■■
echo 作成対象のフォルダを選択して処理が終わるまでお待ち下さい・・・
cd %~dp0
vba\runExcelMacro.vbs %~dp0\tocVba.xlsm main
echo ■■■■■　CREATE TOC処理(VBA)　終了　■■■■■
pause
