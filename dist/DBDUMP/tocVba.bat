@echo off
setlocal
mode 100,5
color 18
echo �����������@CREATE TOC����(VBA)�@�J�n�@����������
echo �쐬�Ώۂ̃t�H���_��I�����ď������I���܂ł��҂��������E�E�E
cd %~dp0
vba\runExcelMacro.vbs %~dp0\tocVba.xlsm main
echo �����������@CREATE TOC����(VBA)�@�I���@����������
pause
