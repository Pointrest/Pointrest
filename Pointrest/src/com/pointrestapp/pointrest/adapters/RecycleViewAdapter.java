package com.pointrestapp.pointrest.adapters;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {



    public static class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView imageView;
		public ViewHolder(View v) {
            super(v);
            imageView =(ImageView) v.findViewById(R.id.imageView1);
        }
    }

	private ArrayList<Integer> imageIndexArray;

    public RecycleViewAdapter(ArrayList<Integer> imageIndexArray) {
        this.imageIndexArray = imageIndexArray;
    }


	// Create new views (invoked by the layout manager)
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.detail_listview_image_cell, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(null);
        Glide.with(holder.imageView.getContext())
	    .load(Constants.BASE_URL + "immagini/" + imageIndexArray.get(position))
        .asBitmap()
	    .placeholder(R.drawable.loading)
	    .error(R.drawable.ic_cancel_black_36dp)
	    .into(holder.imageView);	

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return imageIndexArray.size();
    }
}