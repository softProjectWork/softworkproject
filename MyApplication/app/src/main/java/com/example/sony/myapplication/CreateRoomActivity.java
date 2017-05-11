package com.example.sony.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateRoomActivity extends AppCompatActivity {
    private Button Confirm;
    private Button Exit;

    private int stuId;
    private String nickName;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        Confirm = (Button)findViewById(R.id.Confirm);
        ConfirmClickListener ccl = new ConfirmClickListener();
        Confirm.setOnClickListener(ccl);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

        stuId = this.getIntent().getExtras().getInt("stuId");
        nickName = this.getIntent().getExtras().getString("nickName");
        token = this.getIntent().getExtras().getString("token");

    }

    class ConfirmClickListener implements View.OnClickListener {
        public void onClick(View V) {
            final String roomName = ((EditText)findViewById(R.id.InputRoomName)).getText().toString();
            if(roomName.equals("")) {
                new AlertDialog.Builder(CreateRoomActivity.this)
                        .setMessage("房间名不能为空")
                        .setPositiveButton("确定",null)
                        .show();
            }
            else {
                JSONObject param = new JSONObject();
                try {
                    param.put("stuId",stuId);
                    param.put("roomName",roomName);
                    param.put("token",token);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
                final String content = String.valueOf(param);

                //将客户端包装的JSON数据发送到服务器
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String strUrl = "http://162.105.175.115:8005/clientCall/create.php";
                        URL url = null;
                        try {
                            url = new URL(strUrl);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        HttpURLConnection urlConn = null;
                        try {
                            assert url != null;
                            urlConn = (HttpURLConnection)url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert urlConn != null;
                            urlConn.setConnectTimeout(3000);
                            urlConn.setDoInput(true);
                            urlConn.setDoOutput(true);
                            urlConn.setRequestMethod("POST");
                            urlConn.setUseCaches(true);
                            urlConn.setRequestProperty("Content_Type","application/json");
                            urlConn.setRequestProperty("CharSet","utf-8");

                            urlConn.connect();
                            Log.d("start", "begin");

                            OutputStream out = urlConn.getOutputStream();
                            BufferedWriter dop = new BufferedWriter(new OutputStreamWriter(out) );
                            dop.write("json=" + content);
                            dop.flush();
                            out.close();
                            dop.close();

                            InputStream is = urlConn.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            String str;
                            StringBuilder buffer = new StringBuilder();
                            if ((str = br.readLine()) != null) {
                                buffer.append(str);
                            }
                            is.close();
                            br.close();

                            Log.d("response",buffer.toString());


                            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                JSONObject ret = new JSONObject(buffer.toString());
                                Intent intent = new Intent(CreateRoomActivity.this,RoomActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("stuId",stuId);
                                bundle.putString("nickName",nickName);
                                bundle.putString("token",token);
                                bundle.putInt("roomId",ret.getInt("roomId"));
                                bundle.putInt("port",ret.getInt("port"));
                                bundle.putInt("audio_port",ret.getInt("audio_port"));
                                bundle.putString("roomName",roomName);
                                bundle.putInt("order",ret.getInt("order"));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            else {
                                new AlertDialog.Builder(CreateRoomActivity.this)
                                        .setMessage("创建房间失败")
                                        .setPositiveButton("确定",null)
                                        .show();
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            assert urlConn != null;
                            urlConn.disconnect();
                        }
                        return null;
                    }
                }.execute();

            }
        }
    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(CreateRoomActivity.this,HomePageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stuId",stuId);
            bundle.putString("nickName",nickName);
            bundle.putString("token",token);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}
