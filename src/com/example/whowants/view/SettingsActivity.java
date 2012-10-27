package com.example.whowants.view;

import com.example.whowants.R;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsActivity extends Activity {
	
	private String SHARED_PREF_FILE_NAME = "settingsPreferences";
	private String SHARED_PREF_NAME_KEY = "playerName";
	private String SHARED_PREF_HELPS_KEY = "selectedHelpsNbPosition";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        restoreSettings();
    }

    @Override
	protected void onPause() {
		super.onPause();
		saveSettings();
	}

	private void restoreSettings() {
    	// Get sharedPreferences file
    	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    	// Player name
    	EditText playerNameTxtField = (EditText) findViewById(R.id.editPlayerName);
    	playerNameTxtField.setText(sharedPreferences.getString(SHARED_PREF_NAME_KEY, ""));
    	// Allowed helps number
    	Spinner helpsNbSpinner = (Spinner) findViewById(R.id.spinnerNbHelps);
    	helpsNbSpinner.setSelection(sharedPreferences.getInt(SHARED_PREF_HELPS_KEY, 0));
    }
    
    private void saveSettings() {
    	// Get sharedPreferences file
    	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    	Editor sharedPreferencesEditor = sharedPreferences.edit();
    	// Player name
    	EditText playerNameTxtField = (EditText) findViewById(R.id.editPlayerName);
    	sharedPreferencesEditor.putString(SHARED_PREF_NAME_KEY, playerNameTxtField.getText().toString());
    	// Allowed helps number
    	Spinner helpsNbSpinner = (Spinner) findViewById(R.id.spinnerNbHelps);
    	sharedPreferencesEditor.putInt(SHARED_PREF_HELPS_KEY, helpsNbSpinner.getSelectedItemPosition());
    	// Commit
    	sharedPreferencesEditor.commit();
    }
}
