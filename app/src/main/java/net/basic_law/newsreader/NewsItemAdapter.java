package net.basic_law.newsreader;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int resource = R.layout.news_item;
	private List<NewsParser.Item> newsItems;

	private class ViewHolder {
		ImageView item_thumbnail, item_starred;
		TextView item_title, item_category, item_pubDate, item_source;
	}

	public NewsItemAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.resource = R.layout.news_item;
		this.newsItems = new ArrayList<>();
	}

	public NewsItemAdapter(Context context, int resource) {
		this.inflater = LayoutInflater.from(context);
		this.resource = resource;
		this.newsItems = new ArrayList<>();
	}

	public NewsItemAdapter(Context context, List<NewsParser.Item> newsItems) {
		this.inflater = LayoutInflater.from(context);
		this.resource = R.layout.news_item;
		this.newsItems = newsItems;
	}

	public NewsItemAdapter(Context context, int resource, List<NewsParser.Item> newsItems) {
		this.inflater = LayoutInflater.from(context);
		this.resource = resource;
		this.newsItems = newsItems;
	}

	public NewsItemAdapter clear() {
		this.newsItems.clear();
		this.notifyDataSetChanged();
		return this;
	}

	public void add(NewsParser.Item newsItem) {
		this.newsItems.add(newsItem);
		this.notifyDataSetChanged();
	}

	public void addAll(List<NewsParser.Item> newsItems) {
		this.newsItems.addAll(newsItems);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return newsItems.size();
	}

	@Override
	public NewsParser.Item getItem(int position) {
		return newsItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(resource, null);
			viewHolder = new ViewHolder();
			viewHolder.item_thumbnail = (ImageView) convertView.findViewById(R.id.item_thumbnail);
			viewHolder.item_starred = (ImageView) convertView.findViewById(R.id.item_starred);
			viewHolder.item_title = (TextView) convertView.findViewById(R.id.item_title);
			viewHolder.item_category = (TextView) convertView.findViewById(R.id.item_category);
			viewHolder.item_pubDate = (TextView) convertView.findViewById(R.id.item_pubDate);
			viewHolder.item_source = (TextView) convertView.findViewById(R.id.item_source);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (!newsItems.get(position).getThumbnail().equals("")) {
			new NewsParser.ImageLoadTask(newsItems.get(position).getThumbnail(), viewHolder.item_thumbnail).execute();
		} else {
			viewHolder.item_thumbnail.setImageResource(R.drawable.no_thumb);
		}

		if (newsItems.get(position).getStarred() == 0) {
			// is not starred
			viewHolder.item_starred.setImageDrawable(null);
		} else {
			viewHolder.item_starred.setImageResource(R.drawable.star);
		}

		viewHolder.item_title.setText(newsItems.get(position).getTitle());
		viewHolder.item_category.setText(newsItems.get(position).getCategory());
		viewHolder.item_pubDate.setText(newsItems.get(position).getPubDateString());
		viewHolder.item_source.setText(newsItems.get(position).getSource()[0]);
		return convertView;
	}
}