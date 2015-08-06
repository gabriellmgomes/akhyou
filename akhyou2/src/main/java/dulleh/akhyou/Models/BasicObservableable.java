package dulleh.akhyou.Models;

public interface BasicObservableable {
    // to be used with GeneralUtils.basicAsyncObservable

    /**
     * Return true for success
     * and false for error/failure
     */
    Boolean execute (String String);

    String executeForString (String String);

}
