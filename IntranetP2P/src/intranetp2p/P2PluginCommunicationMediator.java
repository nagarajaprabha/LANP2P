/**
 * 
 */
package intranetp2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author nprabha
 *
 */
public class P2PluginCommunicationMediator {

		/**Plugin requests for the file 
		 * P2PPluginCommunicator asks the CacheMgr to search for a given filename in the localcache
		 * if the file is available in the local cache then that file content is returned in the form of bytes
		 * else 
		 * if the file is not available in the local cache then we call P2PCommunicationMediator 
		 * Gets the Plugin Request that is sent
		 */
		public void listenPluginRequest(){
			try {
				Socket s = new Socket("localmachine",6000);
				DataInputStream dis = (DataInputStream) s.getInputStream();
				DataOutputStream dos = (DataOutputStream) s.getOutputStream();
				byte[] b = null;//variable is 'b' contains fileName
				String fileName = new String();
				CacheMgr mgr = new CacheMgr();
				P2PCommunicationMediator p2pComm = new P2PCommunicationMediator();
				while(true){
				  fileName = dis.readLine();
					if(mgr.isFileAvailable()){
						//if the file is available in the local cache then search and return the file contents
						dos.write(mgr.getFileByName(fileName));
						
					}else{
						//if the file is not available then call the P2PCommunicationMediator
						p2pComm.notifyAllPeers(b.toString());
					}
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Sends the Response to the Plugin
		 * @return
		 */
		public Byte sendResponseToPeer(){
			return null;
		}
		
		/**
		 * Redirects to plugin
		 * @return
		 */
		public Byte redirectToPlugin(){
			return null;
		}
		
}
