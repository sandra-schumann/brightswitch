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
        
        // Preferences from file "BrightnessPreferences"
        String PREF_FILE_NAME = "BrightnessPreferences";
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        
        // Gets preferences
        int stepSaved = preferences.getInt("stepnum", 2);
        stepSaved -= 1;
        int lowest = preferences.getInt("lowest", 1);
        int highest = preferences.getInt("highest", 255);
        float exp = preferences.getInt("exp", 80)/100.0f;
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        
        // Gets current brightness
        float currentBrightness;
        try {
            currentBrightness = Settings.System.getInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            currentBrightness = 0.0f;
        }
        
        // Finds brightness value to be set
        int nextBrightness = nextExponential(currentBrightness, stepSaved, lowest, highest, exp);
        
        // Sets brightness value
        Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        Settings.System.putInt(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, nextBrightness);
        lp.screenBrightness = nextBrightness/255.0f;
        
        getWindow().setAttributes(lp);
        
        // For refreshing screen
        startActivity(new Intent(this,Dummy.class));
        
        // Exits after a while
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable(){
            public void run() {
                finish();
            }}, 200);
    }
    
    // Finds next brightness, assuming brightness is a power-law function
    public int nextExponential(float currentBrightness, int stepSaved, int lowest, int highest, float exponent) {
        float range = (float) (Math.pow(highest, exponent) - Math.pow(lowest, exponent));
        
        // Finds step size and what it currently (approximately) is
        float stepSize = range/stepSaved;
        int currentstep;
        if (currentBrightness < lowest) {
            currentstep = 0;
        }
        else {
            currentstep = (int) Math.round((Math.pow(currentBrightness,exponent)-Math.pow(lowest,exponent))/stepSize);
        }
        
        if (currentstep >= stepSaved) {
            // Returns lowest value
            return lowest;
        }
        else {
            // Returns brightness value for one step up
            int nextBrightness = (int) Math.pow(((currentstep+1)*stepSize), 1.0f/exponent) + lowest;
            return nextBrightness;
        }
    }
}
