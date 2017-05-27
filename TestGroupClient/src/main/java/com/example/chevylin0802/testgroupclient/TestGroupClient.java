package com.example.chevylin0802.testgroupclient;

import java.io.*;
import java.net.*;

public class TestGroupClient {
	
	private static String serverIP, serverPortStr, groupId;
	private static int serverPort = 2060;
	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter writer;
	
	public static void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("Usage : java -jar TestGroupClient.jar serverip[:port] groupid ");
			System.exit(0);
		}
		
		int pos = args[0].indexOf(':');
		if(pos > 0) {
			serverIP = args[0].substring(0, pos);
			serverPortStr = args[0].substring(pos+1);
			serverPort = Integer.parseInt(serverPortStr);
		} else {
			serverIP = args[0].toString();
		}
		
		groupId = args[1].toString();
		
		System.out.println("Server IP = " + serverIP + ", Server Port = " + serverPort + ", Group Id : " + groupId);
		
		ClientCommunicating();
	}
	
	private static void ClientCommunicating() {
		try {
			
			socket = new Socket(serverIP, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
			
			while(true) {
				String outmessage = "REGISTER username=" + groupId + " type=group\r\n";
				writer.print(outmessage);
				writer.flush();
				
				String replymessage = reader.readLine().trim();
				System.out.println(replymessage);
				while(reader.ready()) {
					String str = reader.readLine();
					if(str.length() > 0) {
						System.out.println(str);
					}						
				}
				
				int counts = 0;
				while(socket.isConnected()) {
					outmessage = "MESSAGE number=" + counts + "\r\n";
					System.out.println(outmessage.trim());
					writer.print(outmessage);
					writer.flush();
					
					replymessage = reader.readLine().trim();
					System.out.println(replymessage);
					while(reader.ready()) {
						String str1 = reader.readLine();
						if(str1.length() > 0) {
							System.out.println(str1);
						}
					}
					Thread.sleep(5000);
					counts++;
				}				
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
