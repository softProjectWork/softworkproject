package com.example.sony.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sony.myapplication.util.AmrAudioEncoder;
import com.example.sony.myapplication.util.AmrAudioPlayer;
import com.example.sony.myapplication.util.FirstEvent;
import com.example.sony.myapplication.util.MyApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class GameActivity extends AppCompatActivity{

    private final int PLAYER_NUM = 1;

    private int stuId;
    private String nickName;
    private String token;
    private String role;

    private boolean multipleClick = false;

    private TextView textView;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;
    private ImageButton imageButton7;
    private ImageButton imageButton8;
    private ImageButton imageButton9;

    private Image1ClickListener icl1;
    private Image2ClickListener icl2;
    private Image3ClickListener icl3;
    private Image4ClickListener icl4;
    private Image5ClickListener icl5;
    private Image6ClickListener icl6;
    private Image7ClickListener icl7;
    private Image8ClickListener icl8;
    private Image9ClickListener icl9;

    private Button pass;

    private AmrAudioEncoder amrEncoder;
    private AmrAudioPlayer audioPlayer;

    private Socket audioSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        //注册eventBus
        EventBus.getDefault().register(this);

        //保存自己的学号、昵称、角色，并弹窗显示角色
        stuId = this.getIntent().getExtras().getInt("stuId");
        nickName = this.getIntent().getExtras().getString("nickName");
        token = this.getIntent().getExtras().getString("token");
        role = this.getIntent().getExtras().getString("role");

        //audioSocket = ((MyApp)getApplicationContext()).getSocket();

        new AlertDialog.Builder(this)
                .setMessage("你的身份是"+role)
                .setPositiveButton("确定",null)
                .show();

        //刷新昵称
        for(int i = 1; i <= PLAYER_NUM; i++) {
            final String str = this.getIntent().getExtras().getString("textView"+i);
            TextView tmp = null;
            switch (i) {
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
            final TextView finalTmp = tmp;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalTmp.setText(str);
                }
            });
        }

        textView = (TextView)findViewById(R.id.textView);

        pass = (Button)findViewById(R.id.pass);
        pass.setOnClickListener(new PassClickListener());
        pass.setEnabled(false);

        //绑定图标监听器
        imageButton1 = (ImageButton)findViewById(R.id.imageButton1);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
        imageButton3 = (ImageButton)findViewById(R.id.imageButton3);
        imageButton4 = (ImageButton)findViewById(R.id.imageButton4);
        imageButton5 = (ImageButton)findViewById(R.id.imageButton5);
        imageButton6 = (ImageButton)findViewById(R.id.imageButton6);
        imageButton7 = (ImageButton)findViewById(R.id.imageButton7);
        imageButton8 = (ImageButton)findViewById(R.id.imageButton8);
        imageButton9 = (ImageButton)findViewById(R.id.imageButton9);

        icl1 = new Image1ClickListener();
        icl2 = new Image2ClickListener();
        icl3 = new Image3ClickListener();
        icl4 = new Image4ClickListener();
        icl5 = new Image5ClickListener();
        icl6 = new Image6ClickListener();
        icl7 = new Image7ClickListener();
        icl8 = new Image8ClickListener();
        icl9 = new Image9ClickListener();

        imageButton1.setOnClickListener(icl1);
        imageButton2.setOnClickListener(icl2);
        imageButton3.setOnClickListener(icl3);
        imageButton4.setOnClickListener(icl4);
        imageButton5.setOnClickListener(icl5);
        imageButton6.setOnClickListener(icl6);
        imageButton7.setOnClickListener(icl7);
        imageButton8.setOnClickListener(icl8);
        imageButton9.setOnClickListener(icl9);

        setAllImageButtonOff();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,WakeService.class));

        if(audioSocket != null) {
            try {
                audioSocket.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (amrEncoder != null) {
                amrEncoder.stop();
            }
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void setAllImageButtonOn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageButton1.setEnabled(true);
                imageButton2.setEnabled(true);
                imageButton3.setEnabled(true);
                imageButton4.setEnabled(true);
                imageButton5.setEnabled(true);
                imageButton6.setEnabled(true);
                imageButton7.setEnabled(true);
                imageButton8.setEnabled(true);
                imageButton9.setEnabled(true);
            }
        });
    }

    public void setAllImageButtonOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageButton1.setEnabled(false);
                imageButton2.setEnabled(false);
                imageButton3.setEnabled(false);
                imageButton4.setEnabled(false);
                imageButton5.setEnabled(false);
                imageButton6.setEnabled(false);
                imageButton7.setEnabled(false);
                imageButton8.setEnabled(false);
                imageButton9.setEnabled(false);
            }
        });
    }

    public void sendOrder(int i) {
        JSONObject js = new JSONObject();
        try {
            js.put("type","choose_player");
            js.put("chosen_order",i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new FirstEvent(js));
        if(!multipleClick)
            setAllImageButtonOff();
    }

    private class Image1ClickListener implements View.OnClickListener {
        public void onClick(View V) { sendOrder(1); }
    }

    private class Image2ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(2);
        }
    }

    private class Image3ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(3);
        }
    }

    private class Image4ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(4);
        }
    }

    private class Image5ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(5);
        }
    }

    private class Image6ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(6);
        }
    }

    private class Image7ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(7);
        }
    }

    private class Image8ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(8);
        }
    }

    private class Image9ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            sendOrder(9);
        }
    }

    private class PassClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            pass.setEnabled(false);
            stopEncodeAudio();

            //用原来的socket发送“讲话完毕”信息
            JSONObject js = new JSONObject();
            try {
                js.put("type","speak_over");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new FirstEvent(js));
        }
    }

    private void startEncodeAudio() {
        amrEncoder = AmrAudioEncoder.getArmAudioEncoderInstance();
        amrEncoder.initArmAudioEncoder(this, audioSocket);
        amrEncoder.start();
    }

    private void stopEncodeAudio() {
        if (amrEncoder != null) {
            amrEncoder.stop();
        }
    }

    private void startPlayAudio() {
        audioPlayer = AmrAudioPlayer.getAmrAudioPlayerInstance();
        audioPlayer.initAmrAudioPlayer(this, audioSocket);
        audioPlayer.start();
    }

    private void stopPlayAudio() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
    }

    @Subscribe
    public void onEvent(final FirstEvent event) {
        JSONObject js = event.getJsonData();

        String type = null;
        try {
            type = js.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(type.equals("sys_info")) {
            try {
                textView.setText(js.getString("sys_info"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if( (type.equals("prophet_start") && role.equals("prophet")) ) {
            setAllImageButtonOn();
        }
        if(type.equals("prophet_end") && role.equals("prophet")) {
            setAllImageButtonOff();

            String str = null;
            try {
                str = js.getString("prophet_identify_role");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("你要验证的人是"+str)
                    .setPositiveButton("确定",null)
                    .show();
        }
        if(type.equals("werewolf_start") && role.equals("werewolf")) {
            setAllImageButtonOn();
            multipleClick = true;
        }
        if (type.equals("kill_people_refresh") && role.equals("werewolf")) {
            for(int i = 1; i <= PLAYER_NUM; i++) {
                int cnt = 0;
                try {
                    cnt = js.getInt("player"+i+"_killed_cnt");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView tmp = null;
                switch (i) {
                    case 1:
                        tmp = (TextView)findViewById(R.id.tip1);
                        break;
                    case 2:
                        tmp = (TextView)findViewById(R.id.tip2);
                        break;
                    case 3:
                        tmp = (TextView)findViewById(R.id.tip3);
                        break;
                    case 4:
                        tmp = (TextView)findViewById(R.id.tip4);
                        break;
                    case 5:
                        tmp = (TextView)findViewById(R.id.tip5);
                        break;
                    case 6:
                        tmp = (TextView)findViewById(R.id.tip6);
                        break;
                    case 7:
                        tmp = (TextView)findViewById(R.id.tip7);
                        break;
                    case 8:
                        tmp = (TextView)findViewById(R.id.tip8);
                        break;
                    case 9:
                        tmp = (TextView)findViewById(R.id.tip9);
                        break;
                }
                if(cnt == 0) {
                    final TextView finalTmp = tmp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalTmp.setText("");
                            finalTmp.setBackgroundColor(0x0000FF00);
                        }
                    });
                }
                else if(cnt > 0) {
                    final TextView finalTmp = tmp;
                    final int Cnt = cnt;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalTmp.setText(Cnt);
                            finalTmp.setBackgroundResource(R.drawable.circle_red);
                        }
                    });
                }
            }
        }
        if( (type.equals("werewolf_end") && role.equals("werewolf")) ) {
            setAllImageButtonOff();
            multipleClick = false;
        }
        if(type.equals("witch_save_start") && role.equals("witch")) {
            int killed_player_order = 0;
            try {
                killed_player_order = js.getInt("killed_player_order");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("今晚"+killed_player_order+"号玩家死了，你要救吗？")
                    .setPositiveButton("救",new positiveClickListener())
                    .setNegativeButton("不救",new negativeClickListener())
                    .show();
        }

        if (type.equals("witch_poison_choice") && role.equals("witch")) {
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("今晚你要毒人吗？")
                    .setPositiveButton("毒",new positiveClickListener())
                    .setNegativeButton("不毒",new negativeClickListener())
                    .show();
        }
        if (type.equals("witch_poison_start") && role.equals("witch")) {
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("请选择一个玩家下毒！")
                    .setPositiveButton("确定",null)
                    .show();
            setAllImageButtonOn();
        }
        if(type.equals("switch_to_day")) {
            getWindow().setBackgroundDrawableResource(R.drawable.day);

            for(int i = 1; i <= PLAYER_NUM; i++) {
                int status = 0;
                try {
                    status = js.getInt("player"+i+"_status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(status == 0) {
                    ImageButton tmp = null;
                    switch(i) {
                        case 1:
                            tmp = imageButton1;
                            break;
                        case 2:
                            tmp = imageButton2;
                            break;
                        case 3:
                            tmp = imageButton3;
                            break;
                        case 4:
                            tmp = imageButton4;
                            break;
                        case 5:
                            tmp = imageButton5;
                            break;
                        case 6:
                            tmp = imageButton6;
                            break;
                        case 7:
                            tmp = imageButton7;
                            break;
                        case 8:
                            tmp = imageButton8;
                            break;
                        case 9:
                            tmp = imageButton9;
                            break;
                    }
                    final ImageButton finalTmp = tmp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalTmp.setBackgroundResource(R.drawable.player_die);
                        }
                    });
                }
            }
            setAllImageButtonOff();

            pass.setText("过了");
            pass.setBackgroundResource(R.drawable.round_rectangle);
        }
        if(type.equals("hunter_killed") && role.equals("hunter")) {
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("请选择一个玩家带走")
                    .setPositiveButton("确定",null)
                    .show();
            setAllImageButtonOn();
        }
        if(type.equals("you_are_died") && role != null) {
            role = null;
            setAllImageButtonOff();
            pass.setEnabled(false);
        }
        if(type.equals("your_turn_to_speak") && role != null) {
            pass.setEnabled(true);
            startEncodeAudio();
        }
        if(type.equals("your_can_listen") ) {
            pass.setEnabled(false);
            startPlayAudio();
        }
        if(type.equals("you_have_heard_out")) {
            pass.setEnabled(false);
            stopPlayAudio();
        }
        if(type.equals("vote_start") && role != null) {
            setAllImageButtonOn();
            pass.setEnabled(false);
        }
        if(type.equals("voted_to_die")) {
            for(int i = 1; i <= PLAYER_NUM; i++) {
                int status = 0;
                try {
                    status = js.getInt("player"+i+"_status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(status == 0) {
                    ImageButton tmp = null;
                    switch(i) {
                        case 1:
                            tmp = imageButton1;
                            break;
                        case 2:
                            tmp = imageButton2;
                            break;
                        case 3:
                            tmp = imageButton3;
                            break;
                        case 4:
                            tmp = imageButton4;
                            break;
                        case 5:
                            tmp = imageButton5;
                            break;
                        case 6:
                            tmp = imageButton6;
                            break;
                        case 7:
                            tmp = imageButton7;
                            break;
                        case 8:
                            tmp = imageButton8;
                            break;
                        case 9:
                            tmp = imageButton9;
                            break;
                    }
                    final ImageButton finalTmp = tmp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalTmp.setBackgroundResource(R.drawable.player_die);
                        }
                    });
                }
            }
        }
        if(type.equals( "switch_to_night")) {
            getWindow().setBackgroundDrawableResource(R.drawable.night);
            pass.setEnabled(false);
            pass.setText("");
            pass.setBackgroundColor(0x0000FF00);
            setAllImageButtonOff();
        }
        if(type.equals("game_over")) {
            Intent it = new Intent(GameActivity.this,EndActivity.class);
            Bundle b = new Bundle();
            for(int i = 1; i <= PLAYER_NUM; i++) {   //记录所有玩家昵称
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
                b.putString(("textView"+i),tmp.getText().toString());
            }
            b.putInt("stuId",stuId);
            b.putString("nickName",nickName);
            b.putString("token",token);

            try {
                b.putString("winner",js.getString("winner"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                b.putInt("score",js.getInt("score"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 1; i <= PLAYER_NUM; i++) {
                try {
                    b.putString("player"+i+"_role",js.getString("player"+i+"_role"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            it.putExtras(b);
            startActivity(it);
        }
    }

    private class positiveClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface d, int i) {

            JSONObject js = new JSONObject();
            try {
                js.put("type","yes_or_no");
                js.put("witch_choice",1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new FirstEvent(js));
        }
    }

    private class negativeClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface d, int i) {

            JSONObject js = new JSONObject();
            try {
                js.put("type","yes_or_no");
                js.put("witch_choice",0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new FirstEvent(js));
        }
    }

}
