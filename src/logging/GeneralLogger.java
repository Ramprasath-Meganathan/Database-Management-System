package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class GeneralLogger {

	Logger logger = Logger.getLogger(GeneralLogger.class.getName());

	public void log(String query, String databasename) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Generallogs.log", true);

			logger.addHandler(file);
			logger.info("querytype:" + query + ", databasename: " + databasename + ", query initiated ");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
