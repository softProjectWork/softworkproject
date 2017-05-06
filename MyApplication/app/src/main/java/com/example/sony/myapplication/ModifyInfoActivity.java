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

public class ModifyInfoActivity extends AppCompatActivity {

    private Button Exit;
    private Button Confirm;

    private int stuId;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_info);

        Confirm = (Button)findViewById(R.id.Confirm);
        ConfirmClickListener ccl = new ConfirmClickListener();
        Confirm.setOnClickListener(ccl);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

        stuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");

    }

    private class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(ModifyInfoActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

    private class ConfirmClickListener implements View.OnClickListener {
        public void onClick(View V) {
            String NickName = ((EditText)findViewById(R.id.NickName)).getText().toString();
            String passWd = ((EditText)findViewById(R.id.Passwd)).getText().toString();
            String confirmPassWd = ((EditText)findViewById(R.id.ConfirmPasswd)).getText().toString();

            if(NickName.equals(""))
                NickName = nickName;
            if(passWd.equals("") || !passWd.equals(confirmPassWd)) {
                new AlertDialog.Builder(ModifyInfoActivity.this)
                        .setMessage("密码为空或密码与确认密码不符")
                        .setPositiveButton("确定",null)
                        .show();
            }
            else {
                //发送学号、密码、昵称
                JSONObject param = new JSONObject();
                try {
                    param.put("stuId", Integer.valueOf(stuId));
                    param.put("passWd",passWd);
                    param.put("nickName",NickName);
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
                    int status = ret.getInt("modify_status");
                    if(status == 1) {
                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                .setMessage("修改成功")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                    else if(status == 0) {
                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                .setMessage("修改失败")
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

}
