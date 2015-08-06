package dulleh.akhyou.Anime;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterClickListener;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder>{
    private List<Episode> episodes;
    private final AdapterClickListener<Episode> adapterClickListener;
    //private final int watchedColor;

    public AnimeAdapter(List<Episode> episodes, AdapterClickListener<Episode> episodeSelectedListener, int watchedColor) {
        this.episodes = episodes;
        this.adapterClickListener = episodeSelectedListener;
        //this.watchedColor = watchedColor;
    }

    public void setAnime (List<Episode> episodes) {
        this.clear();
        this.episodes = episodes;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.episodes = new ArrayList<>(0);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public ViewHolder (View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.episode_title_view);
        }
    }

    @Override
    public AnimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.episode_item, parent, false);
        return new AnimeAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AnimeAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.titleView.setText(episodes.get(position).getTitle());
        /*
        if (episodes.get(position).isWatched()) {
            viewHolder.titleView.setTextColor(watchedColor);
        }
        */
        viewHolder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterClickListener.onCLick(episodes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void setWatched (int position) {
        episodes.set(position, episodes.get(position).setWatched(true));
        //this.notifyItemChanged(position);
    }

    public Episode getItemAtPosition (int position) {
        if (episodes != null) {
            return episodes.get(position);
        }
        return null;
    }

}
