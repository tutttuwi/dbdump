@echo off
setlocal
echo ����������������������������������������
echo ���@
echo ���@DB DUMP FILE DIFF�����@�J�n
echo ���@
echo ����������������������������������������

@echo off

rem ==============================
rem ���������w�����
rem ------------------------------
rem srcDir       :��r���f�B���N�g��
rem dstDir       :��r��f�B���N�g��
rem tableKeyFile :�e�[�u��PK�ݒ�t�@�C��
rem diffFileDir  :��r���ʃt�@�C���o�̓f�B���N�g��
rem ==============================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0020.AppConfig0020 diffDataJob ^
  srcDir=./diff/before dstDir=./diff/after ^
  tableKeyFile=.\resources\prop\tablekey.conf diffFileDir=./ 


echo ����������������������������������������
echo ���@
echo ���@DB DUMP FILE DIFF�����@�I��
echo ���@
echo ����������������������������������������

pause

