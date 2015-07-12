package dulleh.akhyou.Search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.R;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(SearchPresenter.class)
public class SearchFragment extends NucleusSupportFragment<SearchPresenter>{
    private RecyclerView searchResultsView;
    private SwipeRefreshLayout refreshLayout;
    private SearchAdapter searchAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        searchResultsView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchResultsView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
        searchAdapter = new SearchAdapter();
        searchResultsView.setAdapter(searchAdapter);
        searchResultsView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.accent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchAdapter.clear();
                setSearchResults(fakeAnimes());
                refreshLayout.setRefreshing(false);
            }
        });

        setSearchResults(fakeAnimes());

        return view;
    }

    public void setSearchResults (Anime[] animes) {
        if (searchResultsView != null) {
            searchAdapter.setAnimes(animes);
        }
    }

    private Anime[] fakeAnimes() {
        Anime anime = new Anime()
                .setTitle(getString(R.string.testTitle))
                .setUrl(getString(R.string.testUrl))
                .setDesc(getString(R.string.testDesc))
                .setImageUrl(getString(R.string.testImageUrl))
                .setEpisodes(fakeEpisodes())
                .setGenres(fakeGenres());
        return new Anime[] {anime, anime, anime, anime, anime, anime};
    }

    private Episode[] fakeEpisodes() {
        Episode episode = new Episode()
                .setTitle(getString(R.string.testEpisodeTitle))
                .setUrl(getString(R.string.testEpisodeUrl))
                .setSources(fakeSources());
        return new Episode[] {episode, episode, episode, episode, episode, episode, episode};
    }

    private Source[] fakeSources() {
        Source source = new Source()
                .setTitle(getString(R.string.testSourceTitle))
                .setUrl(getString(R.string.testSourceUrl));
        return new Source[] {source, source, source, source};
    }

    private String[] fakeGenres() {
        return new String[] {getString(R.string.testGenre1), getString(R.string.testGenre2)};
    }

}
