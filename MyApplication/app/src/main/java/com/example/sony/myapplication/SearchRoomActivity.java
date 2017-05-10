package com.example.sony.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class SearchRoomActivity extends AppCompatActivity {
    private Button RandomIn;
    private Button Exit;
    private SearchView searchView;

    private int stuId;
    private String nickName;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_room);

        RandomIn = (Button)findViewById(R.id.RandomIn);
        RandomInClickListener ricl = new RandomInClickListener();
        RandomIn.setOnClickListener(ricl);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

        searchView = (SearchView)findViewById(R.id.searchView);
        searchViewListener svl = new searchViewListener();
        searchView.setOnQueryTextListener(svl);

        stuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");
        token = savedInstanceState.getString("token");
    }

    private class searchViewListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {

            JSONObject param = new JSONObject();
            try {
                param.put("stuId", stuId);
                param.put("roomId",Integer.valueOf(query));
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
                            Intent intent = new Intent(SearchRoomActivity.this,RoomActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("stuId",stuId);
                            bundle.putString("nickName",nickName);
                            bundle.putInt("roomId",Integer.valueOf(query));
                            bundle.putString("roomName",ret.getString("roomName"));
                            bundle.putInt("port",ret.getInt("port"));
                            bundle.putInt("audio_port",ret.getInt("audio_port"));
                            bundle.putInt("order",ret.getInt("order"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(SearchRoomActivity.this)
                                    .setMessage("加入房间失败，房间已满或不存在")
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

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    private class RandomInClickListener implements View.OnClickListener {
        public void onClick(View V) {

            JSONObject param = new JSONObject();
            try {
                param.put("stuId", stuId);
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
                    String strUrl = "162.105.175.115/backend/clientCall/join.php";
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
                            Intent intent = new Intent(SearchRoomActivity.this,RoomActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("stuId",stuId);
                            bundle.putString("nickName",nickName);
                            bundle.putInt("roomId",ret.getInt("roomId"));
                            bundle.putString("roomName",ret.getString("roomName"));
                            bundle.putInt("port",ret.getInt("port"));
                            bundle.putInt("audio_port",ret.getInt("audio_port"));
                            bundle.putInt("order",ret.getInt("order"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(SearchRoomActivity.this)
                                    .setMessage("没有可用房间")
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

    private class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(SearchRoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
