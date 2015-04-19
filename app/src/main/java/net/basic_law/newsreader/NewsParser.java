package net.basic_law.newsreader;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
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

	public static class Item implements Serializable {
		private long id;
		private String[] source;
		private String title, link, category, description, thumbnail;
		private Date pubDate;

		public Item(String[] source, String title, String link, String category, Date pubDate, String description) {
			this.id = -1;
			this.source = source;
			this.title = title;
			this.link = link;
			this.category = category;
			this.pubDate = pubDate;
			this.description = description.replace("=\"//", "=\"http://");
			this.thumbnail = "";
			// this.thumbnail = this.description.substring( this.description.indexOf( "<img src=\"" )+10, this.description.indexOf( "\" alt=\"\" border=\"1\" " ) );
		}

		public void setID(long id){
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
		public void setPubDateString(String pubDateString) {
			try {
				this.pubDate = ( new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH) ).parse( pubDateString );
			} catch (ParseException e) {
				this.pubDate = new Date(1970, 1, 1, 0, 0, 0);
			}
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public long getID() {
			return this.id;
		}
		public String[] getSource() {
			return this.source;
		}
		public String getTitle() {
			return this.title;
		}
		public String getLink() {
			return this.link;
		}
		public String getCategory() {
			return this.category;
		}
		public Date getPubDate() {
			return this.pubDate;
		}
		public String getPubDateString() {
			return ( new SimpleDateFormat("dd MMM yyyy (EEE) HH:mm:ss", Locale.ENGLISH) ).format( this.pubDate );
		}
		public String getDescription() {
			return this.description;
		}
		public String getThumbnail() {
			return this.thumbnail;
		}

		@Override
		public String toString() {
			return getTitle();
		}
	}

	public static class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.getPubDate().compareTo(item1.getPubDate());
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
					title = readRequiredTag(parser, name).trim();
					break;
				case "link":
					link = readRequiredTag(parser, name).trim();
					break;
				case "category":
					category = readRequiredTag(parser, name).trim();
					break;
				case "pubDate":
					try {
						String pubDateString = readRequiredTag(parser, name).trim();
						pubDate = ( new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH) ).parse( pubDateString );
					} catch (ParseException e) {
						pubDate = new Date(1970, 1, 1, 0, 0, 0);
					}
					break;
				case "description":
					description = readRequiredTag(parser, name).trim();
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
