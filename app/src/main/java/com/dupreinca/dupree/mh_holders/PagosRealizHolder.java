package com.dupreinca.dupree.mh_holders;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dupreeinca.lib_api_rest.model.dto.response.ItemPagosrealizados;
import com.dupreinca.dupree.BaseAPP;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ItemPagosRealizadosBinding;
import com.dupreinca.dupree.databinding.ItemReportesBinding;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class PagosRealizHolder extends RecyclerView.ViewHolder{
    private ItemPagosRealizadosBinding binding;
    private Events events;

    public PagosRealizHolder(@NonNull ItemPagosRealizadosBinding binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final ItemPagosrealizados item) {
        int position = getAdapterPosition();
        Resources resources = BaseAPP.getContext().getResources();
        
        binding.tvFecha.setText(String.valueOf(item.getFecha()));
        binding.tvBanco.setText(item.getBanco());

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        binding.tvValor.setText(resources.getString(R.string.concat_dolar, formatter.format(item.getValor())));

      //  binding.cardViewBackGround.setCardBackgroundColor(position%2!=0 ? resources.getColor(R.color.transp_Accent) : resources.getColor(R.color.transp_azulDupree));
        binding.cardViewBackGround.setCardBackgroundColor(position%2!=0 ? resources.getColor(R.color.gray_4) : resources.getColor(R.color.gray_4));


        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null)
                    events.onClickRoot(item, getAdapterPosition());
            }
        });
    }

    public interface Events{
        void onClickRoot(ItemPagosrealizados dataRow, int row);
    }
}
