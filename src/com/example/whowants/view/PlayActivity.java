package com.example.whowants.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.whowants.R;
import com.example.whowants.model.Game;
import com.example.whowants.model.Question;

public class PlayActivity extends FragmentActivity {

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
	
public static class GameDialogFragment extends DialogFragment {
		
		private String state;
		private String messageDialog;
		private String  messageBoutonYes;
		private String messageBoutonNo;

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	state = getArguments().getString("state");
	    if (state == "win") {
	    	messageDialog = getString(R.string.you_won_dialog);
	    	messageBoutonYes = getString(R.string.yes);
	    	messageBoutonNo = getString(R.string.no);
	    } else if (state == "lost") {
	    	messageDialog = getString(R.string.you_lost_dialog);
	    	messageBoutonYes = getString(R.string.yes);
	    	messageBoutonNo = getString(R.string.no);
	    } else if (state == "next") {
	    	messageDialog = getString(R.string.choice_next_dialog);
	    	messageBoutonYes = getString(R.string.yes);
	    	messageBoutonNo = getString(R.string.no);
	    	
	    }
	    	
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(messageDialog)
	               .setPositiveButton(messageBoutonYes, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // Save the game and go to menu
	                   }
	               })
	               .setNegativeButton(messageBoutonNo, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // Go to menu
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	public void questionAnswered (String state) {
		GameDialogFragment dialog = new GameDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("state", state);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(),":-)");
	}
	
	private void sendScore(final String playerName, final int score) {
		if (playerName.length() <= 0) {
			// TODO display dialog saying to set player name and friend name
			// first
			return;
		}
		new SendPlayerScore().execute(playerName, String.valueOf(score));
	}
	
	private class SendPlayerScore extends AsyncTask<String, Integer, Boolean> {
		private static final String SEND_SCORE_URL = "http://soletaken.disca.upv.es:8080/WWTBAM/rest/highscores";
		private static final String PLAYER_NAME_KEY = "name";
		private static final String SCORE_KEY = "scoring";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String playerName = params[0];
			String score = params[1];
			
			HttpClient client = new DefaultHttpClient();
			HttpPut request = new HttpPut(SEND_SCORE_URL);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair(PLAYER_NAME_KEY, playerName));
			pairs.add(new BasicNameValuePair(SCORE_KEY, score));
			try {
				request.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse response = client.execute(request);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			// Change something in the interface
		}

		@Override
		protected void onPostExecute(Boolean result) {
		    // TODO Auto-generated method stub
		    super.onPostExecute(result);
		}
	}
}
