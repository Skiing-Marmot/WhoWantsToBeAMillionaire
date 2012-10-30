package com.example.whowants.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.whowants.R;
import com.example.whowants.model.HighScore;
import com.example.whowants.model.HighScoreList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity {

    private String	      SHARED_PREF_FILE_NAME = "settingsPreferences";
    private String	      SHARED_PREF_NAME_KEY  = "playerName";
    private static final String GET_HIGHSCORES_URL    = "http://soletaken.disca.upv.es:8080/WWTBAM/rest/highscores?name=Clem";
    private static final String PLAYER_NAME_KEY       = "name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setContentView(R.layout.activity_score);
	setScoresTab();
	setLocalTableLayout();
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

    private void deleteLocalScores() {
	TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
	table.removeAllViews();
	// TODO delete scores from DB
    }

    private void setScoresTab() {
	TabHost host = (TabHost) findViewById(android.R.id.tabhost);
	host.setup();
	// First tab
	TabSpec spec = host.newTabSpec("LocalTab");
	// spec.setIndicator("Local Scores", getResources().getDrawable(R.drawable.Tab1Icon));
	spec.setIndicator(getString(R.string.local_tab_title));
	spec.setContent(R.id.tab1);
	host.addTab(spec);
	// Second tab
	spec = host.newTabSpec("FriendTab");
	// spec.setIndicator("Friends Scores", getResources().getDrawable(R.drawable.Tab2Icon));
	spec.setIndicator(getString(R.string.friends_tab_title));
	spec.setContent(R.id.tab2);
	host.addTab(spec);
	host.setCurrentTabByTag("LocalTab");
    }

    private void setLocalTableLayout() {
	TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
	TableRow row = new TableRow(this);
	TextView tv = new TextView(this);

	// TODO fetch data from the local database
	tv.setText("player1");
	row.addView(tv);
	tv = new TextView(this);
	tv.setText("500");
	row.addView(tv);
	table.addView(row);

	row = new TableRow(this);
	tv = new TextView(this);
	tv.setText("player1");
	row.addView(tv);
	tv = new TextView(this);
	tv.setText("2000");
	row.addView(tv);
	table.addView(row);
    }

    private void setFriendsTableLayout() {
	// Get player name from the sharedPreferences
	SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
	String playerName = sharedPreferences.getString(SHARED_PREF_NAME_KEY, "");
	if (playerName.length() <= 0) { return; }
	new LoadFriendScore().execute(playerName);
    }

    private class LoadFriendScore extends AsyncTask<String, Integer, Boolean> {
	private String playerName     = null;
	String	 responseString = null;

	@Override
	protected void onPreExecute() {
	    // TODO Auto-generated method stub
	}

	@Override
	protected Boolean doInBackground(String... params) {
	    HttpResponse response = null;
	    playerName = params[0];

	    HttpClient client = new DefaultHttpClient();
	    HttpGet request = new HttpGet(GET_HIGHSCORES_URL);

	   // HttpParams httpParams = new BasicHttpParams();
	    //httpParams.setParameter(PLAYER_NAME_KEY, playerName);
	    //request.setParams(httpParams);

	    try {
		response = client.execute(request);
	    } catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    Log.i("test", playerName + " - " + response);
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		try {
		    responseString = EntityUtils.toString(entity);
		} catch (ParseException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
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
	    if (result) {
		TableLayout table = (TableLayout) findViewById(R.id.friendsTableLayout);
		TableRow row;
		TextView tv;

		Log.i("test", responseString);

		// Convert from JSON to HighScoreList object
		GsonBuilder builder = new GsonBuilder();
		Log.i("test", "apres builder");
		Gson gson = builder.create();
		Log.i("test", "apres gson");
		JSONObject json = null;
		try {
		    json = new JSONObject(responseString);
		    Log.i("test", "apres json");
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		HighScoreList friendScoreList = gson.fromJson(json.toString(), HighScoreList.class);
		Log.i("test", "apres conversion");

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
	    }
	}
    }
}
