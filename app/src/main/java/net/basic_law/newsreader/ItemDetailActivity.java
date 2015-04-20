package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class ItemDetailActivity extends Activity {
	private ItemDetailActivity self = this;
	private static final String ITEM_EXTRA = "";
	private NewsParser.Item item;
	private ItemDAO itemDAO;

	public static Intent getStartIntent(Context context, NewsParser.Item item) {
		Intent intent = new Intent(context, ItemDetailActivity.class);
		intent.putExtra(ITEM_EXTRA, item);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);

		itemDAO = new ItemDAO(self);

		// set details for webView
		WebView itemDetail = (WebView) findViewById(R.id.item_description);
		item = (NewsParser.Item) getIntent().getSerializableExtra(ITEM_EXTRA);
		if (item != null) {
			item = itemDAO.get(item.getID()); // get updated instance
			itemDetail.loadDataWithBaseURL("", item.getNewsContent(), "text/html", "UTF-8", "");

			if (item.getStarred() == 0) {
				((ImageButton) findViewById(R.id.add_to_bookmark)).setImageResource(R.drawable.bookmark_off);
				((ImageButton) findViewById(R.id.add_to_bookmark)).setBackgroundColor(Color.parseColor("#ffffcc00"));
			} else {
				((ImageButton) findViewById(R.id.add_to_bookmark)).setImageResource(R.drawable.bookmark_on);
				((ImageButton) findViewById(R.id.add_to_bookmark)).setBackgroundColor(Color.parseColor("#00ffcc00"));
			}

			((ImageButton) findViewById(R.id.add_to_bookmark)).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (item.getStarred() == 0) {
						// add to bookmarks
						item.setStarred((short) 1);
						itemDAO.update(item);
						Toast.makeText(self, "Bookmarks added", Toast.LENGTH_SHORT).show();
						((ImageButton) findViewById(R.id.add_to_bookmark)).setImageResource(R.drawable.bookmark_on);
						((ImageButton) findViewById(R.id.add_to_bookmark)).setBackgroundColor(Color.parseColor("#00ffcc00"));
					} else {
						// remove from bookmarks
						item.setStarred((short) 0);
						itemDAO.update(item);
						Toast.makeText(self, "Bookmarks removed", Toast.LENGTH_SHORT).show();
						((ImageButton) findViewById(R.id.add_to_bookmark)).setImageResource(R.drawable.bookmark_off);
						((ImageButton) findViewById(R.id.add_to_bookmark)).setBackgroundColor(Color.parseColor("#ffffcc00"));
					}

				}
			});
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
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_item_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//
//		if (id == R.id.action_exit) {
//			return true;
//		}

		return super.onOptionsItemSelected(item);
	}

}
