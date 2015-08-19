package dulleh.akhyou.Search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.SearchProviders.AnimeRushSearchProvider;
import dulleh.akhyou.Models.SearchProviders.SearchProvider;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterClickListener;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private AdapterClickListener<Anime> adapterClickListener;

    public SearchAdapter (AdapterClickListener<Anime> adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.title_view);
            descView = (TextView) v.findViewById(R.id.desc_view);
            imageView = (ImageView) v.findViewById(R.id.image_view);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.search_card, parent, false);
        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Anime anime = SearchPresenter.searchResultsCache.get(position);
        viewHolder.titleView.setText(anime.getTitle());
        viewHolder.descView.setText(anime.getDesc());

        Picasso.with(context)
                .load(anime.getImageUrl())
                .error(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterClickListener.onCLick(SearchPresenter.searchResultsCache.get(position), null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return SearchPresenter.searchResultsCache.size();
    }
}
