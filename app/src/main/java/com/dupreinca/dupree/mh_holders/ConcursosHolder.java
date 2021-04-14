package com.dupreinca.dupree.mh_holders;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dupreeinca.lib_api_rest.model.dto.response.TipoConDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;

import com.dupreinca.dupree.databinding.ItemPremioBindingImpl;
import com.dupreinca.dupree.databinding.ItemResumenBindingImpl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ConcursosHolder extends RecyclerView.ViewHolder{
    private String TAG = ConcursosHolder.class.getName();
    private ItemPremioBindingImpl binding;



    public ConcursosHolder(@NonNull ItemPremioBindingImpl binding) {
        super(binding.getRoot());
        this.binding = binding;

    }

    public void bindData(final TipoConDTO item, boolean isEnable, int numEditable, List<String> idEditable) {


        Resources resources = BaseAPP.getContext().getResources();



        binding.txtdescripcion.setText(item.getDescripcion());
        binding.txtpuntos.setText(item.getPuntos());
        try {
            Glide.with(BaseAPP.getContext())
                    .load(item.getImagen_premio())

                    .fitCenter()

                    .into(binding.imagen);
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Exc "+e.getMessage());

        }



        //validar si ya esta (o no) en el carrito.
        //   binding.ctnIncDecCant.setVisibility(item.getCantidad()>0 ? View.VISIBLE : View.GONE);




    }




}
