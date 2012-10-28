package com.example.whowants.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.whowants.R;
import com.example.whowants.model.Question;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PlayActivity extends Activity {

    private String	      SHARED_PREF_FILE_NAME	  	= "savedPlayPreferences";
    private String	      SHARED_PREF_QUESTION_NUMBER   	= "questionNumber";
    private String	      SHARED_PREF_SCORE	      		= "score";
    private String	      SHARED_PREF_AUDIENCE	   	= "isUsedAudienceJoker";
    private String	      SHARED_PREF_FIFTY	      		= "isUsedFiftyJoker";
    private String	      SHARED_PREF_PHONE	     		= "isUsedPhoneJoker";
    private String	      QUESTION_TAG_NAME	      		= "question";
    private String	      QUESTION_NUMBER_ATTRIBUTE_NAME 	= "number";
    private String	      ANSWER1_ATTRIBUTE_NAME	 	= "answer1";
    private String	      ANSWER2_ATTRIBUTE_NAME	 	= "answer2";
    private String	      ANSWER3_ATTRIBUTE_NAME	 	= "answer3";
    private String	      ANSWER4_ATTRIBUTE_NAME		= "answer4";
    private String	      AUDIENCE_ANSWER_ATTRIBUTE_NAME 	= "audience";
    private String	      FIFTY_ANSWER1_ATTRIBUTE_NAME   	= "fifty1";
    private String	      FIFTY_ANSWER2_ATTRIBUTE_NAME   	= "fifty2";
    private String	      PHONE_ANSWER_ATTRIBUTE_NAME    	= "phone";
    private String	      RIGHT_ANSWER_ATTRIBUTE_NAME    	= "right";
    private String	      QUESTION_TEXT_ATTRIBUTE_NAME   	= "text";

    private int		 	questionNumber			= 1;
    private int		 	score				= 0;
    private ArrayList<Question> questionsList;
    private boolean	     	isUsedAudienceJoker		= false;
    private boolean	     	isUsedFiftyJoker		= false;
    private boolean	     	isUsedPhoneJoker		= false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_play);
	// Load questions list and saved state
	questionsList = generateQuestionList();
	loadSavedState();
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
	saveCurrentState();
    }

    private void loadSavedState() {
	// Get sharedPreferences file
	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
	questionNumber = sharedPreferences.getInt(SHARED_PREF_QUESTION_NUMBER, 1);
	score = sharedPreferences.getInt(SHARED_PREF_SCORE, 0);
	isUsedAudienceJoker = sharedPreferences.getBoolean(SHARED_PREF_AUDIENCE, false);
	isUsedFiftyJoker = sharedPreferences.getBoolean(SHARED_PREF_FIFTY, false);
	isUsedPhoneJoker = sharedPreferences.getBoolean(SHARED_PREF_PHONE, false);
    }

    private void saveCurrentState() {
	// Get sharedPreferences file
	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
	Editor sharedPreferencesEditor = sharedPreferences.edit();
	sharedPreferencesEditor.putInt(SHARED_PREF_QUESTION_NUMBER, questionNumber);
	sharedPreferencesEditor.putInt(SHARED_PREF_SCORE, score);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_AUDIENCE, isUsedAudienceJoker);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_FIFTY, isUsedFiftyJoker);
	sharedPreferencesEditor.putBoolean(SHARED_PREF_PHONE, isUsedPhoneJoker);
	sharedPreferencesEditor.commit();
    }

    private ArrayList<Question> generateQuestionList() {
	ArrayList<Question> list = new ArrayList<Question>();

	try {
	    InputStream inputStream = getResources().openRawResource(R.raw.questions0001);
	    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
	    parser.setInput(inputStream, null);
	    int eventType = XmlPullParser.START_DOCUMENT;

	    while (eventType != XmlPullParser.END_DOCUMENT) {
		if (eventType == XmlPullParser.START_TAG && parser.getName() == QUESTION_TAG_NAME) {
		    // New question from the question tag
		    Question q = new Question(parser.getAttributeValue(null, QUESTION_NUMBER_ATTRIBUTE_NAME), parser.getAttributeValue(
			    null, QUESTION_TEXT_ATTRIBUTE_NAME), parser.getAttributeValue(null, ANSWER1_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, ANSWER2_ATTRIBUTE_NAME), parser.getAttributeValue(null, ANSWER3_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, ANSWER4_ATTRIBUTE_NAME), parser.getAttributeValue(null,
				    RIGHT_ANSWER_ATTRIBUTE_NAME), parser.getAttributeValue(null, AUDIENCE_ANSWER_ATTRIBUTE_NAME),
			    parser.getAttributeValue(null, PHONE_ANSWER_ATTRIBUTE_NAME), parser.getAttributeValue(null,
				    FIFTY_ANSWER1_ATTRIBUTE_NAME), parser.getAttributeValue(null, FIFTY_ANSWER2_ATTRIBUTE_NAME));
		    // Add the question to the list at the index corresponding to its number
		    list.add(q.getNumber(), q);
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

	return list;
    }
}
