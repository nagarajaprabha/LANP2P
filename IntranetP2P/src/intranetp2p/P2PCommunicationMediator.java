package intranetp2p;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class P2PCommunicationMediator {
	private static final String IS_FILE_AVAILABLE ="isFileAvailable";

/*	*//**
	 * Mediator opens the connection for communication with other peers
	 *//*
	public void openPeerConnections(){
		
	}
*/	
	/**
	 * Reads the Datagram listening to the port 
	 * searches the file
	 * returns the outputstream
	 * Mediator listens to Peers Request
	 */
	public void listenPeersRequest(){
		try {
			CacheMgr mgr = new CacheMgr();
			DatagramSocket responseDatagram =  new DatagramSocket(5000);
			byte []responseData = null;
			OutputStream os = null;
			//TODO ERROR
			DatagramPacket responsePacket = new DatagramPacket(responseData,responseData.length);
			StringBuffer sb =new StringBuffer();
			String str = new String();
			//TODO error
			while(true){
				responseDatagram.receive(responsePacket);
				responseData = responsePacket.getData();
				if(responseData.length>0){
					sb.insert(0, responseData);
					byte []b  = mgr.searchAndGetFile(sb.toString());
					//SENDING RESPONSE TO PEER
					sendResponseToPeers(os);
				
			}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	
	/** Based on the request from plugin this API notifies all peers to search for a given fileName
	 * ApplicationServer sends the Notification for all peers,to search for a file with the given FileName
	 */
	public void notifyAllPeers(String fileName) throws UnknownHostException{
		try {
			DatagramSocket dgs = new DatagramSocket(5000);
			//InetAddress address = InetAddress.getByName("jenkov.com");
			DatagramSocket c = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(fileName.getBytes(), 0,null, 5000);
			c.send(packet);
			//c.receive(packet);
			//(4)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
