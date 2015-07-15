package dulleh.akhyou.Search;

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

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(SearchPresenter.class)
public class SearchFragment extends NucleusSupportFragment<SearchPresenter>{
    private SwipeRefreshLayout refreshLayout;
    private SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        RecyclerView searchResultsView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchResultsView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
        if (searchAdapter == null) {
            searchAdapter = new SearchAdapter(new Anime[0]);
        }
        searchResultsView.setAdapter(searchAdapter);
        searchResultsView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.accent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().search();
            }
        });

        return view;
    }

    public void setSearchResults (Anime[] animes) {
        searchAdapter.setAnimes(animes);
        setRefreshingFalse();
    }

    public void setRefreshingFalse () {
        refreshLayout.setRefreshing(false);
    }

    public void addToSearchResults (Anime anime) {
        searchAdapter.addAnime(anime);
    }

    public void postSuccess () {
        EventBus.getDefault().post(new SnackbarEvent("SUCCESS", Snackbar.LENGTH_SHORT, null, null, null));
    }

    public void postError () {
        EventBus.getDefault().post(new SnackbarEvent("ERROR", Snackbar.LENGTH_SHORT, null, null, null));
    }

}
