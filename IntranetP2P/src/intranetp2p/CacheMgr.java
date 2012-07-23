/**
 * 
 */
package intranetp2p;

import java.io.*;

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
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.pathSeparator+CACHE_PATH_NAME;
		File dir = new File(path);
		byte[] b = null;
		DataInputStream dis;
		for(File search : dir.listFiles()){
			if(search.getName().equals(fileName)){
		    try {
					dis = new DataInputStream(new FileInputStream(search));
					dis.readFully(b);
					return b;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		return null;
	}
	/**
	 * Called by the ProxyServer by supplying the bytes
	 * @param bytes
	 */
	public void saveFile(Byte []b,String fileName){
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));
			//TODO MIGHT BE A ERROR
			dos.writeBytes(b.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		byte[] b=null;
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
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
		}
		
		/**
		 * Assemble the contents here
		 */
		return null;
		
	}

 
}
