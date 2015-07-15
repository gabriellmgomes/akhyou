package dulleh.akhyou.Episodes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(EpisodesPresenter.class)
public class EpisodesFragment extends NucleusSupportFragment<EpisodesPresenter> {
    private EpisodesAdapter episodesAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.episodes_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayout.VERTICAL, false));
        if (episodesAdapter == null) {
            episodesAdapter = new EpisodesAdapter(new Episode[0]);
        }
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

        return view;
    }

    public void setAnime (Anime anime) {
        episodesAdapter.setEpisodes(anime.getEpisodes());
        setRefreshingFalse();
    }

    public void setRefreshingFalse () {
        refreshLayout.setRefreshing(false);
    }

    public void postSuccess () {
        EventBus.getDefault().post(new SnackbarEvent("SUCCESS", Snackbar.LENGTH_SHORT, null, null, null));
    }

    public void postError () {
        EventBus.getDefault().post(new SnackbarEvent("ERROR", Snackbar.LENGTH_SHORT, null, null, null));
    }

}
