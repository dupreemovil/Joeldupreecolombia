package com.dupreinca.dupree.mh_adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dupreeinca.lib_api_rest.model.dto.response.Tracking;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemTrackingBinding;
import com.dupreinca.dupree.mh_holders.TrackingHolder;

import java.util.List;

/**
 * Created by Marwuin on 8/3/2017.
 */

public class TrackingListAdapter extends RecyclerView.Adapter<TrackingHolder> {

    private List<Tracking> list;
    private TrackingHolder.Events events;

    public TrackingListAdapter(List<Tracking> listFilter, TrackingHolder.Events events){
        this.list = listFilter;
        this.events = events;
    }

    @NonNull
    @Override
    public TrackingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTrackingBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_tracking, parent, false);
        return new TrackingHolder(binding, events);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingHolder holder, int position) {
        if(list.size() > position) {
            if(holder!=null){

                if(list.get(position)!=null){
                    holder.bindData(list.get(position));
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

}
