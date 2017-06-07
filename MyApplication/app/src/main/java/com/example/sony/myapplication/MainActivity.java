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

import com.example.sony.myapplication.util.StringToHex;

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

            final int stu_id = Integer.valueOf(stuId.getText().toString());

            //将用户名和密码包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("stuId", stu_id);
                param.put("passWd", StringToHex.String2Hex(passWd.getText().toString()));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            final String content = String.valueOf(param);

            //将客户端包装的JSON数据发送到服务器
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    String strUrl = "http://162.105.175.115:8005/clientCall/login.php";
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

                        Log.d("request",content.toString());

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
                            Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                            Bundle bundle = new Bundle();
                            Log.d("aa","bb");
                            bundle.putInt("stuId",stu_id);
                            bundle.putString("nickName",ret.getString("nickName"));
                            bundle.putString("token",ret.getString("token"));
                            Log.d("bundle",bundle.toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("登录失败，学号或密码错误")
                                    .setPositiveButton("确定",null)
                                    .show();
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }finally {
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
