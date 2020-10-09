package dbdump.job0010;

import java.io.BufferedWriter;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        try (LineNumberReader br = new LineNumberReader(
                Files.newBufferedReader(Paths.get(execSqlList), Charset.forName("MS932")));) {
            BufferedWriter bw = null;
            // 引数で渡す
            System.out.println(outputDir);
            if(Files.notExists(Paths.get(outputDir))) {
                Files.createDirectory(Paths.get(outputDir));
            }
            String sqlLine = "";
            while ((sqlLine = br.readLine()) != null) {
                System.out.println(sqlLine);
                if (StringUtils.isEmpty(sqlLine)) {
                    continue;
                }
                String outputFilename = sqlLine.split(":")[0] + ".csv";
                String outFullPath =String.join("/", new String[]{outputDir, outputFilename});
                String sql = sqlLine.split(":")[1];
                if (StringUtils.isEmpty(outputFilename) || StringUtils.isEmpty(sql)) {
                    log.warn("行数：{} ファイル名：{} SQL文：{}", br.getLineNumber(), outputFilename, sql);
                    continue;
                }
                bw = Files.newBufferedWriter(Paths.get(outFullPath), Charset.forName("MS932"),
                        StandardOpenOption.CREATE);
                SqlRowSet sqlRs = jdbcTemplate.queryForRowSet(sql);
                String[] colnames = sqlRs.getMetaData().getColumnNames();
                bw.append(String.join(",", colnames));
                bw.newLine();
                while (sqlRs.next()) {
                    List<String> dataList = new ArrayList<>();
                    for (int i = 0; i < colnames.length; i++) {
                        dataList.add(String.valueOf(sqlRs.getObject(colnames[i])));
                    }
                    bw.append(String.join(",", dataList));
                    bw.newLine();
                }
                // 書き込み終了（次の行へ）
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("エラー発生！　メッセージ：{}", e.getMessage());
        }
        return RepeatStatus.FINISHED;
    }
}
