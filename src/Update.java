//import jdk.nashorn.internal.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import logging.EventLogger;
import logging.GeneralLogger;

public class Update {

	public Boolean tableExists(String sql, String dbName) {
		String[] sqlArr = sql.split(" ");
		String tablename = sqlArr[1];
		File tableFile = new File("src/files/" + dbName + "/" + tablename + ".json");
		if (tableFile.exists() && !tableFile.isDirectory()) {
			return true;
		}
		return false;
	}

	public String isTableLocked(String sql, String dbName) {
		String[] sqlArr = sql.split(" ");
		String tableName = sqlArr[1];
		File file = new File("src/files/" + dbName + "/" + tableName + ".json");
		try {
			InputStream tableStream = new FileInputStream(file);
			JSONTokener tokener = new JSONTokener(tableStream);
			JSONObject object = new JSONObject(tokener);
			if (!object.getString("lock").equals("0")) {
				return object.getString("lock");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean keyExists(String key, HashMap<Integer, HashMap<String, String>> database) {
		for (int i = 0; i < database.size(); i++) {
			if (database.get(i).containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public Integer valueExists(String value, HashMap<Integer, HashMap<String, String>> database) {
		for (int i = 0; i < database.size(); i++) {
			for (HashMap.Entry<String, String> entry : database.get(i).entrySet()) {
				if (entry.getValue().equalsIgnoreCase(value)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void updateTable(String sql, String dbName) {
		GeneralLogger log1 = new GeneralLogger();
		EventLogger log2 = new EventLogger();
		log1.log("Update", dbName);
		HashMap<Integer, HashMap<String, String>> dataID = new HashMap<Integer, HashMap<String, String>>();
		String og_sql = sql.toUpperCase();
		sql = sql.trim();
		sql = sql.replaceAll("[^a-zA-Z0-9]", " ");
		String[] sqlArr = sql.split("\\s+");
		String tablename = sqlArr[1];

		File tableFile = new File("src/files/" + dbName + "/" + tablename + ".json");
		try {

			if (sqlArr[2].equals("SET") == false && !sql.contains("WHERE")) {
				throw new Exception("Incorrect Syntax - SET and WHERE Clauses not found");
			}

			InputStream tableStream = new FileInputStream(tableFile);
			JSONTokener tokener = new JSONTokener(tableStream);
			JSONObject object = new JSONObject(tokener);
			JSONArray dataJSON = object.getJSONArray("datalist");

			for (int i = 0; i < dataJSON.length(); i++) {
				HashMap<String, String> dataValues = new HashMap<String, String>();
				JSONObject jsonObject = dataJSON.getJSONObject(i);
				Iterator<String> keys = jsonObject.keys();

				while (keys.hasNext()) {
					String key = keys.next();
					dataValues.put(key, jsonObject.get(key).toString());
				}

				dataID.put(i, dataValues);
			}

			System.out.println(dataID);
			Boolean isOr = false;
			Boolean isAnd = false;

			// SET Clause
			String setClause = og_sql.substring(og_sql.indexOf("SET"), og_sql.indexOf("WHERE"));
			setClause = setClause.replace("SET ", "");
			String[] setParams = setClause.split(",");

			// WHERE Clause
			String whereClause = og_sql.substring(og_sql.indexOf("WHERE"), og_sql.length());
			HashMap<String, String> whereHashMap = new HashMap<String, String>();
			whereClause = whereClause.replace("WHERE", "").trim();
			whereClause = whereClause.replaceAll("[^a-zA-Z0-9=]", "");
			String[] whereConditions = null;
			if (whereClause.contains("AND")) {
				whereConditions = whereClause.split("AND");
				isAnd = true;
			} else if (whereClause.contains("OR")) {
				whereConditions = whereClause.split("OR");
				isOr = true;
			}
			if (whereConditions == null) {
				String whereKey = whereClause.split("=")[0];
				String whereValue = whereClause.split("=")[1];
				Integer whereID = valueExists(whereValue, dataID);
				if (!keyExists(whereKey, dataID) || (whereID == -1)) {
					throw new Exception("Invalid WHERE Condition - Please try again");
				}

				for (int j = 0; j < dataJSON.length(); j++) {
					JSONObject jsonObject = dataJSON.getJSONObject(j);
					Iterator<String> keys = jsonObject.keys();

					while (keys.hasNext()) {
						String key = keys.next();
						if (key.equalsIgnoreCase(whereKey) && jsonObject.getString(key).equalsIgnoreCase(whereValue)) {
							for (int i = 0; i < setParams.length; i++) {
								setParams[i] = setParams[i].trim();
								String setkey = setParams[i].split("=")[0];
								String newValue = setParams[i].split("=")[1];
								newValue = newValue.replaceAll("[\'\"]", "");
								if (!keyExists(setkey, dataID)) {
									throw new Exception("Key Does Not Exist - Please check your syntax");
								}
								System.out.println("Gonna update" + jsonObject);
								jsonObject.put(setkey, newValue);
							}
						}

					}
					System.out.println(jsonObject);
				}
			} else {
				String setkey = null;
				String newValue = null;
				for (int j = 0; j < dataJSON.length(); j++) {
					int trueCount = 0;
					JSONObject jsonObject = dataJSON.getJSONObject(j);
					Iterator<String> keys = jsonObject.keys();
					System.out.println("Checking: " + jsonObject);
					while (keys.hasNext()) {
						String key = keys.next();
						for (int k = 0; k < whereConditions.length; k++) {
							String whereClauseC = whereConditions[k].trim();
							String whereKey = whereClauseC.split("=")[0];
							String whereValue = whereClauseC.split("=")[1];
							Integer whereID = valueExists(whereValue, dataID);
							if (!keyExists(whereKey, dataID) || (whereID == -1)) {
								throw new Exception("Invalid WHERE Conditionss - Please try again"
										+ keyExists(whereKey, dataID) + " " + whereID);
							}
							if (key.equalsIgnoreCase(whereKey)
									&& jsonObject.getString(key).equalsIgnoreCase(whereValue)) {
								trueCount++;
							}
						}
					}

					if (isAnd && trueCount == whereConditions.length) {
						for (int i = 0; i < setParams.length; i++) {
							setParams[i] = setParams[i].trim();
							setkey = setParams[i].split("=")[0];
							newValue = setParams[i].split("=")[1];
							newValue = newValue.replaceAll("[\'\"]", "");
							if (!keyExists(setkey, dataID)) {
								throw new Exception("Key Does Not Exist - Please check your syntax");
							}
							jsonObject.put(setkey, newValue);
						}
						System.out.println("Updated: " + jsonObject);
					}
					System.out.println("TrueC: " + trueCount);
					if (isOr && trueCount > 0) {
						for (int i = 0; i < setParams.length; i++) {
							setParams[i] = setParams[i].trim();
							setkey = setParams[i].split("=")[0];
							newValue = setParams[i].split("=")[1];
							newValue = newValue.replaceAll("[\'\"]", "");
							if (!keyExists(setkey, dataID)) {
								throw new Exception("Key Does Not Exist - Please check your syntax");
							}
							jsonObject.put(setkey, newValue);
						}
						System.out.println("Updated: " + jsonObject);
					}

				}
			}

			FileWriter updateFile = new FileWriter(tableFile);
			updateFile.write(object.toString());
			updateFile.close();
			log2.log(sql, dbName, tablename);
			System.out.println("Records Updated!");
//			Updating the DATA Strucuture
//			for (int i = 0; i < setParams.length; i++) {
//				setParams[i] = setParams[i].trim();
//				System.out.println(setParams[i]);
//				String key = setParams[i].split("=")[0];
//				String newValue = setParams[i].split("=")[1];
//				if (!keyExists(key, dataID)) {
//					throw new Exception("Key Does Not Exist - Please check your syntax");
//				}
//				dataID.get(whereID).replace(key, newValue);
//			}
//			System.out.println("------------");
//			System.out.println(dataID.toString());

		} catch (FileNotFoundException e) {
			System.out.println("Unable to read file");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Incorrect Syntax - Please try again");
			e.printStackTrace();
		}
	}
}
