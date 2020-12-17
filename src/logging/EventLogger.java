package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventLogger {
	Logger logger = Logger.getLogger(EventLogger.class.getName());

	public void log(String query, String databasename, String tablename) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventlogs.log", true);

			logger.addHandler(file);
			logger.info("query: " + query + ", databasename: " + databasename + ", tablename: " + tablename
					+ ", query successful ");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void authlog(String username) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventlogs.log", true);

			logger.addHandler(file);
			logger.info("user authenticated: " + username);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reglog(String username) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventlogs.log", true);

			logger.addHandler(file);
			logger.info("user registered: " + username);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void errorreglog(String username) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventlogs.log", true);

			logger.addHandler(file);
			logger.log(Level.SEVERE, "user not registered: " + username);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void errorauthlog(String username) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventlogs.log", true);

			logger.addHandler(file);
			logger.log(Level.SEVERE, "user not authenticated: " + username);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void error(String query, String databasename, String tablename) {
		FileHandler file;
		try {
			file = new FileHandler("src/logfiles/Eventogs.log", true);

			logger.addHandler(file);
			logger.log(Level.SEVERE, "query:" + query + ", databasename: " + databasename + "tablename: " + tablename
					+ ", Error occured , query unsuccessful ");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
