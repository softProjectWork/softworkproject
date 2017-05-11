package com.example.sony.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class WakeService extends Service{

    private static Binder mBinder;
    private static String TAG = "WakeService";
    private Thread mThread = null;
    private boolean runFlag = true;
    private static Socket socket=null;
    private final int PLAYER_NUM = 4;
    //private Context context;

    private int port;
    private MyReceiver receiver;

    private boolean choose_player = false;
    private int chosen_order;

    private boolean yes_or_no = false;
    private int witch_choice;

    private boolean speak_over = false;

    private int stuId;
    private String nickName;
    private int order;


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
    public void onDestroy(){
        Log.i(TAG, "LocalService onDestroy");
        super.onDestroy();
        runFlag = false;

        if(socket != null) {
            try {
                socket.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i(TAG, "LocalService onStartCommand");
        Log.i(TAG, "action is "+intent.getAction());

        Bundle bundle = intent.getExtras();
        port = bundle.getInt("port");
        Log.d("port", String.valueOf(port));
        stuId = bundle.getInt("stuId");
        Log.d("stuId", String.valueOf(stuId));
        nickName = bundle.getString("nickName");
        order = bundle.getInt("order");

        //注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.sony.myapplication.GameActivity");
        this.registerReceiver(receiver,filter);

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
                    try {
                        doRequest();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                case "another_player_ready":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","another_player_ready");

                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        String nickName = jsonData.getString("nickName"+i);
                        bundle.putString(("nickName"+i),nickName);
                    }

                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "can_start_game":
                    String role = jsonData.getString("role");

                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","can_start_game");
                    bundle.putString("role",role);
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "sys_info":
                    String info = jsonData.getString("sys_info");
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","sys_info");
                    bundle.putString("sys_info",info);
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "prophet_start":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","prophet_start");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "prophet_end":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","prophet_end");
                    bundle.putString("prophet_identify_role",jsonData.getString("prophet_identify_role"));
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "werewolf_start":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","werewolf_start");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "kill_people_refresh":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","kill_people_refresh");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        bundle.putInt("player"+i+"_killed_cnt",jsonData.getInt("player"+i+"_killed_cnt"));
                    }
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "werewolf_end":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","werewolf_end");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "witch_save_start":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","witch_save_start");
                    bundle.putInt("killed_player_order",jsonData.getInt("killed_player_order"));
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "witch_poison_choice":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","witch_poison_choice");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "witch_poison_start":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","witch_poison_start");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "switch_to_day":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","switch_to_day");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        bundle.putInt("player"+i+"_status",jsonData.getInt("player"+i+"_status"));
                    }
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "hunter_killed":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","hunter_killed");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "you_are_died":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","you_are_died");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "your_turn_to_speak":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","your_turn_to_speak");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "you_can_listen":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","you_can_listen");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "you_have_heard_out":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","you_have_heard_out");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "vote_start":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","vote_start");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "voted_to_die":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","voted_to_die");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        bundle.putInt("player"+i+"_status",jsonData.getInt("player"+i+"_status"));
                    }
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "switch_to_night":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","switch_to_night");
                    it.putExtras(bundle);
                    sendBroadcast(it);
                    break;
                case "game_over":
                    it = new Intent();
                    bundle = new Bundle();
                    bundle.putString("type","game_over");
                    bundle.putString("winner",jsonData.getString("winner"));
                    bundle.putInt("score",jsonData.getInt("score"));
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        bundle.putString("player"+i+"_role",jsonData.getString("player"+i+"_role"));
                    }
                    it.putExtras(bundle);
                    sendBroadcast(it);
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
    private void doRequest() throws IOException {
        Socket socket = getSocket();

        if (socket.isConnected()){
            Log.i(TAG, "socket connected");
            InputStream is = null;
            OutputStream os = null;
            //持续请求服务器
            while (runFlag) {
                try {
                    is = socket.getInputStream();
                    byte[] resp = new byte[1000];
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

                if(choose_player) {
                    try {
                        os = socket.getOutputStream();
                        JSONObject js = new JSONObject();
                        js.put("type","choose_player");
                        js.put("chosen_order",chosen_order);
                        byte[] sendp = js.toString().getBytes();
                        os.write(sendp);
                        os.flush();
                        choose_player = false;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        if(os != null) {
                            try {
                                os.close();
                            }
                            catch(IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                if(yes_or_no) {
                    try {
                        os = socket.getOutputStream();
                        JSONObject js = new JSONObject();
                        js.put("type","witch_choice");
                        js.put("witch_choice",witch_choice);
                        byte[] send = js.toString().getBytes();
                        os.write(send);
                        os.flush();
                        yes_or_no = false;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        if(os != null) {
                            try {
                                os.close();
                            }
                            catch(IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                if(speak_over) {
                    try {
                        os = socket.getOutputStream();
                        JSONObject js = new JSONObject();
                        js.put("type","speak_over");
                        byte[] sendp = js.toString().getBytes();
                        os.write(sendp);
                        os.flush();
                        speak_over = false;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        if(os != null) {
                            try {
                                os.close();
                            }
                            catch(IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

            }

        }

    }

    /*private Context getContext(){
        if(context == null){
            context = this;
        }
        return context;
    }*/

    private Socket getSocket() throws IOException {
        if(socket==null){
            String ip = "162.105.175.115";
            try {
                socket=new Socket(ip,port);
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //建立连接后，立马发送stuId、nickName、order
            if (socket.isConnected()){
                OutputStream os = null;
                try {
                    os = socket.getOutputStream();
                    JSONObject js = new JSONObject();
                    js.put("stuId",stuId);
                    js.put("nickName",nickName);
                    js.put("order",order);
                    Log.d("----------",js.toString());
                    byte[] sendp = js.toString().getBytes();
                    os.write(sendp);
                    os.flush();
                }
                catch(Exception e) {
                    e.printStackTrace();

//                } finally {
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }

        }
        return socket;
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();    //记录长连接service传送的信息
            if (bundle != null) {
                String type = bundle.getString("type");
                if(type.equals("choose_player")) {
                    choose_player = true;
                    chosen_order = bundle.getInt("chosen_order");
                }
                if(type.equals("yes_or_no")) {
                    yes_or_no = true;
                    witch_choice = bundle.getInt("witch_choice");
                }
                if(type.equals("speak_over")) {
                    speak_over = true;
                }
            }
        }
    }

}