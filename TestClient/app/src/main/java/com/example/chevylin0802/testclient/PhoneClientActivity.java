package com.example.chevylin0802.testclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PhoneClientActivity extends AppCompatActivity {

    private EditText textIPAddr, textPort, textUsername, textGroup;
    private Button buttonStart, buttonStop;
    private TextView textMessages;
    private String strIPAddr;
    private String strPort;
    private int numPort;
    private String strUsername;
    private String strGroup;
    private String strMessages;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Handler handler = new Handler();
    private boolean thread_flag = false;

    public PhoneClientActivity() {
        strPort = "2060";
        numPort = 2060;
        strIPAddr = "127.0.0.1";
        strUsername = "H123223323";
        strGroup = "YDH01002";
        strMessages = "";
        socket = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        textIPAddr = (EditText) findViewById(R.id.text_ipaddr);
        textPort = (EditText) findViewById(R.id.text_port);
        textUsername = (EditText) findViewById(R.id.text_username);
        textGroup = (EditText) findViewById(R.id.text_group);
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStop = (Button) findViewById(R.id.button_stop);
        textMessages = (TextView) findViewById(R.id.text_messages);
        buttonStart.setOnClickListener(listenStart);
        buttonStop.setOnClickListener(listenStop);
        textMessages.setText("TestClient Started!");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final Runnable refresh = new Runnable() {
        public void run() {
            textMessages.setText(strMessages);
        }
    };

    private final Thread connectStart = new Thread() {
        public void run() {
            try {
                strMessages = "Connect to Server : " + strIPAddr + ":" + strPort;
                handler.post(refresh);
                socket = new Socket(strIPAddr, numPort);

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                while (socket.isConnected() && thread_flag) {
                    String strRegister = "REGISTER username=" + strUsername + " type=user\r\n";
                    System.out.println("Write Message : " + strRegister);
                    writer.write(strRegister);
                    writer.flush();

                    strMessages = reader.readLine();
                    handler.post(refresh);

                    String strJoin = "JOIN group=" + strGroup + "\r\n";
                    System.out.println("Write Message : " + strJoin);
                    writer.write(strJoin);
                    writer.flush();

                    strMessages = reader.readLine();
                    handler.post(refresh);

                    while (socket.isConnected() && thread_flag) {
                        if (reader.ready()) {
                            String mesg1 = reader.readLine().trim();

                            System.out.println("recv data : " + mesg1);
                            if (mesg1.contains("MESSAGE")) {
                                String strReply = "HIBP/1.0 200 OK\r\n";
                                writer.write(strReply);
                                writer.flush();
                            }
                            if(mesg1.length() > 0) {
                                strMessages = mesg1 + "\r\n";
                                handler.post(refresh);
                            }
                        }
                        Thread.sleep(100);
                    }
                }

                reader.close();
                writer.close();
                socket.close();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "連結中斷", Toast.LENGTH_SHORT).show();
            }

            reader = null;
            writer = null;
            socket = null;

        }
    };

    public void connectStop() {
        thread_flag = false;
    }

    private Button.OnClickListener listenStart = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            boolean error = false;
            while(true) {
                strIPAddr = textIPAddr.getText().toString();
                if (strIPAddr == null || strIPAddr.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "請輸入Server IP", Toast.LENGTH_SHORT).show();
                    error = true;
                    break;
                }
                strPort = textPort.getText().toString();
                if (strPort == null || strPort.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "請輸入Server Port", Toast.LENGTH_SHORT).show();
                    error = true;
                    break;
                }
                numPort = Integer.parseInt(strPort);
                System.out.println("strPort = " + numPort);

                strUsername = textUsername.getText().toString();
                if (strUsername == null || strUsername.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "請輸入Username", Toast.LENGTH_SHORT).show();
                    error = true;
                    break;
                }
                strGroup = textGroup.getText().toString();
                if (strGroup == null || strGroup.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "請輸入Group", Toast.LENGTH_SHORT).show();
                    error = true;
                    break;
                }
                error = false;
                break;
            }
            if(!error) {
                thread_flag = true;
                connectStart.start();
            }
        }
    };

    private Button.OnClickListener listenStop = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            connectStop();
        }
    };

}