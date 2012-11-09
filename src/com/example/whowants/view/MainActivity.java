package com.example.whowants.view;

import com.example.whowants.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Class for the Main activity (Home)
 */
public class MainActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Button to the Settings screen
		final Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    // on click, start the Settings activity
				startActivity(new Intent(MainActivity.this,
								SettingsActivity.class));
			}
		});

		// Button to the game
		final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
		buttonPlay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// on click, start the Play activity
				startActivity(
						new Intent(MainActivity.this,
								PlayActivity.class));
			}
		});
		
		// Button to the Scores screen
		final Button buttonScores = (Button) findViewById(R.id.buttonScores);
		buttonScores.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// on click, start the Score activity
				startActivity(
						new Intent(MainActivity.this,
								ScoreActivity.class));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// There is only one item, it display the Credits dialog
		DialogFragment test = MyDialog.newInstance();
		test.show(getSupportFragmentManager(), "dialog");
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Class for the Credits dialog
	 * It extends DialogDragment
	 */
	private static class MyDialog extends DialogFragment {

		public static DialogFragment newInstance() { 
			return new MyDialog(); 
		} 

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) { 
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
			LayoutInflater inflater = getActivity().getLayoutInflater(); 
			builder.setView(inflater.inflate(R.layout.credits_dialog, null)); 
			return builder.create(); 
		} 

	} 

}
