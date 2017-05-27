package com.example.chevylin0802.TestServer;

//import java.io.*;
import java.net.*;
import java.util.*;

// Server主程式
public class TestServer {

    private static final int default_port = 5555;
    private static List<ClientClass> clients = new ArrayList<ClientClass>();	// 模擬客戶端資料庫列表

    // 使用方法 : java -jar TestServer.jar [port]
    public static void main(String[] args) {

        int server_port;
        ServerSocket server;

        if(args.length < 1) {
            System.out.println("Server Port = " + default_port);
            server_port = default_port;
        } else {
            System.out.println("Server Port = " + args[0]);
            server_port = Integer.parseInt(args[0]);
        }

        try {
            server = new ServerSocket(server_port);					// 開啟 Server 
            System.out.println("Created Server Socket!");		
            while (true) {
                Socket socket = server.accept();					// 有客戶端的連線進來
                BCastHandler handler = new BCastHandler(socket);	// 新增與客戶端連線的執行緒
                handler.start();									// 啟動與客戶端連線的執行緒
            }
        } catch (Exception e) {
            System.err.println("Exception caught:" + e);			// 客戶端無預警中斷連線
        }
    }

    public static List<ClientClass> getClients() {					// 提取客戶端資料庫表列
        return clients;
    }

}
