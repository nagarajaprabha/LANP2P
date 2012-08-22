/**
 * 
 */
package intranetp2p;

import java.io.*;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import java.sql.*;

/**
 * @author nprabha
 * 
 */
public class CacheMgr {

	// C:\Document and Settings\nprabha\My Document\LANP2P
	private static final String APPLICATION_DIR_NAME = "LANP2P";
	private static final String CACHE_DIR_NAME = "cache_files";
	private static final String CACHE_DB_FILE_NAME = "cache.db";
	
	Connection con;
	Statement stmt;
	String appDirPath;
	String cacheDirPath;
	String cacheDbFilePath;

	public CacheMgr() {
		try {
			String homeDir = System.getProperty("user.home") + File.pathSeparatorChar;
			appDirPath = homeDir + APPLICATION_DIR_NAME;
			cacheDirPath = appDirPath + File.pathSeparatorChar + CACHE_DIR_NAME;
			cacheDbFilePath = appDirPath + File.pathSeparatorChar + CACHE_DB_FILE_NAME;
			
			createDirectoryIfNecessary(new File(appDirPath));
			createDirectoryIfNecessary(new File(cacheDirPath));
			
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:"+cacheDbFilePath);

		} catch (ClassNotFoundException e) {

			throw new RuntimeException(e);
		} catch (SQLException e) {

			throw new RuntimeException(e);
		}

	}

	/**
	 * Called by proxyserver Application Server returns the InputStream for a
	 * given fileName to the Stub or to the Peer
	 * 
	 * @param fileName
	 * @return
	 */
	public byte[] searchAndGetFile(String fileName) {

		File dir = new File(cacheDirPath);

		DataInputStream dis = null;

		System.out.println("Path Info " + cacheDirPath);
		System.out.println("Directory Info " + dir + " IsDirectory "
				+ dir.isDirectory() + " Directory Exists? " + dir.exists());

		for (File search : dir.listFiles()) {

			System.out.println(" Required File is " + (search.getName())
					+ " Required Search FileName is " + fileName);

			if ((search.getName()).equals(fileName)) {

				try {

					dis = new DataInputStream(new FileInputStream(search));
					byte[] b1 = new byte[dis.available()];
					dis.readFully(b1);
					System.out.println(" Printing the bytes" + b1.length);
					return b1;

				} catch (FileNotFoundException e) {

					throw new RuntimeException(e);
				} catch (EOFException eof) {
					throw new RuntimeException(eof);
				} catch (IOException e) {

					throw new RuntimeException(e);
				} finally {
					try {
						dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		return null;
	}

	public static String removeExtension(String s) {

		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path upto the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}

	/**
	 * @param dir
	 */
	public void createDirectoryIfNecessary(File dir) {
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	/**
	 * Called by the ProxyServer by supplying the bytes TODO refactor b to
	 * String SUGGESTION : HOW WOULD IT BE IF WE STORE THE FILENAME WITH THE URL
	 * 
	 * @param bytes
	 */
	public void saveFile(byte[] b, String url) {
		DataOutputStream dos = null;
		File dir = new File(cacheDirPath);
		String fileName = url.substring(url.lastIndexOf('/'), url.length());
		File newFile = null;
		try {
			// TODO Check whether file already exists
			String fname = isFileURLNameAvailable(null, url);
			if (fname == null) {
				// isFileAvailable(fileName);
				newFile = new File(cacheDirPath + File.separator + fileName);
				dos = new DataOutputStream(new FileOutputStream(newFile));
				System.out.println(cacheDirPath + File.separator + fileName);
				// TODO MIGHT BE A ERROR
				System.out.println(b.length);
				for (int i = 0; i < b.length; i++) {
					dos.writeByte((b[i]));
				}
				// TODO pass url as a parameter

				insertFileProperties(url, newFile);

			} else {

			}

		} catch (FileNotFoundException e) {

			throw new RuntimeException(e);
		} catch (IOException e) {

			throw new RuntimeException(e);
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This is called by ApplicationServer This API returns Bytes of a file when
	 * offset and length is provided
	 * 
	 * @param fileName
	 * @param offSet
	 * @param length
	 *            TODO refactor this to Byte Streams
	 * @return
	 */
	public byte[] getFile(String fileName, int off, int len) {
		File file = null;
		InputStream is = null;
		DataInputStream dis = null;
		byte[] b = null;
		File dir = new File(cacheDirPath);

		try {
			dis = new DataInputStream(new FileInputStream(fileName));
			dis.read(b, off, len);

			return b;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 1. Search for the File 2. Get the total Number of Peers and size of
		 * the File 3. Compute and request the number of bytes of a given file
		 * 4. Assemble all the parts
		 */
		catch (IOException e) {

			throw new RuntimeException(e);
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Assemble the contents here
		 */
		return null;

	}

	public boolean isFileAvailable(String fileName) {

		File dir = new File(cacheDirPath);

		DataInputStream dis;
		System.out.println("Path Info " + cacheDirPath);
		System.out.println("Directory Info " + dir + " IsDirectory "
				+ dir.isDirectory() + " Directory Exists? " + dir.exists());
		boolean flag = false;
		for (File search : dir.listFiles()) {
			// System.out.println(" Required File is "+
			// removeExtension(search.getName()) + " Required Search FileName is
			// "+fileName);
			if (removeExtension(search.getName()).equals(fileName)) {
				flag = true;
				break;
			} else {
				flag = false;
				break;
			}
		}
		return flag;

	}

	/**
	 * @deprecated
	 * @param url
	 * @param fileName
	 * @return
	 */
	public byte[] getFileByName(String url, String fileName) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con
					.prepareStatement("SELECT fileName FROM CACHE WHERE url = ?");
			pstmt.setString(1, url);
			rs = pstmt.executeQuery();
			if (rs.getFetchSize() > 0) {
				return searchAndGetFile(fileName);
			}
		} catch (SQLException sql) {
			throw new RuntimeException(sql);
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param fileObj
	 */
	public void insertFileProperties(String url, File fileObj) {
		try {
			PreparedStatement pstmt = con
					.prepareStatement("INSERT INTO CACHE (url,actualfilename,status,datecreated) VALUES (?,?,?,?)");
			pstmt.setString(1, url);
			pstmt.setString(2, url.substring(url.lastIndexOf('/') + 1, url
					.length()));
			pstmt.setString(3, "DOWNLOADING");
			pstmt.setString(4, java.util.Calendar.getInstance().getTime()
					.toString());
			boolean f = pstmt.execute();
			if (!f) {
				throw new RuntimeException("Error in operation");
			}

		} catch (SQLException e) {

			throw new RuntimeException(e);
		}

	}

	public String isFileURLAvailable(String url) {
		ResultSet rs = null;
		try {
			// System.out.println( " Printing the URL Request : "+ url);
			stmt = con.createStatement();
			String response = new String();
			int rid = 0;
			rs = stmt
					.executeQuery("SELECT DISTINCT filename,url,size FROM CACHE WHERE URL = "
							+ "'" + url + "'");
			if (rs == null) {
				System.out.println(" OOPS RS is null");
			}
			while (rs.next()) {
				/*
				 * response = rs.getString(1) + "\n" + rs.getString(2) + "\n" +
				 * rs.getString(3) + "\n" + rs.getString(4);
				 */
				response = rs.getString(1);

				// System.out.println(" Printing the Response " + response);

				return response;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				new RuntimeException(e);
			}
		}

		return null;
	}

	public String isFileURLNameAvailable(String url, String fileName) {
		ResultSet rs = null;
		try {
			// System.out.println( " Printing the URL Request : "+ url);
			stmt = con.createStatement();
			String response = new String();
			int rid = 0;
			if (url != null) {
				rs = stmt
						.executeQuery("SELECT DISTINCT filename,url,size FROM CACHE WHERE URL = "
								+ "'" + url + "'");

			} else if (fileName != null) {
				rs = stmt
						.executeQuery("SELECT DISTINCT filename,url,size FROM CACHE WHERE filename = "
								+ "'" + fileName + "'");

			} else {
				rs = stmt
						.executeQuery("SELECT DISTINCT filename,url,size FROM CACHE WHERE URL = "
								+ "'"
								+ url
								+ "'"
								+ " AND  filename = '"
								+ fileName + "'");

			}
			if (rs == null) {
				System.out.println(" OOPS RS is null");
			}
			while (rs.next()) {
				/*
				 * response = rs.getString(1) + "\n" + rs.getString(2) + "\n" +
				 * rs.getString(3) + "\n" + rs.getString(4);
				 */
				response = rs.getString(1);

				// System.out.println(" Printing the Response " + response);

				return response;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				new RuntimeException(e);
			}
		}

		return null;
	}

}
