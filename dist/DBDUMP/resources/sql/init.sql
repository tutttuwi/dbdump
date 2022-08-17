
/*
==================================================
                 初期化用クエリ
==================================================
*/

/*
【SQL Server用】
[##TMP_TABLE名]グローバル一時テーブルを作成
ここで作成したグローバルテーブルは後続の「execSqlList.txt」ファイルで発行されるクエリ内で利用できます。
*/
SELECT * INTO ##TMP_APPLE FROM FRUIT(NOLOCK) WHERE name = 'APPLE';
/*
結果セット返却
*/
SELECT * FROM ##TMP_APPLE(NOLOCK);
