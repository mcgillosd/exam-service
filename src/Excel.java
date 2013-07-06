/*
 * Excel.java
 * 
 * Created on 2013-06-11 12:13:25 PM
 */
//import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
	private final int NB_COL = 17;
	/**
	 * Creates an empty container
	 */
	public Excel()  {
		// default 
	}
	public void setFile(String filename) {
		file = new File(filename);
	}
	/**
	 * Creates an empty Excel file with headers
	 * 
	 * @param name name of the file to be created, format of the term
	 * @see Term 
	 */
	public void create(String name) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		Font font = setCustomFont(workbook, "Arial", 10, true);
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		XSSFSheet sheet = workbook.createSheet(name);
		
		String[] headers = {"#", "Date", "Family name", "First name", "Course number", 
				"Section", "Exam location", "Start", "Finish", "Length", 
				"Professor name", "Professor email", "Extra time", 
				"Stopwatch", "PC", "Other", "Invigilator"};
	
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		// write headers
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(style);
		}
		String filename = name + " exam schedule.xlsx";
		try {
			File file = new File(filename);
			if (file.exists()) {
				int result = JOptionPane.showConfirmDialog(
						null,"The file already exists, overwrite it?", 
						"Warning",JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					FileOutputStream out = new FileOutputStream(file);
					workbook.write(out);
					out.close();
					String message = "File " + filename + " has been created";
					new Message(message);
                } 
				else {
					// nothing
				}
			}
			else {
				FileOutputStream out = new FileOutputStream(file);
				workbook.write(out);
				out.close();
				String message = "File " + filename + " has been created";
				new Message(message);
			}
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
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
	 */
	public void update(ArrayList<Student> list, String term) throws IOException {
		// sort the list by the date of the exam
		Collections.sort(list, new Student.DateExamComparator());
	
		String filename = term + " exam schedule.xlsx";
		setFile(filename);
		
		if (! file.exists()) {
			create(term);
		}

		try {
			FileInputStream fis = new FileInputStream(file);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
		
			CellStyle[] styles = getAllStyles(wb);
			
			XSSFSheet sheet = wb.getSheetAt(0);
			
			if (sheet.getLastRowNum() < 2) { // write to the empty file
				for (int rowXL = 1, i = 0; i < list.size(); i++) {
					Student student = list.get(i);
					Row row = sheet.createRow((short) rowXL++);
					fillRow(row, student, styles);
				}
				modifyWidth(sheet);
				sheet.createFreezePane(0, 1);			
			}
			else { // file already contains entries 
				int index = 0; // for the list
				Student student = list.get(index);
			
				Date dateToAdd = student.getDateExam(); // date of the entry to be added
				Date dateInFile = null; // existing in the file dates.
						
				int rowEnd = sheet.getLastRowNum();
				System.out.println(rowEnd);
				// start reading the file form the 1st row (exclude the header)
				for (int rowNum = 1; index < list.size(); rowNum++) { // || rowNum < endRow
					System.out.println(rowNum);
					Row r = sheet.getRow(rowNum);
					if (r == null) {
						// in which cases?
					}
					Cell cell = r.getCell(1, Row.RETURN_BLANK_AS_NULL); 
					if (cell == null) {
						// in which cases?
					}
					else {
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							dateInFile = cell.getDateCellValue();
														
							if (dateToAdd.compareTo(dateInFile) <= 0) {// if the same or less, then add above
								// check if id is the same, then the entry already exists
								if (dateToAdd.compareTo(dateInFile) == 0) {
									Cell cellPrev = r.getCell(0);
									int idInFile = (int)cellPrev.getNumericCellValue();
									int idToAdd = student.getId();
									// the same entries, do not enter twice, takes more time, but
									// double control if id has been lost, id.txt corrupted, etc...
									if (idInFile == idToAdd) {
										if (++index < list.size()) {
											student = list.get(index);
											dateToAdd = student.getDateExam();
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
									// get the next student
									if (++index < list.size()) {
										student = list.get(index);
										dateToAdd = student.getDateExam();
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
				} // end of the spreadsheet table
			} // end of else
			
			fis.close();
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			fos.flush();
			fos.close();
			String message = "File " + file.getName() + " has been updated";
			new Message(message);
		}
		catch (IOException e) {
			e.printStackTrace();
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
	private CellStyle[] getAllStyles(Workbook wb) {
		CellStyle[] styles = new CellStyle[6];
		
		CellStyle styleHeader = wb.createCellStyle();
		Font fontHeader = setCustomFont(wb, "Arial", 11, true);
		styleHeader.setFont(fontHeader);
		styles[0] = styleHeader;
		
		CellStyle styleNormal = wb.createCellStyle();
		Font fontNormal = setCustomFont(wb, "Calibri", 11, false);
		styleNormal.setFont(fontNormal);
		styles[1] = styleNormal;
		
		CellStyle styleBold = wb.createCellStyle();
		Font fontBold = setCustomFont(wb, "Calibri", 11, true);
		styleBold.setFont(fontBold);
		styles[2] = styleBold;
		
		CellStyle styleDate = wb.createCellStyle();
		DataFormat df = wb.createDataFormat();
		styleDate.setFont(fontBold);
		styleDate.setDataFormat(df.getFormat("d-mmm"));
		styles[3] = styleDate;
		
		CellStyle styleTime = wb.createCellStyle();
		df = wb.createDataFormat();
		styleTime.setFont(fontBold);
		styleTime.setDataFormat(df.getFormat("h:mm"));
		styles[4] = styleTime;
		
		CellStyle timeChanged = wb.createCellStyle();
		df = wb.createDataFormat();
		timeChanged.setFont(fontBold);
		timeChanged.setDataFormat(df.getFormat("h:mm"));
		timeChanged.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
		timeChanged.setFillPattern(CellStyle.BIG_SPOTS);
		styles[5] = timeChanged;
		return styles;
	}
	/* Populates a row */
	private void fillRow(Row row, Student student, CellStyle[] styles) {
		for (int colXL = 0; colXL < NB_COL-2; colXL++) {
			Cell cell = row.createCell(colXL);
			switch (colXL) {
			case 0:
				cell.setCellValue(student.getId()); 
				cell.setCellStyle(styles[1]); break;
			case 1: 
				cell.setCellValue(student.getDateExam());
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
				cell.setCellValue(student.getExamStartTime());
				cell.setCellStyle(styles[4]); break;
			case 8:
				cell.setCellValue(student.getExamFinishTime());
				cell.setCellStyle(styles[4]); break;
			case 9:
				cell.setCellValue(student.getExamLength());
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
			}
		}
	}

	/* Modifies the width of columns */
	private void modifyWidth(Sheet sheet) {
		sheet.setColumnWidth(2, 15*255); // last name
		sheet.setColumnWidth(3, 15*255); // first name
		sheet.setColumnWidth(4, 15*255); // course number
		sheet.setColumnWidth(6, 20*255);
		sheet.setColumnWidth(9, 15*255);
		sheet.setColumnWidth(10, 25*255);
		sheet.setColumnWidth(11, 25*255);
		sheet.setColumnWidth(12, 15*255);
	}
	/**
	 * Writes all data from the web page to the file "Midterms.xlsx"
	 * 
	 * @param 	list list of all students registered for the midterms,
	 * @throws IOException
	 */
	public void export(ArrayList<Student> list) throws IOException {
		Collections.sort(list, new Student.DateExamComparator());
		
		String excelFileName = "Midterms.xlsx";
		
		FileOutputStream fos = new FileOutputStream(excelFileName);	
		XSSFWorkbook wb = new XSSFWorkbook();
	
		CellStyle[] styles = getAllStyles(wb);
		
		Sheet sheet = wb.createSheet("MY_SHEET");
		String[] headers = {"#", "Date", "Family name", "First name", "Course number", "Section",
					"Exam location", "Start", "Finish", "Length", "Professor name", "Professor email",
					"Extra time", "Stopwatch", "PC", "Other", "Invigilator"};
		
		final int NB_COL = 17;
		// creating the first header row
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[0]);
		}
		
		for (int rowXL = 1, i = 0; i < list.size(); i++) {
			Student student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			fillRow(row, student, styles);
		}
		modifyWidth(sheet);
		
		sheet.createFreezePane(0, 1);
		
		wb.write(fos);
		fos.flush();
		fos.close();
		String message = "File " + excelFileName + " has been created";
		new Message(message);
	}
	/**
	 * Adds two empty rows between two different dates of the exam.
	 * 
	 * @param term the term part of the file name
	 * @param frame	frame to show the message dialog
	 */
	public void addEmptyRows(String term, JFrame frame) {
		String filename = term + " exam schedule.xlsx";
		setFile(filename);
		
		if (! file.exists()) {
			JOptionPane.showMessageDialog(
					frame, "File " + filename + "doesn't exist", 
					"Message", JOptionPane.INFORMATION_MESSAGE);
		}
		try {
			FileInputStream inp = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(inp);
			Sheet sheet = wb.getSheetAt(0);		
		
			int rowEnd = sheet.getLastRowNum()+1;
						
			Row row = sheet.getRow(1);
			// shift the first row
			sheet.shiftRows(1, rowEnd, 1);
			
			Cell cell = null;
			Date datePrevious = null;
			Date date = null;
			final int COL_NUM = 1; // # of the 'date of the exam' column
			
			cell = row.getCell(COL_NUM);
			if (cell == null) {
				// remove empty rows first 
				removeEmptyRows(term, frame, false);
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
						// maybe to check if the whole row is empty, if yes remove empty?
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
			String message = "File " + filename + "has been updated";
			new Message(message);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	 
	}
	/**
	 * Removes empty rows. 2 cases: a row is null or the first cell
	 * in the row (with id number) is blank.
	 * 
	 * @param term	the term part of the file name
	 * @param frame	frame for the message dialog
	 * @param message	if <code>true</code> then the message will pop up		
	 */
	public void removeEmptyRows(String term, JFrame frame, boolean message) {
		String filename = term + " exam schedule.xlsx";
		setFile(filename);
		
		if (! file.exists()) {
			JOptionPane.showMessageDialog(
					frame, "File " + filename + "doesn't exist", 
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
		    	String mess = "File " + filename + " has been updated";
		    	new Message(mess);
		    }
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeFinals(ArrayList<Student> list, String term) {
		Collections.sort(list, new Student.DateExamComparator());
		
		String newterm = Character.toUpperCase(term.charAt(0)) + term.substring(1);  
		String fileFinals = newterm + " final exam master list.xlsx";
		setFile(fileFinals);
		
		if (file.exists()) {
			new Message("File " + fileFinals + " already exists");
		}
		else {
			try {
				XSSFWorkbook wb = new XSSFWorkbook();
				CellStyle[] styles = getAllStyles(wb);
				writeSheet1(list, wb, styles);
				writeSheet2(list, wb, styles);
				writeListProf(list, wb);
				
				FileOutputStream fos = new FileOutputStream(file);
				wb.write(fos);
				fos.close();
				String message = "File " + fileFinals + " has been created";
				new Message(message);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void writeSheet1(ArrayList<Student> list, XSSFWorkbook wb, CellStyle[] styles) {
		Sheet sheet = wb.createSheet("Sheet1");
		String[] headers = {"Student ID", "Email", "Name", "Surname", 
				"Section", "Course ID", "Prof first", "Prof last", 
				"Date", "Start Time", "Time", "SW", "PC", "Other", "Warnings"};
					
		final int NB_COL = 15;
				
		// creating the first header row
		
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[2]);
		}
		
		for (int rowXL = 1, i = 0; i < list.size(); i++) {
			Student student = list.get(i);
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
					cell.setCellValue(student.getDateExam());
					cell.setCellStyle(styles[3]); break;
				case 9:
					cell.setCellValue(student.getExamStartTime());
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
	
	private void writeSheet2(ArrayList<Student> list, XSSFWorkbook wb, CellStyle[] styles) {
						
		Sheet sheet = wb.createSheet("by day");
		String[] headers = {"Date", "Name", "Surname", "Section", "Course ID", 
				"Prof last", "Location" , "Start", "Finish", 
				"Extra", "SW", "PC", "Other", "Invigilator"};
					
		final int NB_COL = 14;
					
		// creating the first header row
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			cell.setCellStyle(styles[2]);
		}
			
		for (int rowXL = 1, i = 0; i < list.size(); i++) {
			Student student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			
			for (int col = 0; col < NB_COL-1; col++) { //minus invigilator
				Cell cell = row.createCell(col);
				switch (col) {
				case 0:
					cell.setCellValue(student.getDateExam()); 
					cell.setCellStyle(styles[3]); break;
				case 1: 
					cell.setCellValue(student.getNameFirst());
					cell.setCellStyle(styles[1]); break;
				case 2:
					cell.setCellValue(student.getNameLast());
					cell.setCellStyle(styles[1]); break;
				case 3: 
					String section = student.getSection();
					if (section.length() == 1) {
						section = "00" + section;
					}
					cell.setCellValue(section);
					cell.setCellStyle(styles[1]); break;
				case 4: 
					cell.setCellValue(student.getCourse());
					cell.setCellStyle(styles[1]); break;
				case 5:
					cell.setCellValue(student.getNameProfLast());
					cell.setCellStyle(styles[1]); break;
				case 6:
					if (student.hasConflict()) {
						cell.setCellValue("Conflict");
					}
					else
						cell.setCellValue(student.getLocation());
					cell.setCellStyle(styles[1]); break;
				case 7:
					if (student.timeChanged()) {
						cell.setCellValue(student.getExamStartTime());
						cell.setCellStyle(styles[5]); break;
					}
					else {
						cell.setCellValue(student.getExamStartTime());
						cell.setCellStyle(styles[4]); break;
					}
				case 8:
					cell.setCellValue(student.getExamFinishTime());
					cell.setCellStyle(styles[4]); break;
				case 9:
					cell.setCellValue(student.getExtraTime());
					cell.setCellStyle(styles[1]); break;
				case 10:
					cell.setCellValue(student.getStopwatch());
					cell.setCellStyle(styles[1]); break;
				case 11:
					cell.setCellValue(student.getComputer());
					cell.setCellStyle(styles[1]); break;
				case 12:
					cell.setCellValue(student.getComments());
					cell.setCellStyle(styles[1]); break;
				}
			} // end of columns
		} // end of rows
									
		sheet.autoSizeColumn(0);
		sheet.setColumnWidth(1, 16*255);
		sheet.setColumnWidth(2, 12*255); 
		sheet.setColumnWidth(3, 12*255); 
		sheet.autoSizeColumn(5);
		sheet.setColumnWidth(6, 12*255); 
		sheet.setColumnWidth(7, 12*255);
		
		sheet.createFreezePane(0, 1);		    
	}
	
	private void writeListProf(ArrayList<Student> list, XSSFWorkbook wb) {
		Collections.sort(list, new Student.ProfComparator());
		Sheet sheet = wb.createSheet("lists by profs");
		String[] headers = {"Prof name", "List of students" };
					
		final int NB_COL = 2;
					
		// creating the first header row
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
			colXL++; // skip one, maybe for emails
		}
		
		CellStyle styleVertical = wb.createCellStyle();
		styleVertical.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		CellStyle styleWrap = wb.createCellStyle();
		styleWrap.setWrapText(true);
		
		int rowXL = 1;
		int i = 0;
		while (i < list.size()) {
			boolean noProf = false;
			Student student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			Cell cell = row.createCell(0);
			if (student.getNameProfLast() != "")
				cell.setCellValue(student.getNameProfFirst() + " " + student.getNameProfLast());
			else {
				cell.setCellValue("No prof info");
				noProf = true;
			}
			cell.setCellStyle(styleVertical);
			
			
			/*cell = row.createCell(1);
			if (! noProf) {
				String email = new ProfMail(student).getEmail();
				cell.setCellValue(email);
			}
			else
				cell.setCellValue("email not found");
			cell.setCellStyle(styleVertical);*/
			
			cell = row.createCell(2);
			cell.setCellValue(student.getNameLast() + " " + student.getNameFirst() + " (" + student.getCourse() + ")");
			int count = 1;
			if (noProf) {
				while (++i < list.size() && student.getCourse().equals(list.get(i).getCourse())) {
					count++;
					cell.setCellValue(cell.getStringCellValue() + "\n" + list.get(i).getNameLast() + " " + 
							list.get(i).getNameFirst() + " (" + list.get(i).getCourse() + ")");
				}
			}
			else {
				while (++i < list.size() && student.equalProf(list.get(i))) {
					count++;
					cell.setCellValue(cell.getStringCellValue() + "\n" + list.get(i).getNameLast() + " " + 
							list.get(i).getNameFirst() + " (" + list.get(i).getCourse() + ")");
				}
			}
			cell.setCellStyle(styleWrap);
			row.setHeight((short)(count*230));
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
	}
}