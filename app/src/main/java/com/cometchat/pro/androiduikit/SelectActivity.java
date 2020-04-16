package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import screen.unified.CometChatUnified;;

public class SelectActivity extends AppCompatActivity {

    private RadioGroup screengroup;

    private RadioButton userRb,conversationRb,groupRb,moreInfoRb;

    private MaterialButton logout;

    private MaterialButton unifiedLaunch;

    private MaterialButton screenLaunch;

    private MaterialButton componentLaunch;

    private CardView directIntentFront,directIntentBack,usingScreenFront,usingScreenBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        logout = findViewById(R.id.logout);
        unifiedLaunch = findViewById(R.id.directLaunch);
        screenLaunch = findViewById(R.id.fragmentlaunch);
        componentLaunch = findViewById(R.id.componentLaunch);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser(v);
            }
        });
        unifiedLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this, CometChatUnified.class));
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });
        componentLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this,ComponentListActivity.class));
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });
        screengroup = (RadioGroup)findViewById(R.id.screenselector);
        userRb = (RadioButton)findViewById(R.id.users);
        groupRb = (RadioButton)findViewById(R.id.groups);
        conversationRb = (RadioButton)findViewById(R.id.conversations);
        moreInfoRb = (RadioButton)findViewById(R.id.moreinfo);
        screengroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (userRb.isChecked())
                {
                    userRb.setBackground(getResources().getDrawable(R.drawable.radiobuttonbackground));
                    groupRb.setBackground(null);
                    conversationRb.setBackground(null);
                    moreInfoRb.setBackground(null);
                }
                else if (conversationRb.isChecked())
                {
                    conversationRb.setBackground(getResources().getDrawable(R.drawable.radiobuttonbackground));
                    userRb.setBackground(null);
                    groupRb.setBackground(null);
                    moreInfoRb.setBackground(null);
                }
                else if (groupRb.isChecked())
                {
                    groupRb.setBackground(getResources().getDrawable(R.drawable.radiobuttonbackground));
                    userRb.setBackground(null);
                    conversationRb.setBackground(null);
                    moreInfoRb.setBackground(null);
                }
                else
                {
                    moreInfoRb.setBackground(getResources().getDrawable(R.drawable.radiobuttonbackground));
                    userRb.setBackground(null);
                    groupRb.setBackground(null);
                    conversationRb.setBackground(null);
                }
            }
        });
        screenLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = screengroup.getCheckedRadioButtonId();
                if (id<0)
                {
                    Snackbar.make(view,"Select any one screen.",Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(SelectActivity.this, ComponentLoadActivity.class);
                    intent.putExtra("screen",id);
                    startActivity(intent);
                    overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                }
            }
        });

    }

    private void logoutUser(View view) {
        CometChat.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                startActivity(new Intent(SelectActivity.this,MainActivity.class));
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(view,"Login Error:"+e.getCode(),Snackbar.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (CometChat.getLoggedInUser()==null)
        {
            startActivity(new Intent(SelectActivity.this,MainActivity.class));
        }

    }

}
