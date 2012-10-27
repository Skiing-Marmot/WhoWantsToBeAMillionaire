package com.example.whowants.view;

import com.example.whowants.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
	}

	private void setScoresTab() {
    	TabHost host = (TabHost) findViewById(android.R.id.tabhost);
    	host.setup();
    	// First tab
    	TabSpec spec = host.newTabSpec("LocalTab"); 
    	//spec.setIndicator("Local Scores", getResources().getDrawable(R.drawable.Tab1Icon));
    	spec.setIndicator(getString(R.string.local_tab_title));
    	spec.setContent(R.id.tab1);
    	host.addTab(spec);
    	// Second tab
    	spec = host.newTabSpec("FriendTab"); 
    	//spec.setIndicator("Friends Scores", getResources().getDrawable(R.drawable.Tab2Icon));
    	spec.setIndicator(getString(R.string.friends_tab_title));
    	spec.setContent(R.id. tab2); 
    	host.addTab(spec); 
    	host.setCurrentTabByTag("LocalTab");
    }
    
    private void setLocalTableLayout() {
    	TableLayout table = (TableLayout) findViewById(R.id.localTableLayout);
    	TableRow row = new TableRow(this);
    	TextView tv = new TextView(this);
    	
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
    	TableLayout table = (TableLayout) findViewById(R.id.friendsTableLayout);
    	TableRow row = new TableRow(this);
    	TextView tv = new TextView(this);
    	
    	tv.setText("Juan");
    	row.addView(tv);
    	tv = new TextView(this);
    	tv.setText("500");
    	row.addView(tv);
    	table.addView(row);
    	
    	row = new TableRow(this);
    	tv = new TextView(this);
    	tv.setText("David");
    	row.addView(tv);
    	tv = new TextView(this);
    	tv.setText("2000");
    	row.addView(tv);
    	table.addView(row);
    }
}
