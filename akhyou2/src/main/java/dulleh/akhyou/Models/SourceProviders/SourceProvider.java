package dulleh.akhyou.Models.SourceProviders;


import java.util.List;

import dulleh.akhyou.Models.Video;
import rx.exceptions.OnErrorThrowable;

public interface SourceProvider {

    List<Video> fetchSource (String embedPageUrl) throws OnErrorThrowable;

}
