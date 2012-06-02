package com.sannuloid.brightswitch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import java.lang.Runnable;
import android.os.Message;
import android.content.Intent;
import android.view.WindowManager;
import android.provider.Settings;

public class Dummy extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        finish();
    }

}
