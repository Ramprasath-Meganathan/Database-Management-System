//import jdk.nashorn.internal.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logging.EventLogger;
import logging.GeneralLogger;

public class Create {

	public Boolean tableExists(String table, String dbName) {
		File tableFile = new File("src/files/" + dbName + "/" + table + ".json");
		if (tableFile.exists() && !tableFile.isDirectory()) {
			return true;
		}
		return false;
	}

	public void createTableKeys(String sql, String databasename) throws Exception {
		sql = sql.toUpperCase();
		sql = sql.trim();
		String beforeKeys = sql.substring(0, sql.indexOf("PRIMARY KEY")).replaceAll("[^a-zA-Z0-9]", " ");
		String[] splited = beforeKeys.split("\\s+");
		String tableName = splited[2];

		// Primary Key
		String primaryKey = sql.substring(sql.indexOf("PRIMARY KEY"), sql.indexOf(")")).replace("PRIMARY KEY", "");
		primaryKey = primaryKey.replace("(", "").trim();

		// Foreign Keys
		String[] fkKeys = null;
		ArrayList<String> relationsList = new ArrayList<String>();
		if (sql.contains("FOREIGN KEY")) {
			fkKeys = sql.substring(sql.indexOf("FOREIGN KEY"), sql.length()).split(",");
			for (int i = 0; i < fkKeys.length; i++) {
				fkKeys[i] = fkKeys[i].trim();
				if (!fkKeys[i].contains("REFERENCES")) {
					throw new Exception("Invalid Foreign Key Constraint - No Table Referenced");
				}
				String refString = fkKeys[i].substring(fkKeys[i].indexOf("REFERENCES"), fkKeys[i].length());

				String foreignKey = fkKeys[i].substring(fkKeys[i].indexOf("FOREIGN KEY"), fkKeys[i].indexOf(")"))
						.replace("FOREIGN KEY", "").replace("(", "").trim();
				String refTable = refString.substring(refString.indexOf("REFERENCES"), refString.indexOf("("))
						.replace("REFERENCES", "").trim();
				String refKey = refString.substring(refString.indexOf("("), refString.indexOf(")")).replace("(", "")
						.trim();

				if (!tableExists(refTable, databasename)) {
					throw new Exception("Referenced Table " + refTable + " does not exist!");
				}

				String relations = tableName + ":" + foreignKey + " *--1 " + refTable + ":" + refKey;
				relationsList.add(relations);
			}
		} // End Foreign

		Map<String, String> columns = new HashMap<String, String>();
		for (int i = 3; i < splited.length - 1; i = i + 2) {
			columns.put(splited[i], splited[i + 1]);
		}
		JSONObject obj = new JSONObject();
		JSONArray datalistarray = new JSONArray();
		JSONArray relArray = new JSONArray();
		obj.put("tablename", tableName);
		obj.put("lock", "0");
		obj.put("datalist", datalistarray);
		JSONArray arrayElementOneArray = new JSONArray();
		JSONObject arrayElementOneArrayElementOne = new JSONObject();
		for (Map.Entry<String, String> entry : columns.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(primaryKey)) {
				arrayElementOneArrayElementOne.put("*" + entry.getKey(), entry.getValue());
			}
			arrayElementOneArrayElementOne.put(entry.getKey(), entry.getValue());

		}
		arrayElementOneArray.put(arrayElementOneArrayElementOne);
		obj.put("columnlist", arrayElementOneArray);
		relArray.put(relationsList);
		obj.put("relations", relArray);
		try {
			File fileKey = new File("src/files/" + databasename + "/" + tableName + ".json");

			fileKey.createNewFile();
			FileWriter writerkeys = new FileWriter(fileKey);
			writerkeys.write(obj.toString());
			writerkeys.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createTable(String sql, String databasename) throws JSONException {
		GeneralLogger log1 = new GeneralLogger();
		EventLogger log2 = new EventLogger();
		log1.log("Create", databasename);
		boolean exit = true;
		sql = sql.toUpperCase();
		sql = sql.trim();
		sql = sql.replaceAll("[^a-zA-Z0-9]", " ");
		String[] splited = sql.split("\\s+");
		ArrayList<String> tablename = new ArrayList<String>();
		tablename.add(splited[2]);
		String table = splited[2];
		Map<String, String> columns = new HashMap<String, String>();
		for (int i = 3; i < splited.length - 1; i = i + 2) {
			columns.put(splited[i], splited[i + 1]);
		}
		java.util.List<String> results = new ArrayList<String>();

		File[] files = new File("src/files/" + databasename).listFiles();
		// If this pathname does not denote a directory, then listFiles() returns null.

		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
			}
		}

		for (int i = 0; i < results.size(); i++) {
			if (results.get(i).equals(table + ".json")) {
				System.out.println("Table already exists in this database");
				exit = false;
			}
		}
		if (exit) {
			JSONObject obj = new JSONObject();
			JSONObject obj1 = new JSONObject();
			JSONArray datalistarray = new JSONArray();
			obj.put("tablename", table);
			obj.put("lock", "0");
			obj1.put("tablename", table);
			obj.put("datalist", datalistarray);
			JSONArray arrayElementOneArray = new JSONArray();
			JSONObject arrayElementOneArrayElementOne = new JSONObject();
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				arrayElementOneArrayElementOne.put(entry.getKey(), entry.getValue());

			}
			arrayElementOneArray.put(arrayElementOneArrayElementOne);
			obj.put("columnlist", arrayElementOneArray);
			obj1.put("columnlist", arrayElementOneArray);
			try {
				File file = new File("src/files/" + databasename + "/" + table + ".json");
				File file1 = new File("src/metadata/" + table + ".json");

				file.createNewFile();
				file1.createNewFile();
				FileWriter writer = new FileWriter(file);
				FileWriter writer1 = new FileWriter(file1);
				writer.write(obj.toString());
				writer1.write(obj1.toString());
				writer.close();
				writer1.close();
				log2.log(sql, databasename, table);
			} catch (IOException e) {
				log2.error(sql, databasename, table);
				e.printStackTrace();
			}
		}
	}
}
