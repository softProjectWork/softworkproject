package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoomActivity extends AppCompatActivity {
    private Button Exit;
    private Button Ready;

    private int my_StuId;
    private String nickName;
    private int score;

    private int player_Id;
    private String player_nickName;
    private int order;

    private int startGame;

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

        my_StuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");
        score = savedInstanceState.getInt("score");

    }

    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            startGame = bundle.getInt("startGame");

            if(startGame == 0) {
                player_Id = bundle.getInt("stuId");
                player_nickName = bundle.getString("nickName");
                order = bundle.getInt("order");

                TextView tmp = null;
                switch(order) {
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
            else {
                Intent it = new Intent(this,GameActivity.class);
                bundle = new Bundle();
                for(int i = 1; i <= 9; i++) {
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
                it.putExtras(bundle);
                startActivity(it);
            }

        }

    }

    class ReadyClickListener implements View.OnClickListener {
        public void onClick(View V) {

            //包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("type","room_ready");
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
                dop.write(content.getBytes());
                dop.flush();
                dop.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }

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
                dop.write(content.getBytes());
                dop.flush();
                dop.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Intent intent = new Intent(RoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
