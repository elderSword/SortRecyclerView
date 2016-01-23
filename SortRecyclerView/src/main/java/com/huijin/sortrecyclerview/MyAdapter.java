package com.huijin.sortrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by flaviusmester on 23/02/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements FastScrollRecyclerViewInterface, View.OnClickListener{
    private List<SortModel> mSourceDateList;

    public MyAdapter(List<SortModel> sourceDateList) {
        setHasStableIds(true);
        mSourceDateList = sourceDateList;
    }

    @Override
    public void onClick(View view) {

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        ...
        ViewHolder vh = new ViewHolder((TextView)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mSourceDateList.get(position).getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSourceDateList.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return calculateIndexesForName(mSourceDateList);
    }

    @Override
    public long getItemId(int position) {
        return getItemId(mSourceDateList.get(position).getName());
    }
    private long getItemId(String item) {
        return item.hashCode();
    }

    private HashMap<String, Integer> calculateIndexesForName(List<SortModel> items){
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i<items.size(); i++){
//            String name = items.get(i).getSortLetters();
//            String index = name.substring(0,1);
//            index = index.toUpperCase();
//
//            if (!mapIndex.containsKey(index)) {
//                mapIndex.put(index, i);
//            }
            mapIndex.put(items.get(i).getSortLetters(), i);
        }
        return mapIndex;
    }
}