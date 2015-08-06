package dulleh.akhyou.Utils.Events;

public class SearchSubmittedEvent {
    public final String searchTerm;

    public SearchSubmittedEvent (String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
