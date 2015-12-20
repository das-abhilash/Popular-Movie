package com.example.abhilash.popularmovie;


import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {


    ArrayList<movie> movies;
    GridView gridview;
    CustomGrid mygrid;

    @Override
    protected void onStart() {
        super.onStart();
        //   movies.clear();
        //  finish();
        updateMovie();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*gridview = (GridView) findViewById(R.id.gridview);
        mygrid = new CustomGrid(this, R.layout.grid_single, movies);
        gridview.setAdapter(mygrid);*/
        // updateMovie();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            /*// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }


    public class Downloadtask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsnStr = "";

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY = "sort_by";
            final String APPID_PARAM = "api_key";

            URL url = null;

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY, params[0] + ".desc")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.API_KEY)
                    .build();
            InputStream inputStream = null;

            try {
                url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            movieJsnStr = buffer.toString();
            return movieJsnStr;

        }





        @Override
        protected void onPostExecute(String movieJsnStr) {

            //  final String LOG_TAG = Downloadtask.class.getSimpleName();

            final String title = "original_title";
            final String poster_path = "poster_path";
            final String Synopsis = "overview";
            final String release_date = "release_date";
            final String user_rating = "vote_average";
            String[] Msynopsis = new String[20];
            String[] Mtitle = new String[20];
            String[] Mpath = new String[20];
            String[] Mrelease = new String[20];
            String[] Mrating = new String[20];

            final String result = "results";

            try {
                JSONObject movieJsn = new JSONObject(movieJsnStr);
                JSONArray resultArray = movieJsn.getJSONArray(result);
                for (int i = 0; i < 20; i++) {

                    JSONObject c = resultArray.getJSONObject(i);
                    Mtitle[i] = c.getString(title);
                    Mpath[i] = "http://image.tmdb.org/t/p/w185/" + c.getString(poster_path);
                    Msynopsis[i] = c.getString(Synopsis);
                    Mrelease[i] = c.getString(release_date);
                    Mrating[i] = c.getString(user_rating);

                    movie m = new movie();
                    m.setTitle(Mtitle[i]);
                    m.setPoster(Mpath[i]);
                    m.setSynopsis(Msynopsis[i]);
                    m.setRelease(Mrelease[i]);
                    m.setRating(Mrating[i]);

                    movies.add(m);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mygrid.setGridData(movies);

            PassIntent(movies);
            //  movies.clear();
        }

    }

    public class movie {
        private String poster;
        private String title;
        private String synopsis;
        private String release;
        private String rating;

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSynopsis() {
            return synopsis;
        }

        public void setSynopsis(String synopsis) {
            this.synopsis = synopsis;
        }

        public String getRelease() {
            return release;
        }

        public void setRelease(String release) {
            this.release = release;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

    }

    public void PassIntent(final ArrayList<movie> mo) {

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                // Send intent to SingleViewActivity
                movie me = (movie) parent.getItemAtPosition(position);

                i.putExtra("poster", me.getPoster());
                i.putExtra("title", me.getTitle());
                i.putExtra("about", me.getSynopsis());
                i.putExtra("rating", me.getRating());
                i.putExtra("release", me.getRelease());

                startActivity(i);

            }
        });

    }

    private void updateMovie() {

        movies = new ArrayList<>();

        gridview = (GridView) findViewById(R.id.gridview);
        mygrid = new CustomGrid(this, R.layout.grid_single, movies);
        gridview.setAdapter(mygrid);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String Sort = prefs.getString((getString(R.string.pref_sort_key)), getString(R.string.pref_sort_default));

        new Downloadtask().execute(Sort);

//movies.clear();
    }



}




