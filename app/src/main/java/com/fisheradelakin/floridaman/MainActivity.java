package com.fisheradelakin.floridaman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    public static final String URL = "http://www.reddit.com/r/floridaman.json";

    private static final String TAG_TITLE = "title";
    private static final String TAG_URL = "url";

    ListView listView;
    ArrayList<HashMap<String,String>> newsList = new ArrayList<HashMap<String, String>>();
    TextView title;
    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button newStoryButton = (Button) findViewById(R.id.showAnotherStoryButton);
        //final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

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
            title = (TextView)findViewById(R.id.title);
            desc = (TextView)findViewById(R.id.desc);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting New Stories ...");
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
                    String title = topic.getString(TAG_TITLE);
                    String url = topic.getString(TAG_URL);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_TITLE, title);
                    map.put(TAG_URL, url);
                    newsList.add(map);

                    listView=(ListView)findViewById(R.id.list);
                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, newsList,
                            R.layout.list_item,
                            new String[] { TAG_TITLE,TAG_URL}, new int[] {
                            R.id.title,R.id.desc});
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(MainActivity.this, "You Clicked at "+newsList.get(+position).get("name"), Toast.LENGTH_SHORT).show();
                        }
                    });

                    System.out.println(title);
                    System.out.println(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
