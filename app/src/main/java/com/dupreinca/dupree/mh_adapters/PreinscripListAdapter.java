package com.dupreinca.dupree.mh_adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dupreeinca.lib_api_rest.model.dto.response.Preinscripcion;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemListPreinscripBinding;
import com.dupreinca.dupree.mh_holders.ListPreHolder;

import java.util.List;

/**
 * Created by marwuinh@gmail.com on 8/3/2017.
 */

public class PreinscripListAdapter extends RecyclerView.Adapter<ListPreHolder> {

    private List<Preinscripcion> list;
    private ListPreHolder.Events events;
    public PreinscripListAdapter(List<Preinscripcion> list, ListPreHolder.Events events){
        this.list = list;
        this.events = events;
    }

    @NonNull
    @Override
    public ListPreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        ItemListPreinscripBinding binding= DataBindingUtil.inflate(inflate, R.layout.item_list_preinscrip, parent, false);
        return new ListPreHolder(binding, events);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPreHolder holder, int position) {
        if(list.size() > position) {
            holder.bindData(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }
}
