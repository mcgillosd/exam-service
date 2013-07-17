import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/*
 * Created on Jul 17, 2013 2:39:08 PM
 */

/**
 * Class creates a Student for Finals
 * 
 * @author Olga Tsibulevskaya
 */
public class StudentFinal extends Student {

	private String sidFull;
	private String nameProfFirst;
	private String nameProfLast; 
	
	private boolean timeChanged = false;
	private boolean conflict = false;
	
	public StudentFinal() {
		super();
	}
	
	public String getSidFull() {
		return sidFull;
	}
	public void setSidFull(String sid) {
		this.sidFull = sid;
	}
	
	public void setExamDate(Date date) {
		examDate = date;
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
	public void setExamLength() { 
		int time = 3*60;  // for finals, exam length is 3 hours
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
		setExamStartTime();
		setExamFinishTime();
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
	
	public boolean equalProf(StudentFinal s) {
		int comp = nameProfLast.compareTo(s.nameProfLast);
		if (comp != 0)
			return false;
		return (nameProfFirst.compareTo(s.nameProfFirst) == 0);
	}
	
	// sorts by the prof name, used for lists of students for profs 
	public static class ProfComparator implements Comparator<StudentFinal> {
		public int compare(StudentFinal s1, StudentFinal s2) {
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
