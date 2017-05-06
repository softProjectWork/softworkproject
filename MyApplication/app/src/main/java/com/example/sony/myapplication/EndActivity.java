package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity{
    private TextView result;
    private TextView scoreView;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end);

        for(int i = 1; i <= 9; i++) {
            TextView tmp = null;
            switch(i) {
                case 1:
                    tmp = (TextView) findViewById(R.id.textView1);
                    break;
                case 2:
                    tmp = (TextView) findViewById(R.id.textView2);
                    break;
                case 3:
                    tmp = (TextView) findViewById(R.id.textView3);
                    break;
                case 4:
                    tmp = (TextView) findViewById(R.id.textView4);
                    break;
                case 5:
                    tmp = (TextView) findViewById(R.id.textView5);
                    break;
                case 6:
                    tmp = (TextView) findViewById(R.id.textView6);
                    break;
                case 7:
                    tmp = (TextView) findViewById(R.id.textView7);
                    break;
                case 8:
                    tmp = (TextView) findViewById(R.id.textView8);
                    break;
                case 9:
                    tmp = (TextView) findViewById(R.id.textView9);
                    break;
            }
            tmp.setText(savedInstanceState.getString("textView"+i)+"\n"+savedInstanceState.getString("player"+i+"_role"));
        }

        result = (TextView)findViewById(R.id.result);
        result.setText(savedInstanceState.getString("winner")+"胜利");

        scoreView = (TextView)findViewById(R.id.scoreView);
        scoreView.setText("积分"+savedInstanceState.getInt("score"));

        exit = (Button)findViewById(R.id.exit);
        exitClickListener ecl = new exitClickListener();
        exit.setOnClickListener(ecl);

    }

    private class exitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent it = new Intent(EndActivity.this,HomePageActivity.class);
            startActivity(it);
        }
    }

}
