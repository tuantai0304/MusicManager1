package com.example.tuantai.musicmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by TuanTai on 5/05/2016.
 */
public class MusicItemsAdapter extends ArrayAdapter<MusicItem>{

    MusicItem[] arrMusicItems;

    public MusicItemsAdapter(Context context, int resource, MusicItem[] objects) {
        super(context, resource, objects);
        this.arrMusicItems = objects;
    }

    class ViewHolder {
        TextView titleEditText;
        TextView durationEditText;
        ImageView imageView;

        public ViewHolder(View v) {
            titleEditText = (TextView) v.findViewById(R.id.titleEditText);
            durationEditText = (TextView) v.findViewById(R.id.durationEditText);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder vh = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row, parent, false);

            vh = new ViewHolder(row);
            row.setTag(vh);
        }
        else {
            vh = (ViewHolder) row.getTag();
        }

        vh.titleEditText.setText(arrMusicItems[position].title);
        vh.durationEditText.setText(arrMusicItems[position].duration);

        return row;
    }
}
