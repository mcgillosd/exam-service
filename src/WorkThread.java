import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 
 */

/**
 * @author OSD Admin
 *
 */
public class WorkThread extends Thread {

	static AtomicBoolean stopWork = new AtomicBoolean();
	WebConnect wc;
	boolean update;
	
		
	public WorkThread(WebConnect wc, boolean update) {
		 this.wc = wc;
		 this.update = update;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String html = wc.getContent();
		try {
			stopWork.set(true);
			new StudentsMidtermInit(update).start(html);
			stopWork.set(false);
		} catch (FileNotFoundException e1) {
			return;
		}
	}
}
