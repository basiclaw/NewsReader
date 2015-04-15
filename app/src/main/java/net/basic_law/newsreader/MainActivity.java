package net.basic_law.newsreader;

import android.app.Activity;
import android.content.Intent;
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


public class MainActivity extends Activity implements AdapterView.OnItemClickListener
{
    private MainActivity self = this;
    private List<NewsParser.Item> items = null;
    private ArrayAdapter<String> titleAdapter;

    private class GetFeedTask extends AsyncTask<Void, Void, List<String>>
    {
        @Override
        protected List<String> doInBackground( Void... params )
        {
            try {
                Request request = new Request.Builder()
                        .url( "https://news.google.com/news?pz=1&cf=all&ned=hk&hl=zh-TW&output=rss" )
                        .build();

                Response response = new OkHttpClient().newCall( request ).execute();
                List <String> itemsTitle = new ArrayList<String>();
                try {
                    items = new NewsParser().parse( response.body().byteStream() );
                } catch ( XmlPullParserException e ) {
                    e.printStackTrace();
                }
                for ( NewsParser.Item item : items ) {
                    itemsTitle.add( item.getTitle() );
                }
                return itemsTitle;
            } catch ( IOException e ) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute( List<String> itemsTitle )
        {
            if ( itemsTitle != null ) {
                titleAdapter.clear();
                titleAdapter.addAll( itemsTitle );
            }
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // set adaptor for the listView
        titleAdapter = new ArrayAdapter<>( self , R.layout.news_item );
        ListView listView = ( ListView ) findViewById( R.id.feed_listview );
        listView.setOnItemClickListener( self );
        listView.setAdapter( titleAdapter );
        new GetFeedTask().execute();

        // button onClickListener
        ( ( ImageButton ) findViewById( R.id.nav_home ) ).setOnClickListener( new View.OnClickListener() {
            public void onClick( View v )
            {
                Toast.makeText( self , "reload" , Toast.LENGTH_SHORT ).show();
                new GetFeedTask().execute();
            }
        } );

        ( ( Button ) findViewById( R.id.nav_profile ) ).setOnClickListener( new View.OnClickListener() {
            public void onClick( View v )
            {
                Toast.makeText( self , "test_profile" , Toast.LENGTH_SHORT ).show();
            }
        } );

        ( ( Button ) findViewById( R.id.nav_bookmarks ) ).setOnClickListener( new View.OnClickListener() {
            public void onClick( View v )
            {
                Toast.makeText( self , "test_bookmarks" , Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    public void onItemClick( AdapterView<?> parent , View view , int position , long id )
    {
        startActivity( ItemDetailActivity.getStartIntent( self , items.get( position ) ) );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

}
