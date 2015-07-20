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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Episodes.EpisodesFragment;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.FragmentRequestEvent;
import dulleh.akhyou.Utils.OpenEpisodeEvent;
import dulleh.akhyou.Utils.SearchEvent;
import dulleh.akhyou.Utils.SearchSubmittedEvent;
import dulleh.akhyou.Utils.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.SnackbarEvent;

public class MainActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private RelativeLayout parentLayout;
    private String SEARCH_FRAGMENT;
    private String EPISODES_FRAGMENT;
    private String SETTINGS_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SEARCH_FRAGMENT = getString(R.string.search_fragment);
        EPISODES_FRAGMENT = getString(R.string.episodes_fragment);
        SETTINGS_FRAGMENT = getString(R.string.settings_fragment);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        fragmentManager = getSupportFragmentManager();
        parentLayout = (RelativeLayout) findViewById(R.id.container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
            onEvent(new FragmentRequestEvent(SEARCH_FRAGMENT));
        }

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

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent (SearchSubmittedEvent event) {
        MenuItem searchItem = event.searchItem;
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_item));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // can't use !#.isEmpty because requires api 9+
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

    static int selected = 0;
    public void onEvent (OpenEpisodeEvent event) {
        new MaterialDialog.Builder(this)
                .title(R.string.sources)
                .items(event.getSourcesAsCharSequenceArray())
                .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        selected = i;
                        return false;
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        onEvent(new SnackbarEvent( "STREAM" + event.sources.get(selected).getUrl(), Snackbar.LENGTH_LONG, null, null, null));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        onEvent(new SnackbarEvent( "DOWNLOAD" + event.sources.get(selected).getUrl(), Snackbar.LENGTH_LONG, null, null, null));
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        selected = 0;
                    }

                })
                .widgetColorRes(R.color.primary)
                .positiveText(R.string.stream)
                .positiveColorRes(R.color.primary)
                .negativeText(R.string.download)
                .negativeColorRes(R.color.primary)
                .neutralText(R.string.cancel)
                .neutralColorRes(R.color.grey_darkestXX)
                .show();
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
        } else if (event.tag.equals(SETTINGS_FRAGMENT)) {
            fragmentManager
                    .beginTransaction()
                    .addToBackStack(SEARCH_FRAGMENT)
                    .addToBackStack(EPISODES_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT)
                    .commit();
        }

    }

}