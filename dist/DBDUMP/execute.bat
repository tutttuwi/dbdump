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

rem JAVA_HOME�ݒ�
rem set PATH=[jdk11 directory you downloaded];%PATH%

rem ==============================
rem ���������w�����
rem ------------------------------
rem outputDir            :�f�t�H���g�Ō��ݓ��t+���ݎ���(YYYYMMDD_HHMMSS)
rem charSplitConma       :�J�������̃R���}(,)��������ǂ̂悤�ȕ����ɕϊ����邩���w��B�f�t�H���g�̓h�L�������g�Q��
rem charSplitDoubleQuoted:�J�������̃_�u���N�H�[�e�[�V����(")��������ǂ̂悤�ȕ����ɕϊ����邩���w��B�f�t�H���g�̓h�L�������g�Q��
rem charSplitCr          :�J��������CR(\r)��������ǂ̂悤�ȕ����ɕϊ����邩���w��B�f�t�H���g�̓h�L�������g�Q��  NONE���w�肷��ƕϊ����Ȃ��B
rem charSplitLf          :�J��������LF(\n)��������ǂ̂悤�ȕ����ɕϊ����邩���w��B�f�t�H���g�̓h�L�������g�Q��  NONE���w�肷��ƕϊ����Ȃ��B
rem charSplitCrLf        :�J��������CRLF(\r\n)��������ǂ̂悤�ȕ����ɕϊ����邩���w��B�f�t�H���g�̓h�L�������g�Q��  NONE���w�肷��ƕϊ����Ȃ��B
rem encode               :SQL�t�@�C���y�яo�̓t�@�C���̕����R�[�h���w�肷��BSJIS�ŕ\���ł��Ȃ������񂪊i�[����Ă����ꍇ�G���[�ɂȂ��Ă��܂����߁A�f�t�H���g��UTF-8�BSQL�t�@�C����UTF-8�ō쐬����K�v����B
rem ==============================
java -cp %CD%\resources\prop;.\resources\lib\dbdump-0.0.1-SNAPSHOT-all.jar ^
  org.springframework.batch.core.launch.support.CommandLineJobRunner ^
  dbdump.job0010.AppConfig0010 dbDumpJob ^
  outputDir=%date_str%_%time_str%  execSqlList=.\resources\sql\execSqlList.txt ^
  charSplitConma= charSplitDoubleQuoted= ^
  charSplitCr=NONE charSplitLf=NONE charSplitCrLf=NONE ^
  encode=UTF-8


echo ����������������������������������������
echo ���@
echo ���@DB DUMP�����@�I��
echo ���@
echo ����������������������������������������

pause

