package dulleh.akhyou;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Episodes.EpisodesFragment;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Utils.FragmentRequestEvent;
import dulleh.akhyou.Utils.SearchEvent;
import dulleh.akhyou.Utils.SnackbarEvent;

public class MainActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private FrameLayout parentLayout;
    private String SEARCH_FRAGMENT;
    private String EPISODES_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SEARCH_FRAGMENT == null) {
            SEARCH_FRAGMENT = getString(R.string.search_fragment);
            EPISODES_FRAGMENT = getString(R.string.episodes_fragment);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        fragmentManager = getSupportFragmentManager();
        parentLayout = (FrameLayout) findViewById(R.id.container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
            onEvent(new FragmentRequestEvent(SEARCH_FRAGMENT));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_item);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // can't use .isEmpty because only available on api 9+
                if (query.length() > 0) {
                    EventBus.getDefault().postSticky(new SearchEvent(query));
                    if (fragmentManager.findFragmentByTag(EPISODES_FRAGMENT) != null) {
                        fragmentManager.popBackStack();
                    }
                }
                searchView.clearFocus();
                parentLayout.requestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_item) {
            onEvent(new SnackbarEvent("SETTINGS CLICKED", Snackbar.LENGTH_SHORT, null, null, null));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void onEvent (SnackbarEvent event) {
        if (event.actionTitle == null) {
            Snackbar.make(parentLayout, event.message, event.duration)
                    .show();
        } else {
            Snackbar.make(parentLayout, event.message, event.duration)
                .setAction(event.actionTitle, event.onClickListener)
                .setActionTextColor(event.actionColor)
                .show();
        }
    }

    public void onEvent (FragmentRequestEvent event) {

        if (event.tag.equals(SEARCH_FRAGMENT)) {
            fragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.container, new SearchFragment(), SEARCH_FRAGMENT)
                    .commit();
        } else if (event.tag.equals(EPISODES_FRAGMENT)) {
            fragmentManager
                    .beginTransaction()
                    .addToBackStack(SEARCH_FRAGMENT)
                    .addToBackStack(EPISODES_FRAGMENT)
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .replace(R.id.container, new EpisodesFragment(), EPISODES_FRAGMENT)
                    .commit();
        }

    }

}