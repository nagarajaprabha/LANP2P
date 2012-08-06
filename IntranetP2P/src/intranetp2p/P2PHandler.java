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
/*	*//**
	 * Mediator opens the connection for communication with other peers
	 *//*
	public void openPeerConnections(){
		
	}
	
*/	
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
	 * TODO : Thread Implementation Required
	 */
	public void listenPeersRequest(){

		try {
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			boolean flag = false;
			while(br.read()!=-1){
				if(br.readLine().contains("isFileAvailable")){
					flag = mgr.isFileAvailable(br.readLine());
					if(flag){
						out.write("Available".getBytes());
					}else{
						out.write("NA".getBytes());
					}
					

				}else if(br.readLine().contains("getFile")){
					//FileName is after getFile String
					out.write(mgr.getFileByName(br.readLine().substring("getFile".length())));
				}else if(br.readLine().contains("partFile")){
					//processing the response
					out.write(mgr.getFile(br.readLine().substring("getFile".length()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine())));
				}
			}

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			new RuntimeException(e);
		}

	}
	
	/**
	 * Sends the Response to the Peers
	 */
	public Byte sendResponseToPeers(OutputStream os){
		try {
			DatagramSocket dgs = new DatagramSocket(5000);
			//InetAddress address = InetAddress.getByName("jenkov.com");
			DatagramSocket c = new DatagramSocket();
			String fileName = null;
			//TODO ERROR 
			DatagramPacket packet = new DatagramPacket(fileName.getBytes(), 0,null, 5000);
			c.send(packet);
			//c.receive(packet);
			//(4)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/** Based on the request from proxy ,  notifies all peers to search for a given fileName
	 * ApplicationServer sends the Notification for all peers,to search for a file with the given FileName
	 * 
	 * MY LOCAL IS ACTING AS A CLIENT
	 */
	public void notifyAllPeers(String fileName) throws UnknownHostException{
		Socket []clntSocket = null;
		try {
			
			//write a method getAllPeersAddress
			//Iterate each Peer and Send the Info 
			
			
			byte []infoBytes = mgr.searchAndGetFile("peerList");
			ByteArrayInputStream bis = new ByteArrayInputStream(infoBytes);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			String ipAddress = null;
			InetAddress ip = null;
			SocketAddress sd = null;
			PrintWriter w = null;
			int i = 0;
			while((ipAddress=br.readLine())!=null){

				clntSocket[i] = new Socket(ipAddress,5000);
				clntSocket[i].getOutputStream().write(("isFileAvailable\n"+fileName).getBytes());
				i++;

			}
			// Got the list of PEERS who has the Required FileName
			Socket []availablePeers = null;
			int countOfAvailable = 0;
			for(Socket peer : clntSocket){
				InputStream is = peer.getInputStream();
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
					InputStream is = peer.getInputStream();
					br = new BufferedReader(new InputStreamReader(is));
					
					/**
					 * construct a packet
					 *  1. GET
					 *  2. FILENAME
					 *  
					 */
					
				}
			}else if (countOfAvailable==1){

			}else
			{
				// NO SERVER PEER HAS THE FILE
			}
			for(Socket sock: clntSocket){
				
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
