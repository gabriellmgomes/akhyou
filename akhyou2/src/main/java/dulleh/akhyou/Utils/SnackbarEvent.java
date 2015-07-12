package dulleh.akhyou.Utils;

import android.view.View;

public class SnackbarEvent {
    public final String message;
    public final int duration;
    public final String actionTitle;
    public final android.view.View.OnClickListener onClickListener;
    public final Integer actionColor;

    public SnackbarEvent (String message, int duration, String actionTitle, View.OnClickListener onClickListener, Integer actionColor) {
        this.message = message;
        this.duration = duration;
        this.actionTitle = actionTitle;
        this.onClickListener = onClickListener;
        this.actionColor = actionColor;
    }

}
