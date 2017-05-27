package com.example.chevylin0802.TestServer;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;

/**
 * Created by chevy on 2017/5/22.
 */
public class BCastHandler extends Thread {

    private static final String reply_ok = "HIBP/1.0 200 OK\r\n";
    private static final String reply_error = "HIBP/1.0 402 failure\r\n";
    private static final String reply_error2 = "HIBP/1.0 404 Unknown Protocol\r\n";

    private static final String prot_REGISTER = "REGISTER";
    private static final String prot_JOIN = "JOIN";
    private static final String prot_MESSAGE = "MESSAGE";
    private static final String prot_LOGOUT = "LOGOUT";

    private Socket socket;
    private ClientClass client;
    private boolean reply_flag = false;
    private boolean broadcast_flag = false;
    public BufferedReader reader = null;
    public PrintWriter writer = null;

    BCastHandler (Socket socket) {

        this.socket = socket;
        this.client = new ClientClass(socket, this);
        List<ClientClass> clients = TestServer.getClients();
        clients.add(client);
    }

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

    private void BroadcastMessages(String mesg) {

        List<ClientClass> clients = TestServer.getClients();

        for(ClientClass c : clients) {
            if(c.getUsertype() == ClientClass.USERTYPE.USER) {
                if(c.checkGroupOf(client.getUsername())) {
                    BCastHandler handler = c.getThread();
                    handler.writer.print(mesg + "\r\n");
                    handler.writer.flush();
                }
            }
        }

    }

    public void run () {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String line = reader.readLine();
                System.out.println("recv: " + line.trim());
                if (line.trim().equals(prot_LOGOUT.toString())) {
                    writer.println("LOGOUT!");
                    writer.flush();
                    break;
                }
                boolean flag = parseReceiveData(line.trim());
                if(reply_flag) {
                    if(flag) {
                        writer.print(reply_ok);
                    } else {
                        writer.print(reply_error2);
                    }
                    writer.flush();
                }

                if(broadcast_flag) {
                    BroadcastMessages(line.trim());
                }
                broadcast_flag = false;
            }
        } catch (Exception e) {
            System.err.println("Exception caught: client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (Exception e ){ ; }
            List<ClientClass> clients = TestServer.getClients();
            clients.remove(client);
        }
    }

}
