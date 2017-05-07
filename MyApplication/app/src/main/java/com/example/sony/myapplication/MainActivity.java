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
                int status = ret.getInt("login_status");

                if(status == 1) {
                    Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("stuId",Integer.valueOf(stuId.getText().toString()));
                    bundle.putString("nickName",ret.getString("nickName"));
                    bundle.putString("token",ret.getString("token"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(status == 0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("登录失败，学号或密码错误")
                            .setPositiveButton("确定",null)
                            .show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class RegisterClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

}
