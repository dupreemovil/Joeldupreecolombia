package com.dupreinca.dupree.mh_adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemCatalogoBinding;
import com.dupreinca.dupree.databinding.ItemCatalogoMadrugonBindingImpl;
import com.dupreinca.dupree.mh_holders.MadrugonHolder;
import com.dupreinca.dupree.mh_holders.MadrugonHolder;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by marwuinh@gmail.com on 8/3/2017.
 */

public class MadrugonAdapter extends RecyclerView.Adapter<MadrugonHolder> {
    private String TAG = CarritoListAdapter.class.getName();

    private List<ItemCarrito> list;
    private MadrugonHolder.Events events;
    private boolean enable = true;
    private List<String> idEditable;//para saber si hay elementos que modificar

    public MadrugonAdapter(List<ItemCarrito> list, MadrugonHolder.Events events){
        this.list = list;
        this.events = events;
        idEditable=new ArrayList<>();
    }

    @NonNull
    @Override
    public MadrugonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        ItemCatalogoMadrugonBindingImpl binding = DataBindingUtil.inflate(inflate, R.layout.item_catalogo_madrugon, parent, false);
        return new MadrugonHolder(binding, events);
    }

    private int numEditable=0;
    @Override
    public void onBindViewHolder(@NonNull MadrugonHolder holder, int position) {
        if(list.size() > position) {
            holder.bindData(list.get(position), isEnable(), numEditable, idEditable);
        }
        numEditable = idEditable.size();



    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void clearEditable(){
        numEditable=0;
        idEditable.clear();
    }
}
