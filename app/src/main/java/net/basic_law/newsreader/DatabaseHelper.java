package net.basic_law.newsreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	//DB name
	public static final String DATABASE_NAME = "newsDatabase.db";
	//DB Version
	public static final int VERSION = 1;
	private static SQLiteDatabase database;

	public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static SQLiteDatabase getDatabase(Context context) {
		if (database == null || !database.isOpen()) {
			database = new DatabaseHelper(context, DATABASE_NAME,
					null, VERSION).getWritableDatabase();
		}

		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//create table
		db.execSQL(ItemDAO.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Delete original table, do later
		db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
		//call onCreate() to create new table
		onCreate(db);
	}

}
