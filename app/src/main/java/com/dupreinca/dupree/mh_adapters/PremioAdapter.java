package com.dupreinca.dupree.mh_adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dupreeinca.lib_api_rest.model.dto.response.TipoConDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemPremioBindingImpl;
import com.dupreinca.dupree.databinding.ItemResumenBindingImpl;
import com.dupreinca.dupree.mh_holders.CarritoHolderM;
import com.dupreinca.dupree.mh_holders.ConcursosHolder;

import java.util.ArrayList;
import java.util.List;

public class PremioAdapter extends RecyclerView.Adapter<ConcursosHolder> {
    private String TAG = PremioAdapter.class.getName();

    private List<TipoConDTO> list;

    private boolean enable = true;
    private List<String> idEditable;//para saber si hay elementos que modificar

    public PremioAdapter(List<TipoConDTO> list){
        this.list = list;

        idEditable=new ArrayList<>();
    }

    @NonNull
    @Override
    public ConcursosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        ItemPremioBindingImpl binding = DataBindingUtil.inflate(inflate, R.layout.item_premio, parent, false);
        return new ConcursosHolder(binding);
    }

    private int numEditable=0;
    @Override
    public void onBindViewHolder(@NonNull ConcursosHolder holder, int position) {
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

