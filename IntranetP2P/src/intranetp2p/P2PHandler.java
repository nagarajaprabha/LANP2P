package intranetp2p;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.filechooser.FileSystemView;

import sun.management.FileSystem;
import sun.security.x509.AVA;

public class P2PHandler {
	
	private static final String IS_FILE_AVAILABLE ="isFileAvailable";
	
	CacheMgr mgr ;
	Socket clientSocket ;

	public P2PHandler(){
		mgr = new CacheMgr();
		//Started the LocalServer
		Server server = new Server(7777);
		server.startServer();
		/**
		 * ClientSocket Responsibilities are 
		 *  1. Listen to the ClientRequests and Sends the Responses
		 */
		clientSocket = server.getClientSocket();
	}
	/**
	 * Reads the Datagram listening to the port 
	 * searches the file
	 * returns the outputstream
	 * Mediator listens to Peers Request
	 * TODO : Thread Implementation may be Required
	 */
	public void listenPeersRequest(){

		try {
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			boolean flag = false;
			while(true){
				
					/**
					 * FirstLine : isFileAvailable
					 * SecondLine : FileName
					 */
					if(br.readLine().contains("isFileAvailable")){
						flag = mgr.isFileAvailable(br.readLine());
						if(flag){
							out.write("Available".getBytes());
							
						}else{
							out.write("NA".getBytes());
							
						}
						

					}else if(br.readLine().contains("getFile")){
						//FileName is after getFile String
						/**
						 * FirstList : getFile+"FileName"
						 */
						out.write(mgr.getFileByName(br.readLine().substring("getFile".length())));
						
					}else if(br.readLine().contains("partFile")){
						//processing the response
						/**
						 * First Line : getFile+"FileName"
						 * Second Line : int offset
						 * Third Line :  int length
						 */
						out.write(mgr.getFile(br.readLine().substring("getFile".length()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine())));
						
					}
				}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			new RuntimeException(e);
		}

	}
	

	/** Based on the request from proxy ,  notifies all peers to search for a given fileName
	 * ApplicationServer sends the Notification for all peers,to search for a file with the given FileName
	 * 
	 * MY LOCAL IS ACTING AS A CLIENT
	 * 
	 * TODO : Can we rename this method as getFileFromPeers
	 */
	public void notifyAllPeers(String fileName) throws UnknownHostException{
		Socket []clntSocket = null;
		try {
			
			//write a method getAllPeersAddress
			//Iterate each Peer and Send the Info 
			
			
			byte []infoBytes = mgr.searchAndGetFile("peerList");
			ByteArrayInputStream bis = new ByteArrayInputStream(infoBytes);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			BufferedWriter bw = null;
			String ipAddress = null;
			InetAddress ip = null;
			
			SocketAddress sd = null;
			PrintWriter w = null;
			int i = 0;
			
			InputStream is = null;
			OutputStream os = null;
			/**
			 * Sending Request PART
			 */
			while((ipAddress=br.readLine())!=null){

				clntSocket[i] = new Socket(ipAddress,5000);
				/**
				 * Format :
				 * 
				 *FirstLine : isFileAvailable
				 *SecondLine : FileName
				 */
				clntSocket[i].getOutputStream().write(("isFileAvailable\n"+fileName).getBytes());
				i++;

			}
			/**
			 * Receving RESPONSE PART
			 */
			Thread.sleep(5000);

			// Get the list of PEERS who has the Required FileName
			Socket []availablePeers = null;
			int countOfAvailable = 0;
			for(Socket peer : clntSocket){
				is = peer.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
								
					if(br.readLine().trim().equals("Available")){
						countOfAvailable++;
						availablePeers[countOfAvailable] = peer;
					}
				
				
			}
			
			//getting the count of AVAILABLE SERVER PEERS
			if(countOfAvailable>1){
				
				for(Socket peer : availablePeers){
					
					//TODO logic of getting the bytes from each
					/**
					 * 1. Get the size of the file
					 * 2. Compute the bytes for each Peer
					 * 3. Request the Bytes
					 * Assuming each peer has COMPLETE FILE
					 * FOR SAMPLE , GET THE FILE SIZE FROM THE FIRST PEER
					 */
					is = peer.getInputStream();
					os = peer.getOutputStream();
					
					br = new BufferedReader(new InputStreamReader(is));
					bw = new BufferedWriter(new OutputStreamWriter(os));
					
					bw.write("get"+fileName);
					
					/**
					 * construct a packet
					 *  1. GET
					 *  2. FILENAME
					 *  
					 */
				
				}
			}else if (countOfAvailable==1){
				
				try{
				
				is = availablePeers[0].getInputStream();
				os = availablePeers[0].getOutputStream();
				
				br = new BufferedReader(new InputStreamReader(is));
				bw = new BufferedWriter(new OutputStreamWriter(os));
				
				bw.write("get"+fileName);
				
				//wait for the response
				Thread.sleep(5000);
				
				boolean flag = false;
				while(true){
					while(br.readLine()!=null){
						mgr.saveFile(br.readLine().getBytes(), fileName);
						flag = true;
					}
					if(flag){
						break;
					}
				}
			 }catch(Exception e){
				throw new RuntimeException(e) ;
			 } finally{
				 is.close();
				 os.close();
			 }
			}else
			{
				// NO SERVER PEER HAS THE FILE
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 
	 * API Sends the response to the Client PRoxy 
	 */
	public Byte sendResponse(){
		return null;
	}
	
	/**
	 * CommunicationMediator calls this API to get Responses from other Peers
	 */
	public void getResponseFromPeers(){
		
		
	}
	
	/**
	 * CommunicationMediator sends a BroadCast Message to all the Clients/Peers conected
	 * @return
	 */
	public Byte broadcastMessage(){
		return null;
	}
	
	

}