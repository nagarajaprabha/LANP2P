/**
 * 
 */
package intranetp2p;

import java.io.*;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;



/**
 * @author nprabha
 *
 */
public class CacheMgr {
	//C:\Document and Settings\nprabha\My Document\LANP2P
	private static final String CACHE_PATH_NAME = "LANP2P";

	/**
	 * Called by  proxyserver
	 * Application Server returns the InputStream for a given fileName to the Stub or to the Peer 
	 * @param fileName
	 * @return
	 */
	public byte[] searchAndGetFile(String fileName){
		//OutputStream os = getFile(fileName);
		//File dir = new File(CACHE_PATH_NAME);
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.separator+CACHE_PATH_NAME;
		File dir = new File(path);
		//ArrayList <Byte> b = new ArrayList<Byte>();
		
		DataInputStream dis;
		System.out.println("Path Info "+path);
		System.out.println("Directory Info "+dir + " IsDirectory "+dir.isDirectory() +" Directory Exists? "+ dir.exists());
		createDirectoryIfNecessary(dir);
		for(File search : dir.listFiles()){
			System.out.println(" Required File is "+ removeExtension(search.getName()) + " Required Search FileName is "+fileName);
			if(removeExtension(search.getName()).equals(fileName)){
		    try {
					dis = new DataInputStream(new FileInputStream(search));
					byte []b1 = new byte[dis.available()];
					dis.readFully(b1);
					System.out.println(" Printing the bytes" + b1.length);
					return b1;
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(EOFException eof){
					eof.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	private void createDirectoryIfNecessary(File dir) {
		if(!dir.exists()){
			dir.mkdir();
		}
	}
	/**
	 * Called by the ProxyServer by supplying the bytes
	 * @param bytes
	 */
	public void saveFile(Byte []b,String fileName){
		DataOutputStream dos = null;
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.separator+CACHE_PATH_NAME;
		File dir = new File(path);
		
		try {
			createDirectoryIfNecessary(dir);
			dos = new DataOutputStream(new FileOutputStream(new File(path+File.separator+fileName)));
			System.out.println(path+File.separator+fileName);
			//TODO MIGHT BE A ERROR
			System.out.println(b.length);
			for(int i = 0 ; i < b.length ; i++){
				dos.writeByte((b[i]));
			}
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This is called by ApplicationServer
	 * This API  returns Bytes of a file when offset and length is provided
	 * @param fileName
	 * @param offSet
	 * @param length
	 * @return
	 */
	public byte[] getFile(String fileName,int off , int len){
		File file = null;
		InputStream is = null;
		DataInputStream dis = null;
		byte[] b=null;
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.separator+CACHE_PATH_NAME;
		File dir = new File(path);

		try {
			createDirectoryIfNecessary(dir);
			dis = new DataInputStream(new FileInputStream(fileName));
			dis.read(b, off, len);
			
			return b;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 1. Search for the File
		 * 2. Get the total Number of Peers and size of the File
		 * 3. Compute and request the number of bytes of a given file 
		 * 4. Assemble all the parts
		 */ catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Assemble the contents here
		 */
		return null;
		
	}
	public boolean isFileAvailable() {
		// TODO Auto-generated method stub
		return false;
		
	}
	public byte[] getFileByName(String string) {
		// TODO Auto-generated method stub
		return null;
	}

 
}
