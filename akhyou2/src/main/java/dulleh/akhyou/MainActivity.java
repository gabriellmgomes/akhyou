package dulleh.akhyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.AnimeFragment;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> implements AdapterClickListener<Anime>{
    private SharedPreferences sharedPreferences;
    private android.support.v4.app.FragmentManager fragmentManager;
    private RelativeLayout parentLayout;
    private DrawerLayout drawerLayout;
    private RecyclerView favouritesList;
    private DrawerAdapter drawerAdapter;

    public static final String SEARCH_FRAGMENT = "SEA";
    public static final String ANIME_FRAGMENT = "ANI";
    public static final String SETTINGS_FRAGMENT = "SET";

    //private static int currentSelectedDrawerItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();

        getPresenter().setSharedPreferences(sharedPreferences);

        parentLayout = (RelativeLayout) findViewById(R.id.container);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout topLayoutRelative = (RelativeLayout) findViewById(R.id.top_level_relative);
            getLayoutInflater().inflate(R.layout.toolbar_shadow, topLayoutRelative, true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //TextView drawerSettingsButton = (TextView) findViewById(R.id.drawer_settings_text);
        RelativeLayout drawerSettingsButton = (RelativeLayout) findViewById(R.id.drawer_settings);
        drawerSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().onEvent(new SettingsItemSelectedEvent());
            }
        });
        favouritesList = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        favouritesList.setLayoutManager(new LinearLayoutManager(this));

        updateAndSetFavourites();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setContentInsetsRelative(0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //MAY PRODUCE NULL POINTER EXCEPTION
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Intent openingIntent = getIntent();

        if (openingIntent.getAction() != null && openingIntent.getAction().equals(Intent.ACTION_SEND)) {
            String intentExtra = openingIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (intentExtra != null && intentExtra.contains("hummingbird.me/anime/")) {
                getPresenter().launchFromHbLink(intentExtra);
            }

        } else if (savedInstanceState == null){
            getPresenter().onFreshStart(this);
        } /*else { if (savedInstanceState != null) {
            currentSelectedDrawerItem = savedInstanceState.getInt(CURRENT_DRAWER_SELECTED, 0);
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //outState.putInt(CURRENT_DRAWER_SELECTED, currentSelectedDrawerItem);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //currentSelectedDrawerItem = savedInstanceState.getInt(CURRENT_DRAWER_SELECTED, 0);
        //navigationView.getMenu().getItem(currentSelectedDrawerItem).setChecked(true);
    }

    public void closeDrawer () {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

    public void requestFragment (String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (tag) {
            case SEARCH_FRAGMENT:
                fragmentTransaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, new SearchFragment(), SEARCH_FRAGMENT);
                break;

            case ANIME_FRAGMENT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_right, 0, 0, R.anim.exit_right)
                        .replace(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);

                /*if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) != null) {
                    fragmentTransaction
                            .replace(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);
                } else {
                    fragmentTransaction
                            .add(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);
                }*/

                if (fragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null) {
                    fragmentTransaction
                            //.hide(fragmentManager.findFragmentByTag(SEARCH_FRAGMENT))
                            .addToBackStack(SEARCH_FRAGMENT);
                }

                break;

            case SETTINGS_FRAGMENT:
                fragmentTransaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT);

                if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) != null) {
                    fragmentTransaction
                            .addToBackStack(ANIME_FRAGMENT)
                            .hide(fragmentManager.findFragmentByTag(ANIME_FRAGMENT));
                } else {
                    fragmentTransaction
                            .hide(fragmentManager.findFragmentByTag(SEARCH_FRAGMENT))
                            .addToBackStack(SEARCH_FRAGMENT);
                }

                break;
        }
        fragmentTransaction.commit();
    }

    public void showSnackBar (SnackbarEvent event) {
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

    @Override
    public void onCLick(Anime item, @Nullable Integer position) {
        if (fragmentManager.findFragmentByTag(ANIME_FRAGMENT) == null) {
            requestFragment(MainActivity.ANIME_FRAGMENT);
            EventBus.getDefault().postSticky(new OpenAnimeEvent(item));
        } else {
            EventBus.getDefault().postSticky(new OpenAnimeEvent(item));
        }

        if (fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT) != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT))
                    .commit();
            fragmentManager.popBackStack();
        }

        closeDrawer();
    }

    private void updateAndSetFavourites () {
        getPresenter().refreshFavouritesList();
        drawerAdapter = new DrawerAdapter(this, getPresenter().favouritesList);
        favouritesList.setAdapter(drawerAdapter);
    }

    public void favouritesChanged () {
        if (drawerAdapter != null) {
            drawerAdapter.setFavourites(getPresenter().favouritesList);
            drawerAdapter.notifyDataSetChanged();
        } else {
            drawerAdapter = new DrawerAdapter(this, getPresenter().favouritesList);
            favouritesList.setAdapter(drawerAdapter);
            drawerAdapter.setFavourites(getPresenter().favouritesList);
            drawerAdapter.notifyDataSetChanged();
        }
    }

}