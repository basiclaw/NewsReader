package net.basic_law.newsreader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    private List<NewsParser.Item> items = null;
    private ArrayAdapter<String> titleAdapter;

    private class GetFeedTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Request request = new Request.Builder()
                        .url("https://news.google.com/news?pz=1&cf=all&ned=hk&hl=zh-TW&output=rss")
                        .build();

                Response response = new OkHttpClient().newCall(request).execute();
                List <String> itemsTitle = new ArrayList<String>();
                try {
                    items = new NewsParser().parse(response.body().byteStream());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                for(NewsParser.Item item : items){
                    itemsTitle.add(item.getTitle());
                }
                return itemsTitle;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> itemsTitle) {
            if (itemsTitle != null) {
                titleAdapter.clear();
                titleAdapter.addAll(itemsTitle);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleAdapter = new ArrayAdapter<>(this, R.layout.news_item);
        ListView listView = (ListView) findViewById(R.id.feed_listview);
        listView.setOnItemClickListener(this);
        listView.setAdapter(titleAdapter);
        new GetFeedTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // startActivity(ItemDetailActivity.getStartIntent(this, adapter.getItem(position)));
        startActivity(ItemDetailActivity.getStartIntent(this, items.get(position)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ImageButton homeButton = (ImageButton) this.findViewById(R.id.nav_home);
    // homeButton.setOnClickListener(new View.OnClickListener(){
    //     public void onClick(View v){
    //         Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
    //     }
    // });
    Button profileButton = (Button) this.findViewById(R.id.nav_profile);

    Button bookmarksButton = (Button) this.findViewById(R.id.nav_bookmarks);

}
