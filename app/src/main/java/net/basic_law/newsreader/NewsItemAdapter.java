package net.basic_law.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NewsItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int resource = R.layout.news_item;
	private List<NewsParser.Item> newsItems;

	private class ViewHolder {
		TextView item_title;
		TextView item_pubDate;
		TextView item_source;
	}

	public NewsItemAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		this.newsItems = new ArrayList<NewsParser.Item>();
	}

	public NewsItemAdapter(Context context, List<NewsParser.Item> newsItems) {
		inflater = LayoutInflater.from(context);
		this.newsItems = newsItems;
	}
	public NewsItemAdapter clear(){
		this.newsItems.clear();
		this.notifyDataSetChanged();
		return this;
	}
	public void add(NewsParser.Item newsItem)
	{
		this.newsItems.add(newsItem);
		this.notifyDataSetChanged();
	}
	public void addAll(List<NewsParser.Item> newsItems)
	{
		this.newsItems.addAll(newsItems);
		this.notifyDataSetChanged();
	}

	public int getCount() {
		return newsItems.size();
	}

	public NewsParser.Item getItem(int position) {
		return newsItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			int x = R.layout.news_item;
			convertView = inflater.inflate(resource, null);
			holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
			holder.item_pubDate = (TextView) convertView.findViewById(R.id.item_pubDate);
			holder.item_source = (TextView) convertView.findViewById(R.id.item_source);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_title.setText(newsItems.get(position).getTitle());
		holder.item_pubDate.setText(newsItems.get(position).getPubDate());
		holder.item_source.setText(newsItems.get(position).getSource());
		return convertView;
	}
}