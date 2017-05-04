package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SearchRoomActivity extends AppCompatActivity {
    private Button RandomIn;
    private Button Exit;

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

    }

    class RandomInClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(SearchRoomActivity.this,RoomActivity.class);
            startActivity(intent);
        }
    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(SearchRoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
