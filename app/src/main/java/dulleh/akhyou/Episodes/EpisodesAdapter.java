package dulleh.akhyou.Episodes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dulleh.akhyou.Episodes.EpisodeObject;
import dulleh.akhyou.R;

public class EpisodesAdapter extends ArrayAdapter<EpisodeObject> {
    private final Context context;

    static class ViewHolderItem {
        TextView textView;
    }

    public EpisodesAdapter(Context context, ArrayList<EpisodeObject> arrayList) {

        super(context, R.layout.episode_item, arrayList);
        this.context = context;

    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.episode_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.textView = (TextView) rowView.findViewById(R.id.episodeTitleTV);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) rowView.getTag();
        }

        viewHolder.textView.setText(getItem(position).getEpisodeTitle());

        return rowView;

    }

}