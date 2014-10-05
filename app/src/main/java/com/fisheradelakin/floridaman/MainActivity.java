package com.fisheradelakin.floridaman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String URL = "http://www.reddit.com/r/floridaman.json";

    public static String[] titles = {"1", "2", "3"};

    ListView listView;
    List<RowItem> rowItems;
    ArrayList<String> ar = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button newStoryButton = (Button) findViewById(R.id.showAnotherStoryButton);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

       /*  rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(titles[i]);
            rowItems.add(item);
        } */

        listView = (ListView) findViewById(R.id.list);
        /* CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                ar );

        listView.setAdapter(arrayAdapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    new JSONParse().execute();
                    //int color = mColor.getColor();
                    //layout.setBackgroundColor(color);
                    //newStoryButton.setTextColor(color);
                }
            }
        };
        newStoryButton.setOnClickListener(listener);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, "No network. Sorry, jokes are not available.", Toast.LENGTH_SHORT).show();
        }

        return isAvailable;
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting News Story ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJsonFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {

                JSONObject response = json.getJSONObject("data");
                System.out.println(response);
                //JSONObject data = response.getJSONObject("data");
                JSONArray hotTopics = response.getJSONArray("children");

                for(int i=0; i<hotTopics.length(); i++) {
                    JSONObject topic = hotTopics.getJSONObject(i).getJSONObject("data");
                    String title = topic.getString("title");
                    String url = topic.getString("url");


                    System.out.println(title);
                    System.out.println(url);
                    ar.add(title);
                    System.out.println(ar);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
