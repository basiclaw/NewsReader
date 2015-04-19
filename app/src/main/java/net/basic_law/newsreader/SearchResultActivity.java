package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SearchResultActivity extends Activity implements AdapterView.OnItemClickListener {
	private SearchResultActivity self = this;
	private List<NewsParser.Item> items = null;
	private static final String ITEM_EXTRA = "";
	private NewsItemAdapter newsItemAdapter;
	private String query;

	public static Intent getStartIntent(Context context, String query) {
		Intent intent = new Intent(context, SearchResultActivity.class);
		intent.putExtra(ITEM_EXTRA, query);
		return intent;
	}

	private void setNewsItemAdapter(String query) {
		ItemDAO itemDAO = new ItemDAO(self);
		items = itemDAO.getSearchResult(query);
		if (items != null) {
			newsItemAdapter.clear();
			for (NewsParser.Item item : items) newsItemAdapter.add(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		ListView listView = (ListView) findViewById(R.id.feed_listview);
		listView.setOnItemClickListener(self);
		newsItemAdapter = new NewsItemAdapter(self, R.layout.news_item_lite);
		listView.setAdapter(newsItemAdapter);

		query = (String) getIntent().getSerializableExtra(ITEM_EXTRA);
		if (query != null) {
			((TextView) findViewById(R.id.search_keywords)).setText("Search result on: "+query);
			setNewsItemAdapter(query);
		}

		// button onClickListener
		((ImageButton) findViewById(R.id.nav_logo)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		((ImageButton) findViewById(R.id.nav_back)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		((ImageButton) findViewById(R.id.nav_none)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		startActivity(ItemDetailActivity.getStartIntent(self, items.get(position)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_exit) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}