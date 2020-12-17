public class QueryProcessing {
	String databasename = null;
	boolean exit = true;

	public void QProcess(String sql, String databasename, String username, Boolean fromTransactions) {
		String[] keywords = sql.trim().split(" ");

		if (sql.toUpperCase().contains("CREATE") && sql.toUpperCase().contains("DATABASE")) {
			Database d;
			d = new Database();
			d.createDatabase(sql);

		}

		else if (sql.toUpperCase().contains("CREATE") && sql.toUpperCase().contains("TABLE") && exit == true) {

			Create c;
			if (sql.contains("PRIMARY KEY")) {
				try {
					c = new Create();
					c.createTableKeys(sql, databasename);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					c = new Create();
					c.createTable(sql, databasename);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if (sql.toUpperCase().contains("INSERT")) {

			Insert c;
			try {
				c = new Insert();
				if (c.isTableLocked(sql, databasename) != null) {
					String user = c.isTableLocked(sql, databasename).split("_")[1];
					System.out.println("Table is locked by " + user);
					throw new Exception("Table Lock In Place");
				}
				c.insert(sql, databasename);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (sql.toUpperCase().contains("SELECT")) {
			Select s;
			try {
				s = new Select();
				s.select(sql, databasename);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (sql.toUpperCase().contains("UPDATE") && sql.toUpperCase().contains("SET")
				&& sql.toUpperCase().contains("WHERE")) {
			Update update;
			try {
				update = new Update();
				if (!update.tableExists(sql, databasename)) {
					throw new Exception("Table Does Not Exist");
				}
				if (update.isTableLocked(sql, databasename) != null) {
					String user = update.isTableLocked(sql, databasename).split("_")[1];
					System.out.println("Table is locked by " + user);
					throw new Exception("Table Lock In Place");
				}
				update.updateTable(sql, databasename);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sql.contains("DELETE") && sql.contains("FROM")) {
			Delete delete;
			try {
				delete = new Delete();
				if (!delete.tableExists(sql, databasename)) {
					throw new Exception("Table Does Not Exist");
				}
				if (delete.isTableLocked(sql, databasename) != null) {
					String user = delete.isTableLocked(sql, databasename).split("_")[1];
					System.out.println("Table is locked by " + user);
					throw new Exception("Table Lock In Place");
				}
				delete.deleteQuery(sql, databasename);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sql.toUpperCase().contains("SHOW") && sql.toUpperCase().contains("TABLES") && keywords.length == 2) {
			Database database = new Database();
			database.showTables(databasename);
		} else if (sql.toUpperCase().contains("DROP") && sql.toUpperCase().contains("DATABASE")
				&& keywords.length == 3) {
			Database database = new Database();
			String dbName = keywords[2].replace(";", "");
			System.out.println(database.dropSchema(dbName));
		} else if (sql.toUpperCase().contains("DROP") && sql.toUpperCase().contains("TABLE") && keywords.length == 3) {
			Delete delete;
			try {
				delete = new Delete();
				if (!delete.tableExists(sql, databasename)) {
					throw new Exception("Table Does Not Exist");
				}
				delete.dropTable(sql, databasename);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sql.toUpperCase().contains("TRUNCATE") && sql.toUpperCase().contains("TABLE")
				&& keywords.length == 3) {
			Delete delete = new Delete();
			delete.deleteQuery(sql, databasename);
		} else if (sql.trim().equalsIgnoreCase("START TRANSACTION") || sql.trim().equalsIgnoreCase("BEGIN WORK")) {
			Transactions transactions = new Transactions();
			transactions.beginTransactions(username, databasename);
		} else {
			System.out.println("Incorrect Syntax - Please try again");
		}

	}
}
