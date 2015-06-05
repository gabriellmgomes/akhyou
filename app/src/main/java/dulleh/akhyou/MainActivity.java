package dulleh.akhyou;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private AnimeObject currentAnime;
    private ArrayAdapter<AnimeObject> searchAdapter;
    private ArrayAdapter<EpisodeObject> episodesAdapter;
    private ArrayList<AnimeObject> animeObjects;
    private String searchQ;
    private Boolean fromSearch;
    int fortySixDP, browsingOrSearching;

    private ProgressBar progressBar;
    private ListView listView;
    private ListView episodesListView;
    MenuItem reverse;
    private SearchView searchView;
    private Animation slideIn;
    private Animation slideOut;

    private SharedPreferences.Editor editor;

    private void showToast(String toastText) {
        try {Looper.prepare();}catch (Exception e){e.printStackTrace();}
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    private void newAnimeObject () {
        currentAnime = new AnimeObject();
        currentAnime.createEpisodeList();
        currentAnime.createGenreList();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
        editor = sharedPrefs.edit();
        editor.apply();

        episodesListView = (ListView) findViewById(R.id.episodeLV);
        episodesListView.setVisibility(View.INVISIBLE);

        newAnimeObject();

        episodesAdapter = new EpisodesAdapter(getApplicationContext(), currentAnime.getEpisodesList());
        episodesListView.setAdapter(episodesAdapter);

        animeObjects = new ArrayList<>();
        searchAdapter = new SearchAdapter(getApplicationContext(), animeObjects);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_bottom);
        slideOut.setDuration((int) (slideOut.getDuration()/2));

        browsingOrSearching = 0;    //0 = CAN'T go back to search, 1 = CAN go back to search
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        fortySixDP = (int) (46 * d);
        fromSearch = false;

        reverse = (MenuItem) findViewById(R.id.reverse);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(searchAdapter);
        episodesListView.setVisibility(View.INVISIBLE);

        Intent fromHB = getIntent();
        String openingAction = fromHB.getAction();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (openingAction.equals(Intent.ACTION_MAIN)) {
            newAnimeObject();

            if (!sharedPrefs.getString("hummingLink", "empty").equals("empty")
                    && networkInfo != null
                    && networkInfo.isConnected()) {
                try {
                    currentAnime.setLink(sharedPrefs.getString("hummingLink", "http://www.animerush.tv/anime/hyouka"));
                    currentAnime.setTitle(currentAnime.getLink().split("/anime/")[1]);
                    new AnimerushEpisodeList().execute(currentAnime);
                } catch (Exception e) {e.printStackTrace(); showToast("Failed to load previous anime.");}
                }
        } else if (openingAction.equals(Intent.ACTION_SEND)) {
            if (networkInfo.isConnected()) {
                try {
                    String[] splitText = fromHB.getStringExtra(Intent.EXTRA_TEXT).split("https://hummingbird.me/anime/");
                    String animeName = splitText[1];
                    try {
                        newAnimeObject();
                        currentAnime.setTitle(animeName);
                        currentAnime.setLink("http://www.animerush.tv/anime/" + animeName);
                        new AnimerushEpisodeList().execute(currentAnime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    showToast("No suitable URL to use.");
                    e.printStackTrace();
                }
            } else {
                showToast("Not connected.");
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentAnime = animeObjects.get(position);
                    fromSearch = true;
                //listView.smoothScrollToPositionFromTop(position, 4);
                //episodesMarginHeight = view.getHeight();
                    if (currentAnime.getEpisodesList() != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                        browsingOrSearching = 0;
                        listView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in));
                        episodesAdapter = new EpisodesAdapter(getApplicationContext(), currentAnime.getEpisodesList());
                        episodesListView.setAdapter(episodesAdapter);
                        episodesListView.setVisibility(View.VISIBLE);
                        episodesListView.startAnimation(slideIn);
                        episodesAdapter.notifyDataSetChanged();
                    } else {
                        new AnimerushEpisodeList().execute(currentAnime);
                    }
               // }
                searchView.clearFocus();
            }
        });
        episodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String URLListItem = currentAnime.getEpisodesList().get(position).getEpisodeLink();
                new AnimerushEpisode().execute(URLListItem);

            }
        });
        episodesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String URLListItem = currentAnime.getEpisodesList().get(position).getEpisodeLink() + "DOWNLOADTHISPLEASE";
                new AnimerushEpisode().execute(URLListItem);

                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (fromSearch){
            if (getCurrentFocus() != null) {
                getCurrentFocus().clearFocus();
            }
            fromSearch = false;
            episodesListView.startAnimation(slideOut);
            episodesListView.setVisibility(View.INVISIBLE);
            browsingOrSearching = 1;
            searchAdapter.notifyDataSetChanged();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String search = searchView.getQuery().toString().trim();
                if (!search.isEmpty()) {
                    searchQ = search;
                    try {
                        new AnimerushSearch().execute(searchQ);
                    }catch (Exception e) {e.printStackTrace(); showToast("Search failed.");}
                }
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reverse) {
            Integer listSize;
            if (browsingOrSearching == 0) {
                listSize = currentAnime.getEpisodesList().size();
                ArrayList<EpisodeObject> oldEpisodeList = new ArrayList<>(currentAnime.getEpisodesList().size());
                oldEpisodeList.addAll(currentAnime.getEpisodesList());

                int t = listSize - 1;
                for (int f = 0; f < listSize; f++) {
                    currentAnime.getEpisodesList().set(f, oldEpisodeList.get(t));
                    t--;
                }
                episodesAdapter.notifyDataSetChanged();
            }else if (browsingOrSearching == 1) {
                listSize = animeObjects.size();
                ArrayList<AnimeObject> oldAnimeObjects = new ArrayList<>(animeObjects.size());
                oldAnimeObjects.addAll(animeObjects);

                int t = listSize - 1;
                for (int f = 0; f < listSize; f++) {
                    animeObjects.set(f, oldAnimeObjects.get(t));
                    t--;
                }
                searchAdapter.notifyDataSetChanged();
            }
            listView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale));
            return true;
        }

        if (id == R.id.refesh) {
            if (currentAnime != null) {
                if (browsingOrSearching == 0) {
                    if (currentAnime.getLink() != null) {
                        new AnimerushEpisodeList().execute(currentAnime);
                    }
                }
                if (browsingOrSearching == 1) {
                    if (searchQ != null) {
                        new AnimerushSearch().execute(searchQ);
                    }
                }
            }
            return true;
        }

        if (id == R.id.search) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToPlayer (String sendData) {
        if (sendData != null) {
            Intent sendLink = new Intent(Intent.ACTION_VIEW);
            sendLink.setDataAndType(Uri.parse(sendData), "video/*");
            if (sendLink.resolveActivity(getPackageManager()) != null) {
                startActivity(sendLink);
            }
        }
    }

    private void sendToDownloader (String sendData) {
        if (sendData != null) {
            Intent sendLink = new Intent(Intent.ACTION_VIEW);
            sendLink.setDataAndType(Uri.parse(sendData), "video/*");
            if (sendLink.resolveActivity(getPackageManager()) != null) {
                startActivity(sendLink);
            }
        }
    }

    private class AnimerushSearch extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            animeObjects.clear();
            String searchTerm = params[0]
                    .replace(":", "%3A")
                    .replace("/", "%2F")
                    .replace("#", "%23")
                    .replace("?", "%3F")
                    .replace("&", "%24")
                    .replace("@", "%40")
                    .replace("%", "%25")
                    .replace("+", "%2B")
                    .replace(" ", "+")
                    .replace(";","%3B")
                    .replace("=", "%3D")
                    .replace("$", "%26")
                    .replace(",", "%2C")
                    .replace("<", "%3C")
                    .replace(">", "%3E")
                    .replace("~", "%25")
                    .replace("^", "%5E")
                    .replace("`", "%60")
                    .replace("\\", "%5C")
                    .replace("[", "%5B")
                    .replace("]", "%5D")
                    .replace("{", "%7B")
                    .replace("|", "%7C")
                    .replace("\"", "%22");
            String url = "http://www.animerush.tv/search.php?searchquery=" + searchTerm;
            try {
                Document document = Jsoup.connect(url).ignoreContentType(true).followRedirects(false).get();
                if (!document.body().select("div#left-column").toString().contains("No results found.")) {
                    Elements searchElements = document.body().select("div.search-page_in_box_mid_link");
                    for (Element e : searchElements) {
                        AnimeObject animeObject = new AnimeObject();

                        animeObject.setTitle(e.select("h3").text().trim());
                        animeObject.setLink(e.select("a.highlightit").attr("href").trim());
                        animeObject.setImageLink(e.select("object.highlightz").attr("data").trim());
                        animeObject.setDesc(e.select("p").text().trim());

                        animeObjects.add(animeObject);

                        //searchList.add(e.select("h3").text());
                        //searchURLsList.add(e.select("a.highlightit").attr("href"));
                    }
                }
            return "Search term: " + searchTerm;
            } catch (IOException io) {
                io.printStackTrace();
                return "Search failed.";
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            fromSearch = false;
            browsingOrSearching = 1;
            episodesListView.setVisibility(View.INVISIBLE);
            searchAdapter.notifyDataSetChanged();
            listView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in));
            if (animeObjects.size() == 1) {
                listView.getOnItemClickListener().onItemClick(listView, listView.getChildAt(0), 0, listView.getItemIdAtPosition(0));
            }
        }
    }

    private class AnimerushEpisodeList extends AsyncTask<AnimeObject, Void, AnimeObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected AnimeObject doInBackground(AnimeObject... params) {
            currentAnime = params[0];
            currentAnime.createEpisodeList(); currentAnime.createGenreList();
            String url = currentAnime.getLink();

            editor.putString("hummingLink", url);
            editor.apply();

            try{
                Document document = Jsoup.connect(url).ignoreContentType(true).get();
                Elements episodesElements = document.select("div.episode_list");

                for (Element e : episodesElements) {

                    EpisodeObject episode = new EpisodeObject();
                    episode.setEpisodeLink(e.select("a.fixedLinkColor").attr("href"));
                    episode.setEpisodeTitle(e.select("a.fixedLinkColor").text().replace("- ", "").replace("Watch", "").replace("now", "").trim());
                    currentAnime.getEpisodesList().add(episode);

                }

                Elements genreElements = document.select("div.cat_box_desc").select("a");

                ArrayList<String> mGenreList = new ArrayList<>(14);
                for (Element e : genreElements) {
                    mGenreList.add(e.text());
                }
                currentAnime.getGenreList().addAll(mGenreList);
                currentAnime.appendGenres();

            }catch (IOException io) {
                io.printStackTrace();
                try {
                    new useHB().execute(url.split("anime/")[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return currentAnime;
        }
        @Override
        protected void onPostExecute(AnimeObject s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            browsingOrSearching = 0;
            episodesAdapter = new EpisodesAdapter(getApplicationContext(), s.getEpisodesList());
            episodesListView.setAdapter(episodesAdapter);
            episodesListView.setVisibility(View.VISIBLE);
            episodesListView.startAnimation(slideIn);
            episodesAdapter.notifyDataSetChanged();
        }
    }


    private class AnimerushEpisode extends AsyncTask<String, Void, ArrayList<AlternateObjects>> {
        Boolean onlyOne = false;
        Boolean forDownload = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected ArrayList<AlternateObjects> doInBackground(String... params) {
            String url;
            if (params[0].contains("DOWNLOADTHISPLEASE")) {
                url = params[0].replace("DOWNLOADTHISPLEASE", "");
                forDownload = true;
            } else {
                 url = params[0];}
            try {
                Document document = Jsoup.connect(url).ignoreContentType(true).get();
                Elements alternateVideos = document.select("div.episode1");

                ArrayList<AlternateObjects> alternatesList = new ArrayList<>();
                for (Element e : alternateVideos) {
                    AlternateObjects alternateObjects = new AlternateObjects();
                    Elements a = e.select("a");
                    String aTextLower = a.text().toLowerCase();
                    if (aTextLower.contains("mp4upload") || aTextLower.contains("goplayer") || aTextLower.contains("dailymotion") || aTextLower.contains("yourupload")) {
                        alternateObjects.setSourceName(a.text().replace(" Video", ""));
                        alternateObjects.setLink(a.attr("href"));
                        alternateObjects.setDubbedOrSubbed(e.select("span").toString().split("mirror-sub")[1].split(">")[1].split("<")[0]);
                        alternatesList.add(alternateObjects);
                    }
                }
                if (alternatesList.size() == 1) {
                    onlyOne = true;
                }
            return alternatesList;
            }catch (IOException io) {io.printStackTrace();
                return null;}
        }
        @Override
        protected void onPostExecute(final ArrayList<AlternateObjects> alternatesList) {
            super.onPostExecute(alternatesList);
            progressBar.setVisibility(View.INVISIBLE);
            if (!onlyOne) {
                final ArrayList<String> alternates = new ArrayList<>(20);
                for (AlternateObjects ao : alternatesList) {
                    alternates.add(ao.getSourceName() + " - " + ao.getDubbedOrSubbed().toUpperCase()); //NEED TO GET NAMES
                }
                final ArrayAdapter<String> alternatesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.alternates_item, alternates);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Source");
                alertDialog.setAdapter(alternatesAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedItem = alternatesAdapter.getItem(which);
                        if (selectedItem.toLowerCase().contains("mp4upload")) {
                            if (forDownload) {
                                new Mp4UploadScrape().execute(alternatesList.get(which).getLink() + "DOWNLOADTHISPLEASE");
                            } else {
                                new Mp4UploadScrape().execute(alternatesList.get(which).getLink());
                            }
                        } else if (selectedItem.toLowerCase().contains("dailymotion")) {
                            if (forDownload) {
                                new DailyMotionScrape().execute(alternatesList.get(which).getLink() + "DOWNLOADTHISPLEASE");
                            } else {
                                new DailyMotionScrape().execute(alternatesList.get(which).getLink());
                            }
                        } else if (selectedItem.toLowerCase().contains("yourupload")) {
                            if (forDownload) {
                                new YourUploadScrape().execute(alternatesList.get(which).getLink() + "DOWNLOADTHISPLEASE");
                            } else {
                                new YourUploadScrape().execute(alternatesList.get(which).getLink());
                            }
                        } else if (selectedItem.toLowerCase().contains("goplayer")) {
                            if (forDownload) {
                                new GoPlayerScrape().execute(alternatesList.get(which).getLink() + "DOWNLOADTHISPLEASE");
                            } else {
                                new GoPlayerScrape().execute(alternatesList.get(which).getLink());
                            }
                        } else {
                            showToast("Akhyou! does not support that source.");
                        }
                    }
                });
                alertDialog.create();
                alertDialog.show();
            } else {
                showToast(alternatesList.get(0).getSourceName() + alternatesList.get(0).getDubbedOrSubbed());
                String selectedItem = alternatesList.get(0).getSourceName();
                if (selectedItem.toLowerCase().contains("mp4upload")) {
                    new Mp4UploadScrape().execute(alternatesList.get(0).getLink());
                } else if (selectedItem.toLowerCase().contains("dailymotion")) {
                    new DailyMotionScrape().execute(alternatesList.get(0).getLink());
                } else if (selectedItem.toLowerCase().contains("yourupload")) {
                    new YourUploadScrape().execute(alternatesList.get(0).getLink());
                } else if (selectedItem.toLowerCase().contains("goplayer")) {
                    new GoPlayerScrape().execute(alternatesList.get(0).getLink());
                } else {
                    showToast("Akhyou! does not support that source.");
                }
            }
        }
    }

    private class DailyMotionScrape extends AsyncTask<String, Void, ArrayList<String>> {
        Boolean forDownload = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String embedSRC;
            ArrayList<String> dailyURLs = new ArrayList<>(5);
            try {
                String url;
                if (params[0].contains("DOWNLOADTHISPLEASE")) {
                    url = params[0].replace("DOWNLOADTHISPLEASE", "");
                    forDownload = true;
                } else {
                    url = params[0];
                }
                Document document1 = Jsoup.connect(url).ignoreContentType(true).get();
                Elements sf = document1.select("div.player-area");
                embedSRC = sf.select("iframe[src]").attr("src");
                System.out.print(embedSRC);
                Document document = Jsoup.connect(embedSRC).ignoreContentType(true).get();
                String[] linkFromPileOfShit = document.select("script").last().toString().split("stream_audio_url")[1].split(",\"stream_hls_url")[0].split(",");
                for (String s : linkFromPileOfShit) {
                    if (!s.contains("null")) {
                        dailyURLs.add(s.replace("\\", "").split("\":\"")[1].replace("\"", ""));
                    }
                }
                return dailyURLs;
            }catch (IOException io) {
                io.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(final ArrayList<String> dailyURLs) {
            if (dailyURLs != null) {
                super.onPostExecute(dailyURLs);
                ArrayList<String> simpled = new ArrayList<>(5);
                for (String s : dailyURLs) {
                    simpled.add(s.split("cdn/")[1].split("/")[0]);
                }
                final ArrayAdapter<String> dailyURLsAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.alternates_item, simpled);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Source");
                if (forDownload) {
                    alertDialog.setAdapter(dailyURLsAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendToDownloader(dailyURLs.get(which));
                        }
                    });
                } else {
                    alertDialog.setAdapter(dailyURLsAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendToPlayer(dailyURLs.get(which));
                        }
                    });
                }
                alertDialog.create();
                alertDialog.show();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                showToast("Could not find the video. Perhaps it was removed.");
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class Mp4UploadScrape extends AsyncTask<String, Void, String> {
        Boolean forDownload = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String url;
                if (params[0].contains("DOWNLOADTHISPLEASE")) {
                    url = params[0].replace("DOWNLOADTHISPLEASE", "");
                    forDownload = true;
                } else {
                    url = params[0];
                }
                Document document = Jsoup.connect(url).ignoreContentType(true).get();
                Elements sf = document.select("div.player-area");
                url = sf.select("iframe[src]").attr("src");
                String url2 = "http://mp4upload.com/" + url.split("embed-")[1].split("-")[0];
                Document documentDownload = Jsoup.connect(url2).ignoreContentType(true).followRedirects(false).get();
                String opVal = documentDownload.select("input[name=op]").first().val();
                String idVal = documentDownload.select("input[name=id]").first().val();
                String randVal = documentDownload.select("input[name=rand]").first().val();

                Connection.Response response = Jsoup.connect(url2).ignoreContentType(true).method(Connection.Method.POST).followRedirects(true)
                        .data("op", opVal)
                        .data("id", idVal)
                        .data("rand", randVal)
                        .execute();
                return response.url().toString();
            }catch (IOException io) {io.printStackTrace();
                return null;}
        }
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.INVISIBLE);
                if (forDownload) {
                    sendToDownloader(s);
                } else {
                    sendToPlayer(s);
                }
            }else {
                showToast("Could not find the video. Perhaps it was removed.");
            }
        }
    }

    private class YourUploadScrape extends AsyncTask<String, Void, String> {
        Boolean forDownload = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String url;
                if (params[0].contains("DOWNLOADTHISPLEASE")) {
                    url = params[0].replace("DOWNLOADTHISPLEASE", "");
                    forDownload = true;
                } else {
                    url = params[0];
                }
                Document document = Jsoup.connect(url).ignoreContentType(true).get();
                Elements sf = document.select("div.player-area");
                url = sf.select("iframe[src]").attr("src");
                Document yourUploadEmbedDoc = Jsoup.connect(url).ignoreContentType(true).get();
                return yourUploadEmbedDoc.head().select("meta[property=og:video]").attr("content");
            }catch (IOException io) {io.printStackTrace();
                return null; }
        }
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.INVISIBLE);
                if (forDownload) {
                    sendToDownloader(s);
                } else {
                    sendToPlayer(s);
                }
            } else {
                showToast("Could not find the video. Perhaps it was removed.");
            }
        }
    }

    private class GoPlayerScrape extends AsyncTask<String, Void, String> {
        Boolean forDownload = false;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String url;
                if (params[0].contains("DOWNLOADTHISPLEASE")) {
                    url = params[0].replace("DOWNLOADTHISPLEASE", "");
                    forDownload = true;
                } else {
                    url = params[0];
                }
                Document document = Jsoup.connect(url).ignoreContentType(true).get();
                Elements sf = document.select("div.player-area");
                url = sf.select("iframe[src]").attr("src");
                Document goPlayerEmbedDoc = Jsoup.connect(url).ignoreContentType(true).get();
                return goPlayerEmbedDoc.select("script").toString().split("_url = \"")[1].split("\";")[0];
            }catch (IOException io) {io.printStackTrace(); return null;}
        }
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.INVISIBLE);
                if (forDownload) {
                    sendToDownloader(s);
                } else {
                    sendToPlayer(s);
                }
            } else {
                showToast("Could not find the video. Perhaps it was removed.");
            }
        }
    }

    private class useHB extends AsyncTask<String, Void, String> {
        String elemente;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            Document HBDoc;
            String url = params[0].trim();
            showToast(url);
            String[] elementeSplit;
            try {
                HBDoc = Jsoup.connect(url).ignoreContentType(true).get();
                try {
                    elemente = HBDoc.toString();
                    elementeSplit = elemente.split("\"title\":\"");
                    elemente = elementeSplit[1];
                    elementeSplit = elemente.split("\"");
                    elemente  = elementeSplit[0];
                }catch (Exception e) {e.printStackTrace();
                    elemente = "failed1";
                    showToast("failed to get the title");}
            } catch (IOException e) {
                e.printStackTrace();
                elemente = "failed2";
            }
            return elemente;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                progressBar.setVisibility(View.INVISIBLE);

                searchQ = s.trim();
                searchView.setQuery(searchQ, false);
                searchView.onActionViewExpanded();
                new AnimerushSearch().execute(searchQ);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}