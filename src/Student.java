/*
 * Student.java
 * 
 * Created on 2013-06-10 1:10:29 PM 
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;


/**
 * Gathers and keeps information about a student.
 * Receives the data from the web-page and sets the properties
 *  
 * @author Olga Tsibulevskaya
 */
public abstract class Student implements Comparable<Student> {
	
	protected Date examDate;
	protected Date examStartTime;
	protected Date examFinishTime;
	
	protected String nameLast;
	protected String nameFirst;
	
	protected String email; // need email for Midterms?
	
	protected String course;
	protected String section;
	
	protected String location;
		
	protected int examLength;
	//private String nameProf;
	
	private String emailProf;  // need it for Finals?
	
	protected String extraTime;
	protected String stopwatch;
	protected String computer;
	protected String comments;
	
	/** Student id */
	protected String sid;
	
	protected String campus;
	
	/** Shows a warning in case something is missing/wrong */
	protected String warning; 
	
	protected Invigilator invigilator;
	
	/* for comparator */
	ArrayList<String> rooms = new ArrayList<String>();
	
	/**
	 * Creates an empty container
	 */
	public Student() {
		// default
	}
	
	public Date getExamDate() {
		return examDate;
	}
		
	public String getNameLast() {
		return nameLast;
	}
	public void setNameLast(String name) {
		nameLast = name;
	}
	public String getNameFirst() {
		return nameFirst;
	}
	public void setNameFirst(String name) {
		nameFirst = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getCourse() {
		return course;
	}
	public abstract void setCourse(String course);
	
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Date getExamStartTime() {
		return examStartTime;
	}
	
	
	public Date getExamFinishTime() {
		return examFinishTime;
	}
	public void setExamFinishTime(Date time) {
		examFinishTime = time;
	}
	public void setExamFinishTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(examStartTime);
		cal.add(Calendar.MINUTE, examLength);
		examFinishTime = cal.getTime();
	}
	
	public int getExamLength() {
		return examLength;
	}
	public abstract void setExamLength();
	
	public String getEmailProf() {
		return emailProf;
	}
	public void setEmailProf(String emailProf) {
		this.emailProf = emailProf;
	}
	public String getExtraTime() {
		return extraTime;
	}
	public void setExtraTime(String extraTime) {
		this.extraTime = extraTime;
	}
	public String getStopwatch() {
		return stopwatch;
	}
	public void setStopwatch(String sw) {
		if (sw.equals("Yes"))
			stopwatch = "sw";
		else
			stopwatch = "";
	}
	public String getComputer() {
		return computer;
	}
	public void setComputer(String comp) {
		if (comp.equalsIgnoreCase("Yes"))
			computer = "pc";
		else
			computer = ""; // or null?
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getWarning() {
		return warning;
	}
	public void setWarning(String warning) {
		this.warning = warning;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String sCampus) {
		campus = sCampus;				
	}
	public Invigilator getInvigilator() {
		return invigilator;
	}
	public void setInvigilator(Invigilator inv) {
		invigilator = inv;
	}
	public int compareTo(Student s) {
		String id = s.sid;
		return this.sid.compareTo(id);
	}
	public int compareTime(Student s) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(examStartTime);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 59);
		Date time = cal.getTime();
		if ((examStartTime.before(time) && s.examStartTime.before(time)) || 
				(examStartTime.after(time) && s.examStartTime.after(time)))
			return 0;
		if (examStartTime.before(time) && s.examStartTime.after(time))
			return -1;
		return 1;
	}
	private void setRooms() {
		rooms = StudentsFinalSec.rooms;		
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (! (obj instanceof Student))
			return false;
		Student s = (Student)obj;
		int comp = nameLast.compareTo(s.getNameLast()); 
		if (comp != 0)
			return false;
		comp = nameFirst.compareTo(s.getNameFirst());
		if (comp != 0)
			return false;
		return examDate.compareTo(s.getExamDate()) == 0; 
	}
	
	public int compareLocation(Student s) {
		if (rooms.size() == 0)
			setRooms();
				
		if (rooms.indexOf(location) >= 0 && rooms.indexOf(s.location) >= 0) {
			if (rooms.indexOf(location) < rooms.indexOf(s.location))
				return -1;
			if (rooms.indexOf(location) > rooms.indexOf(s.location))
				return 1;
			return 0;
		}
		return 0;
	}
	
	public int compareComments(Student another) {
		if (comments != null && another.comments != null) {
			if ((comments.contains("scribe") || comments.contains("rm alone")) 
					&& (! another.comments.contains("scribe") && ! another.comments.contains("rm alone"))) 
				return -1;
			if ((! comments.contains("scribe") && ! comments.contains("rm alone")) 
					&& (another.comments.contains("scribe") || another.comments.contains("rm alone")))
				return 1;
			return 0;
		}
		if (comments == null && another.comments == null)
			return 0;
		else {
			if (comments == null)
				return 1;
			else
				return -1;
		}
	}
	/**
	 * Compares Students by their date of the exam
	 *  
	 * @see Comparator
	 */
	public static class DateExamComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getExamDate().compareTo(s2.getExamDate());  
			if (comp != 0)
				return comp;
			else
				return s1.getExamStartTime().compareTo(s2.getExamStartTime());	
		}
	}
	public static class DateExamCommentsComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getExamDate().compareTo(s2.getExamDate());  
			if (comp != 0)
				return comp;
			else {
				comp = s1.getExamStartTime().compareTo(s2.getExamStartTime());
				if (comp != 0)
					return comp;
				else
					return s1.compareComments(s2);
			}
		}
	}
	/**
	 * Compares by the exam date and by location to write into the file sorted
	 */
	public static class DateExamLocationComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getExamDate().compareTo(s2.getExamDate());  
			if (comp != 0)
				return comp;
			else {
				comp = s1.compareTime(s2);
				if (comp != 0) 
					return comp;
				else {
					return s1.compareLocation(s2);
				}
			}
		}
	}
	// sorts by course and section, used to allocate profs names against each row
	public static class CourseComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getCourse().compareTo(s2.getCourse()); 
			if (comp != 0)
				return comp;
			else {
				return s1.getSection().compareTo(s2.getSection());
			}
		}
	}
	// sorts by students names and date of the exam, used to find conflicts
	public static class StudentDateComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getNameLast().compareTo(s2.getNameLast()); 
			if (comp != 0)
				return comp;
			else {
				comp = s1.getNameFirst().compareTo(s2.getNameFirst());
				if (comp != 0)
					return comp;
				else
					return s1.getExamDate().compareTo(s2.getExamDate());
			}
		}
	}
	
}