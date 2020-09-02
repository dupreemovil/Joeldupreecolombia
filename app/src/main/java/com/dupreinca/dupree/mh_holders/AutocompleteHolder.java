package com.dupreinca.dupree.mh_holders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dupreeinca.lib_api_rest.model.dto.response.BarrioDTO;
import com.dupreinca.dupree.databinding.ItemAddBarrioBinding;
import com.dupreinca.dupree.databinding.ItemReportesBinding;

/**
 * Created by marwuinh@gmail.com on 2/27/19.
 */

public class AutocompleteHolder extends RecyclerView.ViewHolder{
    private ItemAddBarrioBinding binding;
    private Events events;

    public AutocompleteHolder(@NonNull ItemAddBarrioBinding binding, Events events) {
        super(binding.getRoot());
        this.binding = binding;
        this.events = events;
    }

    public void bindData(final BarrioDTO item) {
        binding.txtvNameBarrio.setText(item.getName_barrio());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events != null)
                    events.onClickRoot(item, getAdapterPosition());
            }
        });
    }

    public interface Events{
        void onClickRoot(BarrioDTO dataRow, int row);
    }
}
