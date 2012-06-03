package com.sannuloid.brightswitch;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.PreferenceGroup;
import android.preference.Preference;

public class BrightSwitch extends Activity
{
    // Creates a few variables
    String PREF_FILE_NAME;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    
    TextView stepSavedView;
    TextView lowSavedView;
    TextView highSavedView;
    TextView expSavedView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Finds TextViews for showing current parameters
        stepSavedView = (TextView)findViewById(R.id.stepsaved);
        lowSavedView = (TextView)findViewById(R.id.lowsaved);
        highSavedView = (TextView)findViewById(R.id.highsaved);
        expSavedView = (TextView)findViewById(R.id.expsaved);
        
        // Preferences from file "BrightnessPreferences"
        PREF_FILE_NAME = "BrightnessPreferences";
        preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        
        // Gets current preferences
        int stepSaved = preferences.getInt("stepnum", 2);
        int lowSaved = preferences.getInt("lowest", 1);
        int highSaved = preferences.getInt("highest", 255);
        int expSaved = preferences.getInt("exp", 80);
        
        // Displays current preferences
        stepSavedView.setText(Integer.toString(stepSaved));
        lowSavedView.setText(Integer.toString(lowSaved));
        highSavedView.setText(Integer.toString(highSaved));
        expSavedView.setText(Float.toString(expSaved/100.0f));
        
        // Listens on saveButton
        Button saveButton = (Button) findViewById (R.id.savebutton);
        saveButton.setOnClickListener (btnListener);
    }
    
    private OnClickListener btnListener = new OnClickListener() {
        public void onClick(View v) {
            try {
                // Finds EditTexts
                EditText stepInput = (EditText)findViewById(R.id.step);
                EditText lowInput = (EditText)findViewById(R.id.lowest);
                EditText highInput = (EditText)findViewById(R.id.highest);
                EditText expInput = (EditText)findViewById(R.id.exp);
                
                // For each parameter
                //   if there is no value in EditText, then use the one stored
                //   otherwise get the value from EditText
                Integer stepNum;
                if (stepInput.getText().length() == 0) {
                    stepNum = preferences.getInt("stepnum", 2);
                }
                else {
                    stepNum = Integer.parseInt(stepInput.getText().toString());
                }
                
                Integer lowest;
                if (lowInput.getText().length() == 0) {
                    lowest = preferences.getInt("lowest", 1);
                }
                else {
                    lowest = Integer.parseInt(lowInput.getText().toString());
                }
                
                Integer highest;
                if (highInput.getText().length() == 0) {
                    highest = preferences.getInt("highest", 255);
                }
                else {
                    highest = Integer.parseInt(highInput.getText().toString());
                }
                
                Integer exp;
                if (expInput.getText().length() == 0) {
                    exp = preferences.getInt("exp", 80);
                }
                else {
                    exp = (int) ((Float.valueOf(expInput.getText().toString()).floatValue()) * 100);
                }
                
                // Checks for invalid parameter values
                if (stepNum < 2) {
                    throw new Exception("Wrong stepNum");
                }
                
                if (lowest < 1) {
                    throw new Exception("Wrong lowest");
                }
                else if (lowest > 255) {
                    throw new Exception("Wrong lowest");
                }
                
                if (highest < 1) {
                    throw new Exception("Wrong highest");
                }
                else if (highest > 255) {
                    throw new Exception("Wrong highest");
                }
                
                if (lowest >= highest) {
                    throw new Exception("lowest must be lower than highest");
                }
                
                if (stepNum > (highest-lowest)) {
                    throw new Exception("Too many steps");
                }
                
                // Saves preferences to file "BrightnessPreferences"
                savePreferences(stepNum, lowest, highest, exp);
                
                // Sets TextViews to show current values
                stepSavedView.setText(Integer.toString(stepNum));
                lowSavedView.setText(Integer.toString(lowest));
                highSavedView.setText(Integer.toString(highest));
                expSavedView.setText(Float.toString(exp/100.0f));
            }
            catch (Exception ex) {
                // Displays error message
                Context context = getApplicationContext();
                CharSequence text = "Not a valid operation.";
                int duration = Toast.LENGTH_LONG;
                
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    };
    
    public void savePreferences(int steps, int lowest, int highest, int exp) {
        // Saves preferences to file "BrightnessPreferences"
        editor.putInt("stepnum", steps);
        editor.putInt("lowest", lowest);
        editor.putInt("highest", highest);
        editor.putInt("exp", exp);
        editor.commit();
    }
}
