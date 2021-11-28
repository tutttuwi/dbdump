package dbdump.job0010;

import java.io.BufferedWriter;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
public class DbDumpTasklet implements Tasklet {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("#{jobParameters[outputDir]}")
    String outputDir;
    @Value("#{jobParameters[execSqlList]}")
    String execSqlList;

    // 置換文字列
    @Value("#{jobParameters[charSplitConma]}")
    String strSc;
    @Value("#{jobParameters[charSplitDoubleQuoted]}")
    String strSdq;
    @Value("#{jobParameters[charSplitCr]}")
    String strScr;
    @Value("#{jobParameters[charSplitLf]}")
    String strSlf;
    @Value("#{jobParameters[charSplitCrLf]}")
    String strScrlf;

    @Value("#{jobParameters[encode]}")
    String encode;

    private static final String TAG_DOUBLE_QUOTED = "＜DOUBLE_QUOTED＞";
    private static final String TAG_CONMA = "＜CONMA＞";
    private static final String TAG_CR = "＜CR＞";
    private static final String TAG_LF = "＜LF＞";
    private static final String TAG_CRLF = "＜CRLF＞";

    private static final String SPLIT_NONE = "NONE";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        // 初期処理
        setup();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        try (LineNumberReader br = new LineNumberReader(
                Files.newBufferedReader(Paths.get(execSqlList), Charset.forName(encode)));) {
            BufferedWriter bw = null;
            // 引数で渡す
            log.info("ファイル出力先ディレクトリ [{}] ", outputDir);
            if (Files.notExists(Paths.get(outputDir))) {
                Files.createDirectory(Paths.get(outputDir));
            }

            String sqlLine = "";
            while ((sqlLine = br.readLine()) != null) {
                // マルチスレッド実行
                executor.execute(new ExecuteSql(sqlLine, br, bw));
            }
            // 新規タスクの受付を終了して残ったタスクを継続する．
            executor.shutdown();
            try {
                // 指定時間が経過するか，全タスクが終了するまで処理を停止する．
                executor.awaitTermination(100, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("エラー発生！　メッセージ：{}", e.getMessage());
        } finally {
        }
        return RepeatStatus.FINISHED;
    }

    /**
     * セットアップ処理.
     */
    private void setup() {
        if (StringUtils.isEmpty(strSc)) {
            strSc = TAG_CONMA;
        } else if (strSc.equals(SPLIT_NONE)) {
            strSc = "";
        }
        if (StringUtils.isEmpty(strSdq)) {
            strSdq = TAG_DOUBLE_QUOTED;
        } else if (strSdq.equals(SPLIT_NONE)) {
            strSdq = "";
        }
        if (StringUtils.isEmpty(strScr)) {
            strScr = TAG_CR;
        } else if (strScr.equals(SPLIT_NONE)) {
            strScr = "";
        }
        if (StringUtils.isEmpty(strSlf)) {
            strSlf = TAG_LF;
        } else if (strSlf.equals(SPLIT_NONE)) {
            strSlf = "";
        }
        if (StringUtils.isEmpty(strScrlf)) {
            strScrlf = TAG_CRLF;
        } else if (strScrlf.equals(SPLIT_NONE)) {
            strScrlf = "";
        }

        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
            // 文字コードが指定された場合、指定された文字コードを使用する
        }
    }

    class ExecuteSql implements Runnable {

        private String sqlLine;
        private LineNumberReader br;
        private BufferedWriter bw;

        public ExecuteSql(String sqlLine, LineNumberReader br, BufferedWriter bw) {
            this.sqlLine = sqlLine;
            this.br = br;
            this.bw = bw;
        }

        @Override
        public void run() {
            // スレッドIDを取得
            String threadName = Thread.currentThread().getName();
            long threadID = Thread.currentThread().getId();
            // log.info("【開始】スレッド名：{} スレッドID：{}", threadName, threadID);

            try {
                log.info("実行SQL行：{} [{} {}]", sqlLine, threadName, threadID);
                if (StringUtils.isEmpty(sqlLine)) {
                    return;
                }

                String outputFilename = sqlLine.split(":")[0] + ".csv";
                String outFullPath = String.join("/", new String[] {outputDir, outputFilename});
                String sql = sqlLine.split(":")[1];
                if (StringUtils.isEmpty(outputFilename) || StringUtils.isEmpty(sql)) {
                    log.warn("行数：{} ファイル名：{} SQL文：{}", br.getLineNumber(), outputFilename, sql);
                    return;
                }
                bw = Files.newBufferedWriter(Paths.get(outFullPath), Charset.forName(encode),
                        StandardOpenOption.CREATE);
                SqlRowSet sqlRs = jdbcTemplate.queryForRowSet(sql);
                String[] colnames = sqlRs.getMetaData().getColumnNames();
                // カラム名に改行コードやダブルクォーテーションが入っている想定はしていない
                bw.append(String.join(",", colnames));
                bw.newLine();
                while (sqlRs.next()) {
                    List<String> dataList = new ArrayList<>();
                    for (int i = 0; i < colnames.length; i++) {
                        // ダブルクォーテーションで囲み＆エスケープ処理
                        String data = String.valueOf(sqlRs.getObject(colnames[i]));
                        // @formatter:off
                        data = data.replaceAll(",", strSc)
                                .replaceAll("\"", strSdq)
                                .replaceAll("\r", strScr)
                                .replaceAll("\n", strSlf)
                                .replaceAll("\r\n", strScrlf);
                        // @formatter:on
                        // "'data" 形式でCSVファイルで開いた際も欠落しないように対応
                        dataList.add("\"'" + data + "\"");
                    }
                    bw.append(String.join(",", dataList));
                    bw.newLine();
                }
                // 書き込み終了（次の行へ）
                bw.close();

            } catch (Exception e) {
                e.printStackTrace();
                log.error("エラー発生！　メッセージ：{}", e.getMessage());
            }

            // log.info("【終了】スレッド名：{} スレッドID：{}", threadName, threadID);

        }

    }
}
