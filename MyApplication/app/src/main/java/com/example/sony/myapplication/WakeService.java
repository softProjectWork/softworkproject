package com.example.sony.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.sony.myapplication.util.FirstEvent;

public class WakeService extends Service{

    private final int PLAYER_NUM = 3;

    private static Binder mBinder;
    private static String TAG = "WakeService";
    private Thread mThread = null;
    private boolean runFlag = true;
    private static Socket socket=null;

    //private Context context;

    private int port;

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

        EventBus.getDefault().unregister(this);

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

        //注册eventBus
        EventBus.getDefault().register(this);

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

            Log.d("type",type);

            JSONObject js = new JSONObject();
            switch(type) {
                case "another_player_ready":
                    js.put("type","another_player_ready");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        String nickName = jsonData.getString("nickName"+i);
                        js.put(("nickName"+i),nickName);
                    }
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "can_start_game":
                    js.put("type","another_player_ready");
                    js.put("role",jsonData.getString("role"));
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "sys_info":
                    js.put("type","sys_info");
                    js.put("sys_info",jsonData.getString("sys_info"));
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "prophet_start":
                    js.put("type","prophet_start");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "prophet_end":
                    js.put("type","prophet_end");
                    js.put("prophet_identify_role",jsonData.getString("prophet_identify_role"));
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "werewolf_start":
                    js.put("type","werewolf_start");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "kill_people_refresh":
                    js.put("type","kill_people_refresh");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        js.put("player"+i+"_killed_cnt",jsonData.getInt("player"+i+"_killed_cnt"));
                    }
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "werewolf_end":
                    js.put("type","werewolf_end");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "witch_save_start":
                    js.put("type","witch_save_start");
                    js.put("killed_player_order",jsonData.getInt("killed_player_order"));
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "witch_poison_choice":
                    js.put("type","witch_poison_choice");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "witch_poison_start":
                    js.put("type","witch_poison_start");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "switch_to_day":
                    js.put("type","switch_to_day");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        js.put("player"+i+"_status",jsonData.getInt("player"+i+"_status"));
                    }
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "hunter_killed":
                    js.put("type","hunter_killed");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "you_are_died":
                    js.put("type","you_are_died");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "your_turn_to_speak":
                    js.put("type","your_turn_to_speak");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "you_can_listen":
                    js.put("type","you_can_listen");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "you_have_heard_out":
                    js.put("type","you_have_heard_out");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "vote_start":
                    js.put("type","vote_start");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "voted_to_die":
                    js.put("type","voted_to_die");
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        js.put("player"+i+"_status",jsonData.getInt("player"+i+"_status"));
                    }
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "switch_to_night":
                    js.put("type","switch_to_night");
                    EventBus.getDefault().post(new FirstEvent(js));
                    break;
                case "game_over":
                    js.put("type","game_over");
                    js.put("winner",jsonData.getString("winner"));
                    js.put("score",jsonData.getInt("score"));
                    for(int i = 1; i <= PLAYER_NUM; i++) {
                        js.put("player"+i+"_role",jsonData.getString("player"+i+"_role"));
                    }
                    EventBus.getDefault().post(new FirstEvent(js));
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

    public void onEvent(FirstEvent event) {
        JSONObject js = event.getJsonData();

        Log.d("service_have_received", "");

        String type = null;
        try {
            type = js.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("receive_type", type);

        if(type.equals("choose_player")) {
            choose_player = true;

            try {
                chosen_order = js.getInt("chosen_order");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(type.equals("yes_or_no")) {
            yes_or_no = true;

            try {
                witch_choice = js.getInt("witch_choice");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(type.equals("speak_over")) {
            speak_over = true;
        }
    }

}