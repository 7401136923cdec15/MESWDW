package com.mes.ncr.server.service.po.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel的整个Sheet数据结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-6-10 10:17:32
 */
public class MyExcelSheet implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 数据列表
	 */
	public List<List<String>> DataList = new ArrayList<List<String>>();
	/**
	 * 标题列表
	 */
	public List<String> HeaderList = new ArrayList<String>();
	/**
	 * 表格名称
	 */
	public String SheetName = "";
	/**
	 * 表格标题
	 */
	public String TitleName = "";

	public List<List<String>> getDataList() {
		return DataList;
	}

	public List<String> getHeaderList() {
		return HeaderList;
	}

	public String getSheetName() {
		return SheetName;
	}

	public String getTitleName() {
		return TitleName;
	}

	public void setDataList(List<List<String>> dataList) {
		DataList = dataList;
	}

	public void setHeaderList(List<String> headerList) {
		HeaderList = headerList;
	}

	public void setSheetName(String sheetName) {
		SheetName = sheetName;
	}

	public void setTitleName(String titleName) {
		TitleName = titleName;
	}
}
