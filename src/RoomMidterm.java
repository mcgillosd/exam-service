import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Created on Jul 10, 2013 11:13:04 AM
 */

/**
 * @author Olga Tsibulevskaya
 *
 */
public class RoomMidterm extends Room {
	/** for each day it's own list of places and availabilities*/
	private Map<Date, LinkedList<PlaceSchedule>> map = new HashMap<Date, LinkedList<PlaceSchedule>>();
	private boolean free = true;
	private int place;
	final String filename = "rooms_midterm.xlsx";
	
	public RoomMidterm(Row r) {
		super(r);
	}
	
	public boolean hasPlace(Date date, Date start, Date finish) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			
			CellStyle styleDate = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			styleDate.setDataFormat(df.getFormat("d-mmm"));
			
			//CellStyle styleVertical = wb.createCellStyle();
			//styleVertical.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			CellStyle styleWrap = wb.createCellStyle();
			styleWrap.setWrapText(true);
			
			XSSFSheet sheet = wb.getSheet(id);
			// if a sheet for that room doesn't exist, it is free, create it
			if (sheet == null) {
				fis.close();
				
				System.out.println("create sheet " + id);
				sheet = wb.createSheet(id);
				Row row = sheet.createRow((short) 0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Dates");
				
				int cellNum = 1; // write places on the first row
				while(cellNum <= capacity) {
					cell = row.createCell(cellNum);
					cell.setCellValue(cellNum++);
				}
							
				row = sheet.createRow(1);
				cell = row.createCell(0); // date
				cell.setCellValue(date);
				cell.setCellStyle(styleDate);
				
				cell = row.createCell(1); // row 1 for the 1st place 
				bookPlace(date, start, finish, cell);
				cell.setCellStyle(styleWrap);
				
				sheet.setColumnWidth(1, 12*255);
				
				FileOutputStream out = new FileOutputStream(filename);
				wb.write(out);
				out.close();
				
				return true;
			} 
			// the sheet already exists, read and get the availability info for the given day
			else {
				int rowNum = 1;
				Row row = sheet.getRow(rowNum);
				int rowLast = sheet.getLastRowNum() + 1; // column of dates
				System.out.println("last row: " + rowLast);
				
				Cell cell = row.getCell(0);
				// cell with date
				if (cell == null) {// can be? don't have to initialise and the first column is empty?
					System.out.println("2d option");
					fis.close();
					cell = row.createCell(0);
					cell.setCellValue(date);
					cell.setCellStyle(styleDate);
					
					cell = row.createCell(1);
					bookPlace(date, start, finish, cell);
					cell.setCellStyle(styleWrap);
					
					FileOutputStream out = new FileOutputStream(filename);
					wb.write(out);
					out.close();
					
					return true;
				}
				else { // should be here
					while (rowNum < rowLast) { // rowNum == 1
						row = sheet.getRow(rowNum);
						cell = row.getCell(0);
						// given date exist
						Date dateInFile = cell.getDateCellValue();
						if (date.compareTo(dateInFile) == 0) {
							short colNum = 0;
							while (++colNum <= capacity) {
								cell = row.getCell(colNum);
								// looking for empty cells
								if (cell == null) { // good! available
									fis.close();
									cell = row.createCell(colNum);
									bookPlace(date, start, finish, cell);
									cell.setCellStyle(styleWrap);
									sheet.setColumnWidth(colNum, 12*255);
									
									FileOutputStream out = new FileOutputStream(filename);
									wb.write(out);
									out.close();
																		
									return true;
								}
							}
							// no free places, look for free spots in time
							colNum = 0;
							while (++colNum <= capacity) {
								cell = row.getCell(colNum);
								// looking for spots
								String times = cell.getStringCellValue();
								String[] tarray = times.split(" ");
								LinkedList<Date> schedule = getSchedule(tarray);
								if (addPlace(schedule, start, finish)) {
									fis.close();
									bookPlace(schedule, cell); 
									cell.setCellStyle(styleWrap);
									
									int len = schedule.size()/2;
									row.setHeightInPoints((len*sheet.getDefaultRowHeightInPoints()));
									
									FileOutputStream out = new FileOutputStream(filename);
									wb.write(out);
									out.close();
																		
									return true;
								}
							}
							// didn't find a place
							fis.close();
							return false;
						}
						// no given date
						else if (date.compareTo(dateInFile) < 0) { // add new date
							System.out.println("date less " + rowNum);
							fis.close();
							
							sheet.shiftRows(rowNum, rowLast-1, 1);
							row = sheet.createRow(rowNum);
							cell = row.createCell(0); // date
							cell.setCellValue(date);
							cell.setCellStyle(styleDate);
							
							cell = row.createCell(1); // book the 1st place
							bookPlace(date, start, finish, cell);
							cell.setCellStyle(styleWrap);
							sheet.setColumnWidth(1, 12*255);
							
							FileOutputStream out = new FileOutputStream(filename);
							wb.write(out);
							out.close();
																
							return true;
						}
						else {
							// go till the end of the rows
						}
						rowNum++;
					} // end of while for rows
					// didn't find the date, come to the end, then add new date
					System.out.println("date more " + rowNum);
					fis.close();
					row = sheet.createRow(rowNum);
					cell = row.createCell(0); // date
					cell.setCellValue(date);
					cell.setCellStyle(styleDate);
					
					cell = row.createCell(1); // book the 1st place
					bookPlace(date, start, finish, cell);
					cell.setCellStyle(styleWrap);
					sheet.setColumnWidth(1, 12*255);
					
					FileOutputStream out = new FileOutputStream(filename);
					wb.write(out);
					out.close();
														
					return true;
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	private LinkedList<Date> getSchedule(String[] array) {
		LinkedList<Date> listSchedule = new LinkedList<Date>();
		if (array.length != 0) {
			for (int i = 0; i < array.length; i++) {
				String[] time = array[i].split("-"); // divide start time from finish time
				try {
					Date date = new SimpleDateFormat("HH:mm").parse(time[0]);
					listSchedule.add(date);
					date = new SimpleDateFormat("HH:mm").parse(time[1]);
					listSchedule.add(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		else 
			listSchedule = null;
		return listSchedule;
	}
	
	private boolean addPlace(LinkedList<Date> schedule, Date start, Date finish) {
		for (int i = 0; i < schedule.size(); i++)
			System.out.println("Schedule: " + schedule.get(i));
		System.out.println(start + " " + finish);
		
		int len = schedule.size();
		if (finish.before(schedule.get(0))) { // the 1st
			schedule.add(0,start);
			schedule.add(1,finish);
			return true;
		}
		else if (start.after(schedule.get(len-1))) { //the last
			schedule.add(len, finish);
			schedule.add(len, start);
			return true;
		}
		else {
			for (int i = 1; i < len-1; i+=2) {
				if (finish.before(schedule.get(i+1)) && start.after(schedule.get(i))) { // between
					schedule.add(i+1,start);
					schedule.add(i+2,finish);
					return true;
				}
			}
		}
		return false;
	}
	private void bookPlace(LinkedList<Date> schedule, Cell cell) {
		String time = "";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		for (int i = 0; i < schedule.size(); i++) {
			String timeS = df.format(schedule.get(i));
			if (i % 2 == 0)
				time += timeS + "-";
			else 
				time += timeS + " ";
		}
		cell.setCellValue(time);
	}
	// don't need the date
	private void bookPlace(Date date, Date start, Date finish, Cell cell) {
		if (cell.getCellType() == 1) {// not blank, string
			
		}
		else { // blank
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			String startS = df.format(start);
			String finishS = df.format(finish);
			String total = startS + "-" + finishS;
			cell.setCellValue(total);
		}
	}
	/*private int dateToInt(Date time) {
		System.out.println(time);
		SimpleDateFormat df = new SimpleDateFormat("HH");
		int hour = Integer.parseInt(df.format(time));
		df = new SimpleDateFormat("mm");
		int min = Integer.parseInt(df.format(time));
		int timeInMin = hour*100 + min;
		return timeInMin;
	}*/
	public boolean free() {
		return free;
	}
	public Map<Date, LinkedList<PlaceSchedule>> getMap() {
		return map;
	}
} 		