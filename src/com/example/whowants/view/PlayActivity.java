package com.example.whowants.view;

import java.util.ArrayList;

import com.example.whowants.R;
import com.example.whowants.model.Game;
import com.example.whowants.model.Question;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends Activity {

	private Game game;
	private ArrayList<Question> questionsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		// Load questions list and saved state
		game = new Game(this);
		game.loadSavedState();
		game.generateQuestionList();
		draw();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_jokers_phone:
			// TODO phone call
			break;
		case R.id.menu_jokers_fifty:
			// TODO 50/50
			break;
		case R.id.menu_jokers_audience:
			// TODO audience help
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		game.saveCurrentState();
	}

	public void draw() {
		Question currentQuestion = game.getQuestion();
		
		Integer nbQuestion = currentQuestion.getNumber();
		
		Button btn1 = (Button) findViewById(R.id.button1);
		Button btn2 = (Button) findViewById(R.id.button2);
		Button btn3 = (Button) findViewById(R.id.button3);
		Button btn4 = (Button) findViewById(R.id.button4);
		
		TextView txtQuestionNumber = (TextView) findViewById(R.id.question_number);
		TextView txtQuestion = (TextView) findViewById(R.id.question);
		TextView txtNextLevel = (TextView) findViewById(R.id.level);
		
		btn1.setText(currentQuestion.getAnswer1());
		btn2.setText(currentQuestion.getAnswer2());
		btn3.setText(currentQuestion.getAnswer3());
		btn4.setText(currentQuestion.getAnswer4());
		txtQuestionNumber.setText(nbQuestion.toString());
		txtQuestion.setText(currentQuestion.getText());
		txtNextLevel.setText(Integer.toString(game.getLevelValue(currentQuestion.getNumber())));
	}
	
	private class EndGameDialogFragment extends DialogFragment {
		
		private boolean win;
		
		public void setWin(boolean win) {
			this.win = win;
		}
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());/*
	        builder.setMessage(R.string.dialog_you_lost)
	               .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it*/
	        return builder.create();
	    }
	}
}
