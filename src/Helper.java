/*
 * Helper.java
 * Created on Aug 12, 2013 3:34:29 PM
 */

import java.util.Calendar;
import java.util.Date;

/**
 * Suppose to have small different functions
 * 
 * @author Olga Tsibulevskaya
 */
public class Helper {

	static Date noon;
	/**
	 * 
	 */
	public Helper() {
		
	}
	public Date getNoon(Date startTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 59);
		noon = cal.getTime();
		return noon;
	}
}
