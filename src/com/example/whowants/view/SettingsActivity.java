package com.example.whowants.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.whowants.R;

/**
 * Class for the Settings activity
 */
public class SettingsActivity extends FragmentActivity {

    private String SHARED_PREF_FILE_NAME = "settingsPreferences";
    private String SHARED_PREF_NAME_KEY  = "playerName";
    private String SHARED_PREF_HELPS_KEY = "selectedHelpsNbPosition";
    // private String ADD_FRIEND_URL = getResources().getString(R.string.add_friend_url);
    private String ADD_FRIEND_URL	= "";
    private String PLAYER_NAME_KEY       = "name";
    private String FRIEND_NAME_KEY       = "friend_name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_settings);
	ADD_FRIEND_URL = getResources().getString(R.string.add_friend_url);
	// Load previously saved settings
	restoreSettings();

	final Button buttonAddFriend = (Button) findViewById(R.id.buttonAddFriend);
	buttonAddFriend.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		// on click, add the friend to the the current player friends
		final EditText playerNameTxt = (EditText) findViewById(R.id.editPlayerName);
		final EditText friendNameTxt = (EditText) findViewById(R.id.editFriendName);
		addFriend(playerNameTxt.getText().toString(), friendNameTxt.getText().toString());
	    }
	});
    }

    @Override
    protected void onPause() {
	super.onPause();

	// Save the settings
	saveSettings();
    }

    /**
     * Restore the previously saved settings
     */
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

    /**
     * Saved the settings in the SharedPreferences
     */
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

    /**
     * Add a friend for the current player on the server
     * 
     * @param playerName
     *            , the name of the current player
     * @param friendName
     *            , the name of the friend to add
     */
    private void addFriend(final String playerName, final String friendName) {
	// If there is no player name or friend name, display a dialog to tell the player to add them
	if (playerName.length() <= 0 || friendName.length() <= 0) {
	    AddFriendAlertDialog dialog = new AddFriendAlertDialog();
	    Bundle bundle = new Bundle();
	    bundle.putString("message", getResources().getString(R.string.alert_msg_add_friend));
	    dialog.setArguments(bundle);
	    dialog.show(getSupportFragmentManager(), "alert");
	    return;
	}
	// Else, send the new friend on the server
	new AddFriendTask().execute(playerName, friendName);
    }

    /**
     * Class to display an alert message to the player
     */
    private static class AddFriendAlertDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage(getArguments().getString("message"));
	    return builder.create();
	}
    }

    /**
     * AsyncTask to send a new added friend to the server
     */
    private class AddFriendTask extends AsyncTask<String, Integer, Boolean> {
	private static final int CONNECTION_TIMEOUT = 10000;

	@Override
	protected Boolean doInBackground(String... params) {
	    String playerName = params[0];
	    String friendName = params[1];

	    BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

	    HttpClient client = new DefaultHttpClient();
	    HttpPost request = new HttpPost(ADD_FRIEND_URL);

	    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    pairs.add(new BasicNameValuePair(PLAYER_NAME_KEY, playerName));
	    pairs.add(new BasicNameValuePair(FRIEND_NAME_KEY, friendName));
	    try {
		request.setEntity(new UrlEncodedFormEntity(pairs));
		request.setParams(httpParams);
		client.execute(request);
	    } catch (UnsupportedEncodingException e) {
		// If the friend cannot be added to the server because, we notify the user
		e.printStackTrace();
		AddFriendAlertDialog dialog = new AddFriendAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putString("message", getResources().getString(R.string.alert_msg_add_friend_http));
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "alert");
		return false;
	    } catch (ClientProtocolException e) {
		e.printStackTrace();
		AddFriendAlertDialog dialog = new AddFriendAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putString("message", getResources().getString(R.string.alert_msg_add_friend_http));
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "alert");
		return false;
	    } catch (IOException e) {
		e.printStackTrace();
		AddFriendAlertDialog dialog = new AddFriendAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putString("message", getResources().getString(R.string.alert_msg_add_friend_http));
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "alert");
		return false;
	    } catch (Exception e) {
		AddFriendAlertDialog dialog = new AddFriendAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putString("message", getResources().getString(R.string.alert_msg_add_friend_http));
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "alert");
		return false;
	    }
	    return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
	    // TODO Auto-generated method stub
	    super.onPostExecute(result);
	    if (result) {
		// Once the friend is added to the server, we can clear the friend textfield
		final EditText friendNameTxt = (EditText) findViewById(R.id.editFriendName);
		friendNameTxt.setText("");
	    }
	}
    }
}
