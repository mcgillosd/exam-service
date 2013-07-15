/*
 * Student.java
 * 
 * Created on 2013-06-10 1:10:29 PM 
 */

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Comparator;
/**
 * Gathers and keeps information about a student.
 * Receives the data from the web-page and sets the properties
 *  
 * @author Olga Tsibulevskaya
 */
public class Student implements Comparable<Student> {
	/**
	 * The number given to the student when he submits the form
	 */
	private int id;
	private Date dateExam;
	private String nameLast;
	private String nameFirst;
	private String email;
	private String course;
	private String section;
	private String location;
	private Date examStartTime;
	private Date examFinishTime;
	private int examLength;
	private String nameProf;
	private String nameProfFirst; // for finals
	private String nameProfLast; // for finals
	private String emailProf;
	private String extraTime;
	private String stopwatch;
	private String computer;
	/** Student id */
	private String sidFull;
	private String sid;
	//private String campus;
	private String comments;
	/** Shows a warning in case something is missing/wrong */
	private String warning; 
	private String term;
	private boolean timeChanged = false;
	private boolean conflict = false;
	/**
	 * Creates an empty container
	 */
	public Student() {
		// default
	}
	public int getId() {
		return id;
	}
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
	public Date getDateExam() {
		return dateExam;
	}
	public void setDateExam(String sDateE) {
		try {	
			SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
			dateExam = sdf.parse(sDateE);
			term = new Term(dateExam).getTerm();
		}
		catch (ParseException e) {
			e.printStackTrace(); 
			String message = "There was an error in the date of the exam field";
			new Message(message);
		}
	}
	public void setDateExam(Date date) {
		dateExam = date;
	}
	public String getTerm() {
		return term;
	}
	/**
	 * Registers the term of the exam, will be used to find out
	 * the correct file to write the data for the midterms.
	 * @param t the term in which exam is taken, 
	 * has the format 'name_of_term year_of_term', ex. 'Summer 2013'
	 */
	public void setTerm(Term t) {
		term = t.toString();
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
	public String getSidFull() {
		return sidFull;
	}
	public void setSidFull(String sid) {
		this.sidFull = sid;
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
	public void setCourse(String course) {
		this.course = course.toUpperCase();
	}
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
	public void setExamStartTime(String examTime) {
		try {	
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			examStartTime = sdf.parse(examTime);
		}
		catch (ParseException e) {
			e.printStackTrace();
			String message = "There is an error in the time of the exam field";
			new Message(message);
		}
	}
	public void setExamStartTime(Date examTime) {
		examStartTime = examTime;
	}
	private void setExamStartTime() {
		//set time to 16:05
		Calendar cal = Calendar.getInstance();
		cal.setTime(examStartTime);
		cal.set(Calendar.HOUR_OF_DAY, 19);
		cal.set(Calendar.MINUTE, 0);
		Date startLate = cal.getTime();
		
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(examStartTime);
		Calendar calFinish = Calendar.getInstance();
		calFinish.setTime(examStartTime);
		calFinish.add(Calendar.MINUTE, examLength);
		Date finish = calFinish.getTime();
		if (finish.compareTo(startLate) > 0) {
			//set to 19:00 - the latest time to finish
			int res = (int)((finish.getTime()/60000) - (startLate.getTime()/60000));
			//calStart.set(Calendar.HOUR_OF_DAY, 19);
			calStart.set(Calendar.MINUTE, -res);
			//cal.add(Calendar.MINUTE, -examLength);
			examStartTime = calStart.getTime();
			timeChanged = true;
		}
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
	public boolean timeChanged() {
		return timeChanged;
	}
	public boolean hasConflict() {
		return conflict;
	}
	public void setConflict(boolean bool) {
		conflict = bool;
	}
	public int getExamLength() {
		return examLength;
	}
	// to be changed, have to accept two fields (hours and minutes)
	// and calculate the length
	public void setLengthMidterm(String examLength) {
		this.examLength = 60; // to be changed when length is ready on the webpage
	}
	public void setExamLength(boolean finals) { 
		int time;
		if (finals)
			time = 3*60;  // for finals, exam length is 3 hours
		else
			time = 60; // to be changed to the real time for midterms
		if (extraTime == null)
			examLength = time;
		else if (extraTime.equals("T1/2") || extraTime.equalsIgnoreCase("Time+1/2"))
			examLength = (int)(time + time/2.0);
		else if (extraTime.equals("T1/3") || extraTime.equalsIgnoreCase("Time+1/3"))
			examLength = (int)(time + time/3.0);
		else if (extraTime.equals("T1/4") || extraTime.equalsIgnoreCase("Time+1/4"))
			examLength = (int)(time + time/4.0);
		else if (extraTime.equals("2x"))
			examLength = time*2;
		else 
			examLength = time; // what else can be?
		if (finals)
			setExamStartTime();
		setExamFinishTime();
	}
	public String getNameProf() {
		return nameProf;
	}
	public void setNameProf(String nameProf) {
		this.nameProf = nameProf;
	}
	public String getNameProfFirst() {
		return nameProfFirst;
	}
	public void setNameProfFirst(String nameFirst) {
		nameProfFirst = nameFirst;
	}
	public String getNameProfLast() {
		return nameProfLast;
	}
	public void setNameProfLast(String nameLast) {
		nameProfLast = nameLast;
	}
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
			computer = "";
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
	/*public void setCampus(String sCampus) {
		campus = sCampus;				
	}*/
	
	@Override
	public String toString() {
		return id + "\t" + nameLast + "\t" + dateExam + "\t" + examStartTime + "\t" + 
				examLength + "\t" + computer + "\t" + comments;  
	}
			
	public int compareTo(Student s) {
		String id = s.sid;
		return this.sid.compareTo(id);
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
		comp = dateExam.compareTo(s.getDateExam()); // check time for midterms, not so important for finals
		if (comp != 0)
			return false;
		return (examStartTime.compareTo(s.getExamStartTime()) == 0);
	}
	public boolean equalProf(Student s) {
		int comp = nameProfLast.compareTo(s.nameProfLast);
		if (comp != 0)
			return false;
		return (nameProfFirst.compareTo(s.nameProfFirst) == 0);
	}
	/**
	 * Compares Students by their date of the exam
	 *  
	 * @see Comparator
	 */
	public static class DateExamComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getDateExam().compareTo(s2.getDateExam());  
			if (comp != 0)
				return comp;
			else
				return s1.getExamStartTime().compareTo(s2.getExamStartTime());	
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
					return s1.getDateExam().compareTo(s2.getDateExam());
			}
		}
	}
	// sorts by the prof name, used for lists of students for profs 
	public static class ProfComparator implements Comparator<Student> {
		public int compare(Student s1, Student s2) {
			int comp = s1.getNameProfLast().compareTo(s2.getNameProfLast()); 
			if (comp != 0)
				return comp;
			comp = s1.getNameProfFirst().compareTo(s2.getNameProfFirst());
			if (comp != 0)
				return comp;
			return s1.getCourse().compareTo(s2.getCourse());
		}
	}
}