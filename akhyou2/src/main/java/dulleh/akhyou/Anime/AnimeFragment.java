package dulleh.akhyou.Anime;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.MainActivity;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.Video;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.Events.SearchSubmittedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import dulleh.akhyou.Utils.PaletteTransform;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(AnimePresenter.class)
public class AnimeFragment extends NucleusSupportFragment<AnimePresenter> implements AdapterClickListener<Episode> {
    private AnimeAdapter episodesAdapter;
    private SwipeRefreshLayout refreshLayout;
    //private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView drawerImage;
    private TextView drawerDesc;
    private TextView drawerGenres;
    private CheckBox drawerCheckBox;
    //private BlurTransform blurTransform;
    private Integer position;
    //private float d;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        TypedValue colorPrimaryDark = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimaryDark, colorPrimaryDark, true);

        episodesAdapter = new AnimeAdapter(new ArrayList<>(0), this, getResources().getColor(android.R.color.black), getResources().getColor(colorPrimaryDark.resourceId));
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
        View view = inflater.inflate(R.layout.anime_fragment, container, false);

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

        DrawerLayout animeDrawer = (DrawerLayout) view.findViewById(R.id.anime_drawer_layout);

        drawerImage = (ImageView) animeDrawer.findViewById(R.id.drawer_image_view);
        drawerDesc = (TextView) animeDrawer.findViewById(R.id.drawer_desc_view);
        drawerGenres = (TextView) animeDrawer.findViewById(R.id.drawer_genres_view);

        drawerCheckBox = (CheckBox) animeDrawer.findViewById(R.id.drawer_favourite_checkbox);
        drawerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final Boolean isInFavourites = getPresenter().isInFavourites();
                if (isInFavourites != null) {
                    // This is to ignore that first time the check box is changed.
                    // Should have just set the listener in setAnime ;-;
                    if (!b && isInFavourites) {
                        getPresenter().onFavouriteCheckedChanged(false);
                    } else if (b && !isInFavourites) {
                        getPresenter().onFavouriteCheckedChanged(true);
                    }
                } else {
                    getPresenter().setNeedToGiveFavourite(true);
                }
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
    public void onDestroy() {
        super.onDestroy();
        setToolbarTitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.search_item);

        if (searchItem == null) {
            inflater.inflate(R.menu.search_menu, menu);
            searchItem = menu.findItem(R.id.search_item);
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    EventBus.getDefault().post(new SearchSubmittedEvent(query));
                    searchView.clearFocus();
                    refreshLayout.requestFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.clearFocus();
        refreshLayout.requestFocus();

    }

    public void setAnime (Anime anime, boolean isInFavourites) {
        episodesAdapter.setAnime(anime.getEpisodes());
        setToolbarTitle(anime.getTitle());

        final PaletteTransform paletteTransform = new PaletteTransform();
        Picasso.with(getActivity()).invalidate(anime.getUrl());

        Picasso.with(getActivity())
                .load(anime.getImageUrl())
                .error(R.drawable.placeholder)
                //.transform(blurTransform)
                        //.transform(blurTransform)
                .fit()
                .centerCrop()
                .transform(paletteTransform)
                .into(drawerImage, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        if (paletteTransform.getPallete() != null) {
                            if (paletteTransform.getPallete().getVibrantSwatch() != null) {
                                getPresenter().setMajorColour(paletteTransform.getPallete().getVibrantSwatch().getRgb());
                            } else if (paletteTransform.getPallete().getLightVibrantSwatch() != null) {
                                getPresenter().setMajorColour(paletteTransform.getPallete().getLightVibrantSwatch().getRgb());
                            } else if (paletteTransform.getPallete().getDarkMutedSwatch() != null) {
                                getPresenter().setMajorColour(paletteTransform.getPallete().getDarkMutedSwatch().getRgb());
                            }
                        } else {
                            getPresenter().setMajorColour(getResources().getColor(R.color.accent));
                        }
                    }
                });

        drawerDesc.setText(anime.getDesc());
        drawerGenres.setText(anime.getGenresString());

        // CHECK IF IN FAVOURITES
        drawerCheckBox.setChecked(isInFavourites);
        getPresenter().setNeedToGiveFavourite(false);

        updateRefreshing();
    }

    public void clearAdapter () {
        episodesAdapter.clear();
    }

    public void updateRefreshing () {
        if (getPresenter().isRefreshing) {
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typedValue, true);
            refreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typedValue.resourceId));
            refreshLayout.setRefreshing(true);
        } else{
            refreshLayout.setRefreshing(false);
        }
    }

    public boolean isRefreshing () {
        return refreshLayout.isRefreshing();
    }

    public void setToolbarTitle (String title) {
        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(title); //MAY PRODUCE NULL POINTER EXCEPTION
        } catch (Exception e) {
            EventBus.getDefault().post(new SnackbarEvent("An error occurred."));
            e.printStackTrace();
        }
    }

    public void setFavouriteChecked(boolean isInFavourites) {
        drawerCheckBox.setChecked(isInFavourites);
        getPresenter().setNeedToGiveFavourite(false);
    }

    public void postSuccess (String successMessage) {
        EventBus.getDefault().post(new SnackbarEvent(successMessage));
    }


    private void setRefreshLayoutStatus (boolean setEnabled) {
        refreshLayout.setEnabled(setEnabled);
    }

    @Override
    public void onCLick(Episode episode, @Nullable Integer position) {
        getPresenter().fetchSources(episode.getUrl());
        this.position = position;
    }

    public void showSourcesDialog (List<Source> sources) {
        if (sources.size() >= 1) {
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
                            getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()), false);
                            if (position != null) {
                                episodesAdapter.setWatched(position);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            getPresenter().fetchVideo(sources.get(dialog.getSelectedIndex()), true);
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
        } else {
            getPresenter().postError(new Throwable("Error: No sources found."));
        }
    }

    private void showVideosDialog (List<Video> videos, boolean download) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.quality))
                .items(getVideosAsCharSequenceArray(videos))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        downloadOrStream(videos.get(i), download);
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

    public void shareVideo (Source source, boolean download) {
        if (source.getVideos().size() == 1) {
            downloadOrStream(source.getVideos().get(0), download);
        } else {
            showVideosDialog(source.getVideos(), download);
        }
    }

    public void downloadOrStream (Video video, boolean download) {
        if (download) {
            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(video.getUrl()));
            request.setTitle(video.getTitle());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
            // NEED TO USE BROADCAST RECEIVERS TO HANDLE CLICKS ON THE NOTIFICATION
        } else {
            postIntent(video.getUrl());
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
