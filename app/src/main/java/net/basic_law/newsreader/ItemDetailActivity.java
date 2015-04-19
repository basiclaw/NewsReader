package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
			itemDetail.loadDataWithBaseURL("", item.getNewsContent(), "text/html", "UTF-8", "");
		}

		if (item.getStarred() == 0){
			((TextView) findViewById(R.id.add_to_bookmark)).setText("Add to Bookmarks");
		}else{
			((TextView) findViewById(R.id.add_to_bookmark)).setText("Remove from Bookmarks");
		}

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
		(findViewById(R.id.add_to_bookmark)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (item.getStarred() == 0){
					// add to bookmarks
					item.setStarred((short) 1);
					itemDAO.update(item);
					Toast.makeText(self, "Bookmarks added", Toast.LENGTH_SHORT).show();
					((TextView) findViewById(R.id.add_to_bookmark)).setText("Remove from Bookmarks");
				} else {
					// remove from bookmarks
					item.setStarred((short) 0);
					itemDAO.update(item);
					Toast.makeText(self, "Bookmarks removed", Toast.LENGTH_SHORT).show();
					((TextView) findViewById(R.id.add_to_bookmark)).setText("Add to Bookmarks");
				}

			}
		});
	}

}
