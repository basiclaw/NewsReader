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
	public static final String TABLE_NAME = "all_news";
	public static final String KEY_ID = "id";

	//Column name
	public static final String COLUMN_SOURCE = "source";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_PUBDATE = "pubDate";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_THUMBNAIL = "thumbnail";
	public static final String COLUMN_STARRED = "starred";

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
					COLUMN_THUMBNAIL + "  TEXT, " +
					COLUMN_STARRED + "  INTEGER)";

	private SQLiteDatabase db;

	public ItemDAO(Context context) {
		db = DatabaseHelper.getDatabase(context);
	}

	public void close() {
		db.close();
	}

	public void updateDatabase(List<NewsParser.Item> items) {
		for (NewsParser.Item item : items) {
			NewsParser.Item targetItem = this.getByUnique(item.getSource()[0], (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).format(item.getPubDate()), item.getTitle());
			if (targetItem == null) this.insert(item);
		}
	}

	public NewsParser.Item insert(NewsParser.Item item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SOURCE, item.getSource()[0]);
		cv.put(COLUMN_TITLE, item.getTitle());
		cv.put(COLUMN_LINK, item.getLink());
		cv.put(COLUMN_CATEGORY, item.getCategory());
		cv.put(COLUMN_PUBDATE, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).format(item.getPubDate()));
		cv.put(COLUMN_DESCRIPTION, item.getDescription());
		cv.put(COLUMN_THUMBNAIL, item.getThumbnail());
		cv.put(COLUMN_STARRED, item.getStarred());
		long id = db.insert(TABLE_NAME, null, cv);
		item.setID(id);
		System.out.println(id);
		return item;
	}

	public boolean update(NewsParser.Item item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SOURCE, item.getSource()[0]);
		cv.put(COLUMN_TITLE, item.getTitle());
		cv.put(COLUMN_LINK, item.getLink());
		cv.put(COLUMN_CATEGORY, item.getCategory());
		cv.put(COLUMN_PUBDATE, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).format(item.getPubDate()));
		cv.put(COLUMN_DESCRIPTION, item.getDescription());
		cv.put(COLUMN_THUMBNAIL, item.getThumbnail());
		cv.put(COLUMN_STARRED, item.getStarred());

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
		String orderBy =  COLUMN_PUBDATE + " DESC";
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);

		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}

		cursor.close();
		return result;
	}

	public NewsParser.Item get(long id) {
		NewsParser.Item item = null;
		String where = KEY_ID + "=" + id;
		Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

		if (result.moveToFirst()) {
			item = getRecord(result);
		}
		result.close();
		return item;
	}

	public NewsParser.Item getByUnique(String source, String pubDate, String title) {
		NewsParser.Item item = null;
		String where = COLUMN_SOURCE + "=\"" + source + "\" AND " + COLUMN_PUBDATE + "=\"" + pubDate + "\" AND " + COLUMN_TITLE + "=\"" + title + "\"";
		Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			item = getRecord(cursor);
		}
		cursor.close();
		return item;
	}

	// cursor => object
	private NewsParser.Item getRecord(Cursor cursor) {
		// return type
		NewsParser.Item result = new NewsParser.Item();

		result.setID(cursor.getLong(0));
		result.setSource0(cursor.getString(1));
		result.setTitle(cursor.getString(2));
		result.setLink(cursor.getString(3));
		result.setCategory(cursor.getString(4));
		Date pubDate;
		try {
			pubDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(cursor.getString(5));
		} catch (ParseException e) {
			pubDate = new Date(1970, 1, 1, 0, 0, 0);
		}
		result.setPubDate(pubDate);
		result.setDescription(cursor.getString(6));
		result.setThumbnail(cursor.getString(7));
		result.setStarred(cursor.getShort(8));
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

	public List<NewsParser.Item> getBookmarks() {
		List<NewsParser.Item> result = new ArrayList<>();
		String where = COLUMN_STARRED + "=" + 1;
		Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null, null);
		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}
		cursor.close();
		return result;
	}
}