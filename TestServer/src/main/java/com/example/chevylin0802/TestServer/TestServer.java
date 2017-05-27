package com.example.chevylin0802.TestServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class TestServer {

    private static final int default_port = 2060;
    private static List<ClientClass> clients = new ArrayList<ClientClass>();

    public static void main(String[] args) {

        int server_port;

        if(args.length < 1) {
            System.out.println("Server Port = " + default_port);
            server_port = default_port;
        } else {
            System.out.println("Server Port = " + args[0]);
            server_port = Integer.getInteger(args[0]).intValue();
        }

        try {
            ServerSocket server = new ServerSocket(server_port);
            System.out.println("Created Server Socket!");
            while (true) {
                Socket socket = server.accept();
                BCastHandler handler = new BCastHandler(socket);
                handler.start();
            }
        } catch (Exception e) {
            System.err.println("Exception caught:" + e);
        }
    }

    public static List<ClientClass> getClients() {
        return clients;
    }

}
