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

import com.example.sony.myapplication.util.getHttpResponseBody;

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

public class MainActivity extends AppCompatActivity {

    private Button Login;
    private Button Register;
    private EditText stuId;
    private EditText passWd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Login = (Button)findViewById(R.id.Login);
        LoginClickListener ll = new LoginClickListener();
        Login.setOnClickListener(ll);

        Register = (Button)findViewById(R.id.Register);
        RegisterClickListener rl = new RegisterClickListener();
        Register.setOnClickListener(rl);

        stuId = (EditText)findViewById(R.id.StuID);

        passWd = (EditText)findViewById(R.id.Passwd);

    }

    private class LoginClickListener implements View.OnClickListener {
        public void onClick(View V) {

            //将用户名和密码包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("stuId", Integer.valueOf(stuId.getText().toString()));
                param.put("passWd",passWd.getText().toString());
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            final String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String strUrl = "162.105.175.115/backend/clientCall/login.php";
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
                        //dop.writeBytes("json="+content);
                        dop.write(content);
                        dop.flush();
                        out.close();
                        dop.close();

                        /*InputStream is = urlConn.getInputStream();
                        urlConn.disconnect();

                        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            byte[] responseBody = getHttpResponseBody.GetHttpResponseBody(is);
                            JSONObject ret = new JSONObject(new String(responseBody));

                            Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("stuId",Integer.valueOf(stuId.getText().toString()));
                            bundle.putString("nickName",ret.getString("nickName"));
                            bundle.putString("token",ret.getString("token"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("登录失败，学号或密码错误")
                                    .setPositiveButton("确定",null)
                                    .show();
                        }*/

                        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream in = urlConn.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String str;
                            StringBuilder buffer = new StringBuilder();
                            if ((str = br.readLine()) != null) {
                                buffer.append(str);
                            }
                            in.close();
                            br.close();

                            JSONObject rjson = new JSONObject(buffer.toString());
                            Log.d("response", "rjson = " + rjson);
                            Log.d("note", "返回成功");
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

    private class RegisterClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

}
