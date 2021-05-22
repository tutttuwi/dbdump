package dbdump.job0020;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
 * ファイル差分比較タスクレット
 *
 * @author Tomo
 *
 */
@Slf4j
@Component
@StepScope
public class DiffDataTasklet implements Tasklet {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("#{jobParameters[srcDir]}")
    String srcDir;
    @Value("#{jobParameters[dstDir]}")
    String dstDir;

    @Value("#{jobParameters[tableKeyFile]}")
    String tableKeyFile;
    @Value("#{jobParameters[diffFileDir]}")
    String diffFileDir;

    private Map<String, List<String>> tablePkMap = new HashMap<>();

    // 各種変数設定
    private static final String TOC_NAME = "目次";
    // ヘッダー配列と列位置は揃えること
    private static final String[] HEADER_ARRAY = {"NO", "比較元ファイル名", "比較先ファイル名", "比較結果", "備考"};
    private static final int NO_COL = 0;
    private static final int SRC_FILE_COL = 1;
    private static final int DST_FILE_COL = 2;
    private static final int RET_COL = 3;
    private static final int MISC_COL = 4;
    private static final int START_HEADER_ROW = 0;
    private static final int START_DATA_ROW = 1;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        // 初期設定
        setup();
        // 差分比較ファイル名
        String filename = "diff_" + getYYYYMMDD_HHMMSS() + ".xlsx";

        // Workbook workbook = WorkbookFactory.create(new File("./diff.xlsx"));
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
        // 比較元処理
        Path srcDirPath = Paths.get(srcDir);
        List<Path> srcPathList = Files.list(srcDirPath).sorted(Comparator.comparing(Path::toString))
                .collect(Collectors.toList());
        List<String> srcList = new ArrayList<>();
        for (Path srcPath : srcPathList) {
            srcList.add(srcPath.getFileName().toString());
        }
        // 比較先処理
        Path dstDirPath = Paths.get(dstDir);
        List<Path> dstPathList = Files.list(dstDirPath).sorted(Comparator.comparing(Path::toString))
                .collect(Collectors.toList());
        List<String> dstList = new ArrayList<>();
        for (Path dstPath : dstPathList) {
            dstList.add(dstPath.getFileName().toString());
        }
        if (srcList.isEmpty() || dstList.isEmpty()) {
            log.warn("==================================================");
            log.warn("比較元/比較先ディレクトリにファイルが見つかりませんでした・・・");
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
            Cell srcFileCell = dataRow.createCell(SRC_FILE_COL);
            srcFileCell.setCellValue(srcFileName);
            if (dstList.contains(srcFileName)) {
                Cell dstFileCell = dataRow.createCell(DST_FILE_COL);
                dstFileCell.setCellValue(srcFileName);
                dstList.remove(srcFileName); // 比較先リストから削除しておく
            } else {
                Cell retCell = dataRow.createCell(RET_COL);
                retCell.setCellValue("比較元のみ存在！");
            }
        }
        for (String dstFileName : dstList) {
            Row dataRow = tocSheet.createRow(rowNum);
            String NO_FORMULA = "ROW()-" + START_DATA_ROW;
            Cell noCell = dataRow.createCell(NO_COL);
            noCell.setCellFormula(NO_FORMULA); // NOを計算用にDATA行の開始行を引算
            dataRow.createCell(DST_FILE_COL).setCellValue(dstFileName);
            dataRow.createCell(RET_COL).setCellValue("比較先のみ存在！");
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
            Row row = tocSheet.getRow(rn);
            for (int cn = firstCellNum; cn < lastCellNum; cn++) {
                Cell cell = row.getCell(cn, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellStyle(style);
            }
        }

        // ============================================================
        // 差分比較作成
        // ============================================================
        Sheet diffSheet = workbook.createSheet("差分比較");
        Row diffHeaderRow = diffSheet.createRow(0);
        diffHeaderRow.createCell(0).setCellValue("比較");
        diffHeaderRow.createCell(1).setCellValue("ファイル名");
        diffHeaderRow.createCell(2).setCellValue("比較結果");
        final int DIFF_START_COL = 3;
        int rownum = 1;
        int colnum = DIFF_START_COL;
        for (Path srcPath : srcPathList) {
            log.info("比較元ファイルフルパス：{}", srcPath.toAbsolutePath().toString());
            LineNumberReader lnr =
                    new LineNumberReader(new FileReader(srcPath.toAbsolutePath().toString()));
            String line = "";
            int srcStartRowNum = rownum;
            while ((line = lnr.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                Row r = diffSheet.createRow(rownum++);
                Cell c0 = r.createCell(0);
                c0.setCellValue("比較元"); // TODO: 0を変数化する
                c0.setCellStyle(style);
                Cell c1 = r.createCell(1);
                c1.setCellValue(srcPath.getFileName().toString()); // TODO: 0を変数化する
                c1.setCellStyle(style);
                Cell c2 = r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c2.setCellStyle(style);
                if (lnr.getLineNumber() == 1) {
                    // 1行目はヘッダー行と判定
                    r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ヘッダー");
                    r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                }
                String[] cols = line.split(",");
                for (String cv : cols) {
                    Cell cl = r.createCell(colnum++);
                    cl.setCellValue(cv);
                    cl.setCellStyle(style);
                }
                colnum = DIFF_START_COL; // 列位置初期化
            }
            int srcEndRowNum = rownum;
            // rownum++; // 1行すすめる

            Optional<Path> dstPathFinded = dstPathList.stream().filter(p -> {
                return Objects.equals(p.getFileName().toString(), srcPath.getFileName().toString());
            }).findFirst();
            Path dstPath;
            if (dstPathFinded.isEmpty()) {
                // 比較先ファイルが存在しない旨を出力して次へ
                log.info("比較先ファイルが存在しません。 比較元ファイル名：{}", srcPath.getFileName().toString());
                Row r = diffSheet.createRow(rownum);
                Cell c0 = r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c0.setCellValue("比較先");
                c0.setCellStyle(style);
                Cell c1 = r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c1.setCellValue(srcPath.getFileName().toString());
                c1.setCellStyle(style);
                Cell c2 = r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c2.setCellValue("比較先存在なし");
                c2.setCellStyle(styleSrcOnly);
                rownum++; // 次の行にすすめる
                rownum++; // 次の行にすすめる(２行間隔を開ける)
                continue;
            } else {
                dstPath = dstPathFinded.get();
            }
            // 比較先ファイル書込処理
            log.info("比較先ファイルフルパス：{}", dstPath.toAbsolutePath().toString());
            LineNumberReader dlnr =
                    new LineNumberReader(new FileReader(dstPath.toAbsolutePath().toString()));
            String dline = "";
            int dstStartRowNum = rownum;
            while ((dline = dlnr.readLine()) != null) {
                if (dline.length() == 0) {
                    continue;
                }
                Row r = diffSheet.createRow(rownum++);
                Cell c0 = r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK);;
                c0.setCellValue("比較先"); // TODO: 0を変数化する
                c0.setCellStyle(style);
                Cell c1 = r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c1.setCellValue(dstPath.getFileName().toString()); // TODO: 0を変数化する
                c1.setCellStyle(style);
                Cell c2 = r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                c2.setCellStyle(style);
                if (dlnr.getLineNumber() == 1) {
                    // 1行目はヘッダー行と判定
                    r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ヘッダー");
                    r.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    r.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                    r.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(styleHeader);
                }
                String[] cols = dline.split(",");
                for (String cv : cols) {
                    Cell cl = r.createCell(colnum++);
                    cl.setCellValue(cv);
                    cl.setCellStyle(style);
                }
                colnum = DIFF_START_COL; // 列位置初期化
            }
            int dstEndRowNum = rownum;
            rownum++; // 1行すすめる

            // 差分比較処理
            List<Integer> keyColList = new ArrayList<>();
            for (int rn = srcStartRowNum; rn < srcEndRowNum; rn++) {
                Row srcRow = diffSheet.getRow(rn);
                int fstCol = DIFF_START_COL;
                int endCol = srcRow.getLastCellNum();
                if (rn == srcStartRowNum) {
                    // ヘッダー行なのでキーカラムを探す
                    for (int i = fstCol; i < endCol; i++) {
                        List<String> pkNameList =
                                tablePkMap.get(srcPath.getFileName().toString().split("\\.")[0]); // 拡張子を排除
                        Cell c = srcRow.getCell(i);
                        if (pkNameList.contains(c.getStringCellValue())) {
                            keyColList.add(i); // キーカラムの列位置を追加
                            c.setCellStyle(stylePk);
                        }
                    }
                    rn++; // ヘッダー行は比較対象に入れたくないため次の行へ
                }
                if (keyColList.isEmpty()) {
                    log.warn("キーカラムが見つからないためスキップします。ファイル名：{}", srcPath.getFileName().toString());
                    continue; // ヘッダー行を見たがキーカラムが見つからないため比較ができないので対象外
                }
                String keyValue = "";
                for (Integer kc : keyColList) {
                    keyValue += srcRow.getCell(kc).getStringCellValue();
                }
                boolean hitDiffFlg = false;
                boolean hasDiffFlg = false;
                for (int drn = dstStartRowNum; drn < dstEndRowNum; drn++) {
                    Row dstRow = diffSheet.getRow(drn);
                    if (drn == dstStartRowNum) {
                        // ヘッダー行のキーカラムへ色付け
                        for (Integer kc : keyColList) {
                            Cell c = dstRow.getCell(kc);
                            c.setCellStyle(stylePk);
                        }
                        drn++; // ヘッダー行は比較対象に入れたくないため次の行へ
                    }
                    String dKeyValue = "";
                    for (Integer kc : keyColList) {
                        dKeyValue += dstRow.getCell(kc).getStringCellValue();
                    }
                    if (Objects.equals(keyValue, dKeyValue)) {
                        hitDiffFlg = true;
                        // 比較処理
                        for (int i = fstCol; i < endCol; i++) {
                            Cell srcCell = srcRow.getCell(i);
                            Cell dstCell = dstRow.getCell(i);
                            if (Objects.equals(srcCell.getStringCellValue(),
                                    dstCell.getStringCellValue())) {
                                // 同じ値なので何もしない
                            } else {
                                hasDiffFlg = true; // 1カラムでも差分があればtrue
                                // 差分があるので色付け
                                srcCell.setCellStyle(styleDiff);
                                dstCell.setCellStyle(styleDiff);
                                // 差分ありと記載
                                srcRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                        .setCellValue("差分あり");
                                dstRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                        .setCellValue("差分あり");
                            }
                        }
                        break; // 比較できた行が存在したらループを抜ける
                    }
                }
                // 目次シート更新
                if (hasDiffFlg) {
                    // 1カラムでも差分があれば差分あり更新
                    for (int tocRn = firstRowNum; tocRn <= lastRowNum; tocRn++) {
                        Row row = tocSheet.getRow(tocRn);
                        Cell c1 = row.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK); // 比較元ファイル名列
                        if (Objects.equals(c1.getStringCellValue(),
                                srcRow.getCell(1).getStringCellValue())) {
                            Cell c3 = row.getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            c3.setCellValue("差分あり");
                        }
                    }
                    tocSheet.getRow(rownum);
                }
                if (!hitDiffFlg) {
                    // 比較できた行が存在しない場合
                    Cell c = srcRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    c.setCellValue("比較先に存在しない");
                    c.setCellStyle(styleSrcOnly);

                }
            }

            // 比較先ファイルをループして比較元に存在しない行があるか確認
            for (int rn = dstStartRowNum; rn < dstEndRowNum; rn++) {
                Row dstRow = diffSheet.getRow(rn);
                int fstCol = DIFF_START_COL;
                int endCol = dstRow.getLastCellNum();
                if (rn == dstStartRowNum) {
                    rn++; // ヘッダー行は含めたくないので飛ばす
                }
                String dKeyValue = "";
                for (Integer kc : keyColList) {
                    dKeyValue += dstRow.getCell(kc).getStringCellValue();
                }
                if (dKeyValue == "") {
                    continue;
                }
                boolean isEqRow = false;
                for (int srn = srcStartRowNum; srn < srcEndRowNum; srn++) {
                    Row srcRow = diffSheet.getRow(srn);
                    String sKeyValue = "";
                    for (Integer kc : keyColList) {
                        sKeyValue += srcRow.getCell(kc).getStringCellValue();
                    }
                    if (Objects.equals(dKeyValue, sKeyValue)) {
                        isEqRow = true;
                        break;
                    }
                }
                if (!isEqRow) {
                    // 同じキーの行が比較元に存在しない場合
                    Cell c = dstRow.createCell(2);
                    c.setCellValue("比較元に存在しない");
                    c.setCellStyle(styleDstOnly);
                }
            }
        }

        // 列幅自動調整
        for (int col = 0; col < diffSheet.getLastRowNum(); col++) {
            diffSheet.autoSizeColumn(col);
            // diffSheet.setColumnWidth(col, diffSheet.getColumnWidth(col) + 1000);
        }

        workbook.write(new FileOutputStream(diffFileDir + filename));
        workbook.close();
        return RepeatStatus.FINISHED;

    }


    /**
     * 初期設定
     *
     * @throws Exception
     */
    private void setup() throws Exception {
        // 差分比較ファイル出力先ディレクトリ
        if (StringUtils.isEmpty(diffFileDir)) {
            diffFileDir = "./"; // デフォルト値
        }
        if (StringUtils.isEmpty(srcDir)) {
            log.error("比較元ディレクトリが指定されていません。");
            throw new Exception();
            // srcDir = "./dist/DBDUMP/20210522_210441";
        }
        if (StringUtils.isEmpty(dstDir)) {
            log.error("比較先ディレクトリが指定されていません。");
            throw new Exception();
            // dstDir = "./dist/DBDUMP/20210522_210457";
        }
        if (StringUtils.isEmpty(tableKeyFile)) {
            log.error("テーブルPK設定ファイルが指定されていません。");
            throw new Exception();
            // tableKeyFile = "./dist/DBDUMP/resources/prop/tablekey.conf";
        }
        // テーブルPKマップオブジェクト設定
        tablePkMap = getTableKeyList();
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

    /**
     * PKマップ取得
     *
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getTableKeyList() throws Exception {
        Map<String, List<String>> ret = new HashMap<>();
        try (LineNumberReader lnr = new LineNumberReader(new FileReader(tableKeyFile))) {
            String line = "";
            while ((line = lnr.readLine()) != null) {
                String tableName = line.split(":")[0];
                String pkArray = line.split(":")[1];
                List<String> list = Arrays.asList(pkArray.split(","));
                ret.put(tableName, list);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return ret;
    }
}
