package dulleh.akhyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.AnimeFragment;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SearchEvent;
import dulleh.akhyou.Utils.Events.SearchSubmittedEvent;
import dulleh.akhyou.Utils.Events.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> {
    private SharedPreferences sharedPreferences;
    private android.support.v4.app.FragmentManager fragmentManager;
    private RelativeLayout parentLayout;
    private NavigationView navigationView;

    public static final String SEARCH_FRAGMENT = "SEA";
    public static final String ANIME_FRAGMENT = "ANI";
    public static final String SETTINGS_FRAGMENT = "SET";
    private static final String LAST_ANIME_TITLE_PREF = "last_anime_title_preference";
    private static final String LAST_ANIME_URL_PREF = "last_anime_url_preference";
    private static final String CURRENT_DRAWER_SELECTED = "current_drawer_selected_item";

    private static int currentSelectedDrawerItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        parentLayout = (RelativeLayout) findViewById(R.id.container);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout topLayoutRelative = (RelativeLayout) findViewById(R.id.top_level_relative);
            getLayoutInflater().inflate(R.layout.horizontal_shadow, topLayoutRelative, true);
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.drawer_settings_item) {
                    onEvent(new SettingsItemSelectedEvent());
                    currentSelectedDrawerItem = 0;
                    return true;
                }

                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Intent openingIntent = getIntent();

        if (openingIntent.getAction() != null && openingIntent.getAction().equals(Intent.ACTION_SEND)) {
            String intentExtra = openingIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (intentExtra != null && intentExtra.contains("hummingbird.me/anime/")) {
                getPresenter().launchFromHbLink(intentExtra);
            }

        } else if (savedInstanceState == null){
            sharedPreferences = getPreferences(MODE_PRIVATE);
            String lastAnimeTitle = sharedPreferences.getString(LAST_ANIME_TITLE_PREF, null);
            String lastAnimeUrl = sharedPreferences.getString(LAST_ANIME_URL_PREF, null);

            if (lastAnimeTitle != null && lastAnimeUrl != null) {
                EventBus.getDefault().postSticky(new OpenAnimeEvent(new Anime().setTitle(lastAnimeTitle).setUrl(lastAnimeUrl)));
                requestFragment(ANIME_FRAGMENT);
            } else {
                EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
                requestFragment(SEARCH_FRAGMENT);
            }

        } else { //if (savedInstanceState != null) {
            currentSelectedDrawerItem = savedInstanceState.getInt(CURRENT_DRAWER_SELECTED, 0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(CURRENT_DRAWER_SELECTED, currentSelectedDrawerItem);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSelectedDrawerItem = savedInstanceState.getInt(CURRENT_DRAWER_SELECTED, 0);
        navigationView.getMenu().getItem(currentSelectedDrawerItem).setChecked(true);
    }

    public void setToolbarTitle (String title) {
        getSupportActionBar().setTitle(title);
    }

    private void setTheme () {
        sharedPreferences = getPreferences(MODE_PRIVATE);
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

    public void setLastAnime (Anime anime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_ANIME_TITLE_PREF, anime.getTitle());
        editor.putString(LAST_ANIME_URL_PREF, anime.getUrl());
        editor.apply();
    }

    public void onEvent (SearchSubmittedEvent event) {
        EventBus.getDefault().postSticky(new SearchEvent(event.searchTerm));
        if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) != null) {
            fragmentManager.popBackStack();
        }
        if (fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) == null) {
            requestFragment(SEARCH_FRAGMENT);
        }
    }

    public void onEvent (SettingsItemSelectedEvent event) {
        if (fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT) == null) {
            requestFragment(SETTINGS_FRAGMENT);
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

    public void requestFragment (String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (tag) {
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

    public void postSuccess (String successMessage) {
        onEvent(new SnackbarEvent(successMessage));
    }

    public void postError (String errorMessage) {
        onEvent(new SnackbarEvent(errorMessage));
    }

}