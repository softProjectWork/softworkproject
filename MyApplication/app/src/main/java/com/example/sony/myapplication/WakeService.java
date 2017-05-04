package com.example.sony.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class WakeService extends Service{

    private static Binder mBinder;

    private static String TAG = "WakeService";

    private Thread mThread = null;

    private boolean runFlag = true;

    private static Socket socket=null;

    private Context context;

    class MyBinder extends Binder{

        public Service getLocalService(){
            return WakeService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        if(mBinder == null){
            mBinder = new MyBinder();
        }
        return mBinder;
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "LocalService onCreate");
        super.onCreate();

    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "LocalService onDestroy");
        super.onDestroy();
        runFlag = false;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i(TAG, "LocalService onStartCommand");
        Log.i(TAG, "action is "+intent.getAction());

        Thread thread = getThread();
        if(thread.isAlive()){
            Log.i(TAG, "thread is aleady started");
        }else{
            thread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Thread getThread(){

        if(mThread==null){
            mThread = new Thread(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    //请求服务器
                    doRequest();

                }
            });
        }
        return mThread;
    }

    private void parseJSON(JSONObject jsonData) {
        try{
            String type = jsonData.getString("type");

            Intent it;
            Bundle bundle;
            switch(type) {
                case "room_ready":
                    int stuId = jsonData.getInt("stuId");
                    String nickName = jsonData.getString("nickName");
                    int order = jsonData.getInt("order");

                    it = new Intent(this,RoomActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("stuId",stuId);
                    bundle.putString("nickName",nickName);
                    bundle.putInt("order",order);
                    bundle.putInt("startGame",0);
                    it.putExtras(bundle);
                    startActivity(it);
                    break;
                case "room_full":
                    it = new Intent(this,RoomActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("startGame",1);
                    it.putExtras(bundle);
                    startActivity(it);
                    break;

                default:break;
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    //网络请求
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void doRequest(){
        Socket socket = getSocket();

        if (socket.isConnected()){
            Log.i(TAG, "soceket connected");
            InputStream is = null;
            //持续请求服务器
            while (runFlag) {
                try {
                    is = socket.getInputStream();
                    byte[] resp = new byte[100];
                    is.read(resp);
                    String res = new String(resp);
                    Log.i(TAG, res);
                    JSONObject p = new JSONObject(res);
                    parseJSON(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (is != null)
                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                } finally {
                    Log.i(TAG, "over");
                }
            }

        }

    }

    private Context getContext(){
        if(context == null){
            context = this;
        }
        return context;
    }

    private Socket getSocket(){
        if(socket==null){
            String ip = "192.168.1.100";
            int port = 10000;
            try {
                socket=new Socket(ip,port);
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }
}