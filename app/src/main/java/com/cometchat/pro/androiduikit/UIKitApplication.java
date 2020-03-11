package com.cometchat.pro.androiduikit;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.androiduikit.constants.AppConfig;
import com.cometchat.pro.helpers.Logger;

public class UIKitApplication extends Application {

    private static final String TAG = "UIKitApplication";

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d(TAG, "onCreate: "+this.getPackageName());

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();

        CometChat.init(this, AppConfig.AppDetails.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSuccess: "+s);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(UIKitApplication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
