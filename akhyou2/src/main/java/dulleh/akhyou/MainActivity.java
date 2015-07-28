package dulleh.akhyou;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.AnimeFragment;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.FragmentRequestEvent;
import dulleh.akhyou.Utils.SearchEvent;
import dulleh.akhyou.Utils.SearchSubmittedEvent;
import dulleh.akhyou.Utils.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.SnackbarEvent;
import dulleh.akhyou.Utils.ToolbarTitleChangedEvent;

public class MainActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private RelativeLayout parentLayout;
    private TextView toolbarTitle;

    private static final String SEARCH_FRAGMENT = "SEA";
    private static final String ANIME_FRAGMENT = "ANI";
    private static final String SETTINGS_FRAGMENT = "SET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        parentLayout = (RelativeLayout) findViewById(R.id.container);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
            onEvent(new FragmentRequestEvent(SEARCH_FRAGMENT));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void setTheme () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int themePref = sharedPreferences.getInt(getString(R.string.theme_preference_key), -1);

        switch (themePref) {
            case 1:
                setTheme(R.style.PinkTheme);
                break;

            case 2:
                setTheme(R.style.PurpleTheme);
                break;

            case 3:
                setTheme(R.style.DeepPurpleTheme);
                break;

            case 4:
                setTheme(R.style.IndigoTheme);
                break;

            case 5:
                setTheme(R.style.LightBlueTheme);
                break;

            case 6:
                setTheme(R.style.CyanTheme);
                break;

            case 7:
                setTheme(R.style.TealTheme);
                break;

            case 8:
                setTheme(R.style.GreenTheme);
                break;

            case 9:
                setTheme(R.style.LightGreenTheme);
                break;

            case 10:
                setTheme(R.style.LimeTheme);
                break;

            case 11:
                setTheme(R.style.YellowTheme);
                break;

            case 12:
                setTheme(R.style.OrangeTheme);
                break;

            case 13:
                setTheme(R.style.DeepOrangeTheme);
                break;

            case 14:
                setTheme(R.style.BrownTheme);
                break;

            case 15:
                setTheme(R.style.GreyTheme);
                break;

            case 16:
                setTheme(R.style.BlueGreyTheme);
                break;

            default:
                setTheme(R.style.GreyTheme);
                break;
        }
    }

    public void onEvent (SearchSubmittedEvent event) {
        MenuItem searchItem = event.searchItem;
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    EventBus.getDefault().postSticky(new SearchEvent(query.trim()));
                    if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) != null) {
                        fragmentManager.popBackStack();
                    }
                    if (fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) == null) {
                        onEvent(new FragmentRequestEvent(SEARCH_FRAGMENT));
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
    }

    public void onEvent (SettingsItemSelectedEvent event) {
        if (fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT) == null) {
            onEvent(new FragmentRequestEvent(SETTINGS_FRAGMENT));
        }
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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (event.tag) {
            case SEARCH_FRAGMENT:
                fragmentTransaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, new SearchFragment(), SEARCH_FRAGMENT)
                        .commit();
                break;

            case ANIME_FRAGMENT:
                fragmentTransaction
                        .setCustomAnimations(R.anim.enter_right, 0, 0, R.anim.exit_right)
                        .add(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);

                if (fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null) {
                    fragmentTransaction
                            .addToBackStack(SEARCH_FRAGMENT);
                }

                fragmentTransaction.commit();
                break;

            case SETTINGS_FRAGMENT:
                fragmentTransaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT);

                if (fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null) {
                    fragmentTransaction
                            .hide(fragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                }

                if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) != null) {
                    fragmentTransaction
                            .addToBackStack(ANIME_FRAGMENT)
                            .hide(fragmentManager.findFragmentByTag(ANIME_FRAGMENT));
                } else {
                    fragmentTransaction.addToBackStack(SEARCH_FRAGMENT);
                }

                fragmentTransaction.commit();
                break;
        }
    }

    public void onEvent (ToolbarTitleChangedEvent event) {
        toolbarTitle.setText(event.title);
    }

}