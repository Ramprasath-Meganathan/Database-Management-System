import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import logging.EventLogger;
import logging.GeneralLogger;

public class Insert {

	public String isTableLocked(String sql, String dbName) {
		String[] sqlArr = sql.split(" ");
		String tableName = sqlArr[2];
		File file = new File("src/files/" + dbName + "/" + tableName + ".json");
		try {
			InputStream tableStream = new FileInputStream(file);
			JSONTokener tokener = new JSONTokener(tableStream);
			org.json.JSONObject object = new org.json.JSONObject(tokener);
			if (!object.getString("lock").equals("0")) {
				return object.getString("lock");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insert(String sql, String databasename) {
		GeneralLogger log1 = new GeneralLogger();
		EventLogger log2 = new EventLogger();
		log1.log("Insert", databasename);
		sql = sql.trim();
		sql = sql.replaceAll("[^a-zA-Z0-9]", " ");
		String[] splited = sql.split("\\s+");
		String tablename = splited[2].toUpperCase();

		ArrayList<String> columnName = new ArrayList<String>();
		ArrayList<String> columnValues = new ArrayList<String>();
		int i = 3;
		while (!splited[i].equalsIgnoreCase("VALUES")) {
			columnName.add(splited[i].toUpperCase());
			i++;
		}
		i++;

		while (i < splited.length) {
			columnValues.add(splited[i]);
			i++;
		}
		JSONParser jsonParser = new JSONParser();
		try {
			FileReader reader = new FileReader("src/files/" + databasename + "/" + tablename + ".json");

			Object obj = jsonParser.parse(reader);
			JSONArray datalist = (JSONArray) ((JSONObject) obj).get("datalist");

			for (int i2 = 0; i2 < columnValues.size();) {
				JSONObject row = new JSONObject();
				for (int i1 = 0; i1 < columnName.size(); i1++) {
					row.put(columnName.get(i1), columnValues.get(i2));
					i2++;
				}
				datalist.add(row);
			}

			((JSONObject) obj).put("datalist", datalist);
			FileWriter file = new FileWriter("src/files/" + databasename + "/" + tablename + ".json");
			file.write(obj.toString());
			file.flush();
			log2.log(sql, databasename, tablename);

		} catch (FileNotFoundException e) {
			log2.error(sql, databasename, tablename);
			e.printStackTrace();
		} catch (IOException e) {
			log2.error(sql, databasename, tablename);
			e.printStackTrace();
		} catch (ParseException e) {
			log2.error(sql, databasename, tablename);
			e.printStackTrace();
		}

	}
}

//INSERT INTO Customers(CustomerName, ContactName, Address, City, PostalCode, Country)
//VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');