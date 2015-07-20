package dulleh.akhyou.Episodes;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.OpenEpisodeEvent;
import dulleh.akhyou.Utils.SnackbarEvent;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>{
    private List<Episode> episodes;

    public EpisodesAdapter (List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setEpisodes (List<Episode> episodes) {
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
    public EpisodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.episode_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EpisodesAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.titleView.setText(episodes.get(position).getTitle());
        viewHolder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new OpenEpisodeEvent(episodes.get(position).getSources()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.episodes.size();
    }

}
