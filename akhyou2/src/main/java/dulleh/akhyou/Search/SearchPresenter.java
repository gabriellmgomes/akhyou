package dulleh.akhyou.Search;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Search.Providers.AnimeRushSearchProvider;
import dulleh.akhyou.Search.Providers.SearchProvider;
import dulleh.akhyou.Utils.Events.SearchEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import dulleh.akhyou.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class SearchPresenter extends RxPresenter<SearchFragment> {
    private Subscription subscription;
    private SearchProvider searchProvider;

    private String searchTerm;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (searchProvider == null) {
            searchProvider = new AnimeRushSearchProvider();
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }

    }

    @Override
    protected void onTakeView(SearchFragment view) {
        super.onTakeView(view);
        if (isRefreshing) {
            view.setRefreshing(true);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchProvider = null;
        unsubscribe();
    }

    private void unsubscribe () {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void onEvent (SearchEvent event) {
        this.searchTerm = event.searchTerm;
        search();
    }

    public void search () {
        if (getView() != null && !getView().isRefreshing()) {
            getView().setRefreshing(true);
        }
        isRefreshing = true;

        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                unsubscribe();
            }
        }

        subscription = Observable.defer(new Func0<Observable<List<Anime>>>() {
            @Override
            public Observable<List<Anime>> call() {
                return Observable.just(searchProvider.searchFor(searchTerm));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<List<Anime>>() {
                    @Override
                    public void onNext(List<Anime> animes) {
                        isRefreshing = false;
                        if (getView() != null) {
                            getView().setSearchResults(animes);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().setSearchResults(new ArrayList<>(0));
                        }
                        postError(e);
                        unsubscribe();
                        e.printStackTrace();
                    }
                });
    }

    public void postError (Throwable e) {
        e.printStackTrace();
        EventBus.getDefault().post(new SnackbarEvent(GeneralUtils.formatError(e)));
    }

}
