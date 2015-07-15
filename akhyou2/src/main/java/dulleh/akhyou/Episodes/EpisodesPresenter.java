package dulleh.akhyou.Episodes;

import android.os.Bundle;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Episodes.Providers.EpisodesProvider;
import dulleh.akhyou.Episodes.Providers.FakeEpisodesProvider;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.OpenAnimeEvent;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EpisodesPresenter extends RxPresenter<EpisodesFragment>{
    private Subscription subscription;
    private EpisodesProvider episodesProvider;
    private String lastUrl;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (episodesProvider == null) {
            episodesProvider = new FakeEpisodesProvider();
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

        subscription = Observable.just(episodesProvider.fetchAnime(lastUrl))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliverLatestCache())
                .subscribe(new Subscriber<Anime>() {
                    @Override
                    public void onNext(Anime anime) {
                        getView().postSuccess();
                        getView().setAnime(anime);
                    }

                    @Override
                    public void onCompleted() {
                        getView().setRefreshingFalse();
                        subscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().postError();
                        getView().setRefreshingFalse();
                        e.printStackTrace();
                    }

                });
    }

}
