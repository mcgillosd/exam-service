import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * 
 */

/**
 * @author OSD Admin
 *
 */
public class Log {
	public Log(String message) {
	
		java.nio.file.Path path = Paths.get(System.getProperty("user.home"),"logs", "exams.log");
		File file = new File(path.toString());
		if (! file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		Logger logger = Logger.getLogger("MyLog");  
		logger.setUseParentHandlers(false);
		
		FileHandler fh;  
	          
		try {  
			// This block configure the logger with handler and formatter 
			int limit = 1000000; // 1 Mb  
			fh = new FileHandler(path.toString(), limit,1,true);  
		
			logger.addHandler(fh);  
			
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
	              
			logger.info(message); 
			
	              
	    } catch (SecurityException e) {  
	    	e.printStackTrace();  
	    } catch (IOException e) {  
	    	e.printStackTrace();  
	    }  
		
		logger.setUseParentHandlers(false);
	}
}
