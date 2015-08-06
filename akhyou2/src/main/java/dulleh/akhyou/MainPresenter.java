package dulleh.akhyou;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.HummingbirdApi;
import dulleh.akhyou.Utils.Events.SearchEvent;
import dulleh.akhyou.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxPresenter<MainActivity>{

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
                        getView().postError(GeneralUtils.formatError(e));
                        this.unsubscribe();
                    }
                });

    }

}
