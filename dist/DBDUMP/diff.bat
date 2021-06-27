@echo off
setlocal
echo ����������������������������������������
echo ���@
echo ���@DB DUMP FILE DIFF�����@�J�n
echo ���@
echo ����������������������������������������

@echo off

rem ��r���f�B���N�g���ݒ�
if "%1" EQU "" (
  set srcDir=./diff/before
) else (
  set srcDir=%1
)
rem ��r��f�B���N�g���ݒ�
if "%2" EQU "" (
  set dstDir=./diff/after
) else (
  set dstDir=%2
)
rem �e�[�u���L�[�ݒ�t�@�C��
set tableKeyFile=.\resources\prop\tablekey.conf
rem ��r�ΏۊO�J�����ݒ�t�@�C��
set ignoreColumnFile=.\resources\prop\ignoreColumn.conf
rem ���̓t�@�C�������G���R�[�f�B���O
set inputFileEncode=UTF-8

rem JAVA_HOME�ݒ�
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ���������w�����
rem ------------------------------
rem srcDir       :��r���f�B���N�g��
rem dstDir       :��r��f�B���N�g��
rem tableKeyFile :�e�[�u��PK�ݒ�t�@�C��
rem diffFileDir  :��r���ʃt�@�C���o�̓f�B���N�g��
rem inputFileEncode  :���̓t�@�C�������G���R�[�f�B���O
rem ==============================
echo ==================================================
echo ���ݒ�l��
echo --------------------------------------------------
echo ��r���f�B���N�g�� :  %srcDir%
echo ��r��f�B���N�g�� :  %dstDir%
echo �e�[�u���L�[�t�@�C�� :  %tableKeyFile%
echo ��r�ΏۊO�J�����t�@�C�� :  %ignoreColumnFile%
echo ���̓t�@�C�������G���R�[�f�B���O :  %inputFileEncode%
echo ==================================================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=%srcDir% dstDir=%dstDir% ^
  tableKeyFile=%tableKeyFile% diffFileDir=./ ^
  ignoreColumnFile=%ignoreColumnFile% ^
  inputFileEncode=%inputFileEncode%


echo ����������������������������������������
echo ���@
echo ���@DB DUMP FILE DIFF�����@�I��
echo ���@
echo ����������������������������������������

pause

