package com.example.abhilash.popularmovie;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomGrid extends ArrayAdapter<MainActivity.movie> {

    Context context;
    int resource;
    ArrayList<MainActivity.movie> mov = new ArrayList<MainActivity.movie>();

    public CustomGrid(Context context, int resource, ArrayList<MainActivity.movie> mov) {
        super(context, resource, mov);
        this.context = context;
        this.resource = resource;
        this.mov = mov;
    }

    public void setGridData(ArrayList<MainActivity.movie> mov) {
        this.mov = mov;
        notifyDataSetChanged();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View convertview = convertView;
        ViewHolder holder;

        if (convertview == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertview = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.imageview = (ImageView) convertview.findViewById(R.id.grid_image);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MainActivity.movie movie = mov.get(position);

        Picasso.with(context).load(movie.getPoster()).into(holder.imageview);

        return convertview;

    }

    static class ViewHolder {
        public ImageView imageview;
    }

}
