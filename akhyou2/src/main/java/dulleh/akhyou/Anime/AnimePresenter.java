package dulleh.akhyou.Anime;

import android.os.Bundle;

import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.Providers.AnimeRushAnimeProvider;
import dulleh.akhyou.Anime.Providers.AnimeProvider;
import dulleh.akhyou.MainActivity;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Utils.Events.FavouriteEvent;
import dulleh.akhyou.Utils.Events.LastAnimeEvent;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class AnimePresenter extends RxPresenter<AnimeFragment>{
    private static final String LAST_ANIME_BUNDLE_KEY = "last_anime";

    private Subscription animeSubscription;
    private Subscription episodeSubscription;
    private Subscription videoSubscription;
    private AnimeProvider animeProvider;

    private Anime lastAnime;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (animeProvider == null) {
            animeProvider = new AnimeRushAnimeProvider();
        }

        // subscribe here (rather than in onTakeView() so that we don't receive
        // a stickied event every time the motherfucker takes the view.
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }

    }

    @Override
    protected void onTakeView(AnimeFragment view) {
        super.onTakeView(view);
        if (lastAnime.getTitle() != null) {
            getView().setToolbarTitle(lastAnime.getTitle());
        }
        if (isRefreshing) {
            getView().setRefreshing(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        animeProvider = null;
        unsubscribe();
    }

    private void unsubscribe () {
        if (animeSubscription != null && !animeSubscription.isUnsubscribed()) {
            animeSubscription.unsubscribe();
        }
        if (episodeSubscription != null && !episodeSubscription.isUnsubscribed()) {
            episodeSubscription.unsubscribe();
        }
        if (videoSubscription != null && !videoSubscription.isUnsubscribed()) {
            videoSubscription.unsubscribe();
        }
    }

    public void onEvent (OpenAnimeEvent event) {
        lastAnime = new Anime();
        lastAnime.setUrl(event.anime.getUrl());
        // temporary title until rest of data has loaded so that users don't see a blank toolbar
        lastAnime.setTitle(event.anime.getTitle());
        fetchAnime();
    }

    public void fetchAnime () {
        if (getView() != null && !getView().isRefreshing()) {
            getView().setRefreshing(true);
        }
        isRefreshing = true;

        if (animeSubscription != null) {
            if (!animeSubscription.isUnsubscribed()) {
                animeSubscription.unsubscribe();
            }
        }

        animeSubscription = Observable.defer(new Func0<Observable<Anime>>() {
            @Override
            public Observable<Anime> call() {
                return Observable.just(animeProvider.fetchAnime(lastAnime.getUrl()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliverLatestCache())
                .subscribe(new Subscriber<Anime>() {
                    @Override
                    public void onNext(Anime anime) {
                        lastAnime = anime;
                        getView().setAnime(lastAnime, inInFavourites());
                        EventBus.getDefault().post(new LastAnimeEvent(lastAnime));
                        isRefreshing = false;
                    }

                    @Override
                    public void onCompleted() {
                        getView().setRefreshing(false);
                        animeSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().postError(GeneralUtils.formatError(e));
                        getView().setRefreshing(false);
                        animeSubscription.unsubscribe();
                    }

                });
    }

    public boolean inInFavourites () {
        return ((MainActivity) getView().getActivity()).getPresenter().isInFavourites(new Anime().setTitle(lastAnime.getTitle()).setUrl(lastAnime.getUrl()));
    }

    public void onFavouriteCheckedChanged (boolean b) {
        EventBus.getDefault().post(new FavouriteEvent(b, new Anime()
                .setTitle(lastAnime.getTitle())
                .setUrl(lastAnime.getUrl())));
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
                        getView().postError(GeneralUtils.formatError(e));
                        episodeSubscription.unsubscribe();
                    }

                });
    }

    public void fetchVideo (Source source, boolean download) {
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
                        getView().shareVideo(source, download);
                    }

                    @Override
                    public void onCompleted() {
                        videoSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().postError(GeneralUtils.formatError(e));
                        videoSubscription.unsubscribe();
                    }

                });
    }

}
