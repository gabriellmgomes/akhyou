package dulleh.akhyou.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.SearchSubmittedEvent;
import dulleh.akhyou.Utils.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

@RequiresPresenter(SearchPresenter.class)
public class SearchFragment extends NucleusSupportFragment<SearchPresenter> {
    private SwipeRefreshLayout refreshLayout;
    private SearchAdapter searchAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        searchAdapter = new SearchAdapter(new ArrayList<>(0));
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

        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(false);
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

    public void setSearchResults (List<Anime> animes) {
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

    public void postError (String errorText) {
        EventBus.getDefault().post(new SnackbarEvent(errorText, Snackbar.LENGTH_SHORT, null, null, null));
    }

}
