package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePageActivity extends AppCompatActivity{

    private Button CreateRoom;
    private Button SearchRoom;
    private Button ModifyInfo;
    private TextView nickNameView;

    private int stuId;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        CreateRoom = (Button)findViewById(R.id.CreateRoom);
        CreateRoomClickListener crcl = new CreateRoomClickListener();
        CreateRoom.setOnClickListener(crcl);

        SearchRoom = (Button)findViewById(R.id.SearchRoom);
        SearchRoomClickListener srcl = new SearchRoomClickListener();
        SearchRoom.setOnClickListener(srcl);

        ModifyInfo = (Button)findViewById(R.id.ModifyInfo);
        ModifyInfoClickListener micl = new ModifyInfoClickListener();
        ModifyInfo.setOnClickListener(micl);

        nickNameView = (TextView)findViewById(R.id.nickNameView);

        stuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");
        nickNameView.setText(nickName);
    }

    private class CreateRoomClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,CreateRoomActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stuId",stuId);
            bundle.putString("nickName",nickName);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class SearchRoomClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,SearchRoomActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stuId",stuId);
            bundle.putString("nickName",nickName);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class ModifyInfoClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,ModifyInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stuId",stuId);
            bundle.putString("nickName",nickName);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}
