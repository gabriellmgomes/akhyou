package dulleh.akhyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<AnimeObject>{
    private final Context context;

    public SearchAdapter(Context context, ArrayList<AnimeObject> animeObjects) {
        super(context, R.layout.search_view_card, animeObjects);
        this.context = context;
    }

    static class ViewHolderItem {
        TextView titleTextView;
        TextView descTextView;
        ImageView imageView;
    }
    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_view_card, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
            viewHolder.descTextView = (TextView) rowView.findViewById(R.id.descTextView);
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageView);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) rowView.getTag();
        }

        viewHolder.titleTextView.setText(getItem(position).getTitle());
        
       // for (String s : getItem(position).getGenreList()) {
//        }

        float d = this.context.getResources().getDisplayMetrics().density;

        String imageURl = getItem(position).getImageLink();
        Picasso.with(context)
                .load(imageURl)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(R.drawable.error_image)
                .resize((int) (224 * d * 0.5) , (int) (300 * d * 0.5))
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.descTextView.setText(getItem(position).getDesc());
        
        return rowView;
    }

}
