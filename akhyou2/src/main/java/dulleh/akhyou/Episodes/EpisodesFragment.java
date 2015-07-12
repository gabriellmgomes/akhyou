package dulleh.akhyou.Episodes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.R;

public class EpisodesFragment extends Fragment {
    private Anime anime;
    private EpisodesAdapter episodesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        anime = fakeAnime();
        episodesAdapter = new EpisodesAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.episodes_fragment, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayout.VERTICAL, false));
        recyclerView.setAdapter(episodesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.accent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                episodesAdapter.clear();
                episodesAdapter.setEpisodes(anime.getEpisodes());
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        episodesAdapter.setEpisodes(anime.getEpisodes());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private Anime fakeAnime() {
        return new Anime()
                .setTitle(getString(R.string.testTitle))
                .setUrl(getString(R.string.testUrl))
                .setDesc(getString(R.string.testDesc))
                .setImageUrl(getString(R.string.testImageUrl))
                .setEpisodes(fakeEpisodes())
                .setGenres(fakeGenres());
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
