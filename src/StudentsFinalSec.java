/*
 * Created on Jul 6, 2013 6:32:47 PM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * Creates a list of all students for Finals, by getting the info from the Finals file,
 * since the initial file is supposed to be changed (exam dates due to conflicts, profs added, etc) 
 * 
 * @author Olga Tsibulevskaya
 */
public class StudentsFinalSec {
	
	private static ArrayList<StudentFinal>list = new ArrayList<StudentFinal>();	
	private File fileFinals;	
	/**
	 * Creates an instance of the class and sets the list	
	 * @param file
	 */
	public StudentsFinalSec(File file) {
		fileFinals = file;
		setList();
	}
	
	public static ArrayList<StudentFinal> getList() {
		return list;
	}
	/**
	 * The list is populated by the info taken from the file, created earlier 
	 */
	private void setList() {
		try {
			FileInputStream fis = new FileInputStream(fileFinals);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(1);
			
			if (sheet == null) {
				new Message("Sheet 'by day' doesn't exist");
				// exit?
			}
					
			Row r = sheet.getRow(0);
			int last = sheet.getLastRowNum();
			// start reading the file from the 1st row (exclude the header)
			for (int rowNum = 1; rowNum <= last; rowNum++) {
				StudentFinal student = new StudentFinal();
				r = sheet.getRow(rowNum);
				for (int i = 0; i < 13; i++) {
					switch (i) {
					case 0 :
						Cell cell = r.getCell(i);
						Date date = cell.getDateCellValue();
						student.setExamDate(date);
						break;
					case 1: 
						String nameF = r.getCell(i).getStringCellValue();
						student.setNameFirst(nameF); break;
					case 2:
						String nameL = r.getCell(i).getStringCellValue();
						student.setNameLast(nameL); break;
					case 3:
						String section = r.getCell(i).getStringCellValue();
						student.setSection(section); break;
					case 4:
						String course = r.getCell(i).getStringCellValue();
						student.setCourse(course); break;
					case 5:
						cell = r.getCell(i);
						if (cell != null && (int)((cell.getStringCellValue()).charAt(0)) != 10) {
							String prof = cell.getStringCellValue();
							String[] name = prof.split("\\r?\\n");
							student.setNameProfLast(name[0]); 
							student.setNameProfFirst(name[1]);
						}
						else { 
							student.setNameProfLast(""); // maybe better null?
							student.setNameProfFirst("");
						}
						break;
					case 6:
						// must be empty, location
						break;
					case 7:
						Date timeStart = r.getCell(i).getDateCellValue();
						student.setExamStartTime(timeStart); break;
					case 8:
						Date timeFinish = r.getCell(i).getDateCellValue();
						student.setExamFinishTime(timeFinish); break;
					case 9: 
						cell = r.getCell(i);
						if (cell != null) {
						//if (c.getCellType() == Cell.CELL_TYPE_STRING) {
							String extra = cell.getStringCellValue();
							student.setExtraTime(extra); break;
						}
					case 10:
						cell = r.getCell(i);
						if (cell != null) {
							String stopwatch = r.getCell(i).getStringCellValue();
							if (stopwatch.equalsIgnoreCase("sw"))
								student.setStopwatch("Yes"); 
						}
						break;
					case 11: 
						cell = r.getCell(i);
						if (cell != null) {
							String pc = r.getCell(i).getStringCellValue();
							if (pc.equalsIgnoreCase("pc"))
								student.setComputer("Yes"); 
						}
						break;
					case 12:
						cell = r.getCell(i);
						if (cell != null) {
							String comments = r.getCell(i).getStringCellValue();
							student.setComments(comments); 
						}
						break;
					}
				}
				list.add(student);
			}
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Adds rooms according to the students accommodations and PC demands.
	 */
	public void addLocation() {
		Collections.sort(list, new Student.DateExamComparator());
		
		File file = new File("rooms.xlsx");
		if (! file.exists()) {
			new Message("File " + file.getName() + " doesn't exist");
			return;
		}
		ListOfRooms rList = new ListOfRoomsFinal(file);
				
		Date currentDate = list.get(0).getExamDate();
		// get time sample set to 10:00 to check morning exams
		Calendar cal = Calendar.getInstance();
		cal.setTime(list.get(0).getExamStartTime());
		cal.set(Calendar.HOUR_OF_DAY, 10);
		cal.set(Calendar.MINUTE, 0);
		Date time = cal.getTime(); // first check if it's a morning exam
		
		int i = 0;
		Student s = list.get(i++);
		while (i < list.size()) {
			ListOfRooms clone = (ListOfRooms)rList.clone();
			 // while dates are the same and morning
			while (currentDate.compareTo(s.getExamDate()) == 0 && s.getExamStartTime().compareTo(time) <= 0) { 
				clone.allocateRoom(s);
				if (i < list.size())
					s = list.get(i++);
				else {
					i++;
					break;
				}
			}
			clone = (ListOfRooms)rList.clone();
			// the same day, afternoon
			while (currentDate.compareTo(s.getExamDate()) == 0) {
				clone.allocateRoom(s); 
				if (i < list.size())
					s = list.get(i++);
				else {
					i++;
					break;
				}
			}
			// change date to the next exam date
			currentDate = s.getExamDate();
		}
	}
}