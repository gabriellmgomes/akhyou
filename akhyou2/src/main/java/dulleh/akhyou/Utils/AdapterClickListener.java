package dulleh.akhyou.Utils;

import android.support.annotation.Nullable;

public interface AdapterClickListener<M> {

    void onCLick (M item, @Nullable Integer position);

    void onLongClick (M item, @Nullable Integer position);

}
