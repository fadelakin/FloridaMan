package com.fisheradelakin.floridaman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Activity {

    public static final String URL = "http://www.reddit.com/r/floridaman.json";
    private static final String TITLE = "title";
    private static final String TAG_CHILDREN = "children";
    TextView headlineText;

    JSONArray children;

    static String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button newStoryButton = (Button) findViewById(R.id.showAnotherStoryButton);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

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
                // jokes = json.getJSONArray(TAG_JOKE);
                // getting json from url
               /* JSONObject hello = new JSONObject("result");
                JSONObject c = hello.getJSONObject("data");
                children = c.getJSONArray(TAG_CHILDREN);
                for(int i = 0; i < children.length(); i++){
                    JSONObject j = children.getJSONObject(i).getJSONObject("data");

                    String title = j.getString(TITLE);
                    headlineText.setText(title.replace("&quot;", "\"").replace("&amp;", "&").replace("&#39;", "\'"));
                } */

                JSONObject response = new JSONObject();
                System.out.println(response);
                JSONObject data = response.getJSONObject("data");
                JSONArray hotTopics = data.getJSONArray("children");

                for(int i=0; i<hotTopics.length(); i++) {
                    JSONObject topic = hotTopics.getJSONObject(i).getJSONObject("data");

                    String author = topic.getString("author");
                    String imageUrl = topic.getString("thumbnail");
                    String postTime = topic.getString("created_utc");
                    String rScore = topic.getString("score");
                    String title = topic.getString("title");

                    headlineText.setText(title);
                }


                /* children = c.getJSONArray(TAG_CHILDREN);

                JSONObject random = children.getJSONObject("title");

                String title = random.getString(TITLE);

                headlineText.setText(title); */

                // store json item
                //String joke = c.getString("joke");
                // set json data in textview
                //headlineText = (TextView) findViewById(R.id.headlineText);
                //headlineText.setText(joke.replace("&quot;", "\"").replace("&amp;", "&").replace("&#39;", "\'"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
