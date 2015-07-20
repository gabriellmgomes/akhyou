package dulleh.akhyou.Utils;

import android.view.MenuItem;

public class SearchSubmittedEvent {
    public final String searchTerm;
    public final MenuItem searchItem;

    public SearchSubmittedEvent (String searchTerm, MenuItem searchItem) {
        this.searchTerm = searchTerm;
        this.searchItem = searchItem;
    }
}
