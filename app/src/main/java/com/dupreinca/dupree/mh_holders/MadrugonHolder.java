package com.dupreinca.dupree.mh_holders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemCatalogoBinding;
import com.dupreinca.dupree.databinding.ItemCatalogoMadrugonBindingImpl;
import com.dupreinca.dupree.view.activity.BaseActivity;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class MadrugonHolder extends RecyclerView.ViewHolder{
    private String TAG = CarritoHolder.class.getName();
    private ItemCatalogoMadrugonBindingImpl binding;
    private Events events;

    public MadrugonHolder(@NonNull ItemCatalogoMadrugonBindingImpl binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final ItemCarrito item, boolean isEnable, int numEditable, List<String> idEditable) {
        binding.imgBAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null && isEnable){
                    //setPosSelected(getAdapterPosition());
                    events.onAddCartMClick(item, getAdapterPosition());
                }
            }
        });
        binding.imgBDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null && isEnable){
                    //setPosSelected(getAdapterPosition());
                    events.onDecreaseMClick(item, getAdapterPosition());
                }
            }
        });
        binding.imgBIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null && isEnable){
                    //setPosSelected(getAdapterPosition());
                    events.onIncreaseMClick(item, getAdapterPosition());
                }
            }
        });

        binding.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null && isEnable){
                    events.onAddCartMClick(item, getAdapterPosition());
                }
            }
        });


        Resources resources = BaseAPP.getContext().getResources();

        Realm realm = Realm.getInstance(BaseAPP.getContext1());
      //  System.out.println("la lista "+ item.getTallas().get(0).getTalla());
        List<String> lista = new ArrayList<>();

        if(!realm.isClosed()){

            if(item.getTallas().size()>0){
                System.out.println("El size "+item.getTallas().size());
                for(int j=0;j<item.getTallas().size();j++){

                    lista.add(item.getTallas().get(j));
                }

            }
        }




        binding.spinnersize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                System.out.println("La pos "+lista.get(position));
                item.setTalla(lista.get(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


     //   item.setTalla(lista.get(0));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BaseAPP.getContext(),
                android.R.layout.simple_list_item_1,
                lista );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnersize.setAdapter(adapter);

        binding.tvPage.setText(resources.getString(R.string.concat_pagina, item.getPage()));
        binding.tvCode.setText(resources.getString(R.string.concat_codigo, item.getId()));
        binding.tvDescription.setText(item.getName());

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        binding.tvValor.setText(resources.getString(R.string.concat_dolar, formatter.format(Float.parseFloat(item.getValor()))));

        binding.tvCantidad.setText(String.valueOf(item.getCantidad()));

        System.out.println("La talla "+item.getTalla());

        if(!realm.isClosed()) {
            for (int j = 0; j < item.getTallas().size(); j++) {

                System.out.println("La talla sel " + item.getTalla() + " tallas act " + item.getTallas().get(j));
                if (item.getTallasel().equals(item.getTallas().get(j))) {
                    System.out.println("La posicion de talla " + j);
                    binding.spinnersize.setSelection(j);
                }
            }
        }



        //validar si ya esta (o no) en el carrito.
        binding.ctnIncDecCant.setVisibility(item.getCantidad()>0 ? View.VISIBLE : View.GONE);
        binding.ctnAddCart.setVisibility(item.getCantidad()>0 ? View.GONE : View.VISIBLE);

        binding.imgBDecrease.setColorFilter(isEnable ? resources.getColor(R.color.azulDupree) :resources.getColor(R.color.gray_5),android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.imgBIncrease.setColorFilter(isEnable ? resources.getColor(R.color.azulDupree) :resources.getColor(R.color.gray_5),android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.imgBAddCart.setColorFilter(isEnable ? resources.getColor(R.color.azulDupree) :resources.getColor(R.color.gray_5),android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.tvAddCart.setTextColor(isEnable ? resources.getColor(R.color.azulDupree) : resources.getColor(R.color.gray_5));

        if(item.getCantidad() == item.getCantidadServer()) {
            //SON IGUALES, NO HAY CAMBIO
            if(item.getCantidad()>=1) {// no esta en el server
                binding.imagen.setImageResource(R.drawable.ic_flor180x180);//no hay cambios


                binding.tvStatus.setText("");
                binding.tvStatus.setTextColor(resources.getColor(R.color.azulDupree));

            }else{// si esta en el server pero no hay cambios
                binding.imagen.setImageResource(R.drawable.ic_flor180x180);//no hay cambior
                binding.tvStatus.setText("");
                binding.tvStatus.setTextColor(resources.getColor(R.color.azulDupree));

            }

            removeEditable(idEditable, item.getId());
        }
        else if (item.getCantidad() != item.getCantidadServer())
        {
            if (item.getCantidad() == 0 && item.getCantidadServer() >= 1) {
                binding.imagen.setImageResource(R.drawable.ic_flor_red_180x180);//eliminar
                binding.tvStatus.setText(R.string.eliminar);
                binding.tvStatus.setTextColor(resources.getColor(R.color.red_1));

            } else if (item.getCantidad() >= 1 && item.getCantidadServer() == 0) {
                binding.imagen.setImageResource(R.drawable.ic_flor_pick_180x180);//agregar
                binding.tvStatus.setText(R.string.aniadir);
                binding.tvStatus.setTextColor(resources.getColor(R.color.colorAccent));

            } else {
                binding.imagen.setImageResource(R.drawable.ic_flor_orange_180x180);//editar
                binding.tvStatus.setText(R.string.editar);
                binding.tvStatus.setTextColor(resources.getColor(R.color.orange_600));

            }



            addEditable(idEditable, item.getId());
        }

        System.out.println("Imagen mad "+item.getUrl_img());

        try {
            Glide.with(BaseAPP.getContext())
                    .load(item.getUrl_img())

                    .fitCenter()

                    .into(binding.imagen);
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Exc "+e.getMessage());

        }

        binding.imagezoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertadd = new AlertDialog.Builder(view.getContext());
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View view1 = factory.inflate(R.layout.image_zoom, null);
                alertadd.setView(view1);

                ImageView imgzoom = (ImageView)view1.findViewById(R.id.dialog_imageview);

                try {
                    Glide.with(BaseAPP.getContext())
                            .load(item.getUrl_img())

                            .fitCenter()

                            .into(imgzoom);
                } catch (Exception e) {
                    e.printStackTrace();

                    System.out.println("Exc img "+e.getMessage());

                }

                alertadd.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                });

                AlertDialog alertDialog = alertadd.create();
                alertDialog.show();







            }
        });


        Log.e(TAG, "num. Items editable= "+String.valueOf(idEditable.size()));

        if(numEditable==0 && idEditable.size()==1){
            Log.e(TAG, "num. Items editable = SI EDITAR VARIABLE=true");
            if(events != null) {
                Log.e(TAG, "num. Items editable = SI EVENTS");
                events.onAddEditableMClick(true);
            }
        } else if(numEditable==1 && idEditable.size()==0){
            Log.e(TAG, "num. Items editable = NO EDITAR");
            if(events != null)
                events.onAddEditableMClick(false);
        }
    }

    private void addEditable(List<String> idEditable, String id){
        for(int i=0; i<idEditable.size();i++){
            if(idEditable.get(i).equals(id)){
                return;
            }
        }
        idEditable.add(id);//significa que este id se debe modificar
    }

    private void removeEditable(List<String> idEditable, String id){
        for(int i=0; i<idEditable.size();i++){
            if(idEditable.get(i).equals(id)){
                idEditable.remove(i);
            }
        }
    }

    public interface Events{
        void onAddCartMClick(ItemCarrito dataRow, int row);

        void onIncreaseMClick(ItemCarrito dataRow, int row);
        void onDecreaseMClick(ItemCarrito dataRow, int row);

        void onAddEditableMClick(boolean isEditable);
    }
}
