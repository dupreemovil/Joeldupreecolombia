package com.dupreinca.dupree.mh_adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemCatalogoBinding;
import com.dupreinca.dupree.databinding.ItemCatalogoBindingImpl;
import com.dupreinca.dupree.databinding.ItemResumenBindingImpl;
import com.dupreinca.dupree.mh_holders.CarritoHolder;
import com.dupreinca.dupree.mh_holders.CarritoHolderM;
import com.dupreinca.dupree.mh_holders.CarritoHolderP;

import java.util.ArrayList;
import java.util.List;


public class CarritoPListAdapter extends RecyclerView.Adapter<CarritoHolderP> {
    private String TAG = CarritoListAdapter.class.getName();

    private List<ItemCarrito> list;

    private boolean enable = true;
    private List<String> idEditable;//para saber si hay elementos que modificar

    public CarritoPListAdapter(List<ItemCarrito> list){
        this.list = list;

        idEditable=new ArrayList<>();
    }


    @NonNull
    @Override
    public CarritoHolderP onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        ItemResumenBindingImpl binding = DataBindingUtil.inflate(inflate, R.layout.item_resumen, parent, false);
        return new CarritoHolderP(binding);
    }

    private int numEditable=0;
    @Override
    public void onBindViewHolder(@NonNull CarritoHolderP holder, int position) {
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
