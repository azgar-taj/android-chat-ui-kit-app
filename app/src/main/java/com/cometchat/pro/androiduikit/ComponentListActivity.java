package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ComponentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);
        findViewById(R.id.cometchat_avatar).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_avatar);
            startActivity(intent);
        });
        findViewById(R.id.cometchat_status_indicator).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_status_indicator);
            startActivity(intent);
        });
        findViewById(R.id.cometchat_badge_count).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_badge_count);
            startActivity(intent);
        });
        findViewById(R.id.cometchat_user_view).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_user_view);
            startActivity(intent);
        });
        findViewById(R.id.cometchat_group_view).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_group_view);
            startActivity(intent);
        });
        findViewById(R.id.cometchat_conversation_view).setOnClickListener(view -> {
            Intent intent = new Intent(ComponentListActivity.this, ComponentLoadActivity.class);
            intent.putExtra("screen",R.id.cometchat_conversation_view);
            startActivity(intent);
        });
    }

    public void backClick(View view)
    {
        super.onBackPressed();
    }
}
