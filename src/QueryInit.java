import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QueryInit {

	public void init(String username) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Choose a schema OR Drop a schema: ");
		String sql = scanner.nextLine();

		if (sql.toUpperCase().contains("USE")) {
			sql = sql.trim();
			sql = sql.replaceAll("[^a-zA-Z0-9]", " ");
			String[] splited1 = sql.split("\\s+");
			String databasename = splited1[1];
			try {
				JSONParser jsonParser = new JSONParser();
				FileReader reader;
				reader = new FileReader("src/files/databases.json");
				Object obj;
				obj = jsonParser.parse(reader);

				JSONArray databaseList1 = (JSONArray) obj;

				databaseList1.forEach(db -> {
					if (((JSONObject) db).get("dbname").equals(databasename)) {
						System.out.println("Database exists");

					}
				});
				while (true) {
					System.out.println("Enter your query or enter 0 to exit:");
					String sql1 = scanner.nextLine();
					if (sql1.equals("0")) {
						System.out.println("ended");
						break;
					} else {

						QueryProcessing q1 = new QueryProcessing();
						q1.QProcess(sql1, databasename, username, false);
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			QueryProcessing q1 = new QueryProcessing();
			q1.QProcess(sql, null, username, false);
		}

	}
}