package com.mes.ncr.server.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.ncr.server.service.utils.StringUtils;
 

public class ExcelSheetUtil {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ExcelSheetUtil.class);

	private ExcelSheetUtil() {
		// TODO Auto-generated constructor stub
	}

	private static ExcelSheetUtil Instance;

	public static ExcelSheetUtil getInstance() {

		if (Instance == null)
			Instance = new ExcelSheetUtil();
		return Instance;

	}

	public enum ExcelStyle {
		Head, Column, Data,
	}

	public void SetHeadToSheet(Sheet wISheet, String wTableTitle, List<String> wOrderColumnList,
			int wColumnStartIndex) {

		Row wHead = wISheet.createRow(0);
		wHead.setHeight((short) (20 * 30));

		Row wIRowCloumn = wISheet.createRow(1);
		wIRowCloumn.setHeight((short) (20 * 25));

		for (int n = 0; n < wOrderColumnList.size(); n++) {
			wISheet.setColumnWidth(n + wColumnStartIndex, 30 * 256);
			wIRowCloumn.createCell(n + wColumnStartIndex).setCellValue(wOrderColumnList.get(n));
			wIRowCloumn.getCell(n + wColumnStartIndex)
					.setCellStyle(Getcellstyle(wISheet.getWorkbook(), ExcelStyle.Column));
		}

		int wHeadCloumnSpan = wOrderColumnList.size();

		if (wHeadCloumnSpan >= 1)
			wISheet.addMergedRegion(new CellRangeAddress(0, 0, 0, wHeadCloumnSpan - 1));/// 设置表头跨行

		for (int n = 0; n < wHeadCloumnSpan; n++) {
			wHead.createCell(n).setCellStyle(Getcellstyle(wISheet.getWorkbook(), ExcelStyle.Head));
		}
		wHead.getCell(0).setCellValue(wTableTitle);// 写表头

	}

	public void SetListToSheet(List<Map<String, Object>> wTList, Sheet wISheet, List<String> wOrderPropList,
			int wRowStartIndex, int wColumnStartIndex) {

		for (int m = 0; m < wTList.size(); m++)// 写数据
		{
			Row wDataIRow = null;
			wDataIRow = wISheet.getRow(m + wRowStartIndex);

			if (wDataIRow != null) {
				if (wColumnStartIndex == 0)
					wISheet.removeRow(wDataIRow);
			}

			wDataIRow = wISheet.createRow(m + wRowStartIndex);
			wDataIRow.setHeight((short) (20 * 20));

			for (int n = 0; n < wOrderPropList.size(); n++) {

				if (!wTList.get(m).containsKey(wOrderPropList.get(n)))
					continue;

				wDataIRow.createCell(n + wColumnStartIndex)
						.setCellValue(StringUtils.parseString(wTList.get(m).get(wOrderPropList.get(n))));

				wDataIRow.getCell(n + wColumnStartIndex)
						.setCellStyle(Getcellstyle(wISheet.getWorkbook(), ExcelStyle.Data));
			}

		}
	}

	private Map<ExcelStyle, CellStyle> wICellStyleDictionary = new HashMap<ExcelStyle, CellStyle>();

	private Workbook mWorkbook = null;

	public CellStyle Getcellstyle(Workbook wWorkbook, ExcelStyle wExcelStyle) {
		if (wICellStyleDictionary == null)
			wICellStyleDictionary = new HashMap<ExcelStyle, CellStyle>();
		
		if (wWorkbook == null)
			return null;

		if (mWorkbook == null || mWorkbook != wWorkbook) {
			mWorkbook = wWorkbook;
			wICellStyleDictionary.clear();
			
		}
		if (wICellStyleDictionary.containsKey(wExcelStyle))
			return wICellStyleDictionary.get(wExcelStyle);
		CellStyle wICellStyle = wWorkbook.createCellStyle();
		wICellStyle.setAlignment(HorizontalAlignment.CENTER);
		wICellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		wICellStyle.setWrapText(true);
		Font wIFont = wWorkbook.createFont();
		// region 样式区别
		switch (wExcelStyle) {
		case Head:
			wIFont.setFontName("宋体"); // 字体
			wIFont.setColor(Font.COLOR_NORMAL);
			wIFont.setItalic(true);
			wIFont.setBold(true);
			wIFont.setFontHeight((short) 16);
			wIFont.setFontHeightInPoints((short) 16);

			wICellStyle.setLeftBorderColor((short) 63);
			wICellStyle.setRightBorderColor((short) 63);// 边框颜色
			wICellStyle.setBottomBorderColor((short) 63);
			wICellStyle.setTopBorderColor((short) 63);// 边框颜色

			wICellStyle.setBorderBottom(BorderStyle.THICK);
			wICellStyle.setBorderLeft(BorderStyle.THICK);
			wICellStyle.setBorderRight(BorderStyle.THICK);
			wICellStyle.setBorderTop(BorderStyle.THICK); // 边框

			// wICellStyle.FillBackgroundColor = HSSFColor.Black.Index;//背景色
			wICellStyle.setFillForegroundColor((short) 8);// 前景色
			break;
		case Column:
			wIFont.setFontName("宋体"); // 字体
			wIFont.setColor(Font.COLOR_NORMAL);
			// wIFont.IsItalic = true;//下划线
			wIFont.setBold(true);
			wIFont.setFontHeight((short) 14);
			wIFont.setFontHeightInPoints((short) 14);

			wICellStyle.setBorderBottom(BorderStyle.THIN);
			wICellStyle.setBorderLeft(BorderStyle.THIN);
			wICellStyle.setBorderRight(BorderStyle.THIN);

			wICellStyle.setLeftBorderColor((short) 63);
			wICellStyle.setRightBorderColor((short) 63);// 边框颜色
			wICellStyle.setBottomBorderColor((short) 63);
			// wICellStyle.FillBackgroundColor = HSSFColor.Black.Index;//背景色
			wICellStyle.setFillForegroundColor((short) 8);// 前景色
			break;
		case Data:
			wIFont.setFontName("宋体"); // 字体
			wIFont.setColor(Font.COLOR_NORMAL);
			// wIFont.IsItalic = true;//下划线
			wIFont.setBold(false);
			wIFont.setFontHeight((short) 12);
			wIFont.setFontHeightInPoints((short) 12);

			wICellStyle.setBorderBottom(BorderStyle.THIN);
			wICellStyle.setBorderLeft(BorderStyle.THIN);
			wICellStyle.setBorderRight(BorderStyle.THIN);

			wICellStyle.setLeftBorderColor((short) 63);
			wICellStyle.setRightBorderColor((short) 63);// 边框颜色
			wICellStyle.setBottomBorderColor((short) 63);

			// wICellStyle.FillBackgroundColor = HSSFColor.Black.Index;//背景色
			wICellStyle.setFillForegroundColor((short) 8);// 前景色
			break;
		default:
			break;
		}
		// endRegion
		wICellStyle.setFont(wIFont);
		wICellStyleDictionary.put(wExcelStyle, wICellStyle);

		return wICellStyle;
	}

}
