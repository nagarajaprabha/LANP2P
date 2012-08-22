package intranetp2p;

import java.sql.*;
import java.util.Calendar;

public class CreateTable {

	Connection con;
	Statement stmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CreateTable ct = new CreateTable();
		ct.openConnection();
		ct.createTable();
		ct.insertIntoTable();
		ct.closeConnection();
	}

	public void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.home")+java.io.File.separator+"LANP2P"+java.io.File.separator+"CacheDB");
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
			insertIntoTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}finally{
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
					.executeUpdate("INSERT INTO cache (url , filename ,size ) values ("
							+ "'http://java.sun.com/docs/books/tutorialNB/download/tutorial-5.0.zip','sample1.txt',20)"
							);

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
