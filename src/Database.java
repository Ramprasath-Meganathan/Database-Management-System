import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Database {
	boolean exit = true;

	public void showTables(String dbName) {
		File folder = new File("src/files/" + dbName);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".json")) {
				System.out.println(listOfFiles[i].getName().replace(".json", ""));
			}
		}
	}

	public String dropSchema(String dbName) {
		File schemaFile = new File("src/files/databases.json");
		int count = 0;

		try {
			InputStream dbStream = new FileInputStream(schemaFile);
			JSONTokener tokener = new JSONTokener(dbStream);
			org.json.JSONArray jsonArray = new org.json.JSONArray(tokener);

			for (int i = 0; i < jsonArray.length(); i++) {
				org.json.JSONObject object = jsonArray.getJSONObject(i);
				System.out.println(object.getString("dbname"));
				if (object.getString("dbname").equalsIgnoreCase(dbName)) {
					count++;
				}
			}
			System.out.println(count);
			if (count == 0) {
				throw new Exception("Database Does Not Exist!");
			}
			System.out.println("Are you sure you want to drop " + dbName
					+ "? Dropping the Database will result in losing all your tables and data.");
			System.out.println("Print 0 to abort, 1 to confirm");
			Scanner scanner = new Scanner(System.in);
			String confirm = scanner.nextLine();
			if (confirm.equalsIgnoreCase("0")) {
				return "Aborted Deletion";
			} else if (confirm.equalsIgnoreCase("1")) {
				File folder = new File("src/files/" + dbName);
				File[] listOfFiles = folder.listFiles();
				for (int i = 0; i < listOfFiles.length; i++) {
					listOfFiles[i].delete();
				}
				folder.delete();

				for (int i = 0; i < jsonArray.length(); i++) {
					org.json.JSONObject object = jsonArray.getJSONObject(i);
					if (object.getString("dbname").equalsIgnoreCase(dbName)) {
						jsonArray.remove(i);
					}
				}
				FileWriter updateFile = new FileWriter(schemaFile);
				updateFile.write(jsonArray.toString());
				updateFile.close();
				return dbName + " Deleted!";
			} else {
				return "Invalid Command - try again";
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unable to Delete Database");
			e.printStackTrace();
		}
		return "Database Deletion Executed";

	}

	public void createDatabase(String sql) {
		sql=sql.toUpperCase();
		sql = sql.trim();
		sql = sql.replaceAll("[^a-zA-Z0-9]", " ");
		String[] splited = sql.split("\\s+");
		String databaseName = splited[2];

		JSONParser jsonParser = new JSONParser();
		try {
			FileReader reader = new FileReader("src/files/databases.json");

			Object obj = jsonParser.parse(reader);
			JSONArray databaseList1 = (JSONArray) obj;

			databaseList1.forEach(db -> {
				if (((JSONObject) db).get("dbname").equals(databaseName)) {
					System.out.println("Database already exists");
					exit = false;

				}
			});
			if (exit) {
				JSONArray databaseList;
				JSONObject json1 = new JSONObject();
				FileReader reader1 = new FileReader("src/files/databases.json");

				json1.put("dbname", databaseName);
				// if databases.json is empty
				if (reader1 == null) {
					databaseList = new JSONArray();
					databaseList.add(json1);
					FileWriter file = new FileWriter("src/files/databases.json");
					file.write(databaseList.toString());
					file.flush();
				}
				// reading from databases file and its not empty
				else {
					Object obj1 = jsonParser.parse(reader1);
					JSONArray databaseListFinal = (JSONArray) obj;
					databaseListFinal.add(json1);
					FileWriter file = new FileWriter("src/files/databases.json");
					file.write(databaseListFinal.toString());
					file.flush();

				}

				String path = "src/files/" + databaseName;
				File file1 = new File(path);
				System.out.println(path);
				boolean bool = file1.mkdir();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
