package com.dupreinca.dupree.mh_holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dupreeinca.lib_api_rest.enums.EnumTracking;
import com.dupreeinca.lib_api_rest.model.dto.response.Tracking;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemFaltantesBinding;
import com.dupreinca.dupree.databinding.ItemTrackingBinding;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class TrackingHolder extends RecyclerView.ViewHolder{
    private ItemTrackingBinding binding;
    private Events events;
    private String[] title ={EnumTracking.RECIBIDO.getKey(),EnumTracking.FACTURADO.getKey(), EnumTracking.EMBALADO.getKey(), EnumTracking.DESPACHADO.getKey(),EnumTracking.PLATAFORMA.getKey(), EnumTracking.REPARTO.getKey(), EnumTracking.PENTREGA.getKey(), EnumTracking.ENTREGADO.getKey()};

    public TrackingHolder(@NonNull ItemTrackingBinding binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final Tracking item) {
        Resources resources = BaseAPP.getContext().getResources();

        int position = getAdapterPosition();

        if(position < 8)
            binding.tvTitleRcv.setText(title[position]);

        binding.tvDateRcv.setText(item.getFecha());
        binding.tvDateRcv.setVisibility(item.getFecha().isEmpty() ? View.GONE : View.VISIBLE);

        binding.tvDetailRcv.setText(item.getNombre());
        //backend esta repitiendo el titulo
        binding.tvDetailRcv.setVisibility(item.getNombre().equals(title[position]) ? View.GONE : View.VISIBLE);

        if(item.getImagen()!=null){

            if(item.getImagen().length()>0){

                binding.btnguia.setVisibility(View.VISIBLE);

               // if(position==7){
                    binding.line.getLayoutParams().height = 20;
             //   }

            }
        }

        binding.btnguia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertadd = new AlertDialog.Builder(view.getContext());
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View view1 = factory.inflate(R.layout.image_zoom1, null);
                alertadd.setView(view1);

                ImageView imgzoom = (ImageView)view1.findViewById(R.id.dialog_imageview);

                try {
                    Glide.with(BaseAPP.getContext())
                            .load(item.getImagen())

                            .centerCrop()

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

                DisplayMetrics metrics = new DisplayMetrics();
                alertDialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int height = Math.round(metrics.heightPixels*0.6f);
                int wwidth = Math.round(metrics.widthPixels*0.9f);
                alertDialog.show();
                alertDialog.getWindow().setLayout(wwidth,height);

            }
        });

        if(item.getCheck().equals("1"))
        {
            binding.imgIconCircle.setBackground(resources.getDrawable(R.drawable.circle_green));
            binding.tvTitleRcv.setTypeface(binding.tvTitleRcv.getTypeface(), Typeface.BOLD);
            binding.tvTitleRcv.setTextColor(resources.getColor(R.color.green_check));
        }
        else{
            //binding.imgIconCircle.setBackground(mContext.getResources().getDrawable(R.drawable.circle_concentric_green));
            binding.imgIconCircle.setBackground(resources.getDrawable(R.drawable.circle_concentric_gray));
            binding.tvTitleRcv.setTypeface(binding.tvTitleRcv.getTypeface(), Typeface.NORMAL);
            binding.tvTitleRcv.setTextColor(resources.getColor(R.color.gray_5));
        }

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null)
                    events.onClickRoot(item, getAdapterPosition());
            }
        });
    }

    public interface Events{
        void onClickRoot(Tracking dataRow, int row);
    }
}
