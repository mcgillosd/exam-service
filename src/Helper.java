import java.util.Calendar;
import java.util.Date;

/*
 * Created on Aug 12, 2013 3:34:29 PM
 */

/**
 * @author olga
 *
 */
public class Helper {

	static Date noon;
	/**
	 * 
	 */
	public Helper() {
		// TODO Auto-generated constructor stub
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
