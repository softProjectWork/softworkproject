package com.example.sony.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.sony.myapplication.util.getHttpResponseBody;

public class RoomActivity extends AppCompatActivity {
    private Button Exit;
    private Button Ready;

    private int my_StuId;
    private String nickName;
    private int score;

    private int roomID;    //用户进入房间后，未点击“准备”前保存房间ID，点击准备后发送HTTTP请求房间端口号时使用
    private String roomName;
    private int port;

    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

        Ready = (Button)findViewById(R.id.Ready);
        ReadyClickListener rcl = new ReadyClickListener();
        Ready.setOnClickListener(rcl);

        //保存自己的学号、昵称、成绩
        my_StuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");
        score = savedInstanceState.getInt("score");

    }

    class ReadyClickListener implements View.OnClickListener {
        public void onClick(View V) {

            //请求该房间的端口号
            JSONObject param = new JSONObject();
            try {
                param.put("type","get_port");
                param.put("roomId",roomID);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            String strUrl = "127.0.0.0.1";
            URL url = null;
            try {
                url = new URL(strUrl);
                HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("POST");
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content_Type","application/json");
                urlConn.setRequestProperty("CharSet","utf-8");

                urlConn.connect();

                DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
                dop.writeBytes("json="+content);
                dop.flush();
                dop.close();

                InputStream is = urlConn.getInputStream();
                byte[] responseBody = getHttpResponseBody.GetHttpResponseBody(is);
                JSONObject ret = new JSONObject(new String(responseBody));
                port = ret.getInt("port");

                urlConn.disconnect();

            }
            catch(Exception e){
                e.printStackTrace();
            }

            //开启和该房间端口的长连接
            Intent it = new Intent(RoomActivity.this,WakeService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("port",port);
            it.putExtras(bundle);
            startService(it);   //启动服务
            //注册广播接收器
            receiver = new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.sony.application.WakeService");
            RoomActivity.this.registerReceiver(receiver,filter);

            //将自己的学号发送过去
            //包装成JSON格式
            param = new JSONObject();
            try {
                param.put("type","room_ready");
                param.put("stuId", my_StuId);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            strUrl = "127.0.0.0.1";
            url = null;
            try {
                url = new URL(strUrl);
                HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("POST");
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content_Type","application/json");
                urlConn.setRequestProperty("CharSet","utf-8");

                urlConn.connect();

                DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
                dop.writeBytes("json="+content);
                dop.flush();
                dop.close();
                urlConn.getInputStream();
                urlConn.disconnect();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            //设置退出按钮为不可点击
            Exit.setEnabled(false);
        }
    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {

            //包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("type","room_exit");
                param.put("stuId", my_StuId);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            final String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            String strUrl = "127.0.0.0.1";
            URL url = null;
            try {
                url = new URL(strUrl);
                HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("POST");
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content_Type","application/json");
                urlConn.setRequestProperty("CharSet","utf-8");

                urlConn.connect();

                DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
                dop.writeBytes("json="+content);
                dop.flush();
                dop.close();
                urlConn.getInputStream();
                urlConn.disconnect();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Intent intent = new Intent(RoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent) {
            Bundle bundle = intent.getExtras();    //记录长连接service传送的信息
            if(bundle != null) {
                String type = bundle.getString("type");

                if(type == "another_player_ready") {    //有新玩家准备，刷新页面
                    int num = bundle.getInt("ready_num");
                    //刷新已准备玩家昵称
                    TextView tmp = null;
                    for (int i = 1; i <= num; i++) {
                        String player_nickName = bundle.getString("nickName" + i);
                        switch (i) {
                            case 1:
                                tmp = (TextView) findViewById(R.id.textView1);
                                break;
                            case 2:
                                tmp = (TextView) findViewById(R.id.textView2);
                                break;
                            case 3:
                                tmp = (TextView) findViewById(R.id.textView3);
                                break;
                            case 4:
                                tmp = (TextView) findViewById(R.id.textView4);
                                break;
                            case 5:
                                tmp = (TextView) findViewById(R.id.textView5);
                                break;
                            case 6:
                                tmp = (TextView) findViewById(R.id.textView6);
                                break;
                            case 7:
                                tmp = (TextView) findViewById(R.id.textView7);
                                break;
                            case 8:
                                tmp = (TextView) findViewById(R.id.textView8);
                                break;
                            case 9:
                                tmp = (TextView) findViewById(R.id.textView9);
                                break;
                        }
                        tmp.setText(player_nickName);
                    }
                }
                if(type == "can_start_game") {  //人数已满，开始游戏
                    Intent it = new Intent(RoomActivity.this,GameActivity.class);
                    bundle = new Bundle();
                    for(int i = 1; i <= 9; i++) {   //记录所有玩家昵称，给游戏界面初始化使用
                        TextView tmp = null;
                        switch(i) {
                            case 1:
                                tmp = (TextView) findViewById(R.id.textView1);
                                break;
                            case 2:
                                tmp = (TextView) findViewById(R.id.textView2);
                                break;
                            case 3:
                                tmp = (TextView) findViewById(R.id.textView3);
                                break;
                            case 4:
                                tmp = (TextView) findViewById(R.id.textView4);
                                break;
                            case 5:
                                tmp = (TextView) findViewById(R.id.textView5);
                                break;
                            case 6:
                                tmp = (TextView) findViewById(R.id.textView6);
                                break;
                            case 7:
                                tmp = (TextView) findViewById(R.id.textView7);
                                break;
                            case 8:
                                tmp = (TextView) findViewById(R.id.textView8);
                                break;
                            case 9:
                                tmp = (TextView) findViewById(R.id.textView9);
                                break;
                        }
                        bundle.putString(("Textview"+i),tmp.getText().toString());
                    }
                    bundle.putInt("stuId",my_StuId);
                    bundle.putString("nickName",nickName);
                    bundle.putInt("score",score);

                    it.putExtras(bundle);
                    startActivity(it);
                }
            }
        }
    }

}
