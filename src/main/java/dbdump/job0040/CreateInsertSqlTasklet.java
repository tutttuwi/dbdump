package dbdump.job0040;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import lombok.extern.slf4j.Slf4j;

/**
 * INSERT SQL文 作成タスクレット
 *
 * @author Tomo
 *
 */
@Slf4j
@Component
@StepScope
public class CreateInsertSqlTasklet implements Tasklet {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("#{jobParameters[srcDir]}")
    String srcDir;
    // @Value("#{jobParameters[outFileDir]}")
    // String outFileDir;
    @Value("#{jobParameters[inputFileEncode]}")
    String inputFileEncode;
    @Value("#{jobParameters[ignoreInsertSqlColumnFile]}")
    String ignoreInsertSqlColumnFile;

    private Map<String, List<String>> ignoreInsertSqlColumnMap = new HashMap<>();

    private static final String INSERT_TEMPLATE_SQL =
            "INSERT INTO {TABLE_NAME} ({HEADER_ARY}) VALUES({VALUES_ARY});";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {

        // 初期設定
        setup();
        // ファイル名
        // String insertSqlFilename = "insert_" + getYYYYMMDD_HHMMSS() + ".sql";
        Path srcDirPath = Paths.get(srcDir);
        String dirFullPath = srcDirPath.toAbsolutePath() + "_insert";
        log.info("ファイル出力先ディレクトリ [{}] ", dirFullPath);
        if (Files.notExists(Paths.get(dirFullPath))) {
            Files.createDirectory(Paths.get(dirFullPath));
        }

        List<Path> srcPathList = Files.list(srcDirPath).sorted(Comparator.comparing(Path::toString))
                .collect(Collectors.toList());

        for (Path srcPath : srcPathList) {
            String fileName = srcPath.getFileName().toString();
            String tableName = getPreffix(fileName); // CSVファイル名から拡張子を除去してテーブル名とみなします。

            String outFullPath = String.join("/", new String[] {dirFullPath, tableName + ".sql"});
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(outFullPath),
                    Charset.forName(inputFileEncode), StandardOpenOption.CREATE);
            CSVReader csvReader =
                    new CSVReaderBuilder(new FileReader(srcPath.toAbsolutePath().toString()))
                            .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).build();
            String[] nextLine;
            String[] headerLineAry = csvReader.readNext();

            bw.append("-- " + fileName);
            bw.newLine();
            List<String> ignoreInsertSqlColumnList = ignoreInsertSqlColumnMap.get(tableName);
            while ((nextLine = csvReader.readNext()) != null) {
                List<Integer> ignoreColPos = new ArrayList<Integer>();
                String headerLine = "";
                // log.info("headerLineAry : " + Arrays.asList(headerLineAry));
                for (int i = 0; i < headerLineAry.length; i++) {
                    String headerName = headerLineAry[i];
                    if ((Objects.nonNull(ignoreInsertSqlColumnList)
                            && !ignoreInsertSqlColumnList.contains(headerName))
                            || Objects.isNull(ignoreInsertSqlColumnList)) {
                        headerLine += headerName + ",";
                    } else {
                        ignoreColPos.add(i);
                    }
                    // String headerLine = Arrays.stream(headerLineAry)
                    // .filter(val -> ignoreInsertSqlColumnList.contains(val))
                    // .collect(Collectors.joining(","));
                }
                // log.info(headerLine);
                headerLine = headerLine.replaceAll(",$", ""); // 最後のカンマ削除

                String valuesLine = "";
                // log.info("nextLine : " + Arrays.asList(nextLine));
                for (int i = 0; i < nextLine.length; i++) {
                    String valuesName = nextLine[i];
                    if ("null".equals(valuesName) || "NULL".equals(valuesName)) {
                        valuesName = "";
                    }
                    if (!ignoreColPos.contains(i)) {
                        valuesLine += valuesName + "','";
                    }
                }
                // log.info(valuesLine);
                valuesLine = "'" + valuesLine.replaceAll("','$", "'"); // 先頭にカンマ、最後のカンマ削除

                String insertSql = INSERT_TEMPLATE_SQL; // SQLテンプレート文字列設定
                insertSql = insertSql.replaceAll("\\{TABLE_NAME\\}", tableName);
                insertSql = insertSql.replaceAll("\\{HEADER_ARY\\}", headerLine);
                insertSql = insertSql.replaceAll("\\{VALUES_ARY\\}", valuesLine);
                bw.append(insertSql);
                bw.newLine();
            }
            bw.close();
        }
        return RepeatStatus.FINISHED;

    }


    /**
     * 初期設定
     *
     * @throws Exception
     */
    private void setup() throws Exception {
        if (StringUtils.isEmpty(srcDir)) {
            log.error("ディレクトリが指定されていません。");
            throw new Exception();
        }
        // if (StringUtils.isEmpty(outFileDir)) {
        // outFileDir = "./"; // デフォルト値として現在ディレクトリを指定
        // }
        if (StringUtils.isEmpty(inputFileEncode)) {
            inputFileEncode = "UTF-8";
        }
        // 差分対象外カラムマップオブジェクト設定
        ignoreInsertSqlColumnMap = getIgnoreColumn();
        log.info("ignoreInsertSqlColumnMap : " + ignoreInsertSqlColumnMap.toString());
    }

    /**
     * 日時文字列取得
     *
     * @return
     */
    // private String getYYYYMMDD_HHMMSS() {
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    // return sdf.format(new Date());
    // }

    /**
     * ファイル名から拡張子を取り除いた名前を返します。
     *
     * @param fileName ファイル名
     * @return ファイル名
     */
    public static String getPreffix(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }

    /**
     * INSERT SQL 対象外カラムマップ取得
     *
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getIgnoreColumn() throws Exception {
        Map<String, List<String>> ret = new HashMap<>();
        if (Objects.isNull(ignoreInsertSqlColumnFile)) {
            return ret;
        }
        try (LineNumberReader lnr =
                new LineNumberReader(new FileReader(ignoreInsertSqlColumnFile))) {
            String line = "";
            while ((line = lnr.readLine()) != null) {
                String[] ar = line.split(":");
                if (ar.length != 2) {
                    log.error("INSERT SQL対象外カラムファイル書式誤り！ {}行目の設定に誤りがあります。", lnr.getLineNumber());
                    throw new Exception();
                }
                String tableName = line.split(":")[0];
                String ignoreColumnArray = line.split(":")[1];
                List<String> list = Arrays.asList(ignoreColumnArray.split(","));
                ret.put(tableName, list);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return ret;
    }

}
