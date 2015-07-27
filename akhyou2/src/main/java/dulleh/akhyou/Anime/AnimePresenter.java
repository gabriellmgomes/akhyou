package dulleh.akhyou.Anime;

import android.os.Bundle;

import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.Providers.AnimeRushAnimeProvider;
import dulleh.akhyou.Anime.Providers.AnimeProvider;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Utils.OpenAnimeEvent;
import dulleh.akhyou.Utils.ToolbarTitleChangedEvent;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AnimePresenter extends RxPresenter<AnimeFragment>{
    private Subscription animeSubscription;
    private Subscription episodeSubscription;
    private Subscription videoSubscription;
    private AnimeProvider animeProvider;

    private String lastUrl;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (animeProvider == null) {
            animeProvider = new AnimeRushAnimeProvider();
        }

    }

    @Override
    protected void onTakeView(AnimeFragment view) {
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
        animeProvider = null;
        unsubscribe();
    }

    private void unsubscribe () {
        animeSubscription.unsubscribe();
        episodeSubscription.unsubscribe();
        videoSubscription.unsubscribe();
    }

    public void onEvent (OpenAnimeEvent event) {
        this.lastUrl = event.anime.getUrl();
        EventBus.getDefault().post(new ToolbarTitleChangedEvent(event.anime.getTitle()));
        fetchAnime();
    }

    public void fetchAnime () {
        if (animeSubscription != null) {
            if (!animeSubscription.isUnsubscribed()) {
                animeSubscription.unsubscribe();
            }
        }

        animeSubscription = Observable.defer(new Func0<Observable<Anime>>() {
            @Override
            public Observable<Anime> call() {
                return Observable.just(animeProvider.fetchAnime(lastUrl));
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
                        getView().setRefreshingFalse();
                        animeSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().postError("Error: " + e.getMessage().replace("java.lang.Throwable:", "").trim());
                        getView().setRefreshingFalse();
                        animeSubscription.unsubscribe();
                    }

                });
    }

    public void fetchSources (String url) {
        if (episodeSubscription != null) {
            if (!episodeSubscription.isUnsubscribed()) {
                episodeSubscription.unsubscribe();
            }
        }
        
        episodeSubscription = Observable.defer(new Func0<Observable<List<Source>>>() {
            @Override
            public Observable<List<Source>> call() {
                return Observable.just(animeProvider.fetchSources(url));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<List<Source>>() {
                    @Override
                    public void onNext(List<Source> sources) {
                        getView().showSourcesDialog(sources);
                    }

                    @Override
                    public void onCompleted() {
                        episodeSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().postError("Error: " + e.getMessage().replace("java.lang.Throwable:", "").trim());
                        episodeSubscription.unsubscribe();
                    }

                });
    }

    public void fetchVideo (Source source) {
        if (videoSubscription != null) {
            if (!videoSubscription.isUnsubscribed()) {
                videoSubscription.unsubscribe();
            }
        }

        videoSubscription = Observable.defer(new Func0<Observable<Source>>() {
            @Override
            public Observable<Source> call() {
                return Observable.just(animeProvider.fetchVideo(source));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<Source>() {
                    @Override
                    public void onNext(Source source) {
                        getView().shareVideo(source);
                    }

                    @Override
                    public void onCompleted() {
                        videoSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().postError("Error: " + e.getMessage().replace("java.lang.Throwable:", "").trim());
                        videoSubscription.unsubscribe();
                    }

                });
    }

}
