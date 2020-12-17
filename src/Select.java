import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import logging.EventLogger;
import logging.GeneralLogger;

public class Select {

	public void select(String sql, String databasename) {
		GeneralLogger log1=new GeneralLogger();
		EventLogger log2=new EventLogger();
		log1.log("Select", databasename);
		sql = sql.toUpperCase();
		sql = sql.trim();
		sql = sql.replaceAll("[^a-zA-Z0-9*=!=><]", " ");
		String[] splited = sql.split("\\s+");
		int i = 0;
		while (!splited[i].equalsIgnoreCase("from")) {
			i++;
		}
		String tablename = splited[i + 1];

		if (sql.contains("*") && sql.contains("WHERE")) {
			int i3 = 0;
			while (!splited[i3].equalsIgnoreCase("where")) {
				i3++;
			}
			String conditionColumn = splited[i3 + 1];
			String operator = splited[i3 + 2];
			String conditionValue = splited[i3 + 3];
			JSONParser jsonParser = new JSONParser();
			try {
				FileReader reader = new FileReader("src/files/" + databasename + "/" + tablename + ".json");
				Object obj = jsonParser.parse(reader);
				JSONArray columnlist = (JSONArray) ((JSONObject) obj).get("columnlist");

				JSONObject columns = (JSONObject) columnlist.get(0);
				Set<String> keys = columns.keySet();
				Iterator<String> it1 = keys.iterator();
				while (it1.hasNext()) {
					System.out.print(it1.next() + " | ");

				}
				System.out.println();

				JSONArray datalist = (JSONArray) ((JSONObject) obj).get("datalist");

				datalist.forEach(row -> {
					if (operator.equals("=")) {
						if ((((JSONObject) row).get(conditionColumn)).equals(conditionValue)) {
							Iterator<String> it = keys.iterator();
							while (it.hasNext()) {

								System.out.print(((JSONObject) row).get(it.next()) + " | ");

							}
						}
					}
					if (operator.equals("!=")) {
						if (!(((JSONObject) row).get(conditionColumn)).equals(conditionValue)) {
							Iterator<String> it = keys.iterator();
							while (it.hasNext()) {

								System.out.print(((JSONObject) row).get(it.next()) + " | ");

							}
							System.out.println();
						}
					}
				});
				log2.log(sql, databasename,tablename);

			} catch (FileNotFoundException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			} catch (IOException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			} catch (ParseException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			}
			System.out.println();

//			SELECT * FROM Customers
//			WHERE Country='Mexico';
		} else if (sql.contains("*")) {
			JSONParser jsonParser = new JSONParser();
			try {
				FileReader reader = new FileReader("src/files/" + databasename + "/" + tablename + ".json");
				Object obj = jsonParser.parse(reader);
				JSONArray columnlist = (JSONArray) ((JSONObject) obj).get("columnlist");

				JSONObject columns = (JSONObject) columnlist.get(0);
				Set<String> keys = columns.keySet();
				Iterator<String> it1 = keys.iterator();
				while (it1.hasNext()) {
					System.out.print(it1.next() + " | ");

				}
				System.out.println();

				JSONArray datalist = (JSONArray) ((JSONObject) obj).get("datalist");

				datalist.forEach(row -> {

					Iterator<String> it = keys.iterator();
					while (it.hasNext()) {
						System.out.print(((JSONObject) row).get(it.next()) + " | ");

					}
					System.out.println();
				});
				log2.log(sql, databasename,tablename);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			System.out.println();

		} else {

			ArrayList<String> columnnames = new ArrayList<>();
			int i1 = 1;
			while (!splited[i1].equalsIgnoreCase("from")) {
				columnnames.add(splited[i1]);
				i1++;
			}
			for (int i3 = 0; i3 < columnnames.size(); i3++) {
				System.out.print(columnnames.get(i3) + " | ");
			}
			System.out.println();
			JSONParser jsonParser1 = new JSONParser();
			try {
				FileReader reader = new FileReader("src/files/" + databasename + "/" + tablename + ".json");
				Object obj = jsonParser1.parse(reader);

				JSONArray datalist = (JSONArray) ((JSONObject) obj).get("datalist");

				datalist.forEach(row -> {
					for (int i2 = 0; i2 < columnnames.size(); i2++) {
						System.out.print(((JSONObject) row).get(columnnames.get(i2)));
						System.out.println(" | ");
					}
				});
				System.out.println();
				log2.log(sql, databasename,tablename);

			} catch (FileNotFoundException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			} catch (IOException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			} catch (ParseException e) {
				log2.error(sql, databasename,tablename);
				e.printStackTrace();
			}
		}

	}
}
