@echo off
setlocal
echo ����������������������������������������
echo ���@
echo ���@DB DUMP CREATE Insert Sql �����@�J�n
echo ���@
echo ����������������������������������������

@echo off

rem �f�B���N�g���ݒ�
if "%1" EQU "" (
  echo ---------------------------------------------------------------
  echo.
  echo �f�[�^�i�[�f�B���N�g�����w�肳��Ă��܂���B�������I�����܂��B
  echo.
  echo ---------------------------------------------------------------
  pause
  exit
) else (
  set srcDir=%1
)
rem ���̓t�@�C�������G���R�[�f�B���O
set inputFileEncode=UTF-8

rem ���O�J�����ݒ�t�@�C��
set ignoreInsertSqlColumnFile=.\resources\prop\ignoreInsertSqlColumn.conf

rem JAVA_HOME�ݒ�
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ���������w�����
rem ------------------------------
rem srcDir       :�f�[�^�i�[�f�B���N�g��
rem tocFileDir   :�t�@�C���o�̓f�B���N�g��
rem inputFileEncode  :���̓t�@�C�������G���R�[�f�B���O
rem ==============================
echo ==================================================
echo ���ݒ�l��
echo --------------------------------------------------
echo ���f�B���N�g�� :  %srcDir%
echo �G���R�[�h :  %inputFileEncode%
echo InsertSQL���O�J���� :  %ignoreInsertSqlColumnFile%
echo ==================================================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0040.AppConfig0040 createInsertSqlJob ^
  srcDir=%srcDir% ^
  inputFileEncode=%inputFileEncode% ^
  ignoreInsertSqlColumnFile=%ignoreInsertSqlColumnFile%


echo ����������������������������������������
echo ���@
echo ���@DB DUMP CREATE Insert Sql �����@�I��
echo ���@
echo ����������������������������������������

pause

