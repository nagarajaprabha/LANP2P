package intranetp2p;

import java.io.File;
import java.sql.*;
import java.util.Calendar;

public class CreateTable {

	Connection con;
	Statement stmt;
	String appDirPath;
	String cacheDirPath;
	String cacheDbFilePath;

	private static final String APPLICATION_DIR_NAME = "LANP2P";
	private static final String CACHE_DIR_NAME = "cache_files";
	private static final String CACHE_DB_FILE_NAME = "cache.db";

	public CreateTable() {
		String homeDir = System.getProperty("user.home")
				+ File.separator;

		appDirPath = homeDir + APPLICATION_DIR_NAME;
		cacheDirPath = appDirPath + File.separator + CACHE_DIR_NAME;
		cacheDbFilePath = appDirPath + File.separator
				+ CACHE_DB_FILE_NAME;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CreateTable ct = new CreateTable();
		String homeDir = System.getProperty("user.home")
				+ File.separator;

		ct.openConnection();
		ct.createTable();
		ct.insertIntoTable();
		ct.closeConnection();
	}

	public void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + cacheDbFilePath);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		}
	}

	public void createTable() {
		try {
			stmt = con.createStatement();
			stmt
					.executeUpdate("CREATE TABLE cache (id integer primary key asc , url string ,actualfilename string,localfilename string,status string ,size integer,datecreated date )");
			System.out.println("Table Created");
			// insertIntoTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}

	}

	public void insertIntoTable() {
		try {
			stmt = con.createStatement();
			stmt
					.executeUpdate("INSERT INTO cache (url , actualfilename ,size ,status) values ("
							+ "'http://java.sun.com/docs/books/tutorialNB/download/tutorial-5.0.zip','tutorial-5.0.zip.zip',20,'DOWNLOADED')");
			System.out.println("Row Inserted!!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}
	}

	public void closeConnection() {
		try {
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

	}

}
