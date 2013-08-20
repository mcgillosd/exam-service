import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	final String filename = "rooms_midterm.xlsx";
	boolean set = PanelTabs.set;
	
	public RoomMidterm(Row r) {
		super(r);
	}
	
	public boolean hasPlace(Date date, Date start, Date finish) {
		try {
			InputStream fis = new FileInputStream(filename);
			
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			
			
			CellStyle styleDate = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			styleDate.setDataFormat(df.getFormat("d-mmm"));
			
			CellStyle styleWrap = wb.createCellStyle();
			styleWrap.setWrapText(true);
			
			XSSFSheet sheet = wb.getSheet(id);
			// if a sheet for that room doesn't exist, it is free, create it
			if (sheet == null) {
			// fis.close();
				
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
			//	pkg.close();
				return true;
			} 
			// the sheet already exists, read and get the availability info for the given day
			else {
				int rowNum = 1;
				Row row = sheet.getRow(rowNum);
				int rowLast = sheet.getLastRowNum() + 1; // column of dates
								
				Cell cell = row.getCell(0); // cell with the date
				if (cell == null) {// can be? don't have to initialise and the first column is empty?
			//		fis.close();
					cell = row.createCell(0);
					cell.setCellValue(date);
					cell.setCellStyle(styleDate);
					
					cell = row.createCell(1);
					bookPlace(date, start, finish, cell);
					cell.setCellStyle(styleWrap);
					
					FileOutputStream out = new FileOutputStream(filename);
					wb.write(out);
					out.close();
				//	pkg.close();
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
									//fis.close();
									cell = row.createCell(colNum);
									bookPlace(date, start, finish, cell);
									cell.setCellStyle(styleWrap);
									sheet.setColumnWidth(colNum, 12*255);
									
									File file = new File(filename);
									if (! file.canRead() || ! file.exists())
										System.out.println("Can't: " + date + " " + start);
									FileOutputStream out = new FileOutputStream(filename);
									wb.write(out);
									out.close();
												
									return true;
								}
							}
							// no free places, look for free spots in time
							HashMap<Integer, LinkedList<Date>> map = new HashMap<Integer, LinkedList<Date>>();
							int col = 0;
							while (++col <= capacity) {
								cell = row.getCell(col);
								// looking for spots
								String times = cell.getStringCellValue();
								String[] tarray = times.split(" ");
								LinkedList<Date> schedule = getSchedule(tarray);
								map.put(col, schedule);
							}
							Map<Integer, LinkedList<Date>> sorted = sort(map);
						    						    
						    for (Map.Entry<Integer, LinkedList<Date>> entry : sorted.entrySet()) {
																
								LinkedList<Date> schedule = entry.getValue();
								if (addPlace(schedule, start, finish)) {
					//				fis.close();
									bookPlace(schedule, row.getCell(entry.getKey())); 
									cell.setCellStyle(styleWrap);
									
									int len = schedule.size()/2;
									row.setHeightInPoints((len*sheet.getDefaultRowHeightInPoints()));
									
								//	System.gc();
									File file = new File(filename);
									if (! file.canRead() || ! file.exists())
										System.out.println("Can't: " + date + " " + start);
									FileOutputStream out = new FileOutputStream(filename);
									wb.write(out);
									out.close();
						//			pkg.close();									
									return true;
								}
							}
							// didn't find a place
							fis.close();
							return false;
						}
						// no given date
						else if (date.compareTo(dateInFile) < 0) { // add new date
						//	fis.close();
							
							sheet.shiftRows(rowNum, rowLast-1, 1);
							row = sheet.createRow(rowNum);
							cell = row.createCell(0); // date
							cell.setCellValue(date);
							cell.setCellStyle(styleDate);
							
							cell = row.createCell(1); // book the 1st place
							bookPlace(date, start, finish, cell);
							cell.setCellStyle(styleWrap);
							sheet.setColumnWidth(1, 12*255);
							//System.gc();
							File file = new File(filename);
							if (! file.canRead() || !file.exists())
								System.out.println("Can't: " + date + " " + start);
							FileOutputStream out = new FileOutputStream(filename);
							wb.write(out);
							out.close();
							//pkg.close();				
							return true;
						}
						else {
							// go till the end of the rows
						}
						rowNum++;
					} // end of while for rows
					// didn't find the date, come to the end, then add new date
				
	//				fis.close();
					row = sheet.createRow(rowNum);
					cell = row.createCell(0); // date
					cell.setCellValue(date);
					cell.setCellStyle(styleDate);
					
					cell = row.createCell(1); // book the 1st place
					bookPlace(date, start, finish, cell);
					cell.setCellStyle(styleWrap);
					sheet.setColumnWidth(1, 12*255);
					File file = new File(filename);
					if (! file.canRead() || ! file.exists())
						System.out.println("Can't: " + date + " " + start);
					FileOutputStream out = new FileOutputStream(filename);
					wb.write(out);
					out.close();
					
				//	pkg.close();								
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(date + " " + start + " " + finish + " " + id);
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
		
		int len = schedule.size();
		
		if (datePlus30(finish).before(schedule.get(0))) { // the 1st
			schedule.add(0,start);
			schedule.add(1,finish);
			return true;
		}
		else if (start.after(datePlus30(schedule.get(len-1)))) { //the last
			schedule.add(len, finish);
			schedule.add(len, start);
			return true;
		}
		else {
			for (int i = 1; i < len-1; i+=2) {
				if (datePlus30(finish).before(schedule.get(i+1)) && start.after(datePlus30(schedule.get(i)))) { // between
					schedule.add(i+1,start);
					schedule.add(i+2,finish);
					return true;
				}
			}
		}
		return false;
	}
	private Date datePlus30(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 30);
		Date timePlus = cal.getTime();
		return timePlus;
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
	private Map<Integer, LinkedList<Date>> sort(Map<Integer, LinkedList<Date>> map) {
		List<Entry<Integer, LinkedList<Date>>> list = new LinkedList<Entry<Integer, LinkedList<Date>>>(map.entrySet());
	
		Collections.sort(list, new Comparator<Entry<Integer, LinkedList<Date>>>() {
			public int compare(Entry<Integer, LinkedList<Date>> a, Entry<Integer, LinkedList<Date>> b) {
				return ((Integer)(a.getValue()).size()).compareTo((Integer)((b.getValue()).size()));
			}
		});
		Map<Integer, LinkedList<Date>> sorted = new LinkedHashMap<Integer, LinkedList<Date>>();
		for (Entry<Integer, LinkedList<Date>> entry : list)
			sorted.put(entry.getKey(), entry.getValue());
		return sorted;
	}
} 		