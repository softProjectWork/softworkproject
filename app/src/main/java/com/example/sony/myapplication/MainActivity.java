package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button Login;
    private Button Register;
    private EditText StuID;
    private EditText Passwd;

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

        StuID = (EditText)findViewById(R.id.StuID);

        Passwd = (EditText)findViewById(R.id.Passwd);

        //Cbl t = new Cbl();
        //eatbox.setOnCheckedChangeListener(t);
        //sleepbox.setOnCheckedChangeListener(t);
        //dotabox.setOnCheckedChangeListener(t);

        //textview = (TextView)findViewById(R.id.textView);
        //textview.setText("Hello SZY");
        //textview.setBackgroundColor(Color.BLUE);

        //button = (Button)findViewById(R.id.button);
        //ButtonListener bl = new ButtonListener();
        //button.setOnClickListener(bl);

    }

    class LoginClickListener implements View.OnClickListener {
        public void onClick(View V) {

            //将用户名和密码包装成JSON格式
            JSONObject param = new JSONObject();
            try {
                param.put("stuId", StuID.getText().toString());
                param.put("passWd",Passwd.getText().toString());
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

            Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

    class RegisterClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

    /*class Cbl implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    }*/

}
