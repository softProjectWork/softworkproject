package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ModifyInfoActivity extends AppCompatActivity {

    private Button Exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_info);

        Exit = (Button)findViewById(R.id.Exit);
        ExitClickListener ecl = new ExitClickListener();
        Exit.setOnClickListener(ecl);

    }

    class ExitClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent intent = new Intent(ModifyInfoActivity.this,HomePageActivity.class);
            startActivity(intent);
        }
    }

}
