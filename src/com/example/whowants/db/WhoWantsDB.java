package com.example.whowants.db;

import com.example.whowants.model.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class WhoWantsDB {
	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "scores.db";

	
	private static final String SCORE_TABLE = "ScoreTable";
	private static final String COL_ID = "Id";
	private static final int NUM_COL_ID = 0;
	private static final String COL_NAME = "Name";
	private static final int NUM_COL_NAME = 1;
	private static final String COL_SCORE = "Score";
	private static final int NUM_COL_SCORE = 2;
	
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
	
	public long insertResult(Result result){
		/*
		 * This method insert a result in the database
		 */
		ContentValues values = new ContentValues();
		values.put(COL_NAME, result.getName());
		values.put(COL_SCORE, result.getScore());
		//insertion of the contentValue in the db
		return db.insert(SCORE_TABLE, null, values);
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
