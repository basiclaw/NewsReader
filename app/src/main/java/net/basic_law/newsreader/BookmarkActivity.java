package net.basic_law.newsreader;

import android.app.Activity;
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

public class BookmarkActivity extends Activity implements AdapterView.OnItemClickListener {
	private BookmarkActivity self = this;
	private List<NewsParser.Item> items = null;
	NewsItemAdapter newsItemAdapter;

	private void setNewsItemAdapter() {
		ItemDAO itemDAO = new ItemDAO(self);
		items = itemDAO.getBookmarks();
		if (items != null) {
			newsItemAdapter.clear();
			for (NewsParser.Item item : items) {
				newsItemAdapter.add(item);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);

		ListView listView = (ListView) findViewById(R.id.feed_listview);
		listView.setOnItemClickListener(self);
		newsItemAdapter = new NewsItemAdapter(self);
		listView.setAdapter(newsItemAdapter);

		setNewsItemAdapter();

		// button onClickListener
		(findViewById(R.id.nav_title)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		(findViewById(R.id.nav_back)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		setNewsItemAdapter();
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
