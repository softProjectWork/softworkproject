package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.example.sony.myapplication.util.getHttpResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
                int status = ret.getInt("search_result_status");
                if(status == 1) {
                    Intent intent = new Intent(SearchRoomActivity.this,RoomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("stuId",stuId);
                    bundle.putString("nickName",nickName);
                    bundle.putInt("roomId",Integer.valueOf(query));
                    bundle.putString("roomName",ret.getString("roomName"));
                    bundle.putInt("port",ret.getInt("port"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(status == 0) {
                    new AlertDialog.Builder(SearchRoomActivity.this)
                            .setMessage("加入房间失败，房间已满或不存在")
                            .setPositiveButton("确定",null)
                            .show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
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
                int status = ret.getInt("random_in_status");
                if(status == 1) {
                    Intent intent = new Intent(SearchRoomActivity.this,RoomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("stuId",stuId);
                    bundle.putString("nickName",nickName);
                    bundle.putInt("roomId",ret.getInt("roomId"));
                    bundle.putString("roomName",ret.getString("roomName"));
                    bundle.putInt("port",ret.getInt("port"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(status == 0) {
                    new AlertDialog.Builder(SearchRoomActivity.this)
                            .setMessage("没有可用房间")
                            .setPositiveButton("确定",null)
                            .show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(SearchRoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
