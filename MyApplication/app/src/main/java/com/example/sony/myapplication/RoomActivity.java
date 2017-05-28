package com.example.sony.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sony.myapplication.util.FirstEvent;
import com.example.sony.myapplication.util.MyApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;

public class RoomActivity extends AppCompatActivity {
    private final int PLAYER_NUM = 1;

    private Button Exit;
    private Button Ready;
    private TextView roomInfo;

    private int stuId;
    private String nickName;
    private String token;

    private int roomId;    //用户进入房间后保存房间ID，在点击“准备”发送HTTTP请求时使用
    private String roomName;

    private int port;
    private int audio_port;
    private int order;

    private Socket audioSocket;

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

        roomInfo = (TextView)findViewById(R.id.roomInfo);

        stuId = this.getIntent().getExtras().getInt("stuId");
        nickName = this.getIntent().getExtras().getString("nickName");
        token = this.getIntent().getExtras().getString("token");
        roomId = this.getIntent().getExtras().getInt("roomId");
        roomName = this.getIntent().getExtras().getString("roomName");
        port = this.getIntent().getExtras().getInt("port");
        audio_port = this.getIntent().getExtras().getInt("audio_port");
        order = this.getIntent().getExtras().getInt("order");

        roomInfo.setText(roomId+"房间\n"+roomName);

        //开启和该房间端口的长连接
        Intent it = new Intent(RoomActivity.this,WakeService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stuId",stuId);
        bundle.putString("nickName",nickName);
        bundle.putInt("order",order);
        bundle.putInt("port",port);
        it.putExtras(bundle);
        startService(it);   //启动服务

        //注册eventBus
        EventBus.getDefault().register(this);

        //设置退出按钮为不可点击
        Exit.setEnabled(false);

        //开启语音socket通道
        //MyApp myApp = (MyApp)getApplicationContext();
        //myApp.setSocket(order,audio_port);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private class ReadyClickListener implements View.OnClickListener {
        public void onClick(View V) {
            /*//请求该房间的端口号
            JSONObject param = new JSONObject();
            try {
                param.put("roomId",roomId);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            String strUrl = "162.105.175.115:8005/clientCall/login.php";
            URL url;
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

                //测试用
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String res = "";
                String readLine = null;
                while((readLine = bf.readLine()) != null) {
                    res += readLine;
                }
                bf.close();
                Log.i("log_in",("---------------------------------------------------------\n"+res));
                urlConn.disconnect();
                //到此为止

                InputStream is = urlConn.getInputStream();
                urlConn.disconnect();

                byte[] responseBody = getHttpResponseBody.GetHttpResponseBody(is);
                JSONObject ret = new JSONObject(new String(responseBody));
                port = ret.getInt("port");

            }
            catch(Exception e){
                e.printStackTrace();
            }*/
        }
    }

    private class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {

        /*    //包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("roomId",roomId);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            final String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            String strUrl = "162.105.175.115:8005/clientCall/login.php";
            URL url;
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

                //测试用
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String res = "";
                String readLine = null;
                while((readLine = bf.readLine()) != null) {
                    res += readLine;
                }
                bf.close();
                Log.i("log_in",("---------------------------------------------------------\n"+res));
                urlConn.disconnect();
                //到此为止

                urlConn.getInputStream();
                urlConn.disconnect();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Intent intent = new Intent(RoomActivity.this,HomePageActivity.class);
            startActivity(intent);*/
        }
    }

    @Subscribe
    public void onEvent(final FirstEvent event) {
        JSONObject js = event.getJsonData();

        String type = null;
        try {
            type = js.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(type.equals("another_player_ready")) {    //有新玩家准备，刷新页面
            //刷新已准备玩家昵称
            TextView tmp = null;
            for (int i = 1; i <= PLAYER_NUM; i++) {
                String player_nickName = null;

                try {
                    player_nickName = js.getString("nickName" + i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                final String finalPlayer_nickName = player_nickName;
                final TextView finalTmp = tmp;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalTmp.setText(finalPlayer_nickName);
                    }
                });

            }
        }

        if(type.equals("can_start_game")) {  //人数已满，开始游戏
            Intent it = new Intent(RoomActivity.this,GameActivity.class);
            Bundle b = new Bundle();
            for(int i = 1; i <= PLAYER_NUM; i++) {   //记录所有玩家昵称，给游戏界面初始化使用
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
                b.putString(("textView"+i),tmp.getText().toString());
            }
            b.putInt("stuId",stuId);
            b.putString("nickName",nickName);
            b.putString("token",token);

            try {
                b.putString("role",js.getString("role"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            it.putExtras(b);
            startActivity(it);
        }

    }

}
