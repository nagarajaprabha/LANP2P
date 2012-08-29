/**
 * 
 */
package intranetp2p;

import java.io.*;
import java.util.ArrayList;

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
	static int countsequence;
	public CacheMgr() {
		try {
			String homeDir = System.getProperty("user.home") + File.separator;
			appDirPath = homeDir + APPLICATION_DIR_NAME;
			cacheDirPath = appDirPath + File.separator + CACHE_DIR_NAME;
			cacheDbFilePath = appDirPath + File.separator + CACHE_DB_FILE_NAME;

			System.out.println(" APP DIR PATH " + appDirPath);
			System.out.println(" Cache DIR PATH " + cacheDbFilePath + "\n"
					+ cacheDirPath);

			createDirectoryIfNecessary(new File(appDirPath));
			createDirectoryIfNecessary(new File(cacheDirPath));

			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + cacheDbFilePath);

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

	public void saveFile(InputStream is, String url) {
		DataOutputStream dos = null;
		File dir = new File(cacheDirPath);
		String fileName = getFileNameFromURL(url);
		File newFile = null;
		try {
			// Check whether filename already exists
			String fname = isFileURLAvailable(null, fileName);
			if (fname == null) {
				System.out.println(" IN SAVE" + fileName);
				insertFileProperties(url);
				newFile = new File(cacheDirPath + File.separator + fileName);
				dos = new DataOutputStream(new FileOutputStream(newFile));
				System.out.println(cacheDirPath + File.separator + fileName);
				int i = 0;
				while ((i = is.read()) != -1) {
					dos.write(i);
				}
				updateFileProperties(url, fileName);
			} else {
				System.out.println(" IN ELSE OF SAVE");
				insertFileProperties(url);
				//id is count of available of filename 
				String id = isFileURLAvailable(url, fileName);
				newFile = new File(cacheDirPath + File.separator + fileName
						+ "_" + Integer.parseInt(id)+1);

				dos = new DataOutputStream(new FileOutputStream(newFile));
				System.out.println(cacheDirPath + File.separator + fileName);
				int i = 0;
				while ((i = is.read()) != -1) {
					dos.write(i);
				}
				updateFileProperties(url, newFile.getName());
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (dos != null)
					dos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void saveFileByPart(InputStream is, String url) {
		DataOutputStream dos = null;
	
		String fileName = getFileNameFromURL(url);
		File newFile = null;
		try {
			// Check whether filename already exists
			String fname = isFileURLAvailable(null, fileName);
			if (fname == null) {
				System.out.println(" IN SAVE" + fileName);
				insertFileProperties(url);
			}else{

			}
			newFile = new File(cacheDirPath + File.separator + fileName
					+ "_" + (countsequence));

			countsequence++;

		} finally {
			try {
				if (dos != null)
					dos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
		String fileName = getFileNameFromURL(url);
		File newFile = null;
		try {
			// Check whether filename already exists
			String fname = isFileURLAvailable(null, fileName);
			if (fname == null) {
				insertFileProperties(url);
				newFile = new File(cacheDirPath + File.separator + fileName);
				dos = new DataOutputStream(new FileOutputStream(newFile));
				System.out.println(cacheDirPath + File.separator + fileName);

				System.out.println(b.length);
				for (int i = 0; i < b.length; i++) {
					dos.writeByte((b[i]));
				}

			} else {
				insertFileProperties(url);
				String id = isFileURLAvailable(url, fileName);
				newFile = new File(cacheDirPath + File.separator + fileName
						+ "_" + Integer.parseInt(id));

				dos = new DataOutputStream(new FileOutputStream(newFile));
				System.out.println(cacheDirPath + File.separator + fileName);

				System.out.println(b.length);
				for (int i = 0; i < b.length; i++) {
					dos.writeByte((b[i]));
				}

			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
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
	public void insertFileProperties(String url) {
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("INSERT INTO CACHE (url,actualfilename,status,datecreated) VALUES (?,?,?,?)");
			pstmt.setString(1, url);
			pstmt.setString(2, CacheMgr.getFileNameFromURL(url));
			pstmt.setString(3, "DOWNLOADING");
			pstmt.setString(4, java.util.Calendar.getInstance().getTime()
					.toString());
			boolean f = pstmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);

			}
		}

	}

	public void updateFileProperties(String url, String localfileName) {
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("UPDATE CACHE SET localfilename=? , status= 'DOWNLOADED' WHERE url = ?");
			pstmt.setString(1, localfileName);
			pstmt.setString(2, url);
			int cnt = pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String isFileURLAvailable(String url, String actualfileName) {
		ResultSet rs = null;
		try {
			System.out.println(" Printing the URL Request : " + url);
			stmt = con.createStatement();
			String response = new String();
			int rid = 0;
			if (url != null) {
				rs = stmt
						.executeQuery("SELECT DISTINCT id,actualfilename,url,size FROM CACHE WHERE status = 'DOWNLOADED' AND URL = "
								+ "'" + url + "'");

			} else if (actualfileName != null) {
				rs = stmt
						.executeQuery("SELECT DISTINCT id,actualfilename,url,size FROM CACHE WHERE status = 'DOWNLOADED' AND  actualfilename = "
								+ "'" + actualfileName + "'");

			} else {
				rs = stmt
						.executeQuery("SELECT DISTINCT id,actualfilename,url,size FROM CACHE WHERE status = 'DOWNLOADED' AND  URL = "
								+ "'"
								+ url
								+ "'"
								+ " AND  actualfilename = '"
								+ actualfileName + "'");

			}
			if (rs == null) {
				System.out.println(" OOPS RS is null");
				return null;
			}
			while (rs.next()) {
				/*
				 * response = rs.getString(1) + "\n" + rs.getString(2) + "\n" +
				 * rs.getString(3) + "\n" + rs.getString(4);
				 */
				response = rs.getString(2);
				response = response + "\n";
				response = rs.getString(3);
				response = response + "\n";
				System.out.println(" Printing the Response " + response);

				return response;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				new RuntimeException(e);
			}
		}

		return null;
	}

	public static String getFileNameFromURL(String url) {
		return url.substring(url.lastIndexOf('/') + 1, url.length());
	}

}
