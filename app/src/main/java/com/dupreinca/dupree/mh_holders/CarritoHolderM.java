package com.dupreinca.dupree.mh_holders;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemResumenBindingImpl;
import com.dupreinca.dupree.databinding.ItemCatalogoMadrugonBindingImpl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarritoHolderM extends RecyclerView.ViewHolder{
    private String TAG = CarritoHolderM.class.getName();
    private ItemResumenBindingImpl binding;


    public CarritoHolderM(@NonNull ItemResumenBindingImpl binding) {
        super(binding.getRoot());
        this.binding = binding;

    }

    public void bindData(final ItemCarritoM item, boolean isEnable, int numEditable, List<String> idEditable) {


        Resources resources = BaseAPP.getContext().getResources();



        binding.tvCode.setText(resources.getString(R.string.concat_codigo, item.getId()));
        binding.tvDescription.setText(item.getName());

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        binding.tvValor.setText(resources.getString(R.string.concat_dolar, formatter.format(Float.parseFloat(item.getValor()))));

        binding.tvCantidad.setText("Cant. "+String.valueOf(item.getCantidad()));
        System.out.println("cANTIDAD "+item.getCantidad());

        binding.tvOferta.setVisibility(item.getType() == ItemCarrito.TYPE_OFFERTS ? View.VISIBLE : View.GONE);

        //validar si ya esta (o no) en el carrito.
     //   binding.ctnIncDecCant.setVisibility(item.getCantidad()>0 ? View.VISIBLE : View.GONE);




    }




}
