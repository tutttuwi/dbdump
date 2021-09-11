package dbdump.job0030;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import lombok.extern.slf4j.Slf4j;

/**
 * 目次作成タスクレット
 *
 * @author Tomo
 *
 */
@Slf4j
@Component
@StepScope
public class CreateTocTasklet implements Tasklet {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("#{jobParameters[srcDir]}")
    String srcDir;
    @Value("#{jobParameters[tocFileDir]}")
    String tocFileDir;
    @Value("#{jobParameters[inputFileEncode]}")
    String inputFileEncode;

    // 各種変数設定
    private static final String TOC_NAME = "目次";
    // ヘッダー配列と列位置は揃えること
    private static final String[] HEADER_ARRAY = {"NO", "ファイル名", "備考"};
    private static final int NO_COL = 0;
    private static final int FILE_COL = 1;
    private static final int MISC_COL = 2;
    private static final int START_HEADER_ROW = 0;
    private static final int START_DATA_ROW = 1;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {

        // 初期設定
        setup();
        // ファイル名
        String filename = "toc_" + getYYYYMMDD_HHMMSS() + ".xlsx";

        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet(TOC_NAME);
        Sheet tocSheet = workbook.getSheet(TOC_NAME);
        Font font = workbook.createFont();
        font.setFontName("メイリオ");
        font.setFontHeightInPoints((short) 9);

        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);

        CellStyle styleDiff = workbook.createCellStyle();
        styleDiff.setBorderTop(BorderStyle.THIN);
        styleDiff.setBorderRight(BorderStyle.THIN);
        styleDiff.setBorderBottom(BorderStyle.THIN);
        styleDiff.setBorderLeft(BorderStyle.THIN);
        styleDiff.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleDiff.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleDiff.setFont(font);

        CellStyle stylePk = workbook.createCellStyle();
        stylePk.setBorderTop(BorderStyle.THIN);
        stylePk.setBorderRight(BorderStyle.THIN);
        stylePk.setBorderBottom(BorderStyle.THIN);
        stylePk.setBorderLeft(BorderStyle.THIN);
        stylePk.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylePk.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        stylePk.setFont(font);

        CellStyle styleSrcOnly = workbook.createCellStyle();
        styleSrcOnly.setBorderTop(BorderStyle.THIN);
        styleSrcOnly.setBorderRight(BorderStyle.THIN);
        styleSrcOnly.setBorderBottom(BorderStyle.THIN);
        styleSrcOnly.setBorderLeft(BorderStyle.THIN);
        styleSrcOnly.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleSrcOnly.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleSrcOnly.setFont(font);

        CellStyle styleDstOnly = workbook.createCellStyle();
        styleDstOnly.setBorderTop(BorderStyle.THIN);
        styleDstOnly.setBorderRight(BorderStyle.THIN);
        styleDstOnly.setBorderBottom(BorderStyle.THIN);
        styleDstOnly.setBorderLeft(BorderStyle.THIN);
        styleDstOnly.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleDstOnly.setFillForegroundColor(IndexedColors.TAN.getIndex());
        styleDstOnly.setFont(font);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        styleHeader.setFont(font);


        // ============================================================
        // 目次作成
        // ============================================================

        // ==============================
        // ヘッダー行生成
        // ==============================
        Row headerRow = tocSheet.createRow(0);
        for (int i = 0; i < HEADER_ARRAY.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADER_ARRAY[i]);
        }

        // ==============================
        // データ行生成
        // ==============================
        // 初期設定
        // ファイル処理
        Path srcDirPath = Paths.get(srcDir);
        List<Path> srcPathList = Files.list(srcDirPath).sorted(Comparator.comparing(Path::toString))
                .collect(Collectors.toList());
        List<String> srcList = new ArrayList<>();
        for (Path srcPath : srcPathList) {
            srcList.add(srcPath.getFileName().toString());
        }
        if (srcList.isEmpty()) {
            log.warn("==================================================");
            log.warn("指定ディレクトリにファイルが見つかりませんでした・・・");
            log.warn("処理を終了します。");
            log.warn("==================================================");
            return RepeatStatus.FINISHED;
        }

        int rowNum;
        for (rowNum = START_DATA_ROW; rowNum - START_DATA_ROW < srcList.size(); rowNum++) {
            int pathPos = rowNum - START_DATA_ROW;
            Row dataRow = tocSheet.createRow(rowNum);
            String NO_FORMULA = "ROW()-" + START_DATA_ROW;
            String srcFileName = srcList.get(pathPos);
            Cell noCell = dataRow.createCell(NO_COL);
            noCell.setCellFormula(NO_FORMULA); // NOを計算用にDATA行の開始行を引算
            Cell srcFileCell = dataRow.createCell(FILE_COL);
            srcFileCell.setCellValue(srcFileName);
        }

        // 列幅自動調整
        for (int col = 0; col < HEADER_ARRAY.length; col++) {
            log.info("列幅自動調整：　カラム名：{}　位置：{}", HEADER_ARRAY[col], col);
            tocSheet.autoSizeColumn(col);
        }

        // ==============================
        // スタイル定義（全領域）
        // ==============================
        log.info("開始行：{}", tocSheet.getFirstRowNum());
        log.info("最終行：{}", tocSheet.getLastRowNum());
        log.info("一番上行：{}", tocSheet.getTopRow());
        tocSheet.getRow(1).getLastCellNum();
        int firstRowNum = tocSheet.getFirstRowNum();
        int lastRowNum = tocSheet.getLastRowNum();
        int firstCellNum = tocSheet.getRow(0).getFirstCellNum();
        int lastCellNum = tocSheet.getRow(0).getLastCellNum();
        log.info("開始行:{}", firstRowNum);
        log.info("最後行:{}", lastRowNum);
        log.info("開始列:{}", firstCellNum);
        log.info("最後列:{}", lastCellNum);
        for (int rn = firstRowNum; rn <= lastRowNum; rn++) {
            if (rn == firstRowNum) {
                // ヘッダー行だけスタイルを変える
                Row row = tocSheet.getRow(rn);
                for (int cn = firstCellNum; cn < lastCellNum; cn++) {
                    Cell cell = row.getCell(cn, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(styleHeader);
                }
            } else {
                Row row = tocSheet.getRow(rn);
                for (int cn = firstCellNum; cn < lastCellNum; cn++) {
                    Cell cell = row.getCell(cn, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(style);
                }
            }
        }

        // ============================================================
        // データシート作成
        // ============================================================
        Sheet dataSheet = workbook.createSheet("データ");
        Row diffHeaderRow = dataSheet.createRow(0);
        diffHeaderRow.createCell(0).setCellValue("MARK");
        diffHeaderRow.createCell(1).setCellValue("ファイル名");
        diffHeaderRow.createCell(2).setCellValue("データー＞");
        final int DATA_START_COL = 2;
        int rownum = 1;
        int colnum = DATA_START_COL;
        for (Path srcPath : srcPathList) {
            log.info("ファイルフルパス：{}", srcPath.toAbsolutePath().toString());
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(new File(srcPath.toAbsolutePath().toString())),
                    inputFileEncode));
            String line = "";
            while ((line = lnr.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                Row r = dataSheet.createRow(rownum++);
                Cell c0 = r.createCell(0);
                c0.setCellValue("");
                c0.setCellStyle(style);
                Cell c1 = r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c1.setCellValue(srcPath.getFileName().toString());
                c1.setCellStyle(style);
                if (lnr.getLineNumber() == 1) {
                    // 1行目はヘッダー行と判定
                    r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("※");;
                    r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    String[] cols = line.split(",");
                    for (String cv : cols) {
                        Cell cl = r.createCell(colnum++);
                        cl.setCellValue(cv);
                        cl.setCellStyle(styleHeader);
                    }
                } else {
                    // ヘッダー行ではない場合
                    String[] cols = line.split(",");
                    for (String cv : cols) {
                        Cell cl = r.createCell(colnum++);
                        cl.setCellValue(cv);
                        cl.setCellStyle(style);
                    }
                }
                colnum = DATA_START_COL; // 列位置初期化
            }

        }

        // 列幅自動調整 (列数が多いとかなり時間がかかるため列幅自動調整をコメントアウト）
        // for (int col = 0; col < dataSheet.getLastRowNum(); col++) {
        // dataSheet.autoSizeColumn(col);
        // // diffSheet.setColumnWidth(col, diffSheet.getColumnWidth(col) + 1000);
        // }

        workbook.write(new FileOutputStream(tocFileDir + filename));
        workbook.close();
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
            // srcDir = "./dist/DBDUMP/20210522_210441";
        }
        if (StringUtils.isEmpty(tocFileDir)) {
            tocFileDir = "./"; // デフォルト値として現在ディレクトリを指定
        }
        if (StringUtils.isEmpty(inputFileEncode)) {
            inputFileEncode = "UTF-8";
        }
    }

    /**
     * 日時文字列取得
     *
     * @return
     */
    private String getYYYYMMDD_HHMMSS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}
