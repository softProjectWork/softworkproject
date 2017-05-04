package com.example.sony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button Login;
    private Button Register;

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
