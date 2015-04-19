package net.basic_law.newsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemDAO {
	//Table name
	public static final String TABLE_NAME = "All News";
	public static final String KEY_ID = "id";

	//Column name
	public static final String COLUMN_SOURCE = "source";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_PUBDATE = "pubDate";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_THUMBNAIL = "thumbnail";

	//Create Table command
	public static final String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + " (" +
					KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_SOURCE + "  TEXT, " +
					COLUMN_TITLE + "  TEXT, " +
					COLUMN_LINK + "  TEXT, " +
					COLUMN_CATEGORY + "  TEXT, " +
					COLUMN_PUBDATE + "  DATE, " +
					COLUMN_DESCRIPTION + "  TEXT, " +
					COLUMN_THUMBNAIL + "  TEXT)";

	private SQLiteDatabase db;

	public ItemDAO(Context context) {
		db = DatabaseHelper.getDatabase(context);
	}

	public void close() {
		db.close();
	}

	public NewsParser.Item insert(NewsParser.Item item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SOURCE, item.getSource()[0]);
		cv.put(COLUMN_TITLE, item.getTitle());
		cv.put(COLUMN_LINK, item.getLink());
		cv.put(COLUMN_CATEGORY, item.getCategory());
		cv.put(COLUMN_PUBDATE, ( new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.ENGLISH) ).format( item.getPubDate() ));
		cv.put(COLUMN_DESCRIPTION, item.getDescription());
		cv.put(COLUMN_THUMBNAIL, item.getThumbnail());
		long id = db.insert(TABLE_NAME, null, cv);
		item.setID(id);
		return item;
	}

	public boolean update(NewsParser.Item item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SOURCE, item.getSource()[0]);
		cv.put(COLUMN_TITLE, item.getTitle());
		cv.put(COLUMN_LINK, item.getLink());
		cv.put(COLUMN_CATEGORY, item.getCategory());
		cv.put(COLUMN_PUBDATE, ( new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.ENGLISH) ).format( item.getPubDate() ));
		cv.put(COLUMN_DESCRIPTION, item.getDescription());
		cv.put(COLUMN_THUMBNAIL, item.getThumbnail());

		String where = KEY_ID + "=" + item.getID();

		return db.update(TABLE_NAME, cv, where, null) > 0;
	}

	public boolean delete(long id) {
		String where = KEY_ID + "=" + id;
		return db.delete(TABLE_NAME, where, null) > 0;
	}

	public boolean delete(NewsParser.Item item) {
		String where = KEY_ID + "=" + item.getID();
		return db.delete(TABLE_NAME, where, null) > 0;
	}

	public List<NewsParser.Item> getAll() {
		List<NewsParser.Item> result = new ArrayList<>();
		Cursor cursor = db.query(
				TABLE_NAME, null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}

		cursor.close();
		return result;
	}

	public NewsParser.Item get(long id) {
		NewsParser.Item item = null;
		String where = KEY_ID + "=" + id;
		Cursor result = db.query(
				TABLE_NAME, null, where, null, null, null, null, null);

		if (result.moveToFirst()) {
			item = getRecord(result);
		}
		result.close();
		return item;
	}

	// cursor => object
	public NewsParser.Item getRecord(Cursor cursor) {
		// return type
		NewsParser.Item result = null;

		result.setID(cursor.getLong(0));
		result.setSource0(cursor.getString(1));
		result.setTitle(cursor.getString(2));
		result.setLink(cursor.getString(3));
		result.setCategory(cursor.getString(4));
		Date pubDate;
		try {
			pubDate = ( new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.ENGLISH) ).parse( cursor.getString(5) );
		} catch (ParseException e) {
			pubDate = new Date(1970, 1, 1, 0, 0, 0);
		}
		result.setPubDate(pubDate);
		result.setDescription(cursor.getString(6));
		result.setThumbnail(cursor.getString(7));
		return result;
	}

	public int getCount() {
		int result = 0;
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}

		return result;
	}
}