package com.example.chevylin0802.TestServer;

import java.net.*;

/**
 * ClientClass 物件, 當每一個連線開啟的時候, 用來記錄連線的帳號, 客戶端的類型, 加入的群組, 以及關連於客戶端的socket與thread物件
 */
public class ClientClass {
    public enum USERTYPE {
        NONE, USER, GROUP
    };
    private Socket socket; 			// 連線的socket物件
    private String username; 		// 客戶端輸入的帳號, 以及群組客戶端的群祖帳號
    private USERTYPE type; 			// 類型, 使用者或群組使用者
    private String groupof;			// 所加入的群組帳號名稱
    private int usernumber;			// 客戶端掛號的號碼 (暫時沒有使用到)
    private BCastHandler thread; 	// 執行緒物件

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
