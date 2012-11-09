package com.example.whowants.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.whowants.R;
import com.example.whowants.db.WhoWantsDB;
import com.example.whowants.model.HighScore;
import com.example.whowants.model.HighScoreList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class for the Score activity
 */
public class ScoreActivity extends FragmentActivity {

    private String	      SHARED_PREF_FILE_NAME = "settingsPreferences";
    private String	      SHARED_PREF_NAME_KEY  = "playerName";
    private String	      GET_HIGHSCORES_URL    = "";
    private static final String PLAYER_NAME_KEY       = "name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setContentView(R.layout.activity_score);
	GET_HIGHSCORES_URL = getResources().getString(R.string.send_score_url);
	// Initialize the tab structure
	setScoresTab();
	// Display the local scores
	setLocalTableLayout();
	// Display the scores of the player's friends
	setFriendsTableLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_scores, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Clear local scores
	deleteLocalScores();
	return super.onOptionsItemSelected(item);
    }

    /**
     * Delete local scores from the database and from the local scores tab
     */
    private void deleteLocalScores() {
	WhoWantsDB db = new WhoWantsDB(this.getBaseContext());
	db.open();
	db.deleteAllResults();
	db.close();
	TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
	table.removeAllViews();
	// TODO delete scores from DB
    }

    /**
     * Create the two tabs
     */
    private void setScoresTab() {
	TabHost host = (TabHost) findViewById(android.R.id.tabhost);
	host.setup();
	// First tab
	TabSpec spec = host.newTabSpec("LocalTab");
	spec.setIndicator(getString(R.string.local_tab_title), getResources().getDrawable(R.drawable.ic_action_local));
	spec.setContent(R.id.ScrollView01);
	host.addTab(spec);
	// Second tab
	spec = host.newTabSpec("FriendTab");
	spec.setIndicator(getString(R.string.friends_tab_title), getResources().getDrawable(R.drawable.ic_action_friends));
	spec.setContent(R.id.ScrollView02);
	host.addTab(spec);
	host.setCurrentTabByTag("LocalTab");
    }

    /**
     * Display the scores saved locally
     */
    private void setLocalTableLayout() {
	TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
	TableRow row;
	TextView tv;

	// Get the scores from the local database
	WhoWantsDB db = new WhoWantsDB(this.getBaseContext());
	db.open();
	HighScoreList hgList = db.getAllResults();

	// Add a row for each score with the player name and his score
	for (HighScore hg : hgList.getScores()) {
	    row = new TableRow(getBaseContext());

	    tv = new TextView(getBaseContext());
	    tv.setText(hg.getName());
	    row.addView(tv);
	    tv = new TextView(getBaseContext());
	    tv.setText(Integer.toString(hg.getScoring()));
	    row.addView(tv);

	    table.addView(row);
	}
	db.close();
    }

    /**
     * Display the scores for the players' friends The scores are loaded from the server
     */
    private void setFriendsTableLayout() {
	// Display progress bar
	ScoreActivity.this.setProgressBarIndeterminate(true);
	ScoreActivity.this.setProgressBarIndeterminateVisibility(true);
	// Get player name from the sharedPreferences
	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
	String playerName = sharedPreferences.getString(SHARED_PREF_NAME_KEY, "");

	// If the player has not set his name, we display a popup dialog to tell him we cannot load his friends' scores
	if (playerName.length() <= 0) {
	    DialogFragment dialog = NoPlayerNameDialog.newInstance();
	    dialog.show(getSupportFragmentManager(), "alert");
	    return;
	}

	// Else, we load their scores through a LoadFriendScore AsyncTask
	new LoadFriendScore().execute(playerName);
    }

    /**
     * Class to display an indication for the player
     */
    private static class NoPlayerNameDialog extends DialogFragment {

	public static DialogFragment newInstance() {
	    return new NoPlayerNameDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage(R.string.alert_msg_friend_score);
	    return builder.create();
	}
    }

    /**
     * AsyncTask used to loaded the scores of the player's friends
     */
    private class LoadFriendScore extends AsyncTask<String, Integer, Boolean> {
	private static final int CONNECTION_TIMEOUT = 10000;
	private String	   playerName	 = null;
	String		   responseString     = null;

	@Override
	protected void onPreExecute() {
	    // TODO Auto-generated method stub
	}

	@Override
	protected Boolean doInBackground(String... params) {
	    HttpResponse response = null;
	    playerName = params[0];

	    BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

	    HttpClient client = new DefaultHttpClient();

	    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    pairs.add(new BasicNameValuePair(PLAYER_NAME_KEY, playerName));
	    HttpGet request = new HttpGet(GET_HIGHSCORES_URL + "?" + URLEncodedUtils.format(pairs, "utf-8"));
	    request.setHeader("Accept", "application/json");
	    request.setParams(httpParams);
	    try {
		response = client.execute(request);
	    } catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	    }
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		try {
		    responseString = EntityUtils.toString(entity);
		    return true;
		} catch (ParseException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		    return false;
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		    return false;
		}
	    }
	    return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
	    super.onPostExecute(result);
	    if (result) {
		if (responseString.equals("null")) { return; }

		TableLayout table = (TableLayout) findViewById(R.id.friendsTableLayout);
		TableRow row;
		TextView tv;

		// Convert from JSON to HighScoreList object
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		JSONObject json = null;
		try {
		    json = new JSONObject(responseString);
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		HighScoreList friendScoreList = gson.fromJson(json.toString(), HighScoreList.class);

		// Display the scores in the table
		List<HighScore> scores = friendScoreList.getScores();
		int length = scores.size();

		for (int i = 0; i < length; i++) {
		    row = new TableRow(getBaseContext());
		    tv = new TextView(getBaseContext());
		    tv.setText(scores.get(i).getName());
		    row.addView(tv);
		    tv = new TextView(getBaseContext());
		    tv.setText(String.valueOf(scores.get(i).getScoring()));
		    row.addView(tv);
		    table.addView(row);
		}

		// Hide progress bar
		ScoreActivity.this.setProgressBarIndeterminate(false);
		ScoreActivity.this.setProgressBarIndeterminateVisibility(false);
	    } else {
		// Display error message
		TableLayout table = (TableLayout) findViewById(R.id.friendsTableLayout);
		TableRow row = new TableRow(getBaseContext());
		TextView tv = new TextView(getBaseContext());
		tv.setText(R.string.connection_error_message);
		row.addView(tv);
		table.addView(row);

		// Hide progress bar
		ScoreActivity.this.setProgressBarIndeterminate(false);
		ScoreActivity.this.setProgressBarIndeterminateVisibility(false);
	    }
	}
    }
}
