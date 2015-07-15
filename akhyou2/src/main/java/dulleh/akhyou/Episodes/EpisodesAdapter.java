package dulleh.akhyou.Episodes;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.SnackbarEvent;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>{
    private Episode[] episodes;

    public EpisodesAdapter (Episode[] episodes) {
        this.episodes = episodes;
    }

    public void setEpisodes (Episode[] episodes) {
        this.clear();
        this.episodes = episodes;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.episodes = new Episode[0];
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
        viewHolder.titleView.setText(episodes[position].getTitle());
        viewHolder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SnackbarEvent(episodes[position].getSources()[0].getTitle(), Snackbar.LENGTH_LONG, null, null, null));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.episodes.length;
    }

}
