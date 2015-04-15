package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class ItemDetailActivity extends Activity {
    private static final String ITEM_EXTRA = "item";

    public static Intent getStartIntent(Context context, NewsParser.Item item) {
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra(ITEM_EXTRA, item);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        //TextView itemDetail = (TextView) findViewById(R.id.item_detail);
        WebView itemDetail = (WebView) findViewById(R.id.item_detail);

        NewsParser.Item item = (NewsParser.Item) getIntent().getSerializableExtra(ITEM_EXTRA);
        if (item != null) {
            //itemDetail.setText(item.toString());
            itemDetail.loadDataWithBaseURL("", item.toString(), "text/html", "UTF-8", "");
        }
    }

}
