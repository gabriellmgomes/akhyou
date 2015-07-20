package dulleh.akhyou.Episodes;

import android.os.Bundle;

import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Episodes.Providers.AnimeRushEpisodesProvider;
import dulleh.akhyou.Episodes.Providers.EpisodesProvider;
import dulleh.akhyou.Episodes.Providers.FakeEpisodesProvider;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Utils.OpenAnimeEvent;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class EpisodesPresenter extends RxPresenter<EpisodesFragment>{
    private Subscription subscription;
    private EpisodesProvider episodesProvider;
    private String lastUrl;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (episodesProvider == null) {
            episodesProvider = new AnimeRushEpisodesProvider();
        }

    }

    @Override
    protected void onTakeView(EpisodesFragment view) {
        super.onTakeView(view);
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        episodesProvider = null;
        unsubscribe();
    }

    private void unsubscribe () {
        subscription.unsubscribe();
    }

    public void onEvent (OpenAnimeEvent event) {
        this.lastUrl = event.anime.getUrl();
        fetchAnime();
    }

    public void fetchAnime () {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        subscription = Observable.defer(new Func0<Observable<Anime>>() {
            @Override
            public Observable<Anime> call() {
                return Observable.just(episodesProvider.fetchAnime(lastUrl));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliverLatestCache())
                .subscribe(new Subscriber<Anime>() {
                    @Override
                    public void onNext(Anime anime) {
                        getView().setAnime(anime);
                    }

                    @Override
                    public void onCompleted() {
                        getView().postSuccess();
                        getView().setRefreshingFalse();
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().postError(e.getMessage().replace("java.lang.Throwable:", "").trim());
                        getView().setRefreshingFalse();
                        unsubscribe();
                        e.printStackTrace();
                    }

                });
    }

}
