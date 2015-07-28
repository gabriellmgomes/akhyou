package dulleh.akhyou.Anime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.Video;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.EpisodeSelectedListener;
import dulleh.akhyou.Utils.SearchSubmittedEvent;
import dulleh.akhyou.Utils.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.SnackbarEvent;
import dulleh.akhyou.Utils.ToolbarTitleChangedEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(AnimePresenter.class)
public class AnimeFragment extends NucleusSupportFragment<AnimePresenter> implements EpisodeSelectedListener {
    private AnimeAdapter episodesAdapter;
    private SwipeRefreshLayout refreshLayout;
    //private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView collapsingImage;
    //private BlurTransform blurTransform;
    private Integer position;
    //private float d;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        episodesAdapter = new AnimeAdapter(new ArrayList<>(0), this, getResources().getColor(R.color.grey_dark));
        setHasOptionsMenu(true);

/*
        blurTransform = new BlurTransform();
        blurTransform.setContext(activity);

        d = activity.getResources().getDisplayMetrics().density;

        AppBarLayout appBarLayout =(AppBarLayout) activity.findViewById(R.id.app_bar_layout);
        appBarLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 192 * (int)  d));
        getActivity().getLayoutInflater().inflate(R.layout.episodes_header_collapsing, appBarLayout);


        collapsingToolbarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar_layout);
        collapsingImage = (ImageView) activity.findViewById(R.id.collapsing_image);

        ImageView showMoreButton = (ImageView) activity.findViewById(R.id.expand_more_button);
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SnackbarEvent("Coming soon.", Snackbar.LENGTH_SHORT, null, null, null));
            }
        });
*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.episodes_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayout.VERTICAL, false));
        recyclerView.setAdapter(episodesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.accent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().fetchAnime();
            }
        });

/*
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0) {
                    setRefreshLayoutStatus(true);
                } else {
                    setRefreshLayoutStatus(false);
                }
            }
        });
*/
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    EventBus.getDefault().post(new SearchSubmittedEvent(query, searchItem));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_item) {
            EventBus.getDefault().post(new SettingsItemSelectedEvent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setAnime (Anime anime) {
        episodesAdapter.setAnime(anime.getEpisodes());

/*
        Picasso.with(getActivity())
                .load(anime.getImageUrl())
                .error(R.drawable.placeholder)
                .transform(blurTransform)
                        //.transform(blurTransform)
                .fit()
                .centerCrop()
                .into(collapsingImage);

        headerDescView.setText(anime.getDesc());
        headerGenresView.setText(Arrays.toString(anime.getGenres()));
*/

        setRefreshingFalse();
    }

    public void setRefreshingFalse () {
        refreshLayout.setRefreshing(false);
    }

    public void setToolbarTitle (String animeTitle) {
        EventBus.getDefault().post(new ToolbarTitleChangedEvent(animeTitle));
    }

    public void postSuccess (String successMessage) {
        EventBus.getDefault().post(new SnackbarEvent(successMessage, Snackbar.LENGTH_SHORT, null, null, null));
    }

    public void postError (String errorMessage) {
        EventBus.getDefault().post(new SnackbarEvent(errorMessage, Snackbar.LENGTH_SHORT, null, null, null));
    }

/*
    private void setRefreshLayoutStatus (boolean setEnabled) {
        refreshLayout.setEnabled(setEnabled);
    }
*/

    @Override
    public void onEpisodeSelected(Episode episode, int position) {
        getPresenter().fetchSources(episode.getUrl());
        this.position = position;
    }

    public void showSourcesDialog (List<Source> sources) {
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int accentColor = typedValue.data;

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.sources))
                .items(getSourcesAsCharSequenceArray(sources))
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        return true;
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()));
                        if (position != null) {
                            episodesAdapter.setWatched(position);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()));
                        if (position != null) {
                            episodesAdapter.setWatched(position);
                        }
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        position = null;
                    }

                })
                .widgetColor(accentColor)
                .positiveText(R.string.stream)
                .positiveColor(accentColor)
                .negativeText(R.string.download)
                .negativeColor(accentColor)
                .neutralText(R.string.cancel)
                .neutralColorRes(R.color.grey_darkestXX)
                .cancelable(true)
                .show();
    }

    private void showVideosDialog (List<Video> videos) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.quality))
                .items(getVideosAsCharSequenceArray(videos))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        postIntent(videos.get(i).getUrl());
                    }
                })
                .show();
    }

    private CharSequence[] getSourcesAsCharSequenceArray (List<Source> sources) {
        CharSequence[] sourcesAsArray = new CharSequence[sources.size()];
        for (int i = 0; i < sources.size(); i++) {
            sourcesAsArray[i] = sources.get(i).getTitle();
        }
        return sourcesAsArray;
    }

    private CharSequence[] getVideosAsCharSequenceArray(List<Video> videos) {
        CharSequence[] videosAsArray = new CharSequence[videos.size()];
        for (int i = 0; i < videos.size(); i++) {
            videosAsArray[i] = videos.get(i).getTitle();
        }
        return videosAsArray;
    }

    public void shareVideo (Source source) {
        if (source.getVideos().size() == 1) {
            postIntent(source.getVideos().get(0).getUrl());
        } else {
            showVideosDialog(source.getVideos());
        }
    }

    private void postIntent (String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(videoUrl), "video/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
