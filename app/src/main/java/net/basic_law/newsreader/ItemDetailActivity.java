package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class ItemDetailActivity extends Activity {
	private ItemDetailActivity self = this;
	private static final String ITEM_EXTRA = "";

	public static Intent getStartIntent(Context context, NewsParser.Item item) {
		Intent intent = new Intent(context, ItemDetailActivity.class);
		intent.putExtra(ITEM_EXTRA, item);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);

		// set details for webView
		WebView itemDetail = (WebView) findViewById(R.id.item_description);
		NewsParser.Item item = (NewsParser.Item) getIntent().getSerializableExtra(ITEM_EXTRA);
		if (item != null) {
			itemDetail.loadDataWithBaseURL("", item.getDescription(), "text/html", "UTF-8", "");
		}

		// button onClickListener
		((ImageButton) findViewById(R.id.nav_home)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(self, "test_home", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(self, MainActivity.class));
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

}
