package dulleh.akhyou;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Episodes.EpisodesFragment;
import dulleh.akhyou.Search.SearchFragment;
import dulleh.akhyou.Utils.OpenAnimeEvent;
import dulleh.akhyou.Utils.SnackbarEvent;

public class MainActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private FrameLayout parentLayout;
    public String SEARCH_FRAGMENT = "SEA";
    public String EPISODES_FRAGMENT = "EPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        fragmentManager = getSupportFragmentManager();
        parentLayout = (FrameLayout) findViewById(R.id.container);

        if (savedInstanceState == null) {
            fragmentManager
                .beginTransaction()
                .add(R.id.container, new SearchFragment(), SEARCH_FRAGMENT)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    public void onEvent (OpenAnimeEvent event) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(SEARCH_FRAGMENT)
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                .replace(R.id.container, new EpisodesFragment(), EPISODES_FRAGMENT)
                .commit();
    }

}