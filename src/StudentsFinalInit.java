/* 
 * StudentsFinal.java
 * 
 * Created on 2013-06-17 12:05:43 PM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.swing.JLabel;

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

	private ArrayList<Student>list = new ArrayList<Student>();	
	private JLabel label;
	/**
	 * Creates a list of students registered for finals and writes data into the file
	 */
	public StudentsFinalInit(JLabel label, String term) {
		this.label = label;
		setList(term);
		addProfInfo(term);
		getAccomodations();
		label.setText("Looking for conflicts");
    	label.paintImmediately(label.getVisibleRect());
    	findConflicts();
    //	label.setText("Allocating rooms");
    	//label.paintImmediately(label.getVisibleRect());
    	//addLocation();
		
		label.setText("Writing into Excel");
    	label.paintImmediately(label.getVisibleRect());
    	new Excel().writeFinals(list, term);
    	//label.setText("Adding profs mails");
    	//label.paintImmediately(label.getVisibleRect());
    	
    	//new ProfMail(list.get(0));	
    	label.setText("Choose an option and click the button");
    	label.paintImmediately(label.getVisibleRect());
	}
	private void setList(String term) {
		final String osdReport = "OSD report " + term + ".xlsx";
		File fileOSDReport = new File("finals/" + osdReport);
		if (! fileOSDReport.exists())
			new Message("File " + osdReport + " doesn't exist");
		
		label.setText("Getting info from " + osdReport + " file");
    	label.paintImmediately(label.getVisibleRect());
		
		try {
			FileInputStream fis = new FileInputStream(fileOSDReport);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
								
			boolean endOfFile = false;
			
			Row r = sheet.getRow(0);
			// start reading the file form the 1st row (exclude the header)
			for (int rowNum = 1; ! endOfFile; rowNum++) {
				Student student = new Student();
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
						student.setDateExam(date); break;
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
			e.printStackTrace();
		}
	}
	private void addProfInfo(String term) {
		// sort by course
		Collections.sort(list, new Student.CourseComparator());
		
		final String finalSchedule = "final schedule " + term + " for OSD and storemore.xlsx";
		File fileFinalSchedule = new File("finals/" + finalSchedule);
		
		if (! fileFinalSchedule.exists())
			new Message("File " + finalSchedule + " doesn't exist");
			
		label.setText("Getting info from " + finalSchedule + " file");
	    label.paintImmediately(label.getVisibleRect());
			
		try {
			FileInputStream fis = new FileInputStream(fileFinalSchedule);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
						
			int rowNum = 1;
			for (Student s : list) {
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
	private void getAccomodations() {
		ArrayList<Accommodations> listAcc = new ArrayList<Accommodations>();
		
		final String accommodations = "accommodations.xlsx";
		File fileAccommodations = new File("finals/" + accommodations);
		
		if (! fileAccommodations.exists())
			new Message("File " + accommodations + " doesn't exist");
			
		label.setText("Getting info from " + accommodations + " file");
	    label.paintImmediately(label.getVisibleRect());
	    
		try {
			FileInputStream fis = new FileInputStream(fileAccommodations);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			Cell cell = sheet.getRow(0).getCell(0);
			int rowNum = 0;
			while (! cell.getStringCellValue().equalsIgnoreCase("ID")) {
				cell = sheet.getRow(rowNum++).getCell(0);
				if (cell == null)
					cell = sheet.getRow(rowNum++).getCell(0);
					
			}
						
			Row r = sheet.getRow(rowNum);
			cell = r.getCell(0);
			while (cell != null) {
				Accommodations acc = new Accommodations(r);
				listAcc.add(acc);
				r = sheet.getRow(++rowNum);
				cell = r.getCell(0);
			}
			fis.close();
			Collections.sort(listAcc, new Accommodations.IdAccComparator());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		addAccommodations(listAcc);
	}
	private void addAccommodations(ArrayList<Accommodations> listAcc) {
		Collections.sort(list);
		int i = 0;
		for (Student s : list) {
			String id = s.getSid();
			while (id.compareTo(listAcc.get(i).getId()) > 0) {
				i++;
			}
			if (id.compareTo(listAcc.get(i).getId()) == 0) {
				Accommodations acc = listAcc.get(i);
				setAccommodations(s, acc);
			}
			else { //id < idAcc
				setAccommodations(s, null);
			}
		}
	}
	private void setAccommodations(Student s, Accommodations acc) {
		if (acc != null) {
			s.setEmail(acc.getEmailAcc());
			ArrayList<String> listCodes = acc.getList();
			for (String code : listCodes) {
				if (code.equals("XA")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("rm alone");
					else
						s.setComments(comment + ", " + "rm alone");
				}
				else if (code.equals("XB")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("braille");
					else
						s.setComments(comment + ", " + "braille");	
				}
				else if (code.equals("XC"))
					s.setComputer("Yes");
				else if (code.equals("XD")) {
					s.setExtraTime("2x");
					s.setExamLength();
				}
				else if (code.equals("XE")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("enlarge");
					else
						s.setComments(comment + ", " + "enlarge");
				}			
				else if (code.equals("XF")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("formula sheet");
					else
						s.setComments(comment + ", " + "formula sheet");	
				}
				else if (code.equals("XG")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("proof");
					else
						s.setComments(comment + ", " + "proof");	
				}
				else if (code.equals("XH") && s.getExtraTime() == null) {
					s.setExtraTime("T1/2");
					s.setExamLength();
				}
				else if (code.equals("XL")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("calculator");
					else
						s.setComments(comment + ", " + "calculator");	
				}
				else if (code.equals("XM")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("small room");
					else
						s.setComments(comment + ", " + "small room");	
				}
				else if (code.equals("XR") && s.getExtraTime() == null) {
					s.setExtraTime(""); // regular time
					s.setExamLength();
				}
				else if (code.equals("XS")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("scribe");
					else
						s.setComments(comment + ", " + "scribe");	
				}
				else if (code.equals("XT")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("on tape");
					else
						s.setComments(comment + ", " + "on tape");	
				}
				else if (code.equals("XW"))
					s.setStopwatch("Yes");
				else if (code.equals("XX")) {
					// nothing
				}
				else {
					s.setWarning("Unknown code: " + code);
				}
			}
			String otherAcc = acc.getOther();
			if (otherAcc != null) {
				if ((otherAcc.contains("1/3") || otherAcc.contains("1/4")) && s.getExtraTime() == null) {
					String sub = null;
					if (otherAcc.contains("1/3")) {
						sub = "1/3";
						s.setExtraTime("T1/3");
						s.setExamLength();
					}
					else {
						sub = "1/4";
						s.setExtraTime("T1/4");
						s.setExamLength();
					}
					if (otherAcc.contains("on all")) {
						s.setComments(otherAcc);
						s.setWarning("Please check extra time");
					}
					else {
						int index = otherAcc.indexOf(sub);
						int len = otherAcc.length();
						if (index+4 < len) {
							String comment = otherAcc.substring(index+4);
							s.setComments(comment);
						}
					}
				}
			}
			else {
				//
			}
		}
		else {
			s.setWarning("No accommodations data");
		}
		s.setExamLength();
	}
	private void findConflicts() {
		Collections.sort(list, new Student.StudentDateComparator());
		for (int i = 0; i < list.size()-1; i++) {
			Student s = list.get(i);
			if (s.equals(list.get(i+1))) {
				s.setConflict(true);
				list.get(i+1).setConflict(true);
			}
		}
	}
	
}
