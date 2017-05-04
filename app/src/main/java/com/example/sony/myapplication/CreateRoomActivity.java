package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CreateRoomActivity extends AppCompatActivity {
    private Button Confirm;
    private Button Exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        Confirm = (Button)findViewById(R.id.Confirm);
        ConfirmClickListener ccl = new ConfirmClickListener();
        Confirm.setOnClickListener(ccl);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

    }

    class ConfirmClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(CreateRoomActivity.this,RoomActivity.class);
            startActivity(intent);
        }
    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(CreateRoomActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
