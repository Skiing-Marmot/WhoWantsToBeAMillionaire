package com.example.whowants.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.whowants.R;
import com.example.whowants.model.Game;
import com.example.whowants.model.Question;

/*
 * Class for the Play activity (game)
 */
public class PlayActivity extends FragmentActivity {

    private static final int JOKERS_NUMBER = 3;
    private Game	     game;
    private MenuItem	 menuItem0     = null;
    private MenuItem	 menuItem1     = null;
    private MenuItem	 menuItem2     = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_play);
	// Create a new Game, load questions list and saved state
	game = new Game(this);
	game.loadSavedState();
	game.generateQuestionList();
	// Initialize the interface and display the current question
	init();
	draw();
    }

    /**
     * Return the game played by this activity
     * 
     * @return the game
     */
    public Game getGame() {
	return game;
    }

    /**
     * Initialize the interface and bind the buttons to the corresponding answer
     */
    public void init() {
	Button btn1 = (Button) findViewById(R.id.button1);
	Button btn2 = (Button) findViewById(R.id.button2);
	Button btn3 = (Button) findViewById(R.id.button3);
	Button btn4 = (Button) findViewById(R.id.button4);

	btn1.setOnClickListener(new Button.OnClickListener() {
	    public void onClick(View v) {
		game.testAnswer("1");
	    }
	});
	btn2.setOnClickListener(new Button.OnClickListener() {
	    public void onClick(View v) {
		game.testAnswer("2");
	    }
	});
	btn3.setOnClickListener(new Button.OnClickListener() {
	    public void onClick(View v) {
		game.testAnswer("3");
	    }
	});
	btn4.setOnClickListener(new Button.OnClickListener() {
	    public void onClick(View v) {
		game.testAnswer("4");
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_play, menu);
	menuItem0 = menu.getItem(0);
	menuItem1 = menu.getItem(1);
	menuItem2 = menu.getItem(2);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// The players asks for a joker
	int itemId = item.getItemId();
	// The corresponding joker is hidden and cannot be used anymore for that question
	item.setEnabled(false).setVisible(false);
	// The Game Class will give the corresponding joker
	game.getJokerAnswer(itemId);

	return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	// Before displaying the menu, we test if the user is still allowed to use joker
	if (!game.canUseJoker()) {
	    // if not, we hide all the jokers
	    for (int i = 0; i < JOKERS_NUMBER; i++) {
		menu.getItem(i).setEnabled(false).setVisible(false);
	    }
	}
	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
	super.onPause();
	// We save the current state of the game
	game.saveCurrentState();
    }

    /**
     * Draw the interface according to the current question That method is called for each new question
     */
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

	// Enable and display all buttons if they had been disabled for the 50/50 joker
	btn1.setEnabled(true);
	btn2.setEnabled(true);
	btn3.setEnabled(true);
	btn4.setEnabled(true);
	btn1.setVisibility(android.view.View.VISIBLE);
	btn2.setVisibility(android.view.View.VISIBLE);
	btn3.setVisibility(android.view.View.VISIBLE);
	btn4.setVisibility(android.view.View.VISIBLE);

	// Enable all menu items if they had been disabled
	if (menuItem0 != null && menuItem1 != null && menuItem2 != null) {
	    menuItem0.setEnabled(true).setVisible(true);
	    menuItem1.setEnabled(true).setVisible(true);
	    menuItem2.setEnabled(true).setVisible(true);
	}
    }

    /**
     * Class for the dialog used to inform the player about his answer (if it is correct, if it has won or lost)
     */
    public static class GameDialogFragment extends DialogFragment {

	private String state;
	private String messageDialog;
	private String positiveButton;
	Game	   game = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    state = getArguments().getString("state");
	    game = ((PlayActivity) getActivity()).game;

	    // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    if (state == "win") {
		messageDialog = getString(R.string.you_won_dialog);
		positiveButton = getString(R.string.ok);
	    } else if (state == "lost") {
		messageDialog = getString(R.string.you_lost_dialog);
		positiveButton = getString(R.string.ok);
	    } else if (state == "next") {
		messageDialog = getString(R.string.choice_next_dialog);
		positiveButton = getString(R.string.yes);
	    }

	    // Positive button: OK if win or lost to close the dialog and go back to the Home screen
	    // Yes if there is a next question, to continue to that next question
	    builder.setMessage(messageDialog).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
		    if (state == "win" || state == "lost") {
			// Display the Home screen
			((PlayActivity) getActivity()).displayMenu();
		    } else if (state == "next") {
			// Display the next question
			((PlayActivity) getActivity()).draw();
		    }
		}
	    });

	    if (state.equals("next")) {
		// Negative button: No if there is a next question, stop the game there and go back to the Home screen
		builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			// Save the score and reinitialize the game
			game.saveScore(game.getScore());
			game.reinitGame();
			// Display the Home screen
			((PlayActivity) getActivity()).displayMenu();
		    }
		});
	    }

	    // Create the AlertDialog object and return it
	    return builder.create();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
	    // If the game is finished, you cannot come back to the last
	    // question by clicking back button on the dialog
	    if (state.equals("win") || state.equals("lost")) {
		((PlayActivity) getActivity()).displayMenu();
	    }
	    super.onCancel(dialog);
	}
    }

    /**
     * Called from the game class to the display the dialog according to the answer and the corresponding state
     * 
     * @param state
     *            , the game state: "next" if there is a next question, "lost" or "won" if the player has lost or won
     */
    public void questionAnswered(String state) {
	GameDialogFragment dialog = new GameDialogFragment();
	Bundle bundle = new Bundle();
	bundle.putString("state", state);
	dialog.setArguments(bundle);
	dialog.show(getSupportFragmentManager(), ":-)");

    }

    /**
     * Display the Home screen by finishing this activity
     */
    public void displayMenu() {
	// startActivity(new Intent(PlayActivity.this, MainActivity.class));
	this.finish();
    }

    /**
     * Class to display the phone and audience jokers answers
     */
    public static class JokerDialogFragment extends DialogFragment {

	private int    type;
	private String answer;
	private String messageDialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    type = getArguments().getInt("type");
	    answer = getArguments().getString("answer");

	    if (type == R.id.menu_jokers_audience) {
		messageDialog = getResources().getString(R.string.audience_answer) + answer;
	    } else {
		messageDialog = getResources().getString(R.string.phone_answer) + answer;
	    }

	    // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage(messageDialog);
	    // Create the AlertDialog object and return it
	    return builder.create();
	}
    }

    /**
     * Called by the game class to display the audience or phone jokers answers
     * 
     * @param jokerType
     *            , the type of joker: R.id.menu_jokers_audience or R.id.menu_jokers_phone
     * @param answer
     *            , the supposed answer given by the joker
     */
    public void displayJokerAnswer(int jokerType, String answer) {
	// TODO Auto-generated method stub
	JokerDialogFragment dialog = new JokerDialogFragment();
	Bundle bundle = new Bundle();
	bundle.putInt("type", jokerType);
	bundle.putString("answer", answer);
	dialog.setArguments(bundle);
	dialog.show(getSupportFragmentManager(), "joker");
    }

    /**
     * AsyncTask used to send the score to the server
     */
    public class SendPlayerScore extends AsyncTask<String, Integer, Boolean> {
	private static final String PLAYER_NAME_KEY = "name";
	private static final String SCORE_KEY       = "scoring";
	private final String	SEND_SCORE_URL  = getResources().getString(R.string.send_score_url);

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
		client.execute(request);
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
    }

    /**
     * Called by the game class to hide a false answer when the 50/50 joker is used
     * 
     * @param buttonId
     *            , the number of the button to hide
     */
    public void eliminateAnswer(int buttonId) {
	Button btn = (Button) findViewById(getResources().getIdentifier("button" + buttonId, "id", this.getBaseContext().getPackageName()));
	btn.setEnabled(false);
	btn.setVisibility(android.view.View.INVISIBLE);
    }

}
