import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Transactions {

	public ArrayList<String> lockedTables = new ArrayList<String>();

	public void releaseLocks(String dbName, String username) {
		if (lockedTables.size() == 0) {
			System.out.println("No Locked Tables");
		} else {
			for (String tableName : lockedTables) {
				File file = new File("src/files/" + dbName + "/" + tableName + ".json");
				try {
					InputStream tableStream = new FileInputStream(file);
					JSONTokener tokener = new JSONTokener(tableStream);
					JSONObject object = new JSONObject(tokener);
					if (object.getString("lock").equalsIgnoreCase("1_" + username)) {
						object.put("lock", 0);
						FileWriter updateFile = new FileWriter(file);
						updateFile.write(object.toString());
						updateFile.close();
						System.out.println("Table lock released from " + dbName + " > " + tableName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void lockTables(String queryString, String dbName, String username) {
		String tableName = "";
		String[] queryType = queryString.split(" ");
		if (queryType[0].equalsIgnoreCase("UPDATE")) {
			tableName = queryType[1];
			lockedTables.add(tableName);
			setLock(tableName, dbName, username);
		}
		if (queryType[0].equalsIgnoreCase("INSERT")) {
			tableName = queryType[2];
			lockedTables.add(tableName);
			setLock(tableName, dbName, username);
		}
		if (queryType[0].equalsIgnoreCase("DELETE")) {
			tableName = queryType[2];
			lockedTables.add(tableName);
			setLock(tableName, dbName, username);
		}
	}

	public void setLock(String tableName, String dbName, String username) {
		File file = new File("src/files/" + dbName + "/" + tableName + ".json");
		try {
			InputStream tableStream = new FileInputStream(file);
			JSONTokener tokener = new JSONTokener(tableStream);
			JSONObject object = new JSONObject(tokener);
			if (object.getString("lock").equals("0")) {
				object.put("lock", "1_" + username);
				FileWriter updateFile = new FileWriter(file);
				updateFile.write(object.toString());
				updateFile.close();
				System.out.println("Table lock set on " + dbName + " > " + tableName + " by user:" + username);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void beginTransactions(String username, String dbName) {
		System.out.println("Starting Transaction in Schema " + dbName + " by User " + username);
		HashMap<Integer, String> sequenceMap = new HashMap<Integer, String>();
		QueryProcessing process = new QueryProcessing();
		int seqCounter = 1;
		int rollbackKey = -1;
		Scanner scanner = new Scanner(System.in);
		Boolean bool = true;
		while (bool) {
			System.out.println("Enter Sequence " + seqCounter + ":");
			String queryString = scanner.nextLine();
			String queryType = queryString.split(" ")[0];
			if (queryType.equalsIgnoreCase("UPDATE") || queryType.equalsIgnoreCase("INSERT")
					|| queryType.equalsIgnoreCase("DELETE")) {
				lockTables(queryString, dbName, username);
			}
			if (queryString.trim().equalsIgnoreCase("COMMIT")) {
				bool = false;
				releaseLocks(dbName, username);
				break;
			}
			sequenceMap.put(seqCounter, queryString);
			if (queryString.trim().equalsIgnoreCase("ROLLBACK")) {
				// In case of multiple Rollback statements - get the last Rollback
				rollbackKey = seqCounter;
			}
			seqCounter++;
		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String transactionName = "T_" + username + "_" + dbName + "_" + timestamp.getTime();
		JSONObject jsonObject = new JSONObject();
		try {
			Set<Integer> keySet = sequenceMap.keySet();
			// If Rollback exists - do not perform any queries before it
			if (rollbackKey != -1) {
				for (int key : keySet) {
					if (key > rollbackKey) {
						String query = sequenceMap.get(key);
						process.QProcess(query, dbName, username, true);
					}
				}
			} else {
				for (int key : keySet) {
					String query = sequenceMap.get(key);
					process.QProcess(query, dbName, username, true);
				}
			}

			// Save Transaction Details
			jsonObject.put("username", username);
			jsonObject.put("database", dbName);
			jsonObject.put("sequence", sequenceMap);
			File file = new File("src/transactions/" + transactionName + ".json");
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(jsonObject.toString());
			writer.close();

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Your Transaction Sequence: \n" + jsonObject.toString());
	}

}
