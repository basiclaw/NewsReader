package net.basic_law.newsreader;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class NewsParser {
	private static final String ns = null;
	private String[] source = null;

	public static class Item implements Serializable {
		public String[] source;
		public String title;
		public String link;
		public String description;
		public String thumbnail;
		public String pubDate;

		public Item(String[] source, String title, String link, String description, String pubDate) {
			this.source = source;
			this.title = title;
			this.link = link;
			this.description = description.replace("=\"//", "=\"http://");
			this.thumbnail = "";
			// this.thumbnail = this.description.substring( this.description.indexOf( "<img src=\"" )+10, this.description.indexOf( "\" alt=\"\" border=\"1\" " ) );
			this.pubDate = pubDate;
		}

		public String getSource() {
			return source[0];
		}
		public String getTitle() {
			return title;
		}
		public String getLink() {
			return link;
		}
		public String getDescription() {
			return description;
		}
		public String getThumbnail() {
			return thumbnail;
		}
		public String getPubDate() {
			return pubDate;
		}

		@Override
		public String toString() {
			return getTitle();
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
		String title = null;
		String link = null;
		String description = null;
		String pubDate = null;
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
				case "description":
					description = readRequiredTag(parser, name).trim();
					break;
				case "pubDate":
					pubDate = readRequiredTag(parser, name).trim();
					break;
				default:
					skip(parser);
			}
		}
		return new Item(source, title, link, description, pubDate);
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
