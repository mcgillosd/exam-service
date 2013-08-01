import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * Created on Jul 17, 2013 1:32:55 PM
 */

/**
 * Class creates a Student for Midterms
 * 
 * @author Olga Tsibulevskaya
 */
public class StudentMidterm extends Student {
	/** The number given to students when they submit the form */
	private int id;
	private String nameProf;
	private String commentsFromForm;
	private String term;
	private int length;
	
	public StudentMidterm() {
		super();
	}
	
	public int getId() {
		return id;
	}
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
	
	public void setExamDate(String examDateS) {
		try {	
			SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
			examDate = sdf.parse(examDateS);
			term = new Term(examDate).getTerm();
		}
		catch (ParseException e) {
			e.printStackTrace(); 
			String message = "There was an error in the date of the exam field";
			new Message(message);
		}
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
	public void setExamStartTime(String examTime) {
		try {	
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			examStartTime = sdf.parse(examTime);
			validateTime();
		}
		catch (ParseException e) {
			e.printStackTrace();
			String message = "There is an error in the time of the exam field";
			new Message(message);
		}
	}
	private void validateTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(examStartTime);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0);
		Date time800 = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 59);
		Date time2030 = cal.getTime();
		if (examStartTime.before(time800)) {
			cal.setTime(examStartTime);
			cal.add(Calendar.HOUR, 12);
			examStartTime = cal.getTime();
		}
		else if (examStartTime.after(time2030)) {
			cal.setTime(examStartTime);
			cal.add(Calendar.HOUR, -12);
			examStartTime = cal.getTime();
		}
	}
	public String getNameProf() {
		return nameProf;
	}
	public void setNameProf(String nameProf) {
		this.nameProf = nameProf;
	}
	public String getCommentsFromForm() {
		return commentsFromForm;
	}
	public void setCommentsFromForm(String s) {
		commentsFromForm = s;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length; 
	}
	public void setExamLength() { // TODO: time change
		int time = 60; // to be deleted, old form 	
		//int time = length; /* for the new form */
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
		setExamFinishTime();
	}
	
	public String toString() {
		return id + "\t" + nameLast + "\t" + examDate + "\t" + examStartTime + "\t" + 
				examFinishTime + "\t" + computer + "\t" + comments;  
	}
}
