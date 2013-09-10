/*
 * StudentsMidtermInit.java
 * 
 * Created on 2013-06-10 1:59:46 PM
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JTextArea;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Collects students into lists, gathers all information from the webform results 
 *  
 * @author Olga Tsibulevskaya
 * @see Student
 */
public class StudentsMidtermInit {

	/** General list of all students  */
	private ArrayList<StudentMidterm> listOfStudents;
	/** List of students who take exams in Fall */
	private ArrayList<StudentMidterm> listFall = new ArrayList<StudentMidterm>();
	/** List of students who take exams in Winter */
	private ArrayList<StudentMidterm> listWinter = new ArrayList<StudentMidterm>();
	/** List of students who take exams in Summer */
	private ArrayList<StudentMidterm> listSummer = new ArrayList<StudentMidterm>();

	private ArrayList<StudentMidterm> macdonald = new ArrayList<StudentMidterm>();
	
	private JTextArea label = PanelMidterms.label;
	
	/** The last id from the previous update */
	static int id;
	/** The last id after a new update has been done */
	private int lastid;
		
	private boolean update = false;
	private boolean existNew = false;
		
	private Excel xl;
	private ListOfRoomsMidterm listOfRooms;
	private ListOfAccommodations listAcc;
	
	/**
	 * Triggers access to the web-page in order to get the data,
	 * which will be used to create lists of students.
	 * 
	 * @param frame 	uses frame to create new dialog messages
	 * @param label 	the bottom part of GUI which is used to give
	 * 					information on the performed actions
	 * @param update	if <code>true</code> will only update the files 
	 * 					starting from the last update's id
	 */
	public StudentsMidtermInit(boolean update) {
		this.update = update;
	}
	/**
	 * Gets the last id of the previous update and registers it
	 * @param lastId the last id after the last update
	 */
	public void setId(LastID lastId) {
		id = lastId.getID();
	}
	/**
	 * Creates lists of students by terms or one list in case of "Download" option
	 * if <code>update == true</code> will take into account only new entries
	 * 
	 * @param html a <code>String</code> which contains 
	 * all data from the web-page
	 * @throws FileNotFoundException 
	 */
	public void start(String html) throws FileNotFoundException{
	
		if (update) { 
			boolean accommodationsSet = false;
			try {
				setId(new LastID());
			}
			catch (FileNotFoundException e) {
				StringBuilder sb = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					sb.append(element.toString());
					sb.append("\n");
				}
				new Log(sb.toString());
				return;
			}
			
			setTermLists(html, id);
			
			ArrayList<ArrayList<StudentMidterm>> lists = new ArrayList<ArrayList<StudentMidterm>>(3);
			lists.add(listWinter);
			lists.add(listSummer);
			lists.add(listFall);
			
			label.append("-- Getting accommodations\n");
			label.paintImmediately(label.getVisibleRect());
			
			for (int i = 0; i < lists.size(); i++) {
				if (lists.get(i).size() > 0 ) {
					if (! accommodationsSet) {
						listAcc = new ListOfAccommodations();
						accommodationsSet = true;
					}
					listAcc.findAccommodations(lists.get(i));
				}
			}
			
			/* Checks if files are available */
			for (int i = 0; i < 3; i++) {
				if (lists.get(i).size() > 0) {
					int index = (i+1)*3;
					String term = new Term(index).getTerm();
				//	String filename = term + " exam schedule.xlsx";
					String filename = "F:\\Exams\\" + term + " exam schedule.xlsx";
					File file = new File(filename);
					if (file.exists()) {
						try {
							RandomAccessFile raf = new RandomAccessFile(file, "rw");
							raf.close();
						} catch (IOException e) {
							new Message("File " + filename + " is in use.\nPlease restart when the file is available");
							return;
						}
					}
				}
			}
			
			
			String now = new Term().getTerm();
			if (macdonald.size() > 0) {
				//String filename = now + " exam schedule.xlsx";
				String filename = "F:\\Exams\\" + now + " exam schedule.xlsx";
				File file = new File(filename);
				if (file.exists()) {
					try {
						RandomAccessFile raf = new RandomAccessFile(file, "rw");
						raf.close();
					} catch (IOException e) {
						new Message("File " + filename + " is in use.\nPlease restart when the file is available");
						return;
					}
				}
			}
			/* Allocates rooms */
			if (now.contains("Fall")) {
				if (listFall.size() > 0) {
					label.append("-- Allocating rooms\n");
					label.paintImmediately(label.getVisibleRect());
					listOfRooms = initRooms();
					if (listOfRooms == null)
						return;
					listOfRooms.addLocation(listFall);
				}
			}
			else if (now.contains("Winter")) {
				if (listWinter.size() > 0) {
					label.append("-- Allocating rooms\n");
					label.paintImmediately(label.getVisibleRect());
					listOfRooms = initRooms();
					if (listOfRooms == null)
						return;
					listOfRooms.addLocation(listWinter);
				}
			}
			else if (now.contains("Summer")) {
				if (listSummer.size() > 0) {
					label.append("-- Allocating rooms\n");
					label.paintImmediately(label.getVisibleRect());
					listOfRooms = initRooms();
					if (listOfRooms == null)
						return;
					listOfRooms.addLocation(listSummer); 
				}
			}
			else {
				// error
			}
			
			for (int i = 0; i < 3; i++) {
				if (lists.get(i).size() > 0) {
					int index = (i+1)*3; /* can be any month of the term, so 3 (W), 6 (S) and 9 (F) */
					String term = new Term(index).getTerm();
					label.append("-- Updating Excel files\n");
					label.paintImmediately(label.getVisibleRect());
					xl = new Excel();
					try {
						xl.update(lists.get(i), term);
					} catch (InvalidFormatException e1) {
						StringBuilder sb = new StringBuilder();
						for (StackTraceElement element : e1.getStackTrace()) {
							sb.append(element.toString());
							sb.append("\n");
						}
						new Log(sb.toString());
						return;
					}
					catch (IOException e) {
						new Message("Error occured while writing files into Excel");
						StringBuilder sb = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							sb.append(element.toString());
							sb.append("\n");
						}
						new Log(sb.toString());
						return;
					}
				}
			}
			if (macdonald.size() > 0) {
				label.append("-- Adding Macdonald campus\n");
				label.paintImmediately(label.getVisibleRect());
				try {
					new Excel().writeMacdonald(macdonald);
				} catch (IOException e) {
					StringBuilder sb = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						sb.append(element.toString());
						sb.append("\n");
					}
					new Log(sb.toString());
					return;				}
			}
			if (! existNew) {
				label.append("-- There are no new entries\n");
				label.paintImmediately(label.getVisibleRect());
			}
			else {
				label.append("-- The last id before update is " + id + "\n");
				label.paintImmediately(label.getVisibleRect());
				new LastID().setLastID(lastid); // update id;
			}
		}
		else { // download all
			listOfStudents = new ArrayList<StudentMidterm>();
			setListOfStudents(html);
			Excel xl = new Excel();
			label.append("-- Writing data to the file...\n");
			label.paintImmediately(label.getVisibleRect());
			try {
				xl.export(listOfStudents);
			}
			catch (FileNotFoundException e) {
				new Message("File Midterm.xlsx is currently in use.\nPlease restart when it's available");
				return;
			}
		}
	}
	/**
	 * Populates lists of students (3) according to the term in which exams are taken.
	 * Sets the last id after update
	 * 
	 * @param html	A <code>String</code> which contains all information from the web page
	 * @param id 	The last id from the previous update. The lists will be populated until
	 * 				that id
	 */
	private void setTermLists(String html, int id) {
		Document doc = Jsoup.parse(html);
		
		boolean firstSkipped = false;
		boolean lastIdSet = false;
		
		String text = label.getText();
		
		for(Element element : doc.select("tr")) { 
			if(! firstSkipped) { // the first $tr is the header
				firstSkipped = true;
				continue;
			}	

			int index = 0; // index to go through all @td (columns)                            
			StudentMidterm stud = new StudentMidterm();
	        	
			Elements td = element.select("td"); 
						 
			int item = Integer.parseInt(td.get(0).text()); 
					 
			if (item > id) { // new entries have been added since last visit
				existNew = true;
				 
				stud.setId(td.get(index++).text()); 
				if (! lastIdSet) { // gets the first entry's id - it will be the last updated entry
					lastid = stud.getId(); // write it only after update!
					lastIdSet = true;
					label.append("-- Processing new entries\n");
			    	label.paintImmediately(label.getVisibleRect());
				}
				 
				label.setText(text + "-- Processing ID " + stud.getId() + "\n");
				label.paintImmediately(label.getVisibleRect());
				 
				index += 3; // skip time submission, user, IP
				stud.setExamDate(td.get(index++).text());
				stud.setNameLast(td.get(index++).text());
				stud.setNameFirst(td.get(index++).text());
				stud.setSid(td.get(index++).text());
				index += 2; // skip phone, email
				stud.setCourse(td.get(index++).text());
				stud.setSection(td.get(index++).text());
				
				index++; // skip location
				stud.setExamStartTime(td.get(index++).text());
				 
				String hours = td.get(index++).text();  
				String minutes = td.get(index++).text();
				stud.setLength(calculateLength(hours, minutes));
				 
				stud.setNameProf(td.get(index++).text());
				stud.setEmailProf(td.get(index++).text());
				stud.setCommentsFromForm(td.get(index++).text());
				stud.setCampus(td.get(index++).text()); 
				 
				stud.setExamLength();
			
				String term = stud.getTerm();
				if (stud.getCampus().equalsIgnoreCase("Downtown")) {
					if (term.contains("Fall")) {
						if (! listFall.contains(stud))
							listFall.add(stud);
					}
					else if (term.contains("Winter")) {
						if (! listWinter.contains(stud))
							listWinter.add(stud);
					}
					else if (term.contains("Summer")) {
						if (! listSummer.contains(stud))
							listSummer.add(stud);
					}
				}
				else 
					macdonald.add(stud);
			}
			else
				break;
		}
	}
	
	private int calculateLength(String hours, String minutes) {
		String[] hoursA = hours.split(" ");
		String[] minutesA = minutes.split(" ");
		int h = 0, m = 0;
		if (! hoursA[0].equals(""))
			h = Integer.parseInt(hoursA[0]);
		if (! minutesA[0].equals(""))
			m = Integer.parseInt(minutesA[0]);
		int len = h*60 + m;
		return len;
	}
	
	private ListOfRoomsMidterm initRooms() {
		File file = new File("F:\\Exams\\Files\\rooms_midterm.xlsx");
		if (! file.exists()) {
			new Message("File " + file.getName() + " doesn't exist");
			return null;
		}
		ListOfRoomsMidterm rList = new ListOfRoomsMidterm(file);
		return rList;
	}

	/**
	 * Populates one list, which will contain all information from the web page
	 * 
	 * @param html	A <code>String</code> which contains all information from the web page
	 */
	private void setListOfStudents(String html) {
		Document doc = Jsoup.parse(html);
			
        boolean firstSkipped = false;
        
        for(Element element : doc.select("tr")) { 
        	if(!firstSkipped) { // Skip the first 'tr' tag since it's the header
        		firstSkipped = true;
        		continue;
			}

        	int index = 0;                             
        	StudentMidterm stud = new StudentMidterm();
        	Elements td = element.select("td");    
        	
        	stud.setId(td.get(index++).text());
        	stud.setTimeSubmission(td.get(index++).text());
        	stud.setUser(td.get(index++).text());
        	index++; // skip IP
        	stud.setExamDate(td.get(index++).text());
        	stud.setNameLast(td.get(index++).text());
        	stud.setNameFirst(td.get(index++).text());
        	stud.setSid(td.get(index++).text());
        	stud.setPhone(td.get(index++).text());
        	stud.setEmail(td.get(index++).text());
        	stud.setCourse(td.get(index++).text());
        	stud.setSection(td.get(index++).text());
        	index++; // skip location
        	
        	stud.setExamStartTime(td.get(index++).text());
        	        	
        	String hours = td.get(index++).text();  // for the new form
			String minutes = td.get(index++).text();
			stud.setLength(calculateLength(hours, minutes));
			 
        
        	stud.setNameProf(td.get(index++).text());
        	stud.setEmailProf(td.get(index++).text());
        	stud.setExtraTime(td.get(index++).text());
        	stud.setCampus(td.get(index++).text());
        	stud.setExamLength();
        	
        	listOfStudents.add(stud);    
        }
	}
}