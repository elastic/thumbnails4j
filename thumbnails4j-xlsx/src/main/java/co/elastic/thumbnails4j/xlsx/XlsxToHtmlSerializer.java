package co.elastic.thumbnails4j.xlsx;

import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class XlsxToHtmlSerializer {

    public static int MAX_COLS = 5;
    public static int MAX_ROWS = 20;
    public static int HEADER_COLUMN_WIDTH_INDEX = -1;
    public static int TABLE_WIDTH_INDEX = -2;
    public static String DEFAULT_CLASS = "excelDefaults";
    public static Map<BorderStyle, String> BORDER_STYLE_MAP = new HashMap<>();
    public static Map<HorizontalAlignment, String> HORIZONTAL_ALIGNMENT = new HashMap<>();
    public static Map<VerticalAlignment, String> VERTICAL_ALIGNMENT = new HashMap<>();
    static {
        BORDER_STYLE_MAP.put(BorderStyle.DASH_DOT, "solid 1pt");
        BORDER_STYLE_MAP.put(BorderStyle.DASH_DOT_DOT, "solid 1pt");
        BORDER_STYLE_MAP.put(BorderStyle.DASHED, "solid 1pt");
        BORDER_STYLE_MAP.put(BorderStyle.DOTTED, "dotted 1pt");
        BORDER_STYLE_MAP.put(BorderStyle.DOUBLE, "double 3pt");
        BORDER_STYLE_MAP.put(BorderStyle.HAIR, "solid 1px");
        BORDER_STYLE_MAP.put(BorderStyle.MEDIUM, "solid 2pt");
        BORDER_STYLE_MAP.put(BorderStyle.MEDIUM_DASH_DOT, "solid 2pt");
        BORDER_STYLE_MAP.put(BorderStyle.MEDIUM_DASH_DOT_DOT, "solid 2pt");
        BORDER_STYLE_MAP.put(BorderStyle.MEDIUM_DASHED, "solid 2pt");
        BORDER_STYLE_MAP.put(BorderStyle.NONE, "none");
        BORDER_STYLE_MAP.put(BorderStyle.SLANTED_DASH_DOT, "solid 2pt");
        BORDER_STYLE_MAP.put(BorderStyle.THICK, "solid 3pt");
        BORDER_STYLE_MAP.put(BorderStyle.THIN, "solid 1pt");

        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.LEFT, "left");
        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.CENTER, "center");
        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.RIGHT, "right");
        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.FILL, "left");
        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.JUSTIFY, "left");
        HORIZONTAL_ALIGNMENT.put(HorizontalAlignment.CENTER_SELECTION, "center");

        VERTICAL_ALIGNMENT.put(VerticalAlignment.BOTTOM, "bottom");
        VERTICAL_ALIGNMENT.put(VerticalAlignment.CENTER, "middle");
        VERTICAL_ALIGNMENT.put(VerticalAlignment.TOP, "top");
    }


    private final Workbook workbook;
    private boolean gotBounds = false;
    private int firstColumn = 0;
    private int endColumn = 0;

    public XlsxToHtmlSerializer(Workbook workbook){
        this.workbook = workbook;
    }

    public byte[] getHtml() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Formatter html = new Formatter(outputStream);
        try{
            html.format("<html>%n");
            html.format("<head>%n");
            printInlineStyle(html);
            html.format("</head>%n");
            html.format("<body>%n");

            printSheet(html, workbook.getSheetAt(0));

            html.format("</body>%n");
            html.format("</html>%n");

            html.flush();
            return outputStream.toByteArray();
        } finally {
            IOUtils.closeQuietly(html);
        }
    }

    private void printInlineStyle(Formatter html){
        html.format("<style type=\"text/css\">%n");
        printStyles(html);
        html.format("</style>%n");
    }

    private void printStyles(Formatter html){
        html.format(".excelDefaults {\n" +
                "        background-color: white;\n" +
                "        color: black;\n" +
                "        text-decoration: none;\n" +
                "        direction: ltr;\n" +
                "        text-transform: none;\n" +
                "        text-indent: 0;\n" +
                "        letter-spacing: 0;\n" +
                "        word-spacing: 0;\n" +
                "        white-space: pre-wrap;\n" +
                "        unicode-bidi: normal;\n" +
                "        background-image: none;\n" +
                "        text-shadow: none;\n" +
                "        list-style-image: none;\n" +
                "        list-style-type: none;\n" +
                "        padding: 0;\n" +
                "        margin: 0;\n" +
                "        border-collapse: collapse;\n" +
                "        vertical-align: bottom;\n" +
                "        font-style: normal;\n" +
                "        font-family: sans-serif;\n" +
                "        font-variant: normal;\n" +
                "        font-weight: normal;\n" +
                "        font-size: 10pt;\n" +
                "        text-align: right;\n" +
                "        table-layout: fixed;\n" +
                "        word-wrap: break-word;\n" +
                "        overflow-wrap: break-word;\n" +
                "      }\n" +
                "\n" +
                "      .excelDefaults td {\n" +
                "        padding: 1px 5px;\n" +
                "      }");
        Set<CellStyle> seen = new HashSet<>();
        for(int i = 0; i<workbook.getNumberOfSheets(); i++){
            Sheet sheet = workbook.getSheetAt(i);
            for(Row row : sheet){
                for(Cell cell : row){
                    CellStyle style = cell.getCellStyle();
                    if (!seen.contains(style)){
                        printStyle(html, style);
                        seen.add(style);
                    }
                }
            }
        }
    }

    private void printStyle(Formatter html, CellStyle style){
        html.format(".%s .%s {%n", DEFAULT_CLASS, styleName(style));
        styleContents(html, style);
        html.format("}%n");
    }

    private void styleContents(Formatter html, CellStyle style){
        styleOut(html, "text-align", HORIZONTAL_ALIGNMENT.get(style.getAlignment()));
        styleOut(html, "vertical-align", VERTICAL_ALIGNMENT.get(style.getVerticalAlignment()));
        fontStyle(html, style);
        borderStyles(html, style);
        colorStyles(html, style);
    }

    private void borderStyles(Formatter html, CellStyle style){
        styleOut(html, "border-left", BORDER_STYLE_MAP.get(style.getBorderLeft()));
        styleOut(html, "border-right", BORDER_STYLE_MAP.get(style.getBorderRight()));
        styleOut(html, "border-top", BORDER_STYLE_MAP.get(style.getBorderTop()));
        styleOut(html, "border-bottom", BORDER_STYLE_MAP.get(style.getBorderBottom()));
    }

    private void colorStyles(Formatter html, CellStyle style){
        styleColor(html, ((XSSFCellStyle) style).getFont().getXSSFColor());
    }

    private void styleColor(Formatter html, XSSFColor color){
        if (color != null && !color.isAuto()){
            byte[] rgb = color.getRGB();
            if (rgb != null){
                html.format(" %s: #%02x%02x%02x;%n", "color", rgb[0], rgb[1], rgb[2]);
            }
        }
    }

    private void fontStyle(Formatter html, CellStyle style){
        Font font = workbook.getFontAt(style.getFontIndexAsInt());
        if (font.getBold()){
            html.format("  font-weight: bold;%n");
        }
        if (font.getItalic()){
            html.format("   font-style: italic;%n");
        }
        html.format("  font-size: %dpt;%n", font.getFontHeightInPoints());
    }

    private String styleName(CellStyle style){
        style = style == null ? workbook.getCellStyleAt(0) : style;
        return String.format("style_%02x", style.getIndex());
    }

    private void styleOut(Formatter html, String attr, String value){
        if (value!=null) {
            html.format("  %s: %s;%n", attr, value);
        }
    }

    private void printSheet(Formatter html, Sheet sheet){
        Map<Integer, Integer> widths = computeWidths(sheet);
        int tableWidth = widths.get(TABLE_WIDTH_INDEX);
        html.format("<table class=\"%s\" style=\"width:%dpx; border: solid 1px black;\" cellspacing=\"0\">%n", DEFAULT_CLASS, tableWidth);
        printSheetContent(html, sheet);
        html.format("</table>%n");
    }

    private Map<Integer, Integer> computeWidths(Sheet sheet){
        Map<Integer, Integer> results = new HashMap<>();
        int tableWidth = 0;

        ensureColumnBounds(sheet);
        int headerCharCount = (""+sheet.getLastRowNum()).length();
        int headerColWidth = widthToPixels((headerCharCount+1) * 256.0);
        results.put(HEADER_COLUMN_WIDTH_INDEX, headerColWidth);
        tableWidth += headerColWidth;

        for(int i = firstColumn; i<(endColumn-1); i++){
            int col_width = widthToPixels(sheet.getColumnWidth(i));
            results.put(i, col_width);
            tableWidth += col_width;
        }

        results.put(TABLE_WIDTH_INDEX, tableWidth);
        return results;
    }

    private int widthToPixels(double widthUnits){
        return Math.toIntExact(Math.round((widthUnits * 9 / 256)));
    }

    private CellType ultimateCellType(Cell cell){
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA){
            type = cell.getCachedFormulaResultType();
        }
        return type;
    }

    private void ensureColumnBounds(Sheet sheet){
        if(!gotBounds){
            firstColumn = (sheet.getRow(0) == null ? 0 : Integer.MAX_VALUE);
            endColumn = 0;
            for(Row row: sheet){
                short firstCell = row.getFirstCellNum();
                if (firstCell >= 0){
                    firstColumn = Math.min(firstColumn, firstCell);
                    endColumn = Math.min(Math.max(endColumn, row.getLastCellNum()), MAX_COLS);
                }
            }

            gotBounds = true;
        }
    }

    private void printSheetContent(Formatter html, Sheet sheet){
        html.format("<tbody>%n");
        int lastRow = Math.min((sheet.getFirstRowNum() + MAX_ROWS - 1), sheet.getLastRowNum());
        for(int i = sheet.getFirstRowNum(); i < lastRow; i++){
            Row row = sheet.getRow(i);
            html.format("  <tr>%n");
            if (row != null){
                for(int j = firstColumn; j < endColumn-1; j++){
                    String content = "&nbsp;";
                    String attrs = "";
                    CellStyle style = null;
                    if (j >= row.getFirstCellNum() && j < row.getLastCellNum()){
                        Cell cell = row.getCell(j);
                        if (cell != null){
                            style = cell.getCellStyle();
                            attrs = tagStyle(cell, style);

                            CellFormat cf = CellFormat.getInstance(style.getDataFormatString());
                            CellFormatResult result = cf.apply(cell);
                            content = result.text;
                            if (content.isEmpty()){
                                content = "&nbsp;";
                            }
                        }
                    }
                    html.format("    <td class=\"%s\" %s>%s</td>%n", styleName(style), attrs, content);
                }
            }
            html.format("  </tr>%n");
        }
        html.format("</tbody>%n");
    }

    private String tagStyle(Cell cell, CellStyle style){
        if (style.getAlignment() == HorizontalAlignment.GENERAL){
            CellType ultimateCellType = ultimateCellType(cell);
            if (ultimateCellType == CellType.STRING){
                return "style=\"text-align: left;\"";
            } else if (ultimateCellType == CellType.BOOLEAN || ultimateCellType == CellType.ERROR){
                return "style=\"text-align: center;\"";
            }
        }
        return "";
    }
}
