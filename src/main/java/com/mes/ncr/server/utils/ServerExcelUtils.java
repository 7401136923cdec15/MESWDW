package com.mes.ncr.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.mes.ncr.server.service.po.ServiceResult;
import com.mes.ncr.server.service.utils.StringUtils;
 

public class ServerExcelUtils {
	public enum ExcelExtType {
		error, xls, xlsx,

	}

	private static final Logger logger = LoggerFactory.getLogger(ServerExcelUtils.class);
	private static ServerExcelUtils Instance;

	public static ServerExcelUtils getInstance() {

		if (Instance == null)
			Instance = new ServerExcelUtils();
		return Instance;

	}

	private ServerExcelUtils() {
	}

	/// <summary>
	/// 上传Excel导入
	/// </summary>
	/// <param name="file">上载文件对象</param>
	/// <param name="errorMsg">错误信息</param>
	/// <param name="sheetName">表名，默认取第一张</param>
	/// <returns></returns>
	public ServiceResult<List<Map<String, Object>>> Import(MultipartFile file, String sheetName) {
		ServiceResult<List<Map<String, Object>>> wResult = new ServiceResult<List<Map<String, Object>>>();
		wResult.Result = new ArrayList<Map<String, Object>>();
		wResult.FaultCode = "";
		try {

			if (file == null || file.getSize() <= 0) {
				wResult.FaultCode = "请选择要导入的Excel文件";
				return wResult;
			}
			ExcelExtType excelType = GetExcelFileType(file.getOriginalFilename());
			if (excelType == ExcelExtType.error) {
				wResult.FaultCode = "请选择正确的Excel文件";
				return wResult;
			}

			wResult.Result = ImportExcel(file.getInputStream(), excelType, sheetName);
			if (wResult.Result == null)
				wResult.FaultCode = "导入失败,请选择正确的Excel文件";

		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private ExcelExtType GetExcelFileType(String wFileFullPath) {

		// 2007版本
		if (wFileFullPath.indexOf(".xlsx") > 0)
			return ExcelExtType.xlsx;
		// 2003版本
		else if (wFileFullPath.indexOf(".xls") > 0)
			return ExcelExtType.xls;

		return ExcelExtType.error;
	}

	/// <summary>
	/// 根据Excel格式读取Excel
	/// </summary>
	/// <param name="stream">文件流</param>
	/// <param name="type">Excel格式枚举类型，xls/xlsx</param>
	/// <param name="sheetName">表名，默认取第一张</param>
	/// <returns>DataTable</returns>
	@SuppressWarnings("resource")
	private List<Map<String, Object>> ImportExcel(InputStream stream, ExcelExtType type, String sheetName) {
		List<Map<String, Object>> dt = new ArrayList<Map<String, Object>>();

		try {
			Workbook workbook;
			// xls使用HSSFWorkbook类实现，xlsx使用XSSFWorkbook类实现
			switch (type) {
			case xlsx:
				workbook = new XSSFWorkbook(stream);
				break;
			default:
				workbook = new HSSFWorkbook(stream);
				break;
			}
			Sheet sheet = null;
			// 获取工作表 默认取第一张
			if (StringUtils.isEmpty(sheetName))
				sheet = workbook.getSheetAt(0);
			else
				sheet = workbook.getSheet(sheetName);

			if (sheet == null)
				return dt;
			// IEnumerator rows = sheet.GETR();
			// sheet.NumMergedRegions

			int wRowIndex = 1;
			// region 获取表头
			Row headerRow = sheet.getRow(wRowIndex);
			for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
				headerRow = sheet.getRow(i);
				if (isMergedRegion(sheet, wRowIndex-1, i)) {
					wRowIndex++;
					continue;
				}
				break;
			}

			Map<Integer, String> wRowStringList = new HashMap<Integer, String>();

			int cellCount = headerRow.getLastCellNum();
			for (int j = 0; j < cellCount; j++) {

				Cell cell = headerRow.getCell(j);
				if (cell != null) {

					wRowStringList.put(j, cell.toString());
				} else {
					wRowStringList.put(j, "");
				}
			}

			// endRegion
			// region 获取内容
			for (int i = (sheet.getFirstRowNum() + wRowIndex); i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				Map<String, Object> wRowObject = new HashMap<String, Object>();

				for (int j = row.getFirstCellNum(); j < cellCount; j++) {
					if (row.getCell(j) != null) {
						// 判断单元格是否为日期格式
						switch (row.getCell(j).getCellType()) {
						case BLANK:
							wRowObject.put(wRowStringList.get(j), "");
							break;
						case BOOLEAN:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).getBooleanCellValue());
							break;
						case ERROR:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).toString());
							break;
						case FORMULA:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).toString());
							break;
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(row.getCell(j))) {
								Calendar wTime = Calendar.getInstance();
								wTime.setTime(row.getCell(j).getDateCellValue());
								wRowObject.put(wRowStringList.get(j), wTime);
							} else {
								wRowObject.put(wRowStringList.get(j), row.getCell(j).getNumericCellValue());
							}
							break;
						case STRING:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).getStringCellValue());
							break;
						case _NONE:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).toString());
							break;

						default:
							wRowObject.put(wRowStringList.get(j), row.getCell(j).toString());
							break;
						}

					}
				}
				dt.add(wRowObject);
			}
			// endRegion

		} catch (Exception ex) {

			logger.error(ex.toString());
			dt = null;
		}
		return dt;
	}

	public static boolean isMergedRegion(Sheet sheet, int row, int column) {

		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}

		return false;
	}

	public ServiceResult<String> Export(List<Map<String, Object>> wInputList, Map<String, String> wHeadTitle,
			String wTitle, String wFileName, List<String> wOrderList) {

		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			wResult.FaultCode = "";
			wResult.Result = "";
			wTitle = StringUtils.isEmpty(wTitle) ? "Sheet1" : wTitle;
			wOrderList = wOrderList == null ? new ArrayList<>(wHeadTitle.keySet()) : wOrderList;
			if (wHeadTitle == null || wHeadTitle.size() == 0) {
				wResult.FaultCode = "导出Excel文件输入数据为空！！";
				return null;
			}
			ExcelExtType excelType = GetExcelFileType(wFileName);
			if (excelType == ExcelExtType.error) {
				wResult.FaultCode = "请选择正确的Excel文件名";
				return null;
			}
			Workbook wIWorkbook = null;
			switch (excelType) {

			case xls:
				wIWorkbook = new HSSFWorkbook();
				break;
			case xlsx:
				wIWorkbook = new XSSFWorkbook();
				;
				break;
			default:
				break;
			}
			Map<String, Sheet> wISheetList = new HashMap<String, Sheet>();

			Sheet wISheet = wIWorkbook.createSheet(wTitle);
			wISheetList.put(wTitle, wISheet);

			List<String> wOrderColumnList = wOrderList.stream().map(p -> wHeadTitle.get(p))
					.collect(Collectors.toList());

			ExcelSheetUtil.getInstance().SetHeadToSheet(wISheetList.get(wTitle), wTitle, wOrderColumnList, 0);

			ExcelSheetUtil.getInstance().SetListToSheet(wInputList, wISheetList.get(wTitle), wOrderList, 2, 0);

			Calendar wNow = Calendar.getInstance();

			String wFileFullName = "";
			if (Constants.Client_Upload_Excel_Save_Path.indexOf('/') == 0) {
				wFileFullName = Constants.Client_Upload_Excel_Save_Path.substring(1)
						+ StringUtils.parseCalendarToString(wNow, "yyyy/MM/dd/");
			}

			else {
				wFileFullName = Constants.Client_Upload_Excel_Save_Path
						+ StringUtils.parseCalendarToString(wNow, "yyyy/MM/dd/");
			}
			File wFile = new File(Constants.getStaticPath() + wFileFullName);
			if (!wFile.exists())
				wFile.mkdirs();

			wFileFullName = wFileFullName + wFileName;
			FileOutputStream wOut;

			wOut = new FileOutputStream(Constants.getStaticPath() + wFileFullName);

			wIWorkbook.write(wOut);
			wOut.close();

			wResult.Result = "/" + wFileFullName;
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wResult;
	}

}