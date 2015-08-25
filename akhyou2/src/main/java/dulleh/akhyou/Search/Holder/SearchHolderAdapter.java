package dulleh.akhyou.Search.Holder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Search.Holder.Item.SearchFragment;

public class SearchHolderAdapter extends FragmentStatePagerAdapter{

    public SearchHolderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment searchFragment = new SearchFragment();
        Bundle args = new Bundle(1);


        switch(position) {
            case 0:
                args.putInt(SearchHolderFragment.PROVIDER_TYPE_KEY, Anime.ANIME_RUSH);
                break;
            case 1:
                args.putInt(SearchHolderFragment.PROVIDER_TYPE_KEY, Anime.ANIME_RAM);
                break;
            default: args.putInt(SearchHolderFragment.PROVIDER_TYPE_KEY, Anime.ANIME_RUSH);
        }

        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return Anime.ANIME_RUSH_TITLE;
            case 1:
                return Anime.ANIME_RAM_TITLE;
            default:
                return null;
        }
    }
}
