package net.basic_law.newsreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsParser {
	private static final String ns = null;
	private String[] source = null;

	public static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

		private String url;
		private ImageView imageView;

		public ImageLoadTask(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				URL urlConnection = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			imageView.setImageBitmap(result);
		}
	}

	public static class Item implements Serializable {
		private long id;
		private String[] source;
		private String title, link, category, description, thumbnail;
		private short starred;
		private Date pubDate;

		public Item() {
			this.id = -1;
			this.source = new String[2];
			this.title = "";
			this.link = "";
			this.category = "";
			this.pubDate = null;
			this.description = "";
			this.thumbnail = "";
			this.starred = 0;
		}

		public Item(String[] source, String title, String link, String category, Date pubDate, String description) {
			this.id = -1;
			this.source = source;
			this.title = title;
			this.link = link;
			this.category = category;
			this.pubDate = pubDate;
			description = Html.fromHtml(description).toString();
			description = description.replace("=\"//", "=\"http://");
			this.description = TextUtils.htmlEncode(description);
			this.thumbnail = "";
			int imgPos = description.indexOf("<img src=\"");
			if (imgPos != -1)
				this.thumbnail = description.substring(imgPos + 10, description.indexOf("\"", imgPos + 10));
			this.starred = 0;
		}

		public void setID(long id) {
			this.id = id;
		}

		public void setSource0(String sourceTitle) {
			this.source[0] = sourceTitle;
		}

		public void setSource1(String sourceUrl) {
			this.source[1] = sourceUrl;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public void setPubDate(Date pubDate) {
			this.pubDate = pubDate;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setStarred(short starred) {
			this.starred = starred;
		}

		public long getID() {
			return this.id;
		}

		public String[] getSource() {
			return this.source;
		}

		public String getTitle() {
			return Html.fromHtml(this.title).toString();
		}

		public String getLink() {
			return Html.fromHtml(this.link).toString();
		}

		public String getCategory() {
			return Html.fromHtml(this.category).toString();
		}

		public Date getPubDate() {
			return this.pubDate;
		}

		public String getPubDateString() {
			return (new SimpleDateFormat("dd MMM yyyy (EEE) HH:mm:ss", Locale.ENGLISH)).format(this.pubDate);
		}

		public String getDescription() {
			return Html.fromHtml(this.description).toString();
		}

		public String getNewsContent() {
			return "<div class=\"row\" style=\"padding: 15px;\">" +
					"<h3><a href=\"" + this.getLink() + "\" target=\"_blank\">" + this.getTitle() + "</a></h3>" +
					"<div class=\"row\">" +
					"<div style=\"color: #909090; font-size: 14px;\">" + this.getPubDateString() + "</div>" +
					"<div style=\"color: #909090; font-size: 14px;\">" + this.getSource()[0] + " " + this.getCategory() + "</div>" +
					"</div>" +
					"<div class=\"row\" style=\"padding-top: 1em\">" +
					this.getDescription() +
					"</div>" +
					"</div>";
		}

		public String getThumbnail() {
			return this.thumbnail;
		}

		public short getStarred() {
			return this.starred;
		}

		@Override
		public String toString() {
			return getTitle();
		}
	}

	public static class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.getPubDate().compareTo(item2.getPubDate());
		}
	}

	public List<Item> parse(String[] source, InputStream in) throws XmlPullParserException, IOException {
		try {
			this.source = source;
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readRss(parser);
		} finally {
			in.close();
		}
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) throw new IllegalStateException();
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}

	private List<Item> readRss(XmlPullParser parser) throws IOException, XmlPullParserException {
		List<Item> entries = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, null, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) continue;
			String name = parser.getName();
			// Starts by looking for the channel tag
			if (name.equals("channel")) {
				return readChannel(parser);
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	private List<Item> readChannel(XmlPullParser parser) throws IOException, XmlPullParserException {
		List<Item> entries = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, null, "channel");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) continue;
			String name = parser.getName();
			// Starts by looking for the item tag
			if (name.equals("item")) {
				entries.add(readItem(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	private Item readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String title = "", link = "", category = "", description = "";
		Date pubDate = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) continue;
			String name = parser.getName();
			switch (name) {
				case "title":
					title = TextUtils.htmlEncode(readRequiredTag(parser, name).trim());
					break;
				case "link":
					link = TextUtils.htmlEncode(readRequiredTag(parser, name).trim());
					break;
				case "category":
					category = TextUtils.htmlEncode(readRequiredTag(parser, name).trim());
					break;
				case "pubDate":
					try {
						String pubDateString = readRequiredTag(parser, name).trim();
						pubDate = (new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)).parse(pubDateString);
					} catch (ParseException e) {
						pubDate = new Date(1970, 1, 1, 0, 0, 0);
					}
					break;
				case "description":
					description = TextUtils.htmlEncode(readRequiredTag(parser, name).trim());
					break;
				default:
					skip(parser);
			}
		}
		return new Item(source, title, link, category, pubDate, description);
	}

	private String readRequiredTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String result = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return result;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
