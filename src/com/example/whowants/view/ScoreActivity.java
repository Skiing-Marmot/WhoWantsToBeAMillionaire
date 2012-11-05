package com.example.whowants.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class ScoreActivity extends Activity {

	private String SHARED_PREF_FILE_NAME = "settingsPreferences";
	private String SHARED_PREF_NAME_KEY = "playerName";
	private static final String GET_HIGHSCORES_URL = "http://soletaken.disca.upv.es:8080/WWTBAM/rest/highscores";
	private static final String PLAYER_NAME_KEY = "name";

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
		WhoWantsDB db = new WhoWantsDB(this.getBaseContext());
		db.open();
		db.deleteAllResults();
		db.close();
		TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
		table.removeAllViews();
		// TODO delete scores from DB
	}

	private void setScoresTab() {
		TabHost host = (TabHost) findViewById(android.R.id.tabhost);
		host.setup();
		// First tab
		TabSpec spec = host.newTabSpec("LocalTab");
		// spec.setIndicator("Local Scores",
		// getResources().getDrawable(R.drawable.Tab1Icon));
		spec.setIndicator(getString(R.string.local_tab_title));
		spec.setContent(R.id.tab1);
		host.addTab(spec);
		// Second tab
		spec = host.newTabSpec("FriendTab");
		// spec.setIndicator("Friends Scores",
		// getResources().getDrawable(R.drawable.Tab2Icon));
		spec.setIndicator(getString(R.string.friends_tab_title));
		spec.setContent(R.id.tab2);
		host.addTab(spec);
		host.setCurrentTabByTag("LocalTab");
	}

	private void setLocalTableLayout() {
		TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
		TableRow row; 
		TextView tv; 

		WhoWantsDB db = new WhoWantsDB(this.getBaseContext());
		db.open();
		
		HighScoreList hgList = db.getAllResults();
		
		for (HighScore hg : hgList.getScores()) {
			row = new TableRow(this);
			
			tv = new TextView(this);
			tv.setText(hg.getName());
			row.addView(tv);
			tv = new TextView(this);
			tv.setText(Integer.toString(hg.getScoring()));
			row.addView(tv);
			table.addView(row);
		}
		db.close();
		// TODO fetch data from the local database
		tv = new TextView(this);
		row = new TableRow(this);
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
		// Display progress bar
		ScoreActivity.this.setProgressBarIndeterminate(true);
		ScoreActivity.this.setProgressBarIndeterminateVisibility(true);
		// Get player name from the sharedPreferences
		SharedPreferences sharedPreferences = getSharedPreferences(
				SHARED_PREF_FILE_NAME, MODE_PRIVATE);
		String playerName = sharedPreferences.getString(SHARED_PREF_NAME_KEY,
				"");
		if (playerName.length() > 0)
			new LoadFriendScore().execute(playerName);

	}

	private class LoadFriendScore extends AsyncTask<String, Integer, Boolean> {
		private static final int CONNECTION_TIMEOUT = 10000;
		private String playerName = null;
		String responseString = null;

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
			Log.i("test", playerName + " - " + response);
			HttpEntity entity = response.getEntity();
			Log.i("test", playerName + " - " + entity);
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
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			// Change something in the interface
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {

				if (responseString.equals("null")) {
					return;
				}

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
				HighScoreList friendScoreList = gson.fromJson(json.toString(),
						HighScoreList.class);
				Log.i("test", "apres conversion");

				// Display the scores in the table
				List<HighScore> scores = friendScoreList.getScores();
				Collections.sort(scores, Collections.reverseOrder());
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
