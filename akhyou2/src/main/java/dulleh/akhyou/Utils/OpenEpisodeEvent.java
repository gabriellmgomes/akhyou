package dulleh.akhyou.Utils;

import java.util.List;

import dulleh.akhyou.Models.Source;

public class OpenEpisodeEvent {
    public List<Source> sources;

    public OpenEpisodeEvent (List<Source> sources) {
        this.sources = sources;
    }

    public CharSequence[] getSourcesAsCharSequenceArray () {
        CharSequence[] sourcesAsArray = new CharSequence[sources.size()];
        for (int i = 0; i < sources.size(); i++) {
             sourcesAsArray[i] = sources.get(i).getTitle();
        }
        return sourcesAsArray;
    }

}
