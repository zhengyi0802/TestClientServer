package com.example.chevylin0802.TestServer;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;

/**
 * BCastHandler : 與客戶端連線的執行緒, 所以它必需要繼承Thread物件
 */
public class BCastHandler extends Thread {

    private static final String reply_ok = "HIBP/1.0 200 OK\r\n";
    //private static final String reply_error = "HIBP/1.0 402 failure\r\n";
    private static final String reply_error2 = "HIBP/1.0 404 Unknown Protocol\r\n";

    private static final String prot_REGISTER = "REGISTER";			// 登入命令
    private static final String prot_JOIN = "JOIN";					// 加入群組命令
    private static final String prot_MESSAGE = "MESSAGE";			// 訊息發怖命令
    private static final String prot_LOGOUT = "LOGOUT";				// 登出命令 (暫時沒用到)

    private Socket socket;
    private ClientClass client;
    private boolean reply_flag = false;								// 伺服器是否發出回應的旗標
    private boolean broadcast_flag = false;							// 伺服器是否將訊息廣播到客戶端
    public BufferedReader reader = null;
    public PrintWriter writer = null;

    // 建立物件的建構子函式
    BCastHandler (Socket socket) {

        this.socket = socket;
        this.client = new ClientClass(socket, this);
        List<ClientClass> clients = TestServer.getClients();
        clients.add(client);
    }

    // 解析所接收到的資料函式
    private boolean parseReceiveData(String rData) {

        broadcast_flag = false;

        if(rData.equals(reply_ok.trim().toString())) {
            reply_flag = false;
        } else if(rData.contains(prot_REGISTER.toString())) {
            reply_flag = true;
            int start = rData.indexOf("username=") + 9;
            int end = rData.substring(start).indexOf("type=")+start-1;
            int start1 = end+6;
            String username = rData.substring(start, end);
            String type = rData.substring(start1);
            //System.out.println("USERNAME = '" + username + "', USERTYPE='" + type + "'");
            if(type.toLowerCase().equals("user".toString())) {
                client.setUsername(username, ClientClass.USERTYPE.USER);
            } else if(type.toLowerCase().equals("group".toString())) {
                client.setUsername(username, ClientClass.USERTYPE.GROUP);
            }
        } else if(rData.contains(prot_JOIN.toString())) {
            reply_flag = true;
            int pos = rData.indexOf("group=") + 6;
            String group = rData.substring(pos);
            //System.out.println("Add to group : " + group);
            client.setGroupOf(group);
        } else if(rData.contains(prot_MESSAGE.toString())) {
            reply_flag = true;
            broadcast_flag = true;
        } else {
            reply_flag = true;
            return false;
        }
        return true;
    }

    // 廣播訊息函式, 當伺服器接收到來自群組用戶所發出來的訊息命令時, 就會啟動這個函式
    private void BroadcastMessages(String mesg) {

        List<ClientClass> clients = TestServer.getClients();

        // 檢查所有已連線的客戶端
        for(ClientClass c : clients) {
        	// 只有手機客戶才需要接收廣播訊息
            if(c.getUsertype() == ClientClass.USERTYPE.USER) {
            	// 核對手機用戶是否屬於這個群組
                if(c.checkGroupOf(client.getUsername())) {
                    BCastHandler handler = c.getThread();
                    handler.writer.print(mesg + "\r\n");	// 傳送訊息
                    handler.writer.flush();					// 記得每傳送訊息之後都需要進行緩衝資料的清理, 資料才會立刻傳送到網路上
                }
            }
        }

    }

    // 當執行緒被啟動的時候, 就會執行這個run()函式
    public void run () {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // 設為無窮迴圈
            while (true) {
            	// 伺服器一開始都只會被動接收資料, 並不會主動發送訊息, 所以一開始就是等待資料從客戶端發送出來
                String line = reader.readLine();
                System.out.println("recv: " + line.trim());
                if (line.trim().equals(prot_LOGOUT.toString())) {
                    writer.println("LOGOUT!");
                    writer.flush();
                    break;
                }
                
                // 取得客戶端所傳送過來的資料之後, 需要進行資料解析
                boolean flag = parseReceiveData(line.trim());
                                
                if(reply_flag) {
                	// 當回應旗標為真的時候
                    if(flag) {
                    	// 當解析的客戶端資料是正確的時候
                        writer.print(reply_ok);
                    } else {
                    	// 當解析的客戶端資料是錯誤的時候(基本上不太可能發生)
                        writer.print(reply_error2);
                    }
                    writer.flush();
                }

                if(broadcast_flag) {
                	// 當廣播旗標為真的時候, 執行廣播訊息的函式
                    BroadcastMessages(line.trim());
                }
                broadcast_flag = false;
            }
        } catch (Exception e) {
        	// 當客戶端的連線發生中斷的時候, 產生錯誤訊息
            System.err.println("Exception caught: client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (Exception e ){ ; }
            List<ClientClass> clients = TestServer.getClients();
            // 將已經離線的客戶端資料從資料庫表列當中移除
            clients.remove(client);
        }
    }

}
