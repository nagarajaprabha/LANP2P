package intranetp2p;
import java.sql.*;
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
		ct.closeConnection();
	}
	
	public void openConnection(){
		try{
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:./Database/CacheDB");
		}catch(ClassNotFoundException cnfe){
			throw new RuntimeException(cnfe);
		}catch(SQLException sqle){
			throw new RuntimeException(sqle);
		}
	}
	
	public void createTable(){
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("CREATE TABLE cache (url string ,filename string ,size integer,datecreated date )");
			System.out.println("Table Created");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void closeConnection(){
		try {
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
