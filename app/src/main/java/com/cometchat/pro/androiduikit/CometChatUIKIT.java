package com.cometchat.pro.androiduikit;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import constants.Constant;
import utils.FontUtils;

public class CometChatUIKIT extends Application {

    private static final String TAG = "CometChatUIKIT";

    @Override
    public void onCreate() {
        super.onCreate();
        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(Constant.AppDetails.REGION).build();
        new FontUtils(this);

        CometChat.init(this, Constant.AppDetails.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSuccess: "+s);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(CometChatUIKIT.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
