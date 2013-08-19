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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Creates a list of all students for Finals, by getting the info from the Finals file,
 * since the initial file is supposed to be changed (exam dates due to conflicts, profs added, etc) 
 * 
 * @author Olga Tsibulevskaya
 */
public class StudentsFinalSec {
	
	private static ArrayList<StudentFinal>list = new ArrayList<StudentFinal>();	
	private File fileFinals;	
	static ArrayList<String> rooms = new ArrayList<String>();
	
	private ArrayList<Invigilator> listInv = new ArrayList<Invigilator>();
	
	private ArrayList<Date> dates = new ArrayList<Date>();
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
				if (r == null) {
					String[] name = fileFinals.getName().split(" ");
					String term = "";
					if (name[0].equalsIgnoreCase("December"))
						term = "Fall";
					else if (name[0].equalsIgnoreCase("April"))
						term = "Winter";
					else if (name[0].equalsIgnoreCase("August"))
						term = "Summer";
					else {
						// error
					}
					new Excel().removeEmptyRows("Final", term + " " + name[1], false);
				}
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
						if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
							student.setNameProfLast(cell.getStringCellValue()); 
						}
						else { 
							student.setNameProfLast(""); // maybe better null?
						}
						student.setNameProfFirst("");
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
		Collections.sort(list, new Student.DateExamCommentsComparator());
		
		File file = new File("rooms.xlsx");
		if (! file.exists()) {
			new Message("File " + file.getName() + " doesn't exist");
			return;
		}
		ListOfRoomsFinal rList = new ListOfRoomsFinal(file);
		
		for (Room room : rList)
			rooms.add(room.getId());
		rooms.add("room not found");
		
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
			ListOfRoomsFinal copy = new ListOfRoomsFinal(rList);
			// while dates are the same and morning
			while (currentDate.compareTo(s.getExamDate()) == 0 && s.getExamStartTime().compareTo(time) <= 0) { 
				copy.allocateRoom(s);
				if (i < list.size())
					s = list.get(i++);
				else {
					i++;
					break;
				}
			}
			copy = new ListOfRoomsFinal(rList);
			
			//clone = (ListOfRoomsFinal)rList.clone();
			// the same day, afternoon
			while (currentDate.compareTo(s.getExamDate()) == 0) {
				copy.allocateRoom(s); 
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
	public void getInvigilators() {
		new Login(this);
	}
	public int roomIsSmall(String name) {
		ArrayList<Room> list = ListOfRooms.list;
		if (list.size() > 0) {
			for (Room r : list) {
				if (r.getId().equalsIgnoreCase(name)) {
					if (r.isSmall())
						return 0;
					else 
						return 1; // not small
				}
			}
		}
		return -1; // not found
	}
	private Invigilator[] assignInvigilators(int num, Student s) {

		Date date = s.getExamDate();
		int index = dates.indexOf(date);
		Date time = s.getExamStartTime();
		Date noon = new Helper().getNoon(time);
		
		Collections.sort(listInv, new Invigilator.InvAssignsComparator());
		
		Invigilator[] inv = new Invigilator[num];
		for (int i = 0; i < num; i++) {
			boolean set = false;
			for (Invigilator invig : listInv) {
				if (time.before(noon)) {
					ArrayList<Boolean>morning = invig.getMorning();
					if (morning.get(index) == true) {
						invig.getMorning().set(index, false);
						inv[i] = invig;
						invig.incrementAssignments();
						set = true;
						break;
					}
				}
				if (time.after(noon)) {
					ArrayList<Boolean> afternoon = invig.getAfternoon();
					if (afternoon.get(index) == true) {
						invig.getAfternoon().set(index, false);
						inv[i] = invig;
						invig.incrementAssignments();
						set = true;
						break;
					}
				}
			}
			if (! set) {
				for (Invigilator invig : listInv) {
					ArrayList<Boolean> both = invig.getBoth();
					if (both.get(index) == true) {
						invig.getBoth().set(index, false);
						invig.getSecond().set(index, true);
						inv[i] = invig;
						invig.incrementAssignments();
						set = true;	
						break;
					}
				}
			}
			if (! set) {
				if (time.after(noon)) {
					for (Invigilator invig : listInv) {
						ArrayList<Boolean> second = invig.getSecond();
						if (second.get(index) == true) {
							invig.getSecond().set(index, false);
							inv[i] = invig;
							invig.incrementAssignments();
							set = true;
							break;
						}
					}
				}
			}
			if (! set) {
				inv[i] = new Invigilator();
				inv[i].setName("no");
			}
		}
		s.setInvigilator(inv);
		return inv;
	}
	public void addInvigilators(String html) {
		setListInv(html);
		Collections.sort(list, new Student.DateExamLocationComparator());
		
		String roomNext = "";
		boolean set = false;
		Invigilator[] inv = new Invigilator[0];
						
		for (int i = 0; i < list.size()-1; i++) {
			Student s = list.get(i);
			String room = s.getLocation();
			if (! set) {
				if (room.equalsIgnoreCase("lab") || roomIsSmall(room) == 0) {
					inv = assignInvigilators(1, s);
				}
				else {
					inv = assignInvigilators(2, s);
				}
				set = true;
				
			}
			roomNext = list.get(i+1).getLocation();
			if (room.equals(roomNext)) {
				list.get(i+1).setInvigilator(inv);
			}
			else {
				set = false;
			}
		}
		new Excel().writeLocation(StudentsFinalSec.getList(), fileFinals);
    	
	}
	
	private void setListInv(String html) {
		
		Document doc = Jsoup.parse(html);
			
		boolean firstSkipped = false;
		
		ArrayList<String> months = new ArrayList<String>();
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		
		for(Element element : doc.select("tr")) { 
			if(! firstSkipped) { // the first $tr is the header
				firstSkipped = true;
				
				// temporary, to test invigilators
				for (int i = 0; i < 10; i++) {
				
					Calendar cal = Calendar.getInstance();
					// to get the same year 
					cal.setTime(list.get(0).getExamDate()); 
					cal.set(Calendar.MONTH, 3);
					if (i < 3)
						cal.set(Calendar.DATE, i+17);
					if (i > 2 && i < 8)
						cal.set(Calendar.DATE, i+19);
					if (i > 7)
						cal.set(Calendar.DATE, i+21);
					Date examDate = cal.getTime();
					dates.add(examDate);
				}
				
				// end of temporary methods
				
			/*	Elements td = element.select("th");
				int index = 0;
				index += 8;
				int size = td.size();
				while (index < size) {
					String init = td.get(index++).text();
					String date = init.substring(0, init.length()-2);
					String[] split = date.split(" ");
					String m = split[0];
					int mon = months.indexOf(m);
					if (mon == -1) {
						new Message("Month of the exam written in a wrong format\nCannot assign invigilators");
						return;
					}
					String d = split[1];
					Calendar cal = Calendar.getInstance();
					// to get the same year 
					cal.setTime(list.get(0).getExamDate()); 
					cal.set(Calendar.MONTH, mon);
					cal.set(Calendar.DATE, Integer.parseInt(d));
					Date examDate = cal.getTime();
					dates.add(examDate);
				}*/
				continue;
			}	

			int index = 0; // index used to go through all @td (columns)                            
			Invigilator inv = new Invigilator();
	        	
			Elements td = element.select("td"); 
			int size = td.size();
					
			inv.setId(td.get(index++).text());
			index += 3;
			inv.setName(td.get(index++).text());				 
			inv.setEmail(td.get(index++).text());
			inv.setPhone(td.get(index++).text());
			index++;
			
			String time = "";
			while (index < size) { 
				String text = td.get(index++).text();
				String[] split = text.split("\\s*\\(.+?\\)+\\s*");
				
				time = split[0];
				
				if (split.length < 2) {
					if (split[0].equalsIgnoreCase("Morning")) {
						inv.getMorning().add(true);
						inv.getAfternoon().add(false);
					}
					else if (split[0].equalsIgnoreCase("Afternoon")){
						inv.getMorning().add(false);
						inv.getAfternoon().add(true);
					}
					else {
						inv.getMorning().add(false);
						inv.getAfternoon().add(false);
					}
					inv.getBoth().add(false);
					inv.getSecond().add(false);
					inv.getAvailability().add(time);
				}
				else {
					inv.getAvailability().add("both");
					time = "both";
					inv.getBoth().add(true);
					inv.getMorning().add(false);
					inv.getAfternoon().add(false);
					inv.getSecond().add(false);
				}
			}
			
			listInv.add(inv);
			
		}
	}
}