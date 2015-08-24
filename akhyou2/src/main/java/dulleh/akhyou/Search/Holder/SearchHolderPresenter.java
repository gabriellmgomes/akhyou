package dulleh.akhyou.Search.Holder;

import android.os.Bundle;

import de.greenrobot.event.EventBus;
import nucleus.presenter.Presenter;

public class SearchHolderPresenter extends Presenter<SearchHolderFragment> {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        //subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unsubscribe();
    }

    private void subscribe () {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
}

    private void unsubscribe() {
        EventBus.getDefault().unregister(this);
    }

}
