package com.example.chevylin0802.testclient2;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.chevylin0802.testclient2.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PhoneClientService extends Service {
    private final static String TAG = "PhoneClientService";

    private boolean thread_flag = false;
    private String strIPAddr;
    private int numPort;
    private String strUsername;
    private String strGroup;
    private String strMessages;
    private Notification mNodification;
    private NotificationManager notificationManager;
    private Intent intent;
    private PendingIntent pIntent;
    private Handler mHandler = new Handler();
    private Context context;

    public class LocalBinder extends Binder  {
        PhoneClientService getService() {
            return  PhoneClientService.this;
        }
    }

    private LocalBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable notify = new Runnable() {
        @Override
        public void run() {
            notificationManager.notify(1, mNodification);
        }
    };

    private void showNotification() {
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNodification = new Notification.Builder(this)
                    .setContentTitle("醫院門號碼通知")
                    .setSmallIcon(R.drawable.icon)
                    .setContentText(strMessages)
                    .setContentIntent(pIntent).build();
            // hide the notification after its selected
        //mNodification.flags |= Notification.FLAG_AUTO_CANCEL;
        mHandler.postDelayed(notify, 1000);
    }

    public void netStart(String ip, int port, String username, String group) {
        strIPAddr = ip;
        numPort = port;
        strUsername = username;
        strGroup = group;
        Log.d(TAG, "netStart() : IP = " + strIPAddr + " Port = " + numPort + " Username = " + strUsername + " Group = " + strGroup);
        thread_flag = true;
        connectStart.start();
    }

    // 當停止按紐按下時, 要停止網路連線
    public void netStop() {
        Log.d(TAG, "netStop()");
        thread_flag = false;
    }

    // 與伺服器連線的殖行緒, 當按下啟動按鈕時, 必需要採取開啟執行緒的方式來進行連線, 否則整個程式會被卡死
    private final Thread connectStart = new Thread() {
        private Socket socket;
        private BufferedWriter writer;
        private BufferedReader reader;

        public void run() {
            try {
                strMessages = "Connect to Server : " + strIPAddr + ":" + numPort;
                socket = new Socket(strIPAddr, numPort);

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // 當thread_flag為假的時候, 才跳出迴圈
                while (socket.isConnected() && thread_flag) {
                    // 發送登入訊息
                    String strRegister = "REGISTER username=" + strUsername + " type=user\r\n";
                    Log.d(TAG, "Write Message : " + strRegister);
                    writer.write(strRegister);
                    writer.flush();

                    while(reader.ready()) {
                        strMessages = reader.readLine();
                        if (strMessages.trim().length() > 0) {
                            //handler.post(refresh);
                        }
                    }

                    // 發送加入群組訊息
                    String strJoin = "JOIN group=" + strGroup + "\r\n";
                    Log.d(TAG, "Write Message : " + strJoin);
                    writer.write(strJoin);
                    writer.flush();

                    while(reader.ready()) {
                        strMessages = reader.readLine();
                        if (strMessages.trim().length() > 0) {
                            //handler.post(refresh);
                        }
                    }

                    // 當thread_flag為假的時候, 才跳出迴圈
                    while (socket.isConnected() && thread_flag) {
                        if (reader.ready()) {
                            String mesg1 = reader.readLine().trim();
                            if(mesg1.length() > 8) {
                                strMessages = mesg1.substring(8) + "\r\n";
                                Log.d(TAG, "Messages :" + strMessages);
                                showNotification();
                            }

                            Log.d(TAG, "recv data : " + mesg1);
                            if (mesg1.contains("MESSAGE")) {
                                String strReply = "HIBP/1.0 200 OK\r\n";
                                writer.write(strReply);
                                writer.flush();
                            }
                        }
                        // 延遲讀取, 讓系統可以運作順利
                        Thread.sleep(100);
                    }
                }

                // 當thread_flag為假的時候, 結束連線
                reader.close();
                writer.close();
                socket.close();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "連結中斷", Toast.LENGTH_SHORT).show();
            }

            // 清空連線物件
            reader = null;
            writer = null;
            socket = null;
        }

    };

}
