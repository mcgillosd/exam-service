import java.util.ArrayList;
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
	ArrayList<StudentFinal> list = new ArrayList<StudentFinal>();
	 
	 public WorkThread(ArrayList<StudentFinal> list) {
		 this.list = list;
		 stopWork.set(false);
	 }
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (! stopWork.get()) {
			new Excel().writeListProf(list);
		}
     }
}
