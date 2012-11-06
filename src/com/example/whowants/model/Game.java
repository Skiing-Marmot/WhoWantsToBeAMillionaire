package com.example.whowants.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.whowants.R;
import com.example.whowants.db.WhoWantsDB;
import com.example.whowants.view.PlayActivity;
import com.example.whowants.view.PlayActivity.GameDialogFragment;
import com.example.whowants.view.PlayActivity.SendPlayerScore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class Game {
    private PlayActivity	activity;

    private String	      SHARED_PREF_GAME_FILE_NAME     = "savedPlayPreferences";
    private String	      SHARED_PREF_SETTINGS_FILE_NAME = "settingsPreferences";
    private String	      SHARED_PREF_QUESTION_NUMBER    = "questionNumber";
    private String	      SHARED_PREF_NAME_KEY	   = "playerName";
    private String	      SHARED_PREF_SCORE	      = "score";
    private String	      SHARED_PREF_AUDIENCE	   = "isUsedAudienceJoker";
    private String	      SHARED_PREF_FIFTY	      = "isUsedFiftyJoker";
    private String	      SHARED_PREF_PHONE	      = "isUsedPhoneJoker";
    private String	      QUESTION_TAG_NAME	      = "question";
    private String	      QUESTION_NUMBER_ATTRIBUTE_NAME = "number";
    private String	      ANSWER1_ATTRIBUTE_NAME	 = "answer1";
    private String	      ANSWER2_ATTRIBUTE_NAME	 = "answer2";
    private String	      ANSWER3_ATTRIBUTE_NAME	 = "answer3";
    private String	      ANSWER4_ATTRIBUTE_NAME	 = "answer4";
    private String	      AUDIENCE_ANSWER_ATTRIBUTE_NAME = "audience";
    private String	      FIFTY_ANSWER1_ATTRIBUTE_NAME   = "fifty1";
    private String	      FIFTY_ANSWER2_ATTRIBUTE_NAME   = "fifty2";
    private String	      PHONE_ANSWER_ATTRIBUTE_NAME    = "phone";
    private String	      RIGHT_ANSWER_ATTRIBUTE_NAME    = "right";
    private String	      QUESTION_TEXT_ATTRIBUTE_NAME   = "text";
    private int		 nbQuestions		    = 15;

    private int		 listLevels[]		   = { 0, 100, 200, 300, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000,
	    125000, 250000, 500000, 1000000		   };

    private int		 questionNumber		 = 1;
    private int		 score			  = 0;
    private boolean	     isUsedAudienceJoker	    = false;
    private boolean	     isUsedFiftyJoker	       = false;
    private boolean	     isUsedPhoneJoker	       = false;
    private String	      playerName		     = "unknown player";

    private ArrayList<Question> listQuestions;

    public Game(PlayActivity activity) {
	this.activity = activity;
    }

    public int getQuestionNumber() {
	return questionNumber;
    }

    public int getLevelValue(int level) {
	return listLevels[level];
    }

    public void loadSavedState() {
	// Get sharedPreferences file
	SharedPreferences sharedGamePreferences = activity.getSharedPreferences(SHARED_PREF_GAME_FILE_NAME, Activity.MODE_PRIVATE);
	questionNumber = sharedGamePreferences.getInt(SHARED_PREF_QUESTION_NUMBER, 1);
	score = sharedGamePreferences.getInt(SHARED_PREF_SCORE, 0);
	isUsedAudienceJoker = sharedGamePreferences.getBoolean(SHARED_PREF_AUDIENCE, false);
	isUsedFiftyJoker = sharedGamePreferences.getBoolean(SHARED_PREF_FIFTY, false);
	isUsedPhoneJoker = sharedGamePreferences.getBoolean(SHARED_PREF_PHONE, false);
	SharedPreferences sharedSettingsPreferences = activity.getSharedPreferences(SHARED_PREF_SETTINGS_FILE_NAME, Activity.MODE_PRIVATE);
	playerName = sharedSettingsPreferences.getString(SHARED_PREF_NAME_KEY, "unknown");
    }

    public void saveCurrentState() {
	// Get sharedPreferences file
	SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREF_GAME_FILE_NAME, Activity.MODE_PRIVATE);
	Editor sharedPreferencesEditor = sharedPreferences.edit();
	sharedPreferencesEditor.putInt(SHARED_PREF_QUESTION_NUMBER, questionNumber);
	sharedPreferencesEditor.putInt(SHARED_PREF_SCORE, score);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_AUDIENCE, isUsedAudienceJoker);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_FIFTY, isUsedFiftyJoker);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_PHONE, isUsedPhoneJoker);
	sharedPreferencesEditor.commit();
    }

    public void generateQuestionList() {
	listQuestions = new ArrayList<Question>();

	try {
	    InputStream inputStream = activity.getResources().openRawResource(R.raw.questions0001);
	    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
	    parser.setInput(inputStream, null);
	    int eventType = XmlPullParser.START_DOCUMENT;

	    while (eventType != XmlPullParser.END_DOCUMENT) {
		if (eventType == XmlPullParser.START_TAG && parser.getName().equals(QUESTION_TAG_NAME)) {
		    // New question from the question tag
		    Question q = new Question(parser.getAttributeValue(null, QUESTION_NUMBER_ATTRIBUTE_NAME), parser.getAttributeValue(
			    null, QUESTION_TEXT_ATTRIBUTE_NAME), parser.getAttributeValue(null, ANSWER1_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, ANSWER2_ATTRIBUTE_NAME), parser.getAttributeValue(null, ANSWER3_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, ANSWER4_ATTRIBUTE_NAME), parser.getAttributeValue(null,
				    RIGHT_ANSWER_ATTRIBUTE_NAME), parser.getAttributeValue(null, AUDIENCE_ANSWER_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, PHONE_ANSWER_ATTRIBUTE_NAME), parser.getAttributeValue(null,
				    FIFTY_ANSWER1_ATTRIBUTE_NAME), parser.getAttributeValue(null, FIFTY_ANSWER2_ATTRIBUTE_NAME));
		    // Add the question to the list at the index corresponding
		    // to its number minus one because first question is number
		    // one (index 0)

		    listQuestions.add(q.getNumber() - 1, q);
		}
		eventType = parser.next();
	    }
	    inputStream.close();

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    Log.e("exception", "Could not read questions from XML file.");
	} catch (XmlPullParserException e) {
	    e.printStackTrace();
	    Log.e("exception", "Could not parse XML file.");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public Question getQuestion() {
	return listQuestions.get(questionNumber - 1);
	// -1 because the array starts at 0
    }

    public void testAnswer(String answer) {
	Log.i("lol", answer + " = " + getQuestion().right);
	if (answer.equals(getQuestion().right)) {
	    if (questionNumber == nbQuestions) {
		activity.questionAnswered("win");

	    } else {
		activity.questionAnswered("next");
	    }
	} else {
	    activity.questionAnswered("lost");
	}
    }

    public void getJokerAnswer(int type) {
	Question currentQuestion = getQuestion();

	if (type == R.id.menu_jokers_fifty) {
	    activity.eliminateAnswer(currentQuestion.getFifty1());
	    activity.eliminateAnswer(currentQuestion.getFifty2());
	} else {
	    int supposedAnswer = 0;
	    String stringSupposedAnswer = null;

	    if (type == R.id.menu_jokers_audience) {
		supposedAnswer = currentQuestion.getAudience();
	    } else {
		supposedAnswer = currentQuestion.getPhone();
	    }

	    switch (supposedAnswer) {
		case 1:
		    stringSupposedAnswer = currentQuestion.getAnswer1();
		    break;
		case 2:
		    stringSupposedAnswer = currentQuestion.getAnswer2();
		    break;
		case 3:
		    stringSupposedAnswer = currentQuestion.getAnswer3();
		    break;
		case 4:
		    stringSupposedAnswer = currentQuestion.getAnswer4();
		    break;
	    }
	    activity.displayJokerAnswer(type, stringSupposedAnswer);
	}
    }

    public void saveScore() {
	// locally
	WhoWantsDB db = new WhoWantsDB(activity);
	db.open();
	Log.i("test", playerName);
	db.insertResult(new HighScore(playerName, listLevels[questionNumber - 1]));
	db.close();
	// On the server
	sendScore(playerName, listLevels[questionNumber - 1]);
    }

    public void nextLevel() {
	questionNumber++;
    }

    private void sendScore(final String playerName, final int score) {
	if (playerName.length() <= 0) {
	    // TODO display dialog saying to set player name and friend name
	    // first
	    return;
	}
	activity.new SendPlayerScore().execute(playerName, String.valueOf(score));
    }

    public void reinitGame() {
	questionNumber = 1;
	score = 0;
	isUsedAudienceJoker = false;
	isUsedFiftyJoker = false;
	isUsedPhoneJoker = false;
    }

}
