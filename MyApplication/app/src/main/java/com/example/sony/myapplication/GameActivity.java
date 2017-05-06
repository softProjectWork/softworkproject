package com.example.sony.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity{

    private int stuId;
    private String nickName;
    private String role;

    private MyReceiver receiver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        //保存自己的学号、昵称、角色，并弹窗显示角色
        stuId = savedInstanceState.getInt("stuId");
        nickName = savedInstanceState.getString("nickName");
        role = savedInstanceState.getString("role");

        new AlertDialog.Builder(this)
                .setMessage("你的身份是"+role)
                .setPositiveButton("确定",null)
                .show();

        //刷新昵称
        for(int i = 1; i <= 9; i++) {
            String str = savedInstanceState.getString("textView"+i);
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
            tmp.setText(str);
        }

        textView = (TextView)findViewById(R.id.textView);
        pass = (Button)findViewById(R.id.pass);
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

        imageButton1.setEnabled(false);
        imageButton2.setEnabled(false);
        imageButton3.setEnabled(false);
        imageButton4.setEnabled(false);
        imageButton5.setEnabled(false);
        imageButton6.setEnabled(false);
        imageButton7.setEnabled(false);
        imageButton8.setEnabled(false);
        imageButton9.setEnabled(false);

        //注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.sony.myapplication.WakeService");
        this.registerReceiver(receiver,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,WakeService.class));
    }

    private class Image1ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",1);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image2ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",2);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image3ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",3);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image4ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",4);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image5ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",5);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image6ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",6);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image7ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",7);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image8ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",8);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class Image9ClickListener implements View.OnClickListener {
        public void onClick(View V) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","choose_player");
            bundle.putInt("chosen_order",9);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();    //记录长连接service传送的信息
            if(bundle != null) {
                String type = bundle.getString("type");
                if(type.equals("sys_info")) {
                    textView.setText(bundle.getString("sys_info"));
                }
                if( (type.equals("prophet_start") && role.equals("prophet")) || (type.equals("werewolf_start") && role.equals("werewolf")) ) {
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
                if(type.equals("prophet_end") && role.equals("prophet")) {
                    imageButton1.setEnabled(false);
                    imageButton2.setEnabled(false);
                    imageButton3.setEnabled(false);
                    imageButton4.setEnabled(false);
                    imageButton5.setEnabled(false);
                    imageButton6.setEnabled(false);
                    imageButton7.setEnabled(false);
                    imageButton8.setEnabled(false);
                    imageButton9.setEnabled(false);

                    String str = bundle.getString("prophet_identify_role");
                    new AlertDialog.Builder(GameActivity.this)
                            .setMessage("你要验证的人是"+str)
                            .setPositiveButton("确定",null)
                            .show();
                }
                if (type.equals("kill_people_refresh") && role.equals("werewolf")) {
                    for(int i = 1; i <= 9; i++) {
                        int cnt = bundle.getInt("player"+i+"_killed_cnt");
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
                            tmp.setText("");
                            tmp.setBackgroundColor(0x0000FF00);
                        }
                        else if(cnt > 0) {
                            tmp.setText(cnt);
                            tmp.setBackgroundResource(R.drawable.circle_red);
                        }
                    }
                }
                if( (type.equals("werewolf_end") && role.equals("werewolf")) || ( type.equals("witch_end") && role.equals("witch") )) {
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
                if(type.equals("witch_save_start") && role.equals("witch")) {
                    int killed_player_order = bundle.getInt("killed_player_order");
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
                if(type.equals("switch_to_day")) {
                    getWindow().setBackgroundDrawableResource(R.drawable.day);

                    for(int i = 1; i <= 9; i++) {
                        int status = bundle.getInt("player"+i+"_status");
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
                            tmp.setBackgroundResource(R.drawable.player_die);
                        }
                    }

                    imageButton1.setEnabled(false);
                    imageButton2.setEnabled(false);
                    imageButton3.setEnabled(false);
                    imageButton4.setEnabled(false);
                    imageButton5.setEnabled(false);
                    imageButton6.setEnabled(false);
                    imageButton7.setEnabled(false);
                    imageButton8.setEnabled(false);
                    imageButton9.setEnabled(false);

                    pass.setText("过了");
                    pass.setBackgroundResource(R.drawable.round_rectangle);
                }
                if(type.equals("hunter_killed") && role.equals("hunter")) {
                    new AlertDialog.Builder(GameActivity.this)
                            .setMessage("请选择一个玩家带走")
                            .setPositiveButton("确定",null)
                            .show();
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
                if(type.equals("you_are_died")) {
                    role = null;
                    imageButton1.setEnabled(false);
                    imageButton2.setEnabled(false);
                    imageButton3.setEnabled(false);
                    imageButton4.setEnabled(false);
                    imageButton5.setEnabled(false);
                    imageButton6.setEnabled(false);
                    imageButton7.setEnabled(false);
                    imageButton8.setEnabled(false);
                    imageButton9.setEnabled(false);
                    pass.setEnabled(false);
                }
                if(type.equals("vote_start") && role != null) {
                    imageButton1.setEnabled(true);
                    imageButton2.setEnabled(true);
                    imageButton3.setEnabled(true);
                    imageButton4.setEnabled(true);
                    imageButton5.setEnabled(true);
                    imageButton6.setEnabled(true);
                    imageButton7.setEnabled(true);
                    imageButton8.setEnabled(true);
                    imageButton9.setEnabled(true);
                    pass.setEnabled(true);
                }
                if(type.equals("day_vote_die")) {
                    for(int i = 1; i <= 9; i++) {
                        int status = bundle.getInt("player"+i+"_status");
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
                            tmp.setBackgroundResource(R.drawable.player_die);
                        }
                    }
                }
                if(type.equals( "switch_to_night")) {
                    getWindow().setBackgroundDrawableResource(R.drawable.night);
                    pass.setEnabled(false);
                    pass.setText("");
                    pass.setBackgroundColor(0x0000FF00);
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
                if(type.equals("game_over")) {
                    Intent it = new Intent(GameActivity.this,EndActivity.class);
                    Bundle b = new Bundle();
                    for(int i = 1; i <= 9; i++) {   //记录所有玩家昵称
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
                    b.putString("winner",bundle.getString("winner"));
                    b.putInt("score",bundle.getInt("score"));
                    for(int i = 1; i <= 9; i++) {
                        b.putString("player"+i+"_role",bundle.getString("player"+i+"_role"));
                    }
                    it.putExtras(bundle);
                    startActivity(it);
                }
            }
        }
    }

    private class positiveClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface d, int i) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","yes_or_no");
            bundle.putInt("yes_or_no",1);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

    private class negativeClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface d, int i) {
            Intent it = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("type","yes_or_no");
            bundle.putInt("yes_or_no",0);
            it.putExtras(bundle);
            sendBroadcast(it);
        }
    }

}
