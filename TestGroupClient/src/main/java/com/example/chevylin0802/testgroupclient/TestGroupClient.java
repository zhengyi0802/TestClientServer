package com.example.chevylin0802.testgroupclient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// 醫院門診端所使用的客戶端程式
public class TestGroupClient {
	
	private static String serverIP, serverPortStr, groupId;
	private static int serverPort = 5555;
	private static Socket socket;
	private static BufferedReader reader;
	private static BufferedWriter writer;
	
	// 使用方法 : java -jar TestGroupClient.jar serverip[:port] groupid 
	public static void main(String[] args) {
		
		if(args.length < 2) {
			// 當命令列少於兩個參數的時候, 列出使用方法並直接跳出程式
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
		
		// 執行連線函式
		ClientCommunicating();
	}
	
	// 連線函式, 讓本客戶端與伺服器連線
	private static void ClientCommunicating() {
		try {
			
			socket = new Socket(serverIP, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
			
            // 無窮迴圈
			while(true) {
				// 發送登入訊息
				String outmessage = "REGISTER username=" + groupId + " type=group\r\n";
				writer.write(outmessage);
				writer.flush();
				
				// 伺服器端會發送回應訊息, 所以在此等待資料接收
				String replymessage = reader.readLine().trim();
				System.out.println(replymessage);
				// 確保資料已經接收完畢
				while(reader.ready()) {
					String str = reader.readLine();
					if(str.length() > 0) {
						System.out.println(str);
					}						
				}
				
				int counts = 0;
				while(socket.isConnected()) {					
					// 發送門診叫號訊息
					outmessage = "MESSAGE 當前門診號碼為" + counts + "號\r\n";
					System.out.println(outmessage.trim());
					writer.write(outmessage);
					writer.flush();
					
					// 伺服器端會發送回應訊息, 所以在此等待資料接收
					replymessage = reader.readLine().trim();
					System.out.println(replymessage);
					// 確保資料已經接收完畢
					while(reader.ready()) {
						String str1 = reader.readLine();
						if(str1.length() > 0) {
							System.out.println(str1);
						}
					}
					
					int dig = 0;
					int num = 0;
					boolean nflag = true;
					while(nflag) {
						char ch = (char) System.in.read();
						switch(ch) {
							case '+' :
								counts++;
								nflag = false;
								break;
							case '-' :
								if (counts > 1) counts--;
								nflag = false;
								break;
							default :
								if ( ch >= '0' && ch <= '9') {
									dig = ch-'0';
									num = num * 10 + dig;
									nflag = true;
								} else if (ch == '\n') {
									if(num > 0) {
										counts = num;
										dig = 0;
										num = 0;
										nflag = false;
									} else {
										nflag = true;
									}
								}
								break;
						} // switch(ch) loop
						Thread.sleep(100);
					} // while nflag loop
				} // while socket.isConnected()
			} // while(true)
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// 連線關閉所需做的動作
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
