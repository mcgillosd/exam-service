/*
 * StudentsMidterm.java
 * 
 * Created on 2013-06-10 1:59:46 PM
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Collects students into lists, 
 * if necessary sorts them by the term in which they take exams
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
	
	private JLabel label;
	/** The last id from the previous update */
	private int id;
	/** The last id after a new update has been done */
	private int lastid;
	private boolean update = false;
	private boolean existNew = false;
	private boolean roomsInit = false;
	private Excel xl;
	private ListOfRoomsMidterm listOfRooms;
	private String season = new Term().getSeason();
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
	public StudentsMidtermInit(JFrame frame, JLabel label, boolean update) {
		this.label = label;
		this.update = update;
		new AccessWeb(frame, label, this);  // do not need frame (most likely)
	}
	/**
	 * Gets the last id of the previous update and registers it
	 * @param lastId the last id after the last update
	 */
	public void setId(LastID lastId) {
		this.id = lastId.getID();
		System.out.println(id);
	}
	/**
	 * Invokes setters to create lists of students
	 * based on the flag <code>update</code>
	 * (if <code>update == true</code> will only take into account new entries)
	 * 
	 * @param html a <code>String</code> which contains 
	 * all data from the web-page
	 */
	public void start(String html){
		if (update) { 
			setId(new LastID());
			//boolean existNew = false;
			
			setTermLists(html, id);
			ArrayList<ArrayList<StudentMidterm>> lists = new ArrayList<ArrayList<StudentMidterm>>(3);
			lists.add(listWinter);
			lists.add(listSummer);
			lists.add(listFall);
			for (int i = 0; i < 3; i++) {
				if (lists.get(i).size() > 0) {
					int index = (i+1)*3; // should be any month of the term, so 3 (W), 6 (S) and 9 (F)
					String term = new Term(index).getTerm();
				/*	xl = new Excel();
					try {
						label.setText("Updating files...");
						label.paintImmediately(label.getVisibleRect());
						xl.update(lists.get(i), term);
					}
					catch (IOException e) {
						e.printStackTrace();
					}*/
				}
			}
			if (! existNew) {
				String message = "There are no new entries";
				new Message(message);
			}
			else {
				new Message("The last id before update is " + id);
				new LastID().setLastID(lastid); // update id;
			}
			label.setText("Choose an option and click the button");
			label.paintImmediately(label.getVisibleRect());
		}
		else { // download all
			listOfStudents = new ArrayList<StudentMidterm>();
			setListOfStudents(html);
			try {
				Excel xl = new Excel();
				label.setText("Writing data to the file...");
				label.paintImmediately(label.getVisibleRect());
				xl.export(listOfStudents);
				label.setText("Choose an option and click the button");
				label.paintImmediately(label.getVisibleRect());
			} 
			catch (IOException e) {
				e.printStackTrace();
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
	     
		 for(Element element : doc.select("tr")) { 
			 if(! firstSkipped) { // the first $tr is the header
				 firstSkipped = true;
				 continue;
			 }	

			 int index = 0; // index used to go through all @td (columns)                            
			 StudentMidterm stud = new StudentMidterm();
	        	
			 Elements td = element.select("td"); 
			 int item = GUIPanel.till;
			 
			// int item = Integer.parseInt(td.get(0).text()); // will be $lastid, new id for the next update
			 int idMax = id;  // id from the previous update
			 //System.out.println(item + " " + id);
			 if (item > idMax) { // new entries have been added since last visit
				 existNew = true;
				 if (Integer.parseInt(td.get(index).text()) <= item 
						 && Integer.parseInt(td.get(index).text()) > id) { // only to test, to be removed
				 stud.setId(td.get(index++).text()); 
				 if (! lastIdSet) { // gets the first entry's id - it will be the last updated entry
					 lastid = stud.getId(); // write it only after update!
					 lastIdSet = true;
				 }
				 index += 3; // skip time submission, user, IP
				 stud.setExamDate(td.get(index++).text());
				 stud.setNameLast(td.get(index++).text());
				 stud.setNameFirst(td.get(index++).text());
				 stud.setSid(td.get(index++).text());
				 index += 2; // skip id, phone, email
				 stud.setCourse(td.get(index++).text());
				 stud.setSection(td.get(index++).text());
				 
				 index++; // skip location
				 stud.setExamStartTime(td.get(index++).text());
				 stud.setLengthMidterm(td.get(index++).text());
				 stud.setNameProf(td.get(index++).text());
				 stud.setEmailProf(td.get(index++).text());
				 stud.setExtraTime(td.get(index++).text());
				 stud.setStopwatch(td.get(index++).text());
				 stud.setComputer(td.get(index++).text());
				 stud.setComments(td.get(index++).text());
				 stud.setCampus(td.get(index++).text()); // will need campus
				 
				 stud.setExamLength();
				 System.out.println(stud);
				 
				 String term = stud.getTerm();
				 if (term.contains("Fall")) {
					// existNew = true;
					 if (! listFall.contains(stud));
					 	listFall.add(stud);
				 }
				 else if (term.contains("Winter")) {
					 //existNew = true;
					 if (! listWinter.contains(stud));
					 	listWinter.add(stud);
				 }
				 else if (term.contains("Summer")) {
					 //existNew = true;
					 if (! listSummer.contains(stud))
						 listSummer.add(stud);
				 }
				 if (existNew && ! roomsInit) {
					 System.out.println("init");
					 listOfRooms = initRooms();
					 if (listOfRooms != null)
						 roomsInit = true;
					 else
						 return;
				 }
				 // add location only for the current term
			//	 if (term.contains(season))
				 if (term.contains("Winter")) // just to test
					 addLocation(stud); 
			 }
			 }	
			 else
				 break;
		 }
	}

	private ListOfRoomsMidterm initRooms() {
		File file = new File("rooms_midterm.xlsx");
		if (! file.exists()) {
			new Message("File " + file.getName() + " doesn't exist");
			return null;
		}
		ListOfRoomsMidterm rList = new ListOfRoomsMidterm(file);
		for (Room r : rList)
			System.out.println(r);
		return rList;
	}
	private void addLocation(Student s) {
		if (s.getComments() != null && (s.getComments().contains("rm alone") || s.getComments().contains("scribe"))) {
			RoomMidterm r = listOfRooms.getSmallRoom(s.getExamDate(), s.getExamStartTime(), s.getExamFinishTime());
			if (r != null) {
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("small room not found");
			}
		}
		else if (s.getComments() != null && (s.getComments().contains("wynn") || s.getComments().contains("kurzweil"))) {
			RoomMidterm r = listOfRooms.getRoomByName("OSD Lab", s.getExamDate(), s.getExamStartTime(), s.getExamFinishTime());
			if (r != null && ! r.full()) {
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("no places in OSD lab");
			}
		}
		else if (s.getComputer().equals("pc")) {
			RoomMidterm r = listOfRooms.getLab(s.getExamDate(), s.getExamStartTime(), s.getExamFinishTime());
			if (r != null) {
				s.setLocation(r.getId());
			}
			else { // there are laptops TODO: should students be in the OSD office?
				r = listOfRooms.getRoom(s.getExamDate(), s.getExamStartTime(), s.getExamFinishTime());
				if (r != null) {
					s.setLocation(r.getId());
				}
				else
					s.setLocation("no more places");
			}
		}
		// no special demands
		else {
			RoomMidterm r = listOfRooms.getRoom(s.getExamDate(), s.getExamStartTime(), s.getExamFinishTime());
			if (r != null) {
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("no more places");
			}
		}
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
        	index += 3; // skip time submission, user, IP
        	stud.setExamDate(td.get(index++).text());
        	stud.setNameLast(td.get(index++).text());
        	stud.setNameFirst(td.get(index++).text());
        	index += 3; // skip id, phone, email
        	stud.setCourse(td.get(index++).text());
        	stud.setSection(td.get(index++).text());
        	index++; // skip location
        	//stud.setLocation(td.get(index++).text());
        	stud.setExamStartTime(td.get(index++).text());
        	stud.setLengthMidterm(td.get(index++).text());
        	stud.setNameProf(td.get(index++).text());
        	stud.setEmailProf(td.get(index++).text());
        	stud.setExtraTime(td.get(index++).text());
        	stud.setStopwatch(td.get(index++).text());
        	stud.setComputer(td.get(index++).text());
        	index++; // skip comments
        	//stud.setCampus(td.get(index++).text()); // will need campus 
        	stud.setExamLength();
        	
        	listOfStudents.add(stud);    
        }
	}
}