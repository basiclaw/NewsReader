package net.basic_law.newsreader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
	private MainActivity self = this;
	private List<NewsParser.Item> items = null;
	NewsItemAdapter newsItemAdapter;

	private class GetFeedTask extends AsyncTask<Void, Void, List<NewsParser.Item>> {
		private String[][] sources = {
				{"Google", "https://news.google.com/news?pz=1&cf=all&ned=hk&hl=zh-TW&output=rss"},
				{"Oriental Daily", "http://orientaldaily.on.cc/rss/news.xml"},
				{"Apple Daily", "http://rss.appleactionews.com/rss.xml"},
				{"MingPao", "http://news.mingpao.com/cfm/rss.cfm"},
				{"Yahoo News", "https://hk.news.yahoo.com/sitemap/"},
				{"RTHK", "http://rthk.hk/text/chi/news/rss.htm"},
				{"HK GOV", "http://www.gov.hk/tc/about/rss.htm"},
				{"The Standard", "http://www.thestandard.com.hk/newsfeed/latest/news.xml"},
				{"SCMP", "http://www.scmp.com/rss"},
				{"Wall Street Journal", "http://online.wsj.com/xml/rss/3_8070.xml"}
		};

		@Override
		protected List<NewsParser.Item> doInBackground(Void... params) {
			try {
				items = new ArrayList<>();
				for (String[] src : this.sources) {
					Request request = new Request.Builder().url( src[1] ).build();
					Response response = new OkHttpClient().newCall(request).execute();

					try {
						items.addAll( new NewsParser().parse(src , response.body().byteStream()) );
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
				}
				return items;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<NewsParser.Item> items) {
			if (items != null ){
				newsItemAdapter.clear().addAll( items );
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.feed_listview);
		listView.setOnItemClickListener(self);
		newsItemAdapter = new NewsItemAdapter(self);
		listView.setAdapter(newsItemAdapter);

		new GetFeedTask().execute();

		// button onClickListener
		((ImageButton) findViewById(R.id.nav_home)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "reload", Toast.LENGTH_SHORT).show();
				new GetFeedTask().execute();
			}
		});
		((Button) findViewById(R.id.nav_profile)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "test_profile", Toast.LENGTH_SHORT).show();
			}
		});
		((Button) findViewById(R.id.nav_bookmarks)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "test_bookmarks", Toast.LENGTH_SHORT).show();
			}
		});
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
