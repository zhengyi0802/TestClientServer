package com.example.chevylin0802.testclient2;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chevylin0802.testclient2.R;

public class PhoneClientActivity extends AppCompatActivity {

    private static final String TAG = "PhoneClientActivity";
    private EditText textIPAddr, textPort, textUsername, textGroup;
    private Button buttonStart, buttonStop;
    private TextView textMessages;
    private String strIPAddr;
    private String strPort;
    private int numPort;
    private String strUsername;
    private String strGroup;
    private PhoneClientService mService = null;
    private Intent mIntent = null;
    private Handler handler = new Handler();

    // 手機客戶端連線程式
    public PhoneClientActivity() {
        strPort = "5555";
        numPort = 5555;
        strIPAddr = "127.0.0.1";
        strUsername = "H123223323";
        strGroup = "YDH01002";
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


    // 通知背景服務程式啟動網路連線
    public Runnable netStart = new Runnable() {
        @Override
        public void run() {
            mService.netStart(strIPAddr, numPort, strUsername, strGroup);
        }
    };

    // 回到首頁
    public void hideActivity() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }


    // 啟動背景服務程式
    public void startPhoneClientService() {
        if(mIntent == null) {
            mIntent = new Intent(this, PhoneClientService.class);
            bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        startService(mIntent);
        handler.postDelayed(netStart, 1000);
        hideActivity();
    }

    // 停止背景服務程式
    public void stopPhoneClientService() {
        if(mIntent != null) {
            mService.netStop();
            stopService(mIntent);
        }
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
                //System.out.println("strPort = " + numPort);

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
                startPhoneClientService();
            }
        }
    };

    private Button.OnClickListener listenStop = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            stopPhoneClientService();
        }

    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            // TODO Auto-generated method stub
            mService = ((PhoneClientService.LocalBinder)serviceBinder).getService();
            Log.d(TAG, "onServiceConnected()");
        }

        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
            Log.d(TAG, "onServiceDisconnected()" + name.getClassName());
        }

    };

}
