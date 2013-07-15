import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
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
	
	public RoomMidterm(Row r) {
		super(r);
		System.out.println("rooms_midterm");
		
		try {
			FileInputStream fis = new FileInputStream("rooms_midterm.xlsx");
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheet(id);
			// if a sheet for that room doesn't exist, it is free, create it
			if (sheet == null) {
				System.out.println("create sheet " + id);
				sheet = wb.createSheet(id);
				Row row = sheet.createRow((short) 0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Dates");
				int rowNum = 1;
				while (rowNum <= capacity) {
					row = sheet.createRow(rowNum);
					cell = row.createCell(0);
					cell.setCellValue(rowNum++);
				}
				FileOutputStream out = new FileOutputStream("rooms_midterm.xlsx");
				wb.write(out);
				out.close();
			} 
			// already exists, read and get the availability info for each day and each place
			else {
				int rowNum = 0;
				Row row = sheet.getRow(rowNum++);
				short colNum = row.getLastCellNum(); // row of dates
				System.out.println("columns: " + colNum);
				short col = 1;
				while (col < colNum) {
					Cell cell = row.getCell(col);
					if (cell != null) { // there is a date
						free = false;
						System.out.println(cell.getDateCellValue());
						Date date = cell.getDateCellValue();
						LinkedList<PlaceSchedule> ps = new LinkedList<PlaceSchedule>();
						while (rowNum <= capacity) {
							row = sheet.getRow(rowNum++);
							cell = row.getCell(col);
							if (cell != null) { // the place reserved for that date
								String times = cell.getStringCellValue();
								String[] tarray = times.split(" ");
								PlaceSchedule place = new PlaceSchedule(rowNum-1, tarray);
								ps.add(place);
							}
						}
						map.put(date, ps);
					}
					col++;
				}
			}
			fis.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean free() {
		return free;
	}
	public Map<Date, LinkedList<PlaceSchedule>> getMap() {
		return map;
	}
} 		