/*
 * Excel.java
 * 
 * Created on 2013-06-11 12:13:25 PM
 */
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Writes, reads, modifies Excel 2010 files (.xlsx format)
 * 
 * @author Olga Tsibulevskaya
 */
public class Excel {
	
	private File file;
	private int nbCol = 18;
	private JTextArea labelMidterm = PanelMidterms.label;
	private JTextArea labelFinal = PanelFinals.label;
	private JTextArea labelEditor = PanelEditor.label;
	private XSSFWorkbook wb;
	/**
	 * Creates an empty container
	 */
	public Excel()  {
		// default 
	}
	public void setFile(String filename) {
		file = new File(filename);
	}
	public void setNbCol(int num) {
		nbCol = num;
	}
	/**
	 * Writes headers to the file for the specified term
	 * 
	 * @param name name of the file to be created, format of the term
	 * @see Term 
	 */
	public void create(String name) {
		wb = new XSSFWorkbook();
		
		Font font = setCustomFont(wb, "Calibri", 11, true);
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		
		XSSFSheet sheet = wb.createSheet(name);
		
		setNbCol(18);
		
		String[] headers = {"#", "Date", "Family name", "First name", "Course number", 
				"Section", "Exam location", "Start", "Finish", "Length", 
				"Professor name", "Professor email", "Extra time", 
				"Stopwatch", "PC", "Accommodation", "Comments", "Invigilator"};
	
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		// write headers
		while (colXL < nbCol) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(style);
		}
	}
	public void createMac(String name) {
		wb = new XSSFWorkbook();
		
		Font font = setCustomFont(wb, "Calibri", 11, true);
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		
		XSSFSheet sheet = wb.createSheet(name);
		
		setNbCol(16);
		
		String[] headers = {"#", "Date", "Family name", "First name", "Course number", 
				"Section", "Exam location", "Start", "Finish", "Length", 
				"Professor name", "Professor email", "Extra time", 
				"Stopwatch", "PC", "Accommodation", "Comments" };
	
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		// write headers
		while (colXL < nbCol) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(style);
		}
	}
	public void writeMacdonald(ArrayList<StudentMidterm> list) throws IOException {
		Collections.sort(list, new Student.DateExamComparator());

		String term = new Term().getTerm();
		//String filename = term + " exam schedule.xlsx";
		String filename = "F:\\Exams\\" + term + " exam schedule.xlsx";
		setFile(filename);
		
		
		if (! file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw e;
			}
			createMac(term);
		}
		
		setNbCol(17);
		
		try {
			FileInputStream fis = new FileInputStream(file);	
			if (wb == null) 
				wb = new XSSFWorkbook(fis);
		
			CellStyle[] styles = getAllStyles(wb);
			
			XSSFSheet sheet = wb.getSheet("Macdonald");
			
			if (sheet == null) {
				sheet = wb.createSheet("Macdonald");
				String[] headers = {"#", "Date", "Family name", "First name", "Course number", 
						"Section", "Exam location", "Start", "Finish", "Length", "Professor name", "Professor email", "Extra time", 
						"Stopwatch", "PC", "Accommodation", "Comments" };
			
				Row row = sheet.createRow((short) 0);
				int colXL = 0;
				// write headers
				while (colXL < nbCol) {
					Cell cell = row.createCell(colXL);
					cell.setCellValue(headers[colXL++]);
					cell.setCellStyle(styles[1]);
				}
				sheet.setColumnWidth(2, 15*255); // last name
				sheet.setColumnWidth(3, 15*255); // first name
				sheet.setColumnWidth(4, 15*255); // course number
				sheet.setColumnWidth(6, 25*255);
				sheet.setColumnWidth(9, 15*255);
				sheet.setColumnWidth(10, 25*255);
				sheet.setColumnWidth(11, 25*255);
				sheet.createFreezePane(0, 1);
				
				for (int rowXL = 1, i = 0; i < list.size(); i++) {
					StudentMidterm student = list.get(i);
					row = sheet.createRow((short) rowXL++);
					fillRow(row, student, styles);
				}
			}
			else { // file already contains entries 
				int index = 0; // for the list
				StudentMidterm student = list.get(index);
			
				Date dateToAdd = student.getExamDate(); // date of the entry to be added
				Date dateInFile = null; // existing in the file dates.
						
				int rowEnd = sheet.getLastRowNum();
		
				/* start reading the file form the 1st row (exclude the header) */
				for (int rowNum = 1; index < list.size(); rowNum++) { // || rowNum < endRow
					Row r = sheet.getRow(rowNum);
					if (r == null) { // the last row
						r = sheet.createRow(rowNum);
						fillRow(r, student, styles);
						
						if (++index < list.size()) {
							student = list.get(index);
							dateToAdd = student.getExamDate();
						} 
					}
					else {
						Cell cell = r.getCell(1, Row.RETURN_BLANK_AS_NULL); 
						if (cell == null) {
							continue; 
						}
						else {
							if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								dateInFile = cell.getDateCellValue();
														
								if (dateToAdd.compareTo(dateInFile) <= 0) {// if the same or less, then add above
									/* check if id is the same, then the entry already exists */
									if (dateToAdd.compareTo(dateInFile) == 0) {
										Cell cellPrev = r.getCell(0);
										int idInFile = (int)cellPrev.getNumericCellValue();
										int idToAdd = student.getId();
										/* the same entries, do not enter twice, takes more time, but
										 double control if id has been lost, id.txt corrupted, etc...*/
										if (idInFile == idToAdd) {
											if (++index < list.size()) {
												student = list.get(index);
												dateToAdd = student.getExamDate();
											} 
										}
									}	
									else {
										sheet.shiftRows(rowNum, rowEnd, 1);
										rowEnd++;
								
										Row rowNew = sheet.createRow(rowNum);
										fillRow(rowNew, student, styles);
									
										if (dateToAdd.compareTo(dateInFile) <= 0)
											rowNum--;
										/* get the next student */
										if (++index < list.size()) {
											student = list.get(index);
											dateToAdd = student.getExamDate();
										} 
									} 
								}
								else {
								// continue searching 
								}
							}
							else {
							// wrong format type of the dateExam cell
							}
						}
					}
				} // end of the spreadsheet table
			} // end of else
			
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			fos.flush();
			fos.close();
			wb = null;
			labelMidterm.append("-- File " + file.getName() + " has been updated\n");
			labelMidterm.paintImmediately(labelMidterm.getVisibleRect());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Adds new entries to the files according to the terms in which
	 * exams will be taken
	 * 	
	 * @param list 	list of students to be added
	 * @param term	name of the term (ex. 'Summer 2013')
	 * @throws IOException
	 * @throws InvalidFormatException 
	 */
	public void update(ArrayList<StudentMidterm> list, String term) throws IOException, InvalidFormatException {
		/* sort the list by the date of the exam */
		Collections.sort(list, new Student.DateExamComparator());
		//String filename = term + " exam schedule.xlsx";
		String filename = "F:\\Exams\\" + term + " exam schedule.xlsx";
		setFile(filename);
	
		
		if (! file.exists()) {
			file.createNewFile();
			create(term);
		}
	
		try {
			FileInputStream fis = new FileInputStream(new File(filename));	
			if (wb == null)
				wb = new XSSFWorkbook(fis);
		
			CellStyle[] styles = getAllStyles(wb);
			
			XSSFSheet sheet = wb.getSheetAt(0);
						
			if (sheet.getLastRowNum() < 1) { // write to the empty file
				for (int rowXL = 1, i = 0; i < list.size(); i++) {
					StudentMidterm student = list.get(i);
					Row row = sheet.createRow((short) rowXL++);
					fillRow(row, student, styles);
				}
				sheet.setColumnWidth(2, 15*255); // last name
				sheet.setColumnWidth(3, 15*255); // first name
				sheet.setColumnWidth(4, 15*255); // course number
				sheet.setColumnWidth(9, 15*255);
				sheet.setColumnWidth(10, 25*255);
				sheet.setColumnWidth(11, 25*255);
				sheet.setColumnWidth(12, 15*255);
				sheet.createFreezePane(0, 1);			
			}
			else { // file already contains entries 
				int index = 0; // for the list
				StudentMidterm student = list.get(index);
			
				Date dateToAdd = student.getExamDate(); // date of the entry to be added
				Date dateInFile = null; // existing in the file dates.
						
				int rowEnd = sheet.getLastRowNum();
		
				/* start reading the file form the 1st row (exclude the header) */
				for (int rowNum = 1; index < list.size(); rowNum++) { // || rowNum < endRow
					Row r = sheet.getRow(rowNum);
					
					if (r == null && rowNum < rowEnd) {
						continue; 
					}
					if (r == null) { // the last row
						r = sheet.createRow(rowNum);
						fillRow(r, student, styles);
						
						if (++index < list.size()) {
							student = list.get(index);
							dateToAdd = student.getExamDate();
						} 
					}
					else {
						Cell cell = r.getCell(1, Row.RETURN_BLANK_AS_NULL); 
						if (cell == null) {
							// in which cases?
						}
						else {
							if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								dateInFile = cell.getDateCellValue();
								if (dateToAdd.compareTo(dateInFile) <= 0) {// if the same or less, then add above
									/* check if id is the same, then the entry already exists */
									if (dateToAdd.compareTo(dateInFile) == 0) {
										Cell cellPrev = r.getCell(0);
										int idInFile = (int)cellPrev.getNumericCellValue();
										int idToAdd = student.getId();
										/* the same entries, do not enter twice, takes more time, but
										 double control if id has been lost, id.txt corrupted, etc...*/
										if (idInFile == idToAdd) {
											if (++index < list.size()) {
												student = list.get(index);
												dateToAdd = student.getExamDate();
											} 
										}
									}	
									else {
										sheet.shiftRows(rowNum, rowEnd, 1);
										rowEnd++;
								
										Row rowNew = sheet.createRow(rowNum);
										fillRow(rowNew, student, styles);
									
										if (dateToAdd.compareTo(dateInFile) <= 0)
											rowNum--;
										/* get the next student */
										if (++index < list.size()) {
											student = list.get(index);
											dateToAdd = student.getExamDate();
										} 
									} 
								}
								else {
								// continue searching 
								}
							}
							else {
								new Message("Wrong Date format, row " + rowNum+1);
								throw new InvalidFormatException(null);
							}
						}
					}
				} // end of the spreadsheet table
			} // end of else
			
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			fos.flush();
			fos.close();
			wb = null;
			labelMidterm.append("-- File " + file.getName() + " has been updated\n");
			labelMidterm.paintImmediately(labelMidterm.getVisibleRect());

		}
		catch (IOException e) {
			throw new IOException();
		}
	
	}
	/* Sets fonts based on the preferences given in arguments */
	private Font setCustomFont(Workbook wb, String fontname, int fontsize, boolean bold) {
		Font font = (Font)wb.createFont();
	    font.setFontHeightInPoints((short)fontsize);
	    font.setFontName(fontname);
	    if (bold)
	    	font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
	    return font;
	}
	/* Creates an array of styles for Excel cells */
	private XSSFCellStyle[] getAllStyles(Workbook wb) {
		XSSFCellStyle[] styles = new XSSFCellStyle[8];
		
		XSSFCellStyle styleHeader = (XSSFCellStyle) wb.createCellStyle();
		Font fontHeader = setCustomFont(wb, "Calibri", 11, true);
		styleHeader.setFont(fontHeader);
		styles[0] = styleHeader;
		
		XSSFCellStyle styleNormal = (XSSFCellStyle) wb.createCellStyle();
		Font fontNormal = setCustomFont(wb, "Calibri", 11, false);
		styleNormal.setFont(fontNormal);
		styles[1] = styleNormal;
		
		XSSFCellStyle styleBold = (XSSFCellStyle) wb.createCellStyle();
		Font fontBold = setCustomFont(wb, "Calibri", 11, true);
		styleBold.setFont(fontBold);
		styles[2] = styleBold;
		
		XSSFCellStyle styleDate = (XSSFCellStyle) wb.createCellStyle();
		DataFormat df = wb.createDataFormat();
		styleDate.setFont(fontBold);
		styleDate.setDataFormat(df.getFormat("d-mmm"));
		styles[3] = styleDate;
		
		XSSFCellStyle styleTime = (XSSFCellStyle) wb.createCellStyle();
		df = wb.createDataFormat();
		styleTime.setFont(fontBold);
		styleTime.setDataFormat(df.getFormat("h:mm"));
		styles[4] = styleTime;
		
		XSSFCellStyle timeChanged = (XSSFCellStyle) wb.createCellStyle();
		df = wb.createDataFormat();
		timeChanged.setFont(fontBold);
		timeChanged.setDataFormat(df.getFormat("h:mm"));
		timeChanged.setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));  
		timeChanged.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
		styles[5] = timeChanged;
		
		XSSFCellStyle styleVertical = (XSSFCellStyle) wb.createCellStyle();
		styleVertical.setFont(fontNormal);
		styleVertical.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		styles[6] = styleVertical;
		
		XSSFCellStyle styleCenter = (XSSFCellStyle) wb.createCellStyle();
		styleNormal.setFont(fontNormal);
		styleCenter.setAlignment(CellStyle.ALIGN_CENTER);
		styles[7] = styleCenter;
		
		return styles;
	}
	/* Populates a row */
	private void fillRow(Row row, StudentMidterm student, CellStyle[] styles) {
		for (int colXL = 0; colXL < nbCol-1; colXL++) {
			Cell cell = row.createCell(colXL);
			switch (colXL) {
			case 0:
				cell.setCellValue(student.getId()); 
				cell.setCellStyle(styles[1]); break;
			case 1: 
				cell.setCellValue(student.getExamDate());
				cell.setCellStyle(styles[3]); break;
			case 2:
				cell.setCellValue(student.getNameLast());
				cell.setCellStyle(styles[1]); break;
			case 3: 
				cell.setCellValue(student.getNameFirst());
				cell.setCellStyle(styles[1]); break;
			case 4: 
				cell.setCellValue(student.getCourse());
				cell.setCellStyle(styles[2]); break;
			case 5:
				cell.setCellValue(student.getSection());
				cell.setCellStyle(styles[1]); break;
			case 6:
				cell.setCellValue(student.getLocation());
				cell.setCellStyle(styles[1]); break;
			case 7:
				String date = new Helper().getDateAsString(student.getExamStartTime());
				double d = DateUtil.convertTime(date);
				cell.setCellValue(d);
				cell.setCellStyle(styles[4]); break;
			case 8:
				date = new Helper().getDateAsString(student.getExamFinishTime());
				d = DateUtil.convertTime(date);
				cell.setCellValue(d);
				cell.setCellStyle(styles[4]); break;
			case 9:
				int length = student.getExamLength();
				int hour = length / 60;
				int min = length % 60;
				if (hour != 0 && min != 0)
					cell.setCellValue(hour + " hours " + min + " mins");
				else if (hour != 0 && min == 0)
					cell.setCellValue(hour + " hours");
				else if (hour == 0 && min != 0)
					cell.setCellValue(min + " mins");
				else
					cell.setCellValue("time not known");
				cell.setCellStyle(styles[1]); break;
			case 10:
				cell.setCellValue(student.getNameProf());
				cell.setCellStyle(styles[1]); break;
			case 11:
				cell.setCellValue(student.getEmailProf());
				cell.setCellStyle(styles[1]); break;
			case 12:
				cell.setCellValue(student.getExtraTime());
				cell.setCellStyle(styles[1]); break;
			case 13:
				cell.setCellValue(student.getStopwatch());
				cell.setCellStyle(styles[1]); break;
			case 14:
				cell.setCellValue(student.getComputer());
				cell.setCellStyle(styles[1]); break;
			case 15:
				cell.setCellValue(student.getComments());
				cell.setCellStyle(styles[1]); break;
			case 16:
				cell.setCellValue(student.getCommentsFromForm());
				cell.setCellStyle(styles[1]); break;
			}
		}
	}
	private void fillRowDownload(Row row, StudentMidterm student, CellStyle[] styles, int nbCol) {
		for (int colXL = 0; colXL < nbCol; colXL++) {
			Cell cell = row.createCell(colXL);
			switch (colXL) {
			case 0:
				cell.setCellValue(student.getId()); 
				cell.setCellStyle(styles[1]); break;
			case 1:
				cell.setCellValue(student.getTimeSubmission()); 
				cell.setCellStyle(styles[1]); break;
			case 2:
				cell.setCellValue(student.getUser()); 
				cell.setCellStyle(styles[1]); break;
			case 3: 
				cell.setCellValue(student.getExamDate());
				cell.setCellStyle(styles[3]); break;
			case 4:
				cell.setCellValue(student.getNameLast());
				cell.setCellStyle(styles[1]); break;
			case 5: 
				cell.setCellValue(student.getNameFirst());
				cell.setCellStyle(styles[1]); break;
			case 6:
				cell.setCellValue(student.getSid()); 
				cell.setCellStyle(styles[1]); break;
			case 7:
				cell.setCellValue(student.getPhone()); 
				cell.setCellStyle(styles[1]); break;
			case 8:
				cell.setCellValue(student.getEmail()); 
				cell.setCellStyle(styles[1]); break;
			case 9: 
				cell.setCellValue(student.getCourse());
				cell.setCellStyle(styles[2]); break;
			case 10:
				cell.setCellValue(student.getSection());
				cell.setCellStyle(styles[1]); break;
			case 11:
				String date = new Helper().getDateAsString(student.getExamStartTime());
				double d = DateUtil.convertTime(date);
				cell.setCellValue(d);
				cell.setCellStyle(styles[4]); break;
			case 12:
				date = new Helper().getDateAsString(student.getExamFinishTime());
				d = DateUtil.convertTime(date);
				cell.setCellValue(d);
				cell.setCellStyle(styles[4]); break;
			case 13:
				cell.setCellValue(student.getNameProf());
				cell.setCellStyle(styles[1]); break;
			case 14:
				cell.setCellValue(student.getEmailProf());
				cell.setCellStyle(styles[1]); break;
			case 15:
				cell.setCellValue(student.getExtraTime());
				cell.setCellStyle(styles[1]); break;
			case 16:
				cell.setCellValue(student.getStopwatch());
				cell.setCellStyle(styles[1]); break;
			case 17:
				cell.setCellValue(student.getComputer());
				cell.setCellStyle(styles[1]); break;
			case 18:
				cell.setCellValue(student.getCommentsFromForm());
				cell.setCellStyle(styles[1]); break;
			case 19:
				cell.setCellValue(student.getCampus());
				cell.setCellStyle(styles[1]); break;
			}
		}
	}
		
	/**
	 * Writes all data from the web page to the file "Midterms.xlsx"
	 * 
	 * @param 	listOfStudents list of all students registered for the midterms,
	 * @throws IOException
	 */
	public void export(ArrayList<StudentMidterm> listOfStudents) throws FileNotFoundException {
		Collections.sort(listOfStudents, new Student.DateExamComparator());
		
		String excelFileName = "F:\\Exams\\Midterms.xlsx";
		File file = new File(excelFileName);
		if (file.exists()) {
			int result = JOptionPane.showConfirmDialog(
					null,"The file already exists, overwrite it?", 
					"Warning",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// go on
			}
			else return;
		}
		FileOutputStream fos = new FileOutputStream(excelFileName);
		XSSFWorkbook wb = new XSSFWorkbook();
		
		CellStyle[] styles = getAllStyles(wb);
			
		Sheet sheet = wb.createSheet("Midterms");
		
		String[] headers = {"#", "Submission Time", "User", "Exam Date", "Family Name", "First Name", 
				"Student ID", "Phone", "Email", "Course", "Section", "Exam Start", "Exam Finish", 
				"Professor Name", "Professor Email", "Extra time", "Stopwatch", "PC", "Comments", "Campus"};
			
		int nbCol = 20;
		/* creating the first header row */
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < nbCol) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[0]);
		}
		
		for (int rowXL = 1, i = 0; i < listOfStudents.size(); i++) {
			StudentMidterm student = listOfStudents.get(i);
			row = sheet.createRow((short) rowXL++);
			fillRowDownload(row, student, styles, nbCol);
		}
		sheet.setColumnWidth(1, 15*255);
		sheet.setColumnWidth(2, 20*255); 
		sheet.setColumnWidth(4, 15*255); 
		sheet.setColumnWidth(5, 15*255); 
		sheet.setColumnWidth(6, 15*255);
		sheet.setColumnWidth(7, 20*255);
		sheet.setColumnWidth(8, 20*255);
		sheet.setColumnWidth(13, 15*255);
		sheet.setColumnWidth(14, 20*255);
		sheet.setColumnWidth(18, 25*255);
				
		sheet.createFreezePane(0, 1);
		
		try {
			wb.write(fos);
			fos.flush();
			fos.close();
			labelMidterm.append("-- File " + excelFileName + " has been created\n");
			labelMidterm.paintImmediately(labelMidterm.getVisibleRect());
		} catch (IOException e) {
			new Log(e.getStackTrace().toString());
		}
		
	}
	
	public void writeFinals(ArrayList<StudentFinal> list, String term) {
		Collections.sort(list, new Student.DateExamComparator());
		
		String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
		String fileFinals = "F:\\Exams\\" + newterm + " final exam master list.xlsx";
		//String fileFinals = newterm + " final exam master list.xlsx";
		setFile(fileFinals);
		
		if (file.exists()) {
			new Message("File " + fileFinals + " already exists");
		}
		else {
			try {
				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFCellStyle [] styles = getAllStyles(wb);
				writeSheet1(list, wb, styles);
				writeSheet2(list, wb, styles);
							
				FileOutputStream fos = new FileOutputStream(file);
				wb.write(fos);
				fos.close();
				labelFinal.append("-- File " + fileFinals + " has been created\n");
				labelFinal.paintImmediately(labelFinal.getVisibleRect());

			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void writeSheet1(ArrayList<StudentFinal> list, XSSFWorkbook wb, CellStyle[] styles) {
		Sheet sheet = wb.createSheet("Sheet1");
		String[] headers = {"Student ID", "Email", "Name", "Surname", 
				"Section", "Course ID", "Prof first", "Prof last", 
				"Date", "Start Time", "Time", "SW", "PC", "Other", "Warnings"};
					
		final int NB_COL = 15;
				
		/* creating the first header row */		
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[2]);
		}
		
		for (int rowXL = 1, i = 0; i < list.size(); i++) {
			StudentFinal student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			
			for (int col = 0; col < NB_COL; col++) {
				Cell cell = row.createCell(col);
				switch (col) {
				case 0:
					cell.setCellValue(student.getSidFull()); 
					cell.setCellStyle(styles[1]); break;
				case 1: 
					cell.setCellValue(student.getEmail());
					cell.setCellStyle(styles[1]); break;
				case 2:
					cell.setCellValue(student.getNameFirst());
					cell.setCellStyle(styles[1]); break;
				case 3: 
					cell.setCellValue(student.getNameLast());
					cell.setCellStyle(styles[1]); break;
				case 4: 
					String section = student.getSection();
					if (section.length() == 1) {
						section = "00" + section;
					}
					cell.setCellValue(section);
					cell.setCellStyle(styles[1]); break;
				case 5:
					cell.setCellValue(student.getCourse());
					cell.setCellStyle(styles[1]); break;
				case 6:
					cell.setCellValue(student.getNameProfFirst());
					cell.setCellStyle(styles[1]); break;
				case 7:
					cell.setCellValue(student.getNameProfLast());
					cell.setCellStyle(styles[1]); break;
				case 8:
					cell.setCellValue(student.getExamDate());
					cell.setCellStyle(styles[3]); break;
				case 9:
					String date = new Helper().getDateAsStringFinal(student.getExamStartTime());
					double d = DateUtil.convertTime(date);
					cell.setCellValue(d);
					cell.setCellStyle(styles[4]); break;
				case 10:
					cell.setCellValue(student.getExtraTime());
					cell.setCellStyle(styles[1]); break;
				case 11:
					cell.setCellValue(student.getStopwatch());
					cell.setCellStyle(styles[1]); break;
				case 12:
					cell.setCellValue(student.getComputer());
					cell.setCellStyle(styles[1]); break;
				case 13:
					cell.setCellValue(student.getComments());
					cell.setCellStyle(styles[1]); break;
				case 14:
					cell.setCellValue(student.getWarning());
					cell.setCellStyle(styles[1]); break;
				}
			}
		}
		
		sheet.autoSizeColumn(0);
		sheet.setColumnWidth(1, 16*255);
		sheet.setColumnWidth(2, 12*255); 
		sheet.setColumnWidth(3, 12*255); 
		sheet.autoSizeColumn(5);
		sheet.setColumnWidth(6, 12*255); 
		sheet.setColumnWidth(7, 12*255);
		sheet.setColumnWidth(13, 16*255); 
		sheet.setColumnWidth(14, 16*255); 
		
		sheet.createFreezePane(0, 1);		    
	}
	
	private void writeSheet2(ArrayList<StudentFinal> list, XSSFWorkbook wb, XSSFCellStyle[] styles) {
						
		Sheet sheet = wb.createSheet("by day");
		String[] headers = {"Date", "Name", "Surname", "Section", "Course ID", 
				"Prof First", "Prof Last", "Location" , "Start", "Finish", 
				"Extra", "SW", "PC", "Other", "Invigilator"};
					
		final int NB_COL = 15;
					
		/* creating the first header row */
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[2]);
		}
			
		for (int rowXL = 1, i = 0; i < list.size(); i++) {
			StudentFinal student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			
			for (int col = 0; col < NB_COL-1; col++) { 
				Cell cell = row.createCell(col);
				switch (col) {
				case 0:
					cell.setCellValue(student.getExamDate()); 
					cell.setCellStyle(styles[3]); break;
				case 1: 
					cell.setCellValue(student.getNameFirst());
					cell.setCellStyle(styles[1]); break;
				case 2:
					cell.setCellValue(student.getNameLast());
					cell.setCellStyle(styles[1]); break;
				case 3: 
					String section = student.getSection();
					cell.setCellValue(Integer.parseInt(section));
					cell.setCellStyle(styles[7]); break;
				case 4: 
					cell.setCellValue(student.getCourse());
					cell.setCellStyle(styles[1]); break;
				case 5:
					cell.setCellValue(student.getNameProfFirst());
					cell.setCellStyle(styles[6]); break;
				case 6: 
					cell.setCellValue(student.getNameProfLast());
					cell.setCellStyle(styles[6]); break;
				case 7:
					if (student.hasConflict()) {
						cell.setCellValue("Conflict");
					}
					cell.setCellStyle(styles[7]); break;
				case 8:
					String date = new Helper().getDateAsStringFinal(student.getExamStartTime());
					double d = DateUtil.convertTime(date);
					cell.setCellValue(d);
					if (student.timeChanged()) {
						cell.setCellStyle(styles[5]); break;
					}
					else {
						cell.setCellStyle(styles[4]); break;
					}
				case 9:
					date = new Helper().getDateAsStringFinal(student.getExamFinishTime());
					d = DateUtil.convertTime(date);
					cell.setCellValue(d);
					cell.setCellStyle(styles[4]); break;
				case 10:
					cell.setCellValue(student.getExtraTime());
					cell.setCellStyle(styles[7]); break;
				case 11:
					cell.setCellValue(student.getStopwatch());
					cell.setCellStyle(styles[7]); break;
				case 12:
					cell.setCellValue(student.getComputer());
					cell.setCellStyle(styles[7]); break;
				case 13:
					cell.setCellValue(student.getComments());
					cell.setCellStyle(styles[1]); break;
				}
			} // end of columns
		} // end of rows
									
		sheet.setColumnWidth(0, 12*255);
		sheet.setColumnWidth(1, 16*255);
		sheet.setColumnWidth(2, 16*255); 
		sheet.setColumnWidth(3, 12*255);
		sheet.setColumnWidth(4, 14*225);
		sheet.setColumnWidth(5, 16*225);
		sheet.setColumnWidth(6, 16*255); 
		sheet.setColumnWidth(7, 14*225);
		sheet.setColumnWidth(13, 25*255);
		sheet.setColumnWidth(14, 25*255);
		
		sheet.createFreezePane(0, 1);		    
	}
	/**
	 * Adds locations to the file.
	 *
	 * @param list list of students 
	 * @param file the file where locations should be added (the main file for finals)
	 * @throws FileNotFoundException 
	 */
	
	public void writeLocation(ArrayList<StudentFinal> list, File file) throws FileNotFoundException {
				
		try {
			//FileInputStream inp = new FileInputStream(file);
			XSSFWorkbook wb = StudentsFinalSec.wb;
					//new XSSFWorkbook(inp);
			
			CellStyle[] styles = getAllStyles(wb);
		
			
			Sheet sheet = wb.getSheetAt(1);
			String[] headers = {"Date", "Name", "Surname", "Section", "Course ID", 
					"Prof First", "Prof Last", "Location" , "Start", "Finish", 
					"Extra", "SW", "PC", "Other", "Invigilator"};
						
			final int NB_COL = 15;
						
			/* creating the first header row */
			Row row = sheet.createRow((short) 0);
			int colXL = 0;
			while (colXL < NB_COL) {
				Cell cell = row.createCell(colXL);
				cell.setCellValue(headers[colXL++]);
				cell.setCellStyle(styles[2]);
			}
			
			for (int rowXL = 1, i = 0; i < list.size(); i++) {
				StudentFinal student = list.get(i);
				row = sheet.createRow((short) rowXL++);
				
				// can save only those which are important and can be formatted
				for (int col = 0; col < NB_COL; col++) { 
					Cell cell = row.createCell(col);
					switch (col) {
					case 0:
						cell.setCellValue(student.getExamDate()); 
						cell.setCellStyle(student.getCell(col));
						break;
					case 1: 
						cell.setCellValue(student.getNameFirst());
						cell.setCellStyle(student.getCell(col));
						break;
					case 2:
						cell.setCellValue(student.getNameLast());
						cell.setCellStyle(student.getCell(col));
						break;
					case 3: 
						String section = student.getSection();
						cell.setCellValue(Integer.parseInt(section));
						cell.setCellStyle(student.getCell(col));
						break;
					case 4: 
						cell.setCellValue(student.getCourse());
						cell.setCellStyle(student.getCell(col));
						break;
					case 5:
						cell.setCellValue(student.getNameProfFirst());
						cell.setCellStyle(student.getCell(col));
						break;
					case 6:
						cell.setCellValue(student.getNameProfLast());
						cell.setCellStyle(student.getCell(col));
						break;
					case 7:
						cell.setCellValue(student.getLocation());
						cell.setCellStyle(student.getCell(col));
						break;
					case 8:
						String date = new Helper().getDateAsStringFinal(student.getExamStartTime());
						double d = DateUtil.convertTime(date);
						cell.setCellValue(d);
						cell.setCellStyle(student.getCell(col));
						break;
					case 9:
						date = new Helper().getDateAsStringFinal(student.getExamFinishTime());
						d = DateUtil.convertTime(date);
						cell.setCellValue(d);
						cell.setCellStyle(student.getCell(col));
						break;
					case 10:
						cell.setCellValue(student.getExtraTime());
						cell.setCellStyle(student.getCell(col));
						break;
					case 11:
						cell.setCellValue(student.getStopwatch());
						cell.setCellStyle(student.getCell(col));
						break;
					case 12:
						cell.setCellValue(student.getComputer());
						cell.setCellStyle(student.getCell(col));
						break;
					case 13:
						cell.setCellValue(student.getComments());
						cell.setCellStyle(student.getCell(col));
						break;
					case 14: 
						Invigilator inv = student.getInvigilator();
						if (inv != null) {
							cell.setCellValue(student.getInvigilator().getName());
							cell.setCellStyle(styles[1]); break;
						}
						
					}
				} // end of columns
			} // end of rows
										
		/*	sheet.autoSizeColumn(0);
			sheet.setColumnWidth(1, 16*255);
			sheet.setColumnWidth(2, 12*255); 
			sheet.setColumnWidth(3, 12*255); 
			sheet.autoSizeColumn(5);
			sheet.setColumnWidth(6, 12*255); 
			sheet.setColumnWidth(7, 12*255);
			*/
			sheet.createFreezePane(0, 1);
			
		
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			fos.close();
			labelFinal.append("-- File " + file.getName() + " has been updated\n");
			labelFinal.paintImmediately(labelFinal.getVisibleRect());
			
		}
		catch (FileNotFoundException e) {
			new Message("File " + file.getName() + " is currently in use.\nPlease restart when it's available.");
			throw e;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Adds two empty rows between two different dates of the exam.
	 * 
	 * @param term the term part of the file name
	 * @param exam	Midterm or Final
	 * @throws FileNotFoundException 
	 */
	public void addEmptyRows(String exam, String term) {
		labelEditor.append("-- Adding empty rows\n");
		labelEditor.paintImmediately(labelEditor.getVisibleRect());
		if (exam.equalsIgnoreCase("Midterm")) {
			//String filename = term + " exam schedule.xlsx";
			String filename = "F:\\Exams\\" + term + " exam schedule.xlsx";
			setFile(filename);
			if (! file.exists()) {
				JOptionPane.showMessageDialog(
						null, "File " + filename + " doesn't exist", 
						"Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			try {
				FileInputStream inp = new FileInputStream(file);
				XSSFWorkbook wb = new XSSFWorkbook(inp);
				Sheet sheet = wb.getSheetAt(0);		
			
				int rowEnd = sheet.getLastRowNum()+1;
							
				Row row = sheet.getRow(1);
				/* shift the first row - why? (I forgot) */
				sheet.shiftRows(1, rowEnd, 1);
				
				Cell cell = null;
				Date datePrevious = null;
				Date date = null;
				final int COL_NUM = 1; /* # of the 'date of the exam' column */
				
				cell = row.getCell(COL_NUM);
				if (cell == null) {
					
				}
				else {
					datePrevious = cell.getDateCellValue();
				}
										
				for (int rowNum = 2; rowNum < rowEnd; rowNum++) {
					row = sheet.getRow(rowNum);
					if (row == null) {
						// remove empty rows, a message that it is empty?
					}
					else {
						cell = row.getCell(COL_NUM);
						if (cell == null) {
							String mess = "The date is missing in the row " + (rowNum+1) +
									". Please check the file and remove empty rows first";
							new Message(mess);
							return;
						}
						else {
							date = cell.getDateCellValue();
							if (! (datePrevious.equals(date))) {
								sheet.shiftRows(rowNum, rowEnd, 2);
								rowEnd+=2;
								rowNum++;
							}
						}
						datePrevious = date;
					}
				}
				inp.close();
				FileOutputStream fos = new FileOutputStream(file);
			    wb.write(fos);
			    fos.close();
			    
			    labelEditor.append("-- File " + filename + " has been updated\n");
				labelEditor.paintImmediately(labelEditor.getVisibleRect());
				
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (exam.equalsIgnoreCase("Final")) {
			String[] split = term.split(" ");
			String termS = split[0];
			String month = "";
			if (termS.equalsIgnoreCase("Fall"))
				month = "December";
			else if (termS.equalsIgnoreCase("Winter"))
				month = "April";
			else
				month = "August";
			
			String filename = "F:\\Exams\\" + month + " " + split[1] + " final exam master list.xlsx";
			setFile(filename);
			if (! file.exists()) {
				JOptionPane.showMessageDialog(
						null, "File " + filename + " doesn't exist", 
						"Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				FileInputStream inp = new FileInputStream(file);
				XSSFWorkbook wb = new XSSFWorkbook(inp);
				Sheet sheet = wb.getSheetAt(1);		
			
				int rowEnd = sheet.getLastRowNum()+1;
							
				Row row = sheet.getRow(1);
			
				if (row == null) {
					removeEmptyRows(exam, term, false);
				}
				Cell cell = null;
				String locationPrevious = null;
				String location = null;
				final int COL_NUM = 7; /* # of the 'location' column */
				
				cell = row.getCell(COL_NUM);
				if (cell == null) {
					// error					
				}
				else {
					locationPrevious = cell.getStringCellValue();
				}
										
				for (int rowNum = 2; rowNum < rowEnd; rowNum++) {
					row = sheet.getRow(rowNum);
					if (row == null) {
						// remove empty rows, a message that it is empty?
					}
					else {
						cell = row.getCell(COL_NUM);
						if (cell == null) {
							// maybe to check if the whole row is empty, if yes remove empty?
							String mess = "The date is missing in the row " + (rowNum+1) +
									". Please check the file and remove empty rows first";
							new Message(mess);
							return;
						}
						else {
							location = cell.getStringCellValue();
							if (! (locationPrevious.equals(location))) {
								if ( (! (locationPrevious.equals("small") && location.equals("conf"))) && 
										(! (location.equals("small") && locationPrevious.equals("conf"))) ) {
									//System.out.println(locationPrevious + " "  + location + " " + rowNum);
									sheet.shiftRows(rowNum, rowEnd, 1);
									rowEnd+=1;
									rowNum++;
								}
								
							}
						}
						locationPrevious = location;
					}
				}
				inp.close();
				FileOutputStream fos = new FileOutputStream(file);
			    wb.write(fos);
			    fos.close();
			    
			    labelEditor.append("-- File " + filename + " has been updated\n");
				labelEditor.paintImmediately(labelEditor.getVisibleRect());
				
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Removes empty rows. 2 cases: a row is null or the first cell
	 * in the row (with id number) is blank.
	 * 
	 * @param term	the term part of the file name
	 * @param exam	Midterm or Final
	 * @param message	if <code>true</code> then the message will pop up		
	 */
	public void removeEmptyRows(String exam, String term, boolean message) {
		if (message) {
			labelEditor.append("-- Removing empty rows\n");
			labelEditor.paintImmediately(labelEditor.getVisibleRect());
		}
		if (exam.equalsIgnoreCase("Midterm")) {
			//String filename = term + " exam schedule.xlsx";
			String filename = "F:\\Exams\\" + term + " exam schedule.xlsx";
			setFile(filename);
			
			if (! file.exists()) {
				JOptionPane.showMessageDialog(
						null, "File " + filename + " doesn't exist", 
						"Message", JOptionPane.INFORMATION_MESSAGE);
			}
		
			try {
				FileInputStream inp = new FileInputStream(file);
				XSSFWorkbook wb = new XSSFWorkbook(inp);
				
				Sheet sheet = wb.getSheetAt(0);		
			
				int rowStart = sheet.getFirstRowNum();
				int rowEnd = sheet.getLastRowNum();
						
				for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) { 
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						sheet.shiftRows(rowNum+1, rowEnd, -1);						
						rowEnd--;														
						rowNum--;	
					}
					else { // row has been created, but it is blank 
						Cell cell = row.getCell(0);
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) { 
							sheet.shiftRows(rowNum+1, rowEnd, -1);						
							rowEnd--;														
							rowNum--;													
						}
					}
				}
				inp.close();
				// write into the file 
				FileOutputStream fos = new FileOutputStream(file);
			    wb.write(fos);
			    fos.close();
			    // done
			    if (message) {
			    	labelEditor.append("-- File " + filename + " has been updated\n");
					labelEditor.paintImmediately(labelEditor.getVisibleRect());
			    }
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (exam.equalsIgnoreCase("Final")) {
			String[] split = term.split(" ");
			String termS = split[0];
			String month = "";
			if (termS.equalsIgnoreCase("Fall"))
				month = "December";
			else if (termS.equalsIgnoreCase("Winter"))
				month = "April";
			else
				month = "August";
			
			String filename = "F:\\Exams\\" + month + " " + split[1] + " final exam master list.xlsx";
			setFile(filename);
			
			if (! file.exists()) {
				JOptionPane.showMessageDialog(
						null, "File " + filename + "doesn't exist", 
						"Message", JOptionPane.INFORMATION_MESSAGE);
			}
		
			try {
				FileInputStream inp = new FileInputStream(file);
				XSSFWorkbook wb = new XSSFWorkbook(inp);
				
				Sheet sheet = wb.getSheetAt(1);		
			
				int rowStart = sheet.getFirstRowNum();
				int rowEnd = sheet.getLastRowNum();
						
				for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) { 
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						sheet.shiftRows(rowNum+1, rowEnd, -1);						
						rowEnd--;														
						rowNum--;	
					}
					else { // row has been created, but it is blank 
						Cell cell = row.getCell(0);
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) { 
							sheet.shiftRows(rowNum+1, rowEnd, -1);						
							rowEnd--;														
							rowNum--;													
						}
					}
				}
				inp.close();
				FileOutputStream fos = new FileOutputStream(file);
			    wb.write(fos);
			    fos.close();
			    
			    if (message) {
			    	labelEditor.append("-- File " + filename + " has been updated\n");
					labelEditor.paintImmediately(labelEditor.getVisibleRect());
			    }
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}