package com.mes.ncr.server.serviceimpl.utils.wdw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.ncr.server.service.po.excel.MyExcelSheet;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.Constants;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

//定义导出操作
public class ExcelUtil {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	// 1.创建一个excel文件
	static HSSFWorkbook mWorkbook;// 工作簿

	// 2.创建sheet文件
	static Sheet mSheet;

	// 3.设置头信息(第一行的数据)
	static String[] mHeads;// ={"","",""}

	/**
	 * 通用表头样式
	 * 
	 * @return
	 */
	private static CellStyle HeaderStyle() {
		CellStyle wStyle = mWorkbook.createCellStyle();
		try {
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			wStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			wStyle.setWrapText(true);
			wStyle.setBorderLeft(BorderStyle.THIN); // 左边框
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderTop(BorderStyle.THIN); // 上边框
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderRight(BorderStyle.THIN); // 右边框
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			Font wFont = mWorkbook.createFont();
			// 字体颜色
			wFont.setColor(IndexedColors.WHITE.getIndex());
			wFont.setBold(true);
			wFont.setFontName("Couries New");
			wFont.setFontHeightInPoints((short) 15);
			wStyle.setFont(wFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wStyle;
	}

	/**
	 * 通用行单元格样式
	 * 
	 * @return
	 */
	private static CellStyle RowStyle() {
		CellStyle wStyle = mWorkbook.createCellStyle();
		try {
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setWrapText(true);
			wStyle.setBorderLeft(BorderStyle.THIN); // 左边框
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderTop(BorderStyle.THIN); // 上边框
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderRight(BorderStyle.THIN); // 右边框
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wStyle;
	}

	// 6.导出
	public static void Export(OutputStream wOutput) {
		try {
			// 设置导出时使用表格方式导出
//			mSheet.setGridsPrinted(true);
			mWorkbook.write(wOutput);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	// 导出订单列表相关函数
	public static CellStyle mHeaderStyle = null;
	public static CellStyle mRowStyle = null;

	public static void PO_CreateHeaders(String[] wHeaders) {
		try {
			// ①创建工作簿
			mWorkbook = new HSSFWorkbook();
			// ②定义样式
			mHeaderStyle = HeaderStyle();
			mRowStyle = RowStyle();
			// ③创建行
			Row wRow = mSheet.createRow(1);
			wRow.setHeight((short) 350);
			// ④依次添加列
			mHeads = wHeaders;
			Cell wCell;
			for (int i = 0; i < mHeads.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(mHeads[i]);
				wCell.setCellStyle(mHeaderStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void PO_CreateCols(int wRowNum, List<String> wColList) {
		try {
			// ①创建行
			Row wRow = mSheet.createRow(wRowNum);
			// ②创建列
			Cell wCell;
			for (int i = 0; i < wColList.size(); i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wColList.get(i));
				wCell.setCellStyle(mRowStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static CellStyle mTitleStyle = null;
	public static CellStyle mHeadStyle = null;
	public static CellStyle mHeadStyle_Copy = null;
	public static CellStyle mItemStyle = null;

	public static CreationHelper mCreateHelper = null;
	public static CellStyle mLinkStyle = null;

	public static CellStyle mItemStyle_Green = null;
	public static CellStyle mItemStyle_Blue = null;
	public static CellStyle mItemStyle_Red = null;

	private static CellStyle TitleStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setBold(true);
			hSSFFont.setFontName("Couries New");
			hSSFFont.setFontHeightInPoints((short) 15);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle HeadStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			// 背景颜色
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	@SuppressWarnings("unused")
	private static CellStyle HeadStyleCopy() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			// 背景颜色
			hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	@SuppressWarnings("unused")
	private static CellStyle ItemStyleBlue() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			// 背景颜色
			hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	@SuppressWarnings("unused")
	private static CellStyle ItemStyleGreen() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			// 背景颜色
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	@SuppressWarnings("unused")
	private static CellStyle ItemStyleRed() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			// 背景颜色
			hSSFCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	/**
	 * 绘制滚动计划Excel
	 */
	public static void APS_WriteScrollPlan(List<List<String>> wSourceList, FileOutputStream wFileOutputStream,
			String wType) {
		try {
			if (wSourceList == null || wSourceList.size() <= 0) {
				return;
			}

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mItemStyle = ItemStyle();

			// ①建Sheet
			String wTitle = StringUtils.Format("{0}年{1}滚动计划", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					wType);
			mSheet = (Sheet) mWorkbook.createSheet(wTitle);
			// ②建标题
			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wSourceList.get(0).size() - 1);
			mSheet.addMergedRegion(wRegion);
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wTitle);
			wCell.setCellStyle(mTitleStyle);
			// ③建表头
			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wSourceList.get(0).size(); i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wSourceList.get(0).get(i));
				wCell.setCellStyle(mHeadStyle);
			}
			// ④建内容
			int wRowNum = 2;
			for (int i = 1; i < wSourceList.size(); i++) {
				wRow = mSheet.createRow(wRowNum++);
				for (int j = 0; j < wSourceList.get(i).size(); j++) {
					wCell = wRow.createCell(j);
					wCell.setCellValue(wSourceList.get(i).get(j));
					wCell.setCellStyle(mItemStyle);
				}
			}
			// 导出
			Export(wFileOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导出Excel数据，通用函数
	 */
	public static String ExportData(List<MyExcelSheet> wMyExcelSheetList, String wShortFileName) {
		String wResult = "";
		try {
			if (wMyExcelSheetList == null || wMyExcelSheetList.size() <= 0) {
				return wResult;
			}

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("{1}{0}.xls", wCurTime, wShortFileName);
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mItemStyle = ItemStyle();

			for (MyExcelSheet wMyExcelSheet : wMyExcelSheetList) {
				CreateSheet(wMyExcelSheet);
			}

			Export(wFileOutputStream);

			wResult = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 创建一张表格数据
	 */
	private static void CreateSheet(MyExcelSheet wMyExcelSheet) {
		try {
			mSheet = (Sheet) mWorkbook.createSheet(wMyExcelSheet.SheetName);

			int wRowIndex = 0;
			Row wRow = null;
			Cell wCell = null;

			// 设置列宽
			SetColumnWidth(wMyExcelSheet.HeaderList.size(), 7114);

			if (StringUtils.isEmpty(wMyExcelSheet.TitleName)) {

			} else {
				CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wMyExcelSheet.HeaderList.size() - 1);
				mSheet.addMergedRegion(wRegion);

				wRow = mSheet.createRow(wRowIndex);
				wRow.setHeight((short) 500);
				wCell = wRow.createCell(0);
				wCell.setCellValue(wMyExcelSheet.TitleName);
				wCell.setCellStyle(mTitleStyle);

				wRowIndex++;
			}

			wRow = mSheet.createRow(wRowIndex);
			wRow.setHeight((short) 550);
			for (int j = 0; j < wMyExcelSheet.HeaderList.size(); j++) {
				wCell = wRow.createCell(j);
				wCell.setCellValue(wMyExcelSheet.HeaderList.get(j));
				wCell.setCellStyle(mHeadStyle);
			}
			wRowIndex++;

			for (List<String> wValueList : wMyExcelSheet.DataList) {
				WriteRowItem(wValueList, wRowIndex++);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 设置Excel列的宽度适应A4纸张
	 */
	private static void SetColumnWidth(int wLength, int wWidth) {
		try {
			for (int i = 0; i < wLength; i++) {
				mSheet.setColumnWidth(i, wWidth);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 输出Excel行数据
	 */
	public static void WriteRowItem(List<String> wValueList, int wRowNum) {
		try {
			Row wRow = mSheet.createRow(wRowNum);

			for (int i = 0; i < wValueList.size(); i++) {
				if (wValueList.get(i).contains("http")
						&& (wValueList.get(i).contains(".jpg") || wValueList.get(i).contains(".png"))) {
					Cell wCell = wRow.createCell(i);
					wCell.setCellValue("图片");
					HSSFHyperlink wLink = (HSSFHyperlink) mCreateHelper.createHyperlink(HyperlinkType.URL);
					wLink.setAddress(wValueList.get(i));
					wCell.setHyperlink(wLink);
					wCell.setCellStyle(mLinkStyle);
				} else {
					Cell wCell = wRow.createCell(i);
					wCell.setCellValue(wValueList.get(i));
					wCell.setCellStyle(mItemStyle);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}