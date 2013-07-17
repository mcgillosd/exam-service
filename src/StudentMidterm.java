import java.text.ParseException;
import java.text.SimpleDateFormat;

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
	private String term;
	
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
		}
		catch (ParseException e) {
			e.printStackTrace();
			String message = "There is an error in the time of the exam field";
			new Message(message);
		}
	}
	public String getNameProf() {
		return nameProf;
	}
	public void setNameProf(String nameProf) {
		this.nameProf = nameProf;
	}
	
	// to be changed, have to accept two fields (hours and minutes)
	// and calculate the length
	public void setLengthMidterm(String examLength) {
		this.examLength = 60; // to be changed when length is ready on the webpage
	}
	public void setExamLength() { // TODO: time change
		int time = 60; // to be changed to the real time for midterms
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
