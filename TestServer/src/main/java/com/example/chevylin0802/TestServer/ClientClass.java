package com.example.chevylin0802.TestServer;

import java.net.*;

/**
 * Created by chevy on 2017/5/22.
 */
public class ClientClass {
    public enum USERTYPE {
        NONE, USER, GROUP
    };
    private Socket socket;
    private String username;
    private USERTYPE type;
    private String groupof;
    private int usernumber;
    private BCastHandler thread;

    public ClientClass(Socket socket, BCastHandler thread) {
        this.socket = socket;
        this.username = null;
        this.type = USERTYPE.NONE;
        this.groupof = null;
        this.usernumber = -1;
        this.thread = thread;
    }

    public void setUsername(String username, USERTYPE type) {
        this.username = username;
        this.type = type;
    }

    public void SetUsername(String username) {
        setUsername(username, USERTYPE.NONE);
    }

    public void setGroupOf(String group, int number) {
        this.groupof = group;
        this.usernumber = number;
    }

    public void setGroupOf(String group) {
        setGroupOf(group, -1);
    }
    public void setUserNumber(int number) {
        this.usernumber = number;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getUsername() {
        return this.username;
    }

    public USERTYPE getUsertype() {
        return this.type;
    }

    public String getGroupOf() {
        return this.groupof;
    }

    public int getUsernumber() {
        return this.usernumber;
    }

    public BCastHandler getThread() {
        return this.thread;
    }

    public boolean checkGroupOf(String group) {
        if ( this.type == USERTYPE.USER) {
            if( group.equals(this.groupof) ) {
                return true;
            }
        }
        return false;
    }

}
