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

public class ModifyInfoActivity extends AppCompatActivity {

    private Button Exit;
    private Button Confirm;

    private int stuId;
    private String nickName;
    private String token;

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

        stuId = this.getIntent().getExtras().getInt("stuId");
        nickName = this.getIntent().getExtras().getString("nickName");
        token = this.getIntent().getExtras().getString("token");

    }

    private class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(ModifyInfoActivity.this,HomePageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stuId",stuId);
            bundle.putString("nickName",nickName);
            bundle.putString("token",token);
            intent.putExtras(bundle);
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
                        String strUrl = "http://162.105.175.115:8005/clientCall/modify.php";
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                                .setMessage("修改成功")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                });
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                                .setMessage("修改失败")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                });
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

}
