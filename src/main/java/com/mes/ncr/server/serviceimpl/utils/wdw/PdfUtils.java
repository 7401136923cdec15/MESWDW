package com.mes.ncr.server.serviceimpl.utils.wdw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import com.itextpdf.text.pdf.PdfReader;
import com.mes.ncr.server.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDF文件输出工具类
 */
public class PdfUtils {

	private static Logger logger = LoggerFactory.getLogger(PdfUtils.class);

	/**
	 * 利用模板生成PDF文件
	 */
	public static void pdfOut(Map<String, Object> wMap, OutputStream wOut, String wTemplatePath, int wPageNumber) {
		PdfReader wReader;
		ByteArrayOutputStream wBos;
		PdfStamper wStamper;
		try {
			BaseFont wBF = BaseFont.createFont(Constants.getConfigPath() + "simkai.ttf", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			@SuppressWarnings("unused")
			Font wFontChinese = new Font(wBF, 5, Font.NORMAL);
			wReader = new PdfReader(wTemplatePath);// 读取pdf模板
			wBos = new ByteArrayOutputStream();
			wStamper = new PdfStamper(wReader, wBos);
			AcroFields wForm = wStamper.getAcroFields();
			// 文字类的内容处理
			@SuppressWarnings("unchecked")
			Map<String, String> wDateMap = (Map<String, String>) wMap.get("datemap");
			wForm.addSubstitutionFont(wBF);
			for (String wKey : wDateMap.keySet()) {
				String wValue = wDateMap.get(wKey);
				wForm.setField(wKey, wValue);
			}
			// 图片类的内容处理
			@SuppressWarnings("unchecked")
			Map<String, String> wImgMap = (Map<String, String>) wMap.get("imgmap");
			for (String wKey : wImgMap.keySet()) {
				String wValue = wImgMap.get(wKey);
				String wImgPath = wValue;

				int wPageNo = (int) wForm.getFieldPositions(wKey).get(0).page;
				Rectangle wSingleRect = wForm.getFieldPositions(wKey).get(0).position;
				float wX = wSingleRect.getLeft();
				float wY = wSingleRect.getBottom();
				// 根据路径读取图片
				Image wImage = Image.getInstance(wImgPath);
				// 获取图片页面
				PdfContentByte under = wStamper.getOverContent(wPageNo);
				// 图片大小自适应
				wImage.scaleToFit(wSingleRect.getWidth(), wSingleRect.getHeight());
				// 添加图片
				wImage.setAbsolutePosition(wX, wY);
				under.addImage(wImage);
			}
			wStamper.setFormFlattening(true);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
			wStamper.close();
			Document wDoc = new Document();
			PdfCopy wCopy = new PdfCopy(wDoc, wOut);
			wDoc.open();
			PdfImportedPage wImportPage = null;
			for (int i = 1; i <= wPageNumber; i++) {
				wImportPage = wCopy.getImportedPage(new PdfReader(wBos.toByteArray()), i);
				wCopy.addPage(wImportPage);
			}
			wDoc.close();
		} catch (IOException e) {
			logger.error(e.toString());
		} catch (DocumentException e) {
			logger.error(e.toString());
		}
	}
}
