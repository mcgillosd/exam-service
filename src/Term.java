/*
 * Term.java
 * 
 * Created on 2013-06-11 11:43:59 AM
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Determines the term based on a month and a year.  
 * The term consists of two words: name of the term (Summer, Fall, Winter) 
 * and year of the term in which exam will be taken. </p>
 * <p>Year for the files (which file to be updated)
 * is chosen according to the date of the exam.
 * In other cases must be set to the current year</p>
 *  
 * @author Olga Tsibulevskaya
 */
public class Term { 
			
	private String term;
	private String season;
	private int year;
	/**
	 * Creates a term, based on the current date
	 */
	public Term() {
		Calendar cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1; // months start from 0
		setTerm(month);
	}
	/**
	 * Creates a term according to the month in the arguments
	 * @param month	the month for which to determine a term
	 */
	public Term(int month) {
		Calendar cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		setTerm(month);
	}
	public Term(String season, int year) {
		term = season + " " + year;
		this.season = season;
		this.year = year;
	}
	/**
	 * Creates a term according to the date provided in the arguments
	 * @param date the date for which to determine a term
	 */
	public Term(Date date) {
		SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
		year = Integer.parseInt(formatYear.format(date));
		SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
		int month = Integer.parseInt(formatMonth.format(date));
		setTerm(month);
	}
	/**
	 * Chooses the correct term according to the month,
	 * creates a term by appending the name of the term and its year
	 * 
	 * @param month month of the term
	 */
	public void setTerm(int month) {
		if (month > 0 && month < 5) {
			term = "Winter " + year;
			season = "Winter";
		}
		else if (month > 4 && month < 9) {
			term = "Summer " + year;
			season = "Summer";
		}
		else if (month > 8 && month < 13) {
			term = "Fall " + year;
			season = "Fall";
		}
	} 
	public Term termNext() {
		String seasonNext = "";
		int yearNext = 0;
		if (season.equals("Fall")) {
			seasonNext = "Winter";
			yearNext = year + 1;
		}
		if (season.equals("Winter")) {
			seasonNext = "Summer";
			yearNext = year;
		}
		if (season.equals("Summer")) {
			seasonNext = "Fall";
			yearNext = year;
		}
		Term next = new Term(seasonNext, yearNext);
		return next;
	}
	public String getTerm() {
		return term;
	}
	public int getYear() {
		return year;
	}
	public String getSeason() {
		return season;
	}
	/**
	 * Returns the final month of the term, for finals
	 * The string for month contains the year as well
	 * @return the final month of the term
	 */
	public String getMonth() {
		String[] array = term.split(" ");
		if (array[0].equalsIgnoreCase("Winter"))
			return "april " + array[1];
		else if (array[0].equalsIgnoreCase("Summer"))
			return "august " + array[1];
		else 
			return "december " + array[1]; 
	}
}
