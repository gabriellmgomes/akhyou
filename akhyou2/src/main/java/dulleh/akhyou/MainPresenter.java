package dulleh.akhyou;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.HummingbirdApi;
import dulleh.akhyou.Utils.Events.FavouriteEvent;
import dulleh.akhyou.Utils.Events.LastAnimeEvent;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SearchEvent;
import dulleh.akhyou.Utils.Events.SearchSubmittedEvent;
import dulleh.akhyou.Utils.Events.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import dulleh.akhyou.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxPresenter<MainActivity>{
    private static final String FAVOURITES_KEY = "favourites_key";

    private MainModel mainModel;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        EventBus.getDefault().register(this);

        if (savedState != null) {
            //favouritesList = savedState.getParcelableArrayList(FAVOURITES_KEY);
            mainModel.favouritesList = savedState.getParcelableArrayList(FAVOURITES_KEY);
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        //if (favouritesList != null) {
        //    state.putParcelableArrayList(FAVOURITES_KEY, favouritesList);
        //}
        if (mainModel.favouritesList != null) {
            state.putParcelableArrayList(FAVOURITES_KEY, mainModel.favouritesList);
        }
        mainModel.saveFavourites();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
    }

    // must be done every time activity onCreate()
    public void setSharedPreferences (SharedPreferences sharedPreferences) {
        if (mainModel != null) {
            mainModel.setSharedPreferences(sharedPreferences);
        } else {
            mainModel = new MainModel(sharedPreferences);
        }
    }

    public MainModel getModel () {
        return mainModel;
    }

    public void launchFromHbLink (String url) {

        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(HummingbirdApi.getTitle(url));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String title) {
                        EventBus.getDefault().postSticky(new SearchEvent(title));
                    }

                    @Override
                    public void onCompleted() {
                        getView().requestFragment(MainActivity.SEARCH_FRAGMENT);
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showSnackBar(new SnackbarEvent(GeneralUtils.formatError(e)));
                        this.unsubscribe();
                    }
                });

    }

    public void refreshFavouritesList () {
        mainModel.refreshFavouritesList();
    }

    public List<Anime> getFavourites () {
        return mainModel.favouritesList;
    }

    // Must have run setSharedPreferences() before this.
    public void onFreshStart (MainActivity mainActivity) {
        String lastAnimeTitle = mainModel.getLastAnimeTitle();
        String lastAnimeUrl = mainModel.getLastAnimeUrl();

        if (lastAnimeTitle != null && lastAnimeUrl != null) {
            EventBus.getDefault().postSticky(new OpenAnimeEvent(new Anime().setTitle(lastAnimeTitle).setUrl(lastAnimeUrl)));
            mainActivity.requestFragment(MainActivity.ANIME_FRAGMENT);
        } else {
            EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
            mainActivity.requestFragment(MainActivity.SEARCH_FRAGMENT);
        }
        //refreshFavouritesList();
        mainModel.refreshFavouritesList();
    }

    public void onEvent (FavouriteEvent event) {
        // colors are inconsistent for whatever reason, causing duplicate favourites,
        // so Set is pretty useless ;-;

        try {
            mainModel.addOrRemoveFromFavourites(event);

            if (getView() != null) {
                getView().favouritesChanged();
            }

        } catch (Exception e) {
            postError(e);
        }

    }

    public void onEvent (LastAnimeEvent event) {
        mainModel.saveNewLastAnime(event);
    }


    public void onEvent (SearchSubmittedEvent event) {
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.ANIME_FRAGMENT) != null) {
            getView().getSupportFragmentManager().popBackStack();
        }
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.SEARCH_FRAGMENT) == null) {
            getView().requestFragment(MainActivity.SEARCH_FRAGMENT);
        }
        EventBus.getDefault().postSticky(new SearchEvent(event.searchTerm));
    }

    public void onEvent (SettingsItemSelectedEvent event) {
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.SETTINGS_FRAGMENT) == null) {
            getView().requestFragment(MainActivity.SETTINGS_FRAGMENT);
        }
        getView().closeDrawer();
    }

    public void onEvent (SnackbarEvent event) {
        getView().showSnackBar(event);
    }

    public void postError (Throwable e) {
        e.printStackTrace();
        EventBus.getDefault().post(new SnackbarEvent(GeneralUtils.formatError(e)));
    }

}
