package com.mes.ncr.server.serviceimpl.utils.wdw;

import java.awt.Color;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

//import org.apache.poi.ss.usermodel.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfGState;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfStamper;
//import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.mes.ncr.server.service.utils.Configuration;
import com.mes.ncr.server.service.utils.StringUtils;
import com.mes.ncr.server.utils.Constants;

/**
 * 用iText生成PDF文档需要5个步骤：
 * 
 * ①建立com.lowagie.text.Document对象的实例。 Document document = new Document();
 * ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
 * PDFWriter.getInstance(document, new FileOutputStream("Helloworld.PDF"));
 * ③打开文档。 document.open(); ④向文档中添加内容。 document.add(new Paragraph("Hello
 * World")); ⑤关闭文档。 document.close();
 *
 */
public class NewCreditReportUtil {
	private static Logger logger = LoggerFactory.getLogger(NewCreditReportUtil.class);

	/**
	 * 定义静态变量，用于生成水印文件名称
	 */
	private final static String RESULT_FILE = Configuration.readConfigString("pdf.test.file.path", "config/config");

	/**
	 * 生成PDF模板样例
	 * 
	 * @param NewCorpReportMap 输入参数映射
	 * @return 生成成功与否
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文本异常
	 */
	public static boolean generateDeepthCreditReport(Map<String, Object> NewCorpReportMap)
			throws MalformedURLException, IOException, DocumentException {
		boolean wReslut = false;
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document doc = new Document();
			doc.setMargins(20, 20, 30, 30);

			String fontPath = Constants.getConfigPath();

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(doc, new FileOutputStream(RESULT_FILE));

			// 设置中文字体
			BaseFont fontChinese = BaseFont.createFont(fontPath + "simkai.ttf", BaseFont.IDENTITY_H,
					BaseFont.NOT_EMBEDDED);
			Font chinese = new Font(fontChinese, 10, Font.NORMAL);

			// 添加页眉页脚
			String headertitle = "*****报告" + "         " + "*******";
			addHeaderAndFooter(doc, chinese, headertitle);

			// ③打开文档。
			doc.open();

			// 18.0F是字体大小，0表示字体倾斜度, font.setStyle(1); 0：无变化1：加粗；2：斜体...
			// font.setFamily("微软雅黑"); // 设置字体
			Font myfont = setfont(fontPath + "/simkai.ttf", 13.0F, 0, Color.BLACK, 0);// 基本字体
			// Font myfont1 = setfont(fontPath + "/msyh.ttf", 36.0F, 0, Color.BLACK, 1);//
			// 标头字体（一级字体）
			// Font myfont2 = setfont(fontPath + "/msyh.ttf", 27.0F, 0, Color.BLACK, 1);//
			// 标头字体（二级字体）
			Font myfont3 = setfont(fontPath + "/simkai.ttf", 18.0F, 0, Color.BLACK, 1);// 标头字体（三级字体）
			// Font myfont4 = setfont(fontPath + "/msyh.ttf", 13.0F, 0, Color.BLACK, 1);//
			// 标头字体（四级字体）
			// Font myfont5 = setfont(fontPath + "/msyh.ttf", 12.0F, 0, Color.BLACK, 0);//
			// 标头字体（五级字体）

			// 初始化pdf基本功能性文本
			Image image = null;
			PdfPTable table;
			PdfPCell cell = null;
			Paragraph paragraph = null;

			// 准备工作结束，进行文档内容填充：
			// 添加公司logo图片
			table = new PdfPTable(1);
			String picpath = NewCorpReportMap.get("reportLogoFilePath").toString();
			addpicture(table, image, picpath, cell, doc);

			// 添加报告信息
			firstPage(cell, table, paragraph, NewCorpReportMap.get("corpname").toString(), "企业信用报告",
					NewCorpReportMap.get("reportNo").toString(), myfont, myfont3, myfont3, doc);

			// 第二页 （固定死页面）
			doc.newPage();
			doc.add(new Paragraph("       ", myfont));
			paragraph = new Paragraph("报告说明", myfont3);
			paragraph.setAlignment(1);
			doc.add(paragraph);
			doc.add(new Paragraph("       ", myfont));

			geshi1(new Paragraph("1. 内容1", myfont), doc);
			geshi1(new Paragraph("2. 内容2", myfont), doc);
			geshi1(new Paragraph("3. 内容3", myfont), doc);
			geshi1(new Paragraph("4. 内容4", myfont), doc);

			// 第三页 报告摘要,每页空2行留给页眉
//			doc.newPage();
//			doc.add(new Paragraph("       ", myfont));
//			doc.add(new Paragraph("       ", myfont));
			// 第四页添加Table
			/*
			 * doc.newPage(); PdfPTable wTable=new PdfPTable(3); wTable.setTotalWidth(new
			 * float[] {105,170,105,170}); wTable.setLockedWidth(true);
			 * 
			 * doc.add(wTable); doc.close();
			 */

			doc.newPage();
			doc.add(new Paragraph("       ", myfont));
			doc.add(new Paragraph("       ", myfont));
			PdfPTable table1 = new PdfPTable(5);
			for (int aw = 0; aw < 10; aw++) {
				// 构建每一格
				table1.addCell("cell");
			}
			doc.add(table1);
			doc.close();

			/*
			 * // 创建PdfWriter对象 PdfWriter writer = PdfWriter.getInstance(doc, new
			 * FileOutputStream(RESULT_FILE)); // 打开文档 doc.open();
			 * 
			 * // 添加表格，4列 PdfPTable wPdfPTable = new PdfPTable(4); //// 设置表格宽度比例为%100
			 * wPdfPTable.setWidthPercentage(100); // 设置表格的宽度 wPdfPTable.setTotalWidth(500);
			 * // 也可以每列分别设置宽度 wPdfPTable.setTotalWidth(new float[] { 160, 70, 130, 100 });
			 * // 锁住宽度 wPdfPTable.setLockedWidth(true); // 设置表格上面空白宽度
			 * wPdfPTable.setSpacingBefore(10f); // 设置表格下面空白宽度
			 * wPdfPTable.setSpacingAfter(10f); // 设置表格默认为无边框
			 * wPdfPTable.getDefaultCell().setBorder(0); PdfContentByte cb =
			 * writer.getDirectContent();
			 * 
			 * // 构建每个单元格 PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1")); // 设置跨两行
			 * cell1.setRowspan(2); // 设置距左边的距离 cell1.setPaddingLeft(10); // 设置高度
			 * cell1.setFixedHeight(20); // 设置内容水平居中显示
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER); // 设置垂直居中
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell1);
			 * 
			 * PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
			 * cell2.setPaddingLeft(10); cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell2.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell2);
			 * 
			 * PdfPCell cell3 = new PdfPCell(new Paragraph("Cell 3"));
			 * cell3.setPaddingLeft(10); // 设置无边框 cell3.setBorder(Rectangle.NO_BORDER);
			 * cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell3.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell3);
			 * 
			 * PdfPCell cell5 = new PdfPCell(new Paragraph("Cell 5"));
			 * cell5.setPaddingLeft(10); // 设置占用列数 cell5.setColspan(1);
			 * cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell5.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell5);
			 * doc.add(table); // 关闭文档 doc.close();
			 */
			wReslut = true;
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return wReslut;
	}

	/**
	 * 给PDF文件添加图片水印
	 * 
	 * @param InPdfFile  要加水印的原pdf文件路径
	 * @param outPdfFile 加了水印后要输出的路径
	 * @param object     水印图片路径
	 * @param pageSize   原pdf文件的总页数（该方法是我当初将数据导入excel中然后再转换成pdf所以我这里的值是用excel的行数计算出来的，
	 *                   如果不是我这种可以 直接用reader.getNumberOfPages()获取pdf的总页数）
	 * @throws Exception
	 */
	public static void addPdfMark(String InPdfFile, String outPdfFile, String readpicturepath) throws Exception {
		try {
			PdfReader reader = new PdfReader(InPdfFile);
			int pageSize = reader.getNumberOfPages();
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
			Image img = Image.getInstance(readpicturepath);// 插入水印
			img.setAbsolutePosition(50, 50);
			for (int i = 1; i <= pageSize; i++) {
				PdfContentByte under = stamp.getUnderContent(i);
				under.addImage(img);
			}
			stamp.close();// 关闭
			File tempfile = new File(InPdfFile);
			if (tempfile.exists()) {
				tempfile.delete();
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加文字水印
	 * 
	 * @param inputFile     输入PDF的文件路径
	 * @param outputFile    输出PDF的文件路径
	 * @param waterMarkName 水印文字
	 */
	public static void waterMark(String inputFile, String outputFile, String waterMarkName) {
		int interval = -5;
		try {
			PdfReader reader = new PdfReader(inputFile);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

			BaseFont base = BaseFont.createFont("C:/windows/fonts/simsun.ttc,1", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			Rectangle pageRect = null;
			PdfGState gs = new PdfGState();
			// 设置透明度
			gs.setFillOpacity(0.1f);
			gs.setStrokeOpacity(0.4f);
			int total = reader.getNumberOfPages() + 1;

			JLabel label = new JLabel();
			FontMetrics metrics;
			int textH = 0;
			int textW = 0;
			label.setText(waterMarkName);
			metrics = label.getFontMetrics(label.getFont());
			textH = metrics.getHeight();
			textW = metrics.stringWidth(label.getText());

			PdfContentByte under;
			for (int i = 1; i < total; i++) {
				pageRect = reader.getPageSizeWithRotation(i);
				under = stamper.getOverContent(i);
				under.saveState();
				under.setGState(gs);
				under.beginText();
				under.setFontAndSize(base, 20);

				// 水印文字成30度角倾斜
				// 你可以随心所欲的改你自己想要的角度
				for (int height = interval + textH; height < pageRect.getHeight(); height = height + textH * 3) {
					for (int width = interval + textW; width < pageRect.getWidth() + textW; width = width + textW * 2) {
						under.showTextAligned(Element.ALIGN_LEFT, waterMarkName, width - textW, height - textH, 30);
					}
				}
				// 添加水印文字
				under.endText();
			}

			// 一定不要忘记关闭流
			stamper.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 段落格式
	 * 
	 * @param paragraph 段落对象
	 * @param doc       文本上下文
	 * @throws DocumentException 文本异常
	 */
	public static void geshi1(Paragraph paragraph, Document doc) throws DocumentException {// 段落的格式
		try {
			paragraph.setIndentationLeft(30);
			paragraph.setIndentationRight(30);
			paragraph.setFirstLineIndent(20f);
			paragraph.setSpacingAfter(10f);
			paragraph.setSpacingBefore(10f);
			doc.add(paragraph);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(居中无边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi2(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(不居中无边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi12(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(居中有边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi22(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格个格式(居中有边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi32(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setColspan(3);
			cell.setBorder(0);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 对doc文件设置页面页脚(页眉页脚的设置一定要在open前设置好)
	 * 
	 * @param wDoc         文件上下文
	 * @param wChineseFont 字体对象
	 * @param wHeaderTitle 标题
	 */
	public static void addHeaderAndFooter(Document wDoc, Font wChineseFont, String wHeaderTitle) {
		try {
			// HeaderFooter的第2个参数为非false时代表打印页码 页眉页脚中也可以加入图片，并非只能是文字
			HeaderFooter wHeader = new HeaderFooter(new Phrase(wHeaderTitle, wChineseFont), false);
			wHeader.setBorder(Rectangle.NO_BORDER);
			wHeader.setBorder(Rectangle.BOTTOM);
			wHeader.setAlignment(1);
			wHeader.setBorderColor(Color.red);
			wDoc.setHeader(wHeader);

			HeaderFooter wFooter = new HeaderFooter(new Phrase("第-", wChineseFont), new Phrase("-页", wChineseFont));
			// 0是靠左 1是居中 2是居右
			wFooter.setAlignment(1);
			wFooter.setBorderColor(Color.red);
			wFooter.setBorder(Rectangle.NO_BORDER);
			wDoc.setFooter(wFooter);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 设置字体
	 * 
	 * @param fonttype  字体类型
	 * @param fontsize  字体大小
	 * @param fontflag  字体标记
	 * @param fontcolor 字体颜色
	 * @param fontstyle 字体样式
	 * @return
	 * @throws DocumentException 文本异常
	 * @throws IOException       IO异常
	 */
	public static Font setfont(String fonttype, float fontsize, int fontflag, Color fontcolor, int fontstyle)
			throws DocumentException, IOException {
		BaseFont baseFont5 = BaseFont.createFont(fonttype, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font font = new Font(baseFont5, fontsize, fontflag);
		try {
			font.setColor(fontcolor);
			if (fontstyle != 0) {// 如果传参为0不设置字体
				font.setStyle(fontstyle);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return font;
	}

	/**
	 * 插入图片
	 * 
	 * @param table   表格
	 * @param image   图片对象
	 * @param picpath 图片路径
	 * @param cell    单元格
	 * @param doc     文本上下文
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文本异常
	 */
	public static void addpicture(PdfPTable table, Image image, String picpath, PdfPCell cell, Document doc)
			throws MalformedURLException, IOException, DocumentException {
		try {
			image = Image.getInstance(picpath);
			cell = new PdfPCell(image);
			geshi2(cell, table);
			doc.add(table);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 首页-固定格式布局
	 * 
	 * @param wCell       单元格
	 * @param wTable      表格
	 * @param wParagraph  段落对象
	 * @param wCorName    公司名称
	 * @param wReportType 报告类型
	 * @param wRepoartNo  报告编号
	 * @param wMyFont     我的字体对象
	 * @param wMyFont3    我的字体对象
	 * @param wDoc        文本上下文
	 * @throws DocumentException 文本异常
	 */
	public static void firstPage(PdfPCell wCell, PdfPTable wTable, Paragraph wParagraph, String wCorName,
			String wReportType, String wRepoartNo, Font wMyFont, Font wMyFont3, Font wUnderlineFont, Document wDoc)
			throws DocumentException {
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy年MM月dd日");

			// 公司名
			wParagraph = new Paragraph(wCorName, wMyFont3);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			// 报告类型
			wParagraph = new Paragraph(wReportType, wMyFont3);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));

			wTable = new PdfPTable(2);

			wCell = new PdfPCell(new Phrase("报告编号：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase(wRepoartNo, wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告生成时间：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase(wSDF.format(new Date()), wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告生成机构：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("广州电力机车", wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告结论机构：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("技术中心", wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wDoc.add(wTable);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 将数字转换为汉字
	 * 
	 * @param wStr
	 * @return
	 */
	public static String toChinese(String wStr) {
		String wResult = "";
		try {
			String[] wS1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
			String[] wS2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };
			int wN = wStr.length();
			for (int i = 0; i < wN; i++) {
				int wNum = wStr.charAt(i) - '0';
				if (i != wN - 1 && wNum != 0) {
					wResult += wS1[wNum] + wS2[wN - 2 - i];
				} else {
					wResult += wS1[wNum];
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 非空判断
	 * 
	 * @param a 对象
	 * @return 对象字符串
	 */
	public static String isnull(Object a) {
		if (a != null && a != "" && a != "null" && a.toString() != "null") {
			return a.toString();
		} else {
			return "";
		}
	}

	/**
	 * @Description //TODO 设置页面横向
	 **/
	public static void setPageSizeHen(Document document) {
		try {
			// 横向
			Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
			pageSize.rotate();
			document.setPageSize(pageSize);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * @Description //TODO 设置页面竖向
	 **/
	public static void setPageSizeShu(Document document) {
		try {
			// 竖向
			Rectangle pageSize = new Rectangle(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			pageSize.rotate();
			document.setPageSize(pageSize);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 输出批量返修记录PDF
	 */
	public static void outputBatchRepairPdf(Map<String, String> wMapValue, List<List<String>> wValueList,
			OutputStream wStream) {
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document wDoc = new Document();
			wDoc.setMargins(20, 20, 30, 30);

			String wFontPath = Constants.getConfigPath() + "simkai.ttf";

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(wDoc, wStream);

			// 设置中文字体
			BaseFont wBaseFont = BaseFont.createFont(wFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font wChineseFont = new Font(wBaseFont, 10, Font.NORMAL);

			// 添加页眉页脚
			addHeaderAndFooter(wDoc, wChineseFont, "机车返修报告");

			// ②页面横向
			setPageSizeHen(wDoc);
			// ③打开文档。
			wDoc.open();
			// 基本字体
			Font wMyBaseFont = setfont(wFontPath, 13.0F, 0, Color.BLACK, 0);
			// 表格标题字体
			Font wMyTitleFont = setfont(wFontPath, 18.0F, 0, Color.BLACK, 1);
			// 下划线字体
			Font wUnderlineFont = new Font(wBaseFont, 13.0F, Font.UNDERLINE);
			// 加粗字体
			Font wBoldFont = new Font(wBaseFont, 13.0F, Font.BOLD);

			// ①添加标题
			String wTitle = StringUtils.Format("{0}型电力机车{1}修异常状态汇总表", wMapValue.get("ProductNo"),
					wMapValue.get("LineName"));
			Paragraph wParagraph = new Paragraph(wTitle, wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph(new Chunk("    ", wMyBaseFont)));
			wDoc.add(new Paragraph(new Chunk("    ", wMyBaseFont)));
			// ②添加表头信息
			Paragraph wInfo = new Paragraph(new Chunk("车型/号：", wMyBaseFont));
			String wPartNo = StringUtils.Format("{0}({1})", wMapValue.get("PartNo"), wMapValue.get("CustomerName"));
			wInfo.add(new Chunk(wPartNo, wUnderlineFont));

			wInfo.add(new Chunk("    制表人：", wMyBaseFont));
			String wTableMaker = wMapValue.get("TableMaker");
			wInfo.add(new Chunk(wTableMaker, wUnderlineFont));

			wInfo.add(new Chunk("    制表日期：", wMyBaseFont));
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy.MM.dd");
			String wCurDate = wSDF.format(Calendar.getInstance().getTime());
			wInfo.add(new Chunk(wCurDate, wUnderlineFont));

			wInfo.add(new Chunk("    部门主管领导：", wMyBaseFont));
			String wLeader = wMapValue.get("Leader");
			wInfo.add(new Chunk(wLeader, wUnderlineFont));

			wDoc.add(wInfo);
			// ③添加表格信息
			wDoc.add(new Paragraph(new Chunk("    ", wMyBaseFont)));

			PdfPTable wPdfPTable = new PdfPTable(11);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 300, 100, 100, 100, 100, 100, 100, 100, 100, 100 });

			for (int i = 0; i < wValueList.size(); i++) {
				if (i == 0) {
					for (String wValue : wValueList.get(i)) {
						Paragraph wPara = new Paragraph(wValue, wBoldFont);
						PdfPCell wTitleCell = new PdfPCell(wPara);
						wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						wTitleCell.setPadding(10);
						wPdfPTable.addCell(wTitleCell);
					}
				} else {
					for (String wValue : wValueList.get(i)) {
						Paragraph wPara = new Paragraph(wValue, wMyBaseFont);
						PdfPCell wTitleCell = new PdfPCell(wPara);
						wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						wTitleCell.setPadding(10);
						wPdfPTable.addCell(wTitleCell);
					}
				}
			}
			wDoc.add(wPdfPTable);
			// ④添加footer信息
			String wFooter = "技术创新，精益管理，持续改进，以人为本，以高质量的产品奉献顾客和社会";
			Paragraph wFParagraph = new Paragraph(wFooter, wMyBaseFont);
			wFParagraph.setAlignment(1);
			wDoc.add(wFParagraph);

			wDoc.close();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 输出合并单元格PDF
	 */
	public static void outputMergeCell(OutputStream wStream) {
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document wDoc = new Document();
			wDoc.setMargins(20, 20, 30, 30);

			String wFontPath = Constants.getConfigPath() + "simkai.ttf";

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(wDoc, wStream);

			// 设置中文字体
			BaseFont wBaseFont = BaseFont.createFont(wFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font wChineseFont = new Font(wBaseFont, 10, Font.NORMAL);

			// 添加页眉页脚
			addHeaderAndFooter(wDoc, wChineseFont, "合并单元格示例");

			// ③打开文档。
			wDoc.open();
			// 基本字体
			Font wMyBaseFont = setfont(wFontPath, 13.0F, 0, Color.BLACK, 0);

			// ③添加表格信息
			wDoc.add(new Paragraph(new Chunk("    ", wMyBaseFont)));
			PdfPCell wCell = null;

			PdfPTable wPdfPTable = new PdfPTable(2);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 200, 100 });

			PdfPTable wCellTable = new PdfPTable(2);
			wCellTable.setWidths(new int[] { 100, 100 });

			wCell = new PdfPCell(new Paragraph("A"));
			wCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wCell.setVerticalAlignment(Element.ALIGN_CENTER);
			wCell.setColspan(2);
			wCell.setPadding(10);
			wCellTable.addCell(wCell);

			wCell = new PdfPCell(new Paragraph("B"));
			wCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wCell.setVerticalAlignment(Element.ALIGN_CENTER);
			wCell.setPadding(10);
			wCellTable.addCell(wCell);

			wCell = new PdfPCell(new Paragraph("C"));
			wCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wCell.setVerticalAlignment(Element.ALIGN_CENTER);
			wCell.setPadding(10);
			wCellTable.addCell(wCell);

			wCell = new PdfPCell(wCellTable);
			wCell.setPadding(0);
			wPdfPTable.addCell(wCell);

			wCell = new PdfPCell(new Paragraph("D"));
			wCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wCell.setPadding(10);
			wPdfPTable.addCell(wCell);

			wDoc.add(wPdfPTable);

			wDoc.close();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}