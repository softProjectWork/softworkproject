package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity{

    private Button CreateRoom;
    private Button SearchRoom;
    private Button ModifyInfo;

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

    }

    class CreateRoomClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,CreateRoomActivity.class);
            startActivity(intent);
        }
    }

    class SearchRoomClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,SearchRoomActivity.class);
            startActivity(intent);
        }
    }

    class ModifyInfoClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(HomePageActivity.this,ModifyInfoActivity.class);
            startActivity(intent);
        }
    }

}
