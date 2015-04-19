package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
	private MainActivity self = this;
	private List<NewsParser.Item> items = null;
	ItemDAO itemDAO;
	NewsItemAdapter newsItemAdapter;

	private class GetFeedTask extends AsyncTask<Void, Void, List<NewsParser.Item>> {
		private String[][] sources = {
				{"Google", "https://news.google.com/news?pz=1&cf=all&ned=hk&hl=zh-TW&output=rss"},
				{"Oriental Daily", "http://orientaldaily.on.cc/rss/news.xml"},
				{"Apple Daily", "http://rss.appleactionews.com/rss.xml"},
				{"MingPao", "http://news.mingpao.com/rss/pns/s00001.xml"},
				{"Yahoo News", "https://hk.news.yahoo.com/rss/hong-kong "},
				{"RTHK", "http://www.rthk.org.hk/rthk/news/rss/c_expressnews.xml"},
				{"HK GOV", "http://www.gov.hk/tc/about/rss.htm"},
				{"The Standard", "http://www.thestandard.com.hk/newsfeed/latest/news.xml"},
				{"SCMP", "http://www.scmp.com/rss/2/feed"},
				{"Wall Street Journal", "http://online.wsj.com/xml/rss/3_8070.xml"}
		};

		@Override
		protected List<NewsParser.Item> doInBackground(Void... params) {
			try {
				items = new ArrayList<>();
				for (String[] src : this.sources) {
					Request request = new Request.Builder().url(src[1]).build();
					Response response = new OkHttpClient().newCall(request).execute();

					try {
						items.addAll(new NewsParser().parse(src, response.body().byteStream()));
					} catch (XmlPullParserException e) {
						System.err.println("Exception throw in Main Activity when loading " + src[0]);
					}
				}

				Collections.sort(items, new NewsParser.ItemComparator());

				itemDAO.updateDatabase(items);
				items = itemDAO.getAll();

				return items;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<NewsParser.Item> items) {
			if (items != null) {
				newsItemAdapter.clear();
				for (NewsParser.Item item : items) {
					newsItemAdapter.add(item);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		itemDAO = new ItemDAO(self);

		ListView listView = (ListView) findViewById(R.id.feed_listview);
		listView.setOnItemClickListener(self);
		newsItemAdapter = new NewsItemAdapter(self);
		listView.setAdapter(newsItemAdapter);

		new GetFeedTask().execute();

		// button onClickListener
		(findViewById(R.id.nav_title)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "reload", Toast.LENGTH_SHORT).show();
				new GetFeedTask().execute();
			}
		});
		(findViewById(R.id.nav_bookmark)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "button_bookmarks", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(self, BookmarkActivity.class));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetFeedTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		startActivity(ItemDetailActivity.getStartIntent(self, items.get(position)));
		Toast.makeText(self, "item clicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_home) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
