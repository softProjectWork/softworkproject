package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sony.myapplication.util.getHttpResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateRoomActivity extends AppCompatActivity {
    private Button Confirm;
    private Button Exit;

    private int stuId;
    private String nickName;

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

        stuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");

    }

    class ConfirmClickListener implements View.OnClickListener {
        public void onClick(View V) {
            String roomName = ((EditText)findViewById(R.id.InputRoomName)).getText().toString();
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

                /*//测试用
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String res = "";
                String readLine = null;
                while((readLine = bf.readLine()) != null) {
                    res += readLine;
                }
                bf.close();
                Log.i("log_in",("---------------------------------------------------------\n"+res));
                urlConn.disconnect();
                //到此为止*/

                    InputStream is = urlConn.getInputStream();
                    urlConn.disconnect();

                    byte[] responseBody = getHttpResponseBody.GetHttpResponseBody(is);
                    JSONObject ret = new JSONObject(new String(responseBody));
                    int status = ret.getInt("create_room_status");
                    if(status == 1) {
                        Intent intent = new Intent(CreateRoomActivity.this,HomePageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("stuId",stuId);
                        bundle.putString("nickName",nickName);
                        bundle.putInt("roomId",ret.getInt("roomId"));
                        bundle.putString("roomName",roomName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else if(status == 0) {
                        new AlertDialog.Builder(CreateRoomActivity.this)
                                .setMessage("创建房间失败")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(CreateRoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
