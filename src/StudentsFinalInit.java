/* 
 * StudentsFinal.java
 * 
 * Created on 2013-06-17 12:05:43 PM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Gets information from Excel files, creates Students, adds them to a list
 * and finally writes data about all students registered for finals to the file
 * 
 * @author Olga Tsibulevskaya
 */
public class StudentsFinalInit {

	private ArrayList<StudentFinal>list = new ArrayList<StudentFinal>();	
	private JTextArea label = PanelFinals.label;
	/**
	 * Creates a list of students registered for finals and writes data into the file
	 * @throws FileNotFoundException 
	 */
	public StudentsFinalInit(String term) throws FileNotFoundException {
		setList(term);
		addProfInfo(term);
		
		label.append("-- Getting accommodations info\n");
	    label.paintImmediately(label.getVisibleRect());
	    ListOfAccommodations listAcc = new ListOfAccommodations();
	    listAcc.addAccommodations(list);
		
		label.append("-- Looking for conflicts\n");
    	label.paintImmediately(label.getVisibleRect());
    	findConflicts();
   
		
		label.append("-- Writing into Excel\n");
    	label.paintImmediately(label.getVisibleRect());
    	new Excel().writeFinals(list, term);
    	
    	label.append("-- Choose an option by clicking the button\n");
    	label.paintImmediately(label.getVisibleRect());
	}
	private void setList(String term) {
		final String osdReport = "OSD report " + term + ".xlsx";
		File fileOSDReport = new File("finals/" + osdReport);
		if (! fileOSDReport.exists())
			new Message("File " + osdReport + " doesn't exist");
		
		label.append("-- Getting info from " + osdReport + " file\n");
    	label.paintImmediately(label.getVisibleRect());
		
		try {
			FileInputStream fis = new FileInputStream(fileOSDReport);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
								
			boolean endOfFile = false;
			
			Row r = sheet.getRow(0);
			// start reading the file form the 1st row (exclude the header)
			for (int rowNum = 1; ! endOfFile; rowNum++) {
				StudentFinal student = new StudentFinal();
				r = sheet.getRow(rowNum);
				for (int i = 0; i < 7; i++) {
					switch (i) {
					case 0 :
						Cell cell = r.getCell(i);
						if (cell != null) {
							String sid = cell.getStringCellValue();
							student.setSidFull(sid);
							String[] array = sid.split(" ");
							student.setSid(array[1]);
						}
						else 
							endOfFile = true;
						break;
					case 1: 
						String nameF = r.getCell(i).getStringCellValue();
						student.setNameFirst(nameF); break;
					case 2:
						String nameL = r.getCell(i).getStringCellValue();
						student.setNameLast(nameL); break;
					case 3:
						int section = (int)r.getCell(i).getNumericCellValue();
						student.setSection(Integer.toString(section)); break;
					case 4:
						String course = r.getCell(i).getStringCellValue();
						student.setCourse(course); break;
					case 5:
						Date date = r.getCell(i).getDateCellValue();
						student.setExamDate(date); break;
					case 6:
						// gets the date in year 1899, has to increment to be after 1900
						// to write correctly in Excel
						Date time = r.getCell(i).getDateCellValue();
						Calendar cal = Calendar.getInstance();
						cal.setTime(time);
						cal.add(Calendar.YEAR, 100);
						time = cal.getTime();
						student.setExamStartTime(time); 
						break;
					}
					if (endOfFile)
						break;
				}
				if (! endOfFile) 
					list.add(student);
			}
			fis.close();
		}
		catch (IOException e) {
			new Message("File " + osdReport + " is not available.\nPlease check if it's opened");
			return;
		}
	}
	private void addProfInfo(String term) {
		// sort by course
		Collections.sort(list, new Student.CourseComparator());
		
		final String finalSchedule = "final schedule " + term + " for OSD and storemore.xlsx";
		File fileFinalSchedule = new File("finals/" + finalSchedule);
		
		if (! fileFinalSchedule.exists())
			new Message("File " + finalSchedule + " doesn't exist");
			
		label.append("-- Getting info from " + finalSchedule + " file\n");
	    label.paintImmediately(label.getVisibleRect());
			
		try {
			FileInputStream fis = new FileInputStream(fileFinalSchedule);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
						
			int rowNum = 1;
			for (StudentFinal s : list) {
				boolean found = false;
				// have to save only the first row of the course which may have different sections
				boolean first = true;
				// memorise # of the row with the course in case there are several rows
				// with the same course but different sections
				int rowCourse = 0; 
							
				while (s.getCourse().compareTo(sheet.getRow(rowNum).getCell(2).getStringCellValue()) >= 0) {
					Row r = sheet.getRow(rowNum);
					// in case the cell is empty, have to let know that the info is missing
					String noProf = null;
					String course = r.getCell(2).getStringCellValue();
					if (s.getCourse().equals(course)) {
						// compare sections
						int section = (int)r.getCell(1).getNumericCellValue();
						if (first) {
							rowCourse = rowNum;
							first = false;
						}
						if (s.getSection().equals(Integer.toString(section))) {
							Cell cell = r.getCell(4);
							if (cell != null) {
								s.setNameProfFirst(r.getCell(4).getStringCellValue());
								s.setNameProfLast(r.getCell(5).getStringCellValue());
								found = true;
								// if found, go to the first row of that course
								rowNum = rowCourse;
								break;
							}
							else {
								rowNum++;
								noProf = course; 
								s.setNameProfFirst("");
								s.setNameProfLast("");
							}
						}
						else {
							rowNum++;
						}
					}
					else if (s.getCourse().equals(noProf)) 
						break;
					else
						rowNum++;
				}
				if (! found) {
					s.setNameProfFirst("");
					s.setNameProfLast("");
				}
			} 
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void findConflicts() {
		Collections.sort(list, new Student.StudentDateComparator());
		for (int i = 0; i < list.size()-1; i++) {
			StudentFinal s = list.get(i);
			if (s.equals(list.get(i+1))) {
				s.setConflict(true);
				list.get(i+1).setConflict(true);
			}
		}
	}
	
}
