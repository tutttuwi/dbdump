@echo off
setlocal
echo ����������������������������������������
echo ���@
echo ���@DB DUMP�����@�J�n
echo ���@
echo ����������������������������������������

@echo off
rem ���ݓ��t�iYYYYMMDD�j�̎擾
set date_str=%date:~-10,4%%date:~-5,2%%date:~-2,2%
rem ���ݎ����iHHMMSS�j�̎擾
set time_str=%time: =0%
set time_str=%time_str:~0,2%%time_str:~3,2%%time_str:~6,2%

rem ���݃f�B���N�g���̎擾
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem JAVA_HOME�ݒ�
set path

java -cp %current_dir%resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0010.AppConfig0010 dbDumpJob ^
  outputDir=%date_str%_%time_str%  execSqlList=.\resources\sql\execSqlList.txt ^
  charSplitConma= charSplitDoubleQuoted= ^
  charSplitCr=NONE charSplitLf=NONE charSplitCrLf=NONE


echo ����������������������������������������
echo ���@
echo ���@DB DUMP�����@�I��
echo ���@
echo ����������������������������������������

pause

