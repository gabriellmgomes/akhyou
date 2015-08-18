package dulleh.akhyou.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.MainActivity;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SearchEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(SearchPresenter.class)
public class SearchFragment extends NucleusSupportFragment<SearchPresenter> implements AdapterClickListener<Anime> {
    private SwipeRefreshLayout refreshLayout;
    private SearchAdapter searchAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        searchAdapter = new SearchAdapter(new ArrayList<>(0), this);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        RecyclerView searchResultsView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchResultsView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
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
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    getPresenter().onEvent(new SearchEvent(query));
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

    public void setSearchResults (List<Anime> animes) {
        searchAdapter.setAnimes(animes);
        setRefreshing(false);
    }

    public void setRefreshing (boolean bool) {
        if (bool) {
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

    @Override
    public void onCLick(Anime anime, @Nullable Integer position) {
        ((MainActivity) getActivity()).requestFragment(MainActivity.ANIME_FRAGMENT);
        EventBus.getDefault().postSticky(new OpenAnimeEvent(anime));
    }

    @Override
    public void onLongClick(Anime item, @Nullable Integer position) {

    }

    public void addToSearchResults (Anime anime) {
        searchAdapter.addAnime(anime);
    }

}
