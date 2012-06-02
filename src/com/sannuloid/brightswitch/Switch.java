package com.sannuloid.brightswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.provider.Settings;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import java.lang.Runnable;
import android.widget.Toast;
import android.provider.Settings.SettingNotFoundException;
import java.lang.Math;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.PreferenceGroup;
import android.preference.Preference;

public class Switch extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switcher);
        
        String PREF_FILE_NAME = "BrightnessPreferences";
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        
        int stepSaved = preferences.getInt("stepnum", 2);
        stepSaved -= 1;
        int lowest = preferences.getInt("lowest", 1);
        int highest = preferences.getInt("highest", 255);
        int range = highest-lowest;
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        
        float currentBrightness;
        try {
            currentBrightness = Settings.System.getInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            currentBrightness = 0.0f;
        }
        
        float stepSize = ((float) range)/stepSaved;
        int currentstep;
        if (currentBrightness < lowest) {
            currentstep = 0;
        }
        else {
            currentstep = Math.round((currentBrightness-lowest)/stepSize);
        }
        
        if (currentstep >= stepSaved) {
            Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, lowest);
            lp.screenBrightness = lowest/255.0f;
        }
        else {
            Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            int nextBrightness = (int) ((currentstep+1)*stepSize) + lowest;
            Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, nextBrightness);
            lp.screenBrightness = nextBrightness/255.0f;
        }
        
        getWindow().setAttributes(lp);
        
        startActivity(new Intent(this,Dummy.class));
        
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable(){
            public void run() {
                finish();
            }}, 200);
    };
}
