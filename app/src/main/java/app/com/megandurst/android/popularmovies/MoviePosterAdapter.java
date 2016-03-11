package app.com.megandurst.android.popularmovies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviePosterAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> items;

    public MoviePosterAdapter(Context context, ArrayList<String> items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView img;
        if (convertView == null) {
            img = new ImageView(context);
            img.setAdjustViewBounds(true);
            img.setPadding(0, 0, 0, 0);
        } else {
            img = (ImageView) convertView;
        }

        Picasso.with(context)
                .load((String) getItem(position))
                .into(img);

        return img;
    }
}
