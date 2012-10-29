package com.example.whowants.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {


	private static final String SCORE_TABLE = "ScoreTable";
	private static final String COL_ID = "Id";
	private static final String COL_NAME = "Name";
	private static final String COL_SCORE = "Score";
	
	
	
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String cmdCreateDb = "CREATE TABLE " + SCORE_TABLE + " ("
				+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " TEXT NOT NULL, "
				+ COL_SCORE + " INTEGER NOT NULL);";
		db.execSQL(cmdCreateDb);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void recreateDB(SQLiteDatabase db) {
		db.execSQL("DROP TABLE " + SCORE_TABLE + ";");
		onCreate(db);
	}

}
