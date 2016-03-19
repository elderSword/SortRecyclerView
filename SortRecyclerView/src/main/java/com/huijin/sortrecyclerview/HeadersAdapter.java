package com.huijin.sortrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by maciejwitowski on 3/14/15.
 */
public class HeadersAdapter implements StickyHeadersAdapter<HeadersAdapter.HeaderViewHolder> {

    private List<SortModel> items = new ArrayList<>();


    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.header_view, viewGroup, false);

        return new HeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder headerViewHolder, int position) {
        SortModel item = items.get(position);
        if(item != null) {
            headerViewHolder.title.setText(item.getSortTitle());
        }

    }

    @Override
    public long getHeaderId(int position) {
        return getHeaderId(items.get(position).getSortLetters());
    }


    public long getHeaderId(String item) {
        return item.charAt(0);
    }

    public void replaceItems(List<SortModel> newWords) {
        if(newWords != null) {
            items = newWords;
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
