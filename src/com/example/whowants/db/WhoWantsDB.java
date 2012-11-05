package com.example.whowants.db;

import com.example.whowants.model.HighScore;
import com.example.whowants.model.HighScoreList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WhoWantsDB {
	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "scores.db";

	
	private static final String SCORE_TABLE = "ScoreTable";
	private static final String COL_NAME = "Name";
	private static final String COL_SCORE = "Score";
	
	private SQLiteDatabase db;
	 
	private DataBaseHelper myDBHelper;
	
	public WhoWantsDB(Context context){
		// Create the db and its table
		myDBHelper = new DataBaseHelper(context, DB_NAME, null, DB_VERSION);
	}
	public void open(){
		// Open DB with write right
		db = myDBHelper.getWritableDatabase();
	}
 
	public void close(){
		// Close the db
		db.close();
	}
	public SQLiteDatabase getDB(){
		// get DB
		return db;
	}
	
	public long insertResult(HighScore result){
		/*
		 * This method insert a result in the database
		 */
		ContentValues values = new ContentValues();
		values.put(COL_NAME, result.getName());
		values.put(COL_SCORE, result.getScoring());
		//insertion of the contentValue in the db
		return db.insert(SCORE_TABLE, null, values);
	}
	
	public HighScoreList getAllResults() {
		Cursor cur = db.query(SCORE_TABLE, new String[] {COL_NAME, COL_SCORE}, null, null, null, null, null);
		cur.moveToFirst();
		HighScoreList hgList = new HighScoreList();
		HighScore hg;
		int i = 0;
		while (!cur.isAfterLast()) {
			i++;
			Log.i("malus", "mira => "+i);
			hg = new HighScore(cur.getString(0), cur.getInt(1));
			hgList.getScores().add(hg);
			cur.moveToNext();
		}
		cur.close();
		return hgList;
	}
	public void deleteAllResults() {
		/*
		 * This method delete all the local results.
		 * I chose to drop the table and recreate it => cleaner than removing
		 * each entry one by one.
		 */
		myDBHelper.recreateDB(db);
	}
}
