package com.dupreinca.dupree.mh_fragments_menu;


import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemPagosrealizados;
import com.dupreeinca.lib_api_rest.model.dto.response.ListPagos;
import com.dupreeinca.lib_api_rest.model.dto.response.ListPagosRealizados;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentReportePagosRealizadosBinding;
import com.dupreinca.dupree.mh_adapters.PagosRealizadoListAdapter;
import com.dupreinca.dupree.mh_fragments_menu.reportes.ReportesActivity;
import com.dupreinca.dupree.mh_holders.PagosRealizHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.model_view.DataAsesora;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportePagosRealizadosFragment extends BaseFragment implements PagosRealizHolder.Events{

    private final String TAG = ReportePagosRealizadosFragment.class.getName();

    private ReportesController reportesController;
    private PagosRealizadoListAdapter adapter_pagos;
    private List<ItemPagosrealizados> listPagos, listFilter;

    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;

    public ReportePagosRealizadosFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_reporte_pagos_realizados;
    }

    private FragmentReportePagosRealizadosBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentReportePagosRealizadosBinding) view;

        reportesController = new ReportesController(getContext());

        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
        binding.tvNombreAsesora.setText("");

        binding.rcvPagosRealizados.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.rcvPagosRealizados.setHasFixedSize(true);

        listPagos = new ArrayList<>();
        listFilter = new ArrayList<>();

        perfil = getPerfil();
        timeinit = System.currentTimeMillis();
        adapter_pagos = new PagosRealizadoListAdapter(listPagos, listFilter, this);
        binding.rcvPagosRealizados.setAdapter(adapter_pagos);

    }

    @Override
    protected void onLoadedView() {
        checkPagos();
    }

    private void checkPagos(){
        Bundle bundle;
        DataAsesora data;
        if((bundle = getArguments()) != null && (data = bundle.getParcelable(ReportesActivity.TAG)) != null){
            searchNewIdenty(data.getCedula());
        }
    }

    private void updateView(ListPagos listaPagos){
        listPagos.clear();
        listFilter.clear();
        if(listaPagos.getPago()!=null){
            listPagos.addAll(listaPagos.getPago());
            listFilter.addAll(listaPagos.getPago());
        }



        binding.cardViewBackGround.setVisibility(View.VISIBLE);
        binding.tvNombreAsesora.setText(listaPagos.getAsesora());

        adapter_pagos.notifyDataSetChanged();
    }

    public void searchNewIdenty(String cedula){
        showProgress();
        reportesController.getPagos(new Identy(cedula), new TTResultListener<ListPagosRealizados>() {
            @Override
            public void success(ListPagosRealizados result) {
                dismissProgress();
                updateView(result.getResult());

                if(result!=null){

                    if(result.getResult()!=null){


                        if(result.getResult().getPago()!=null){

                            if(result.getResult().getPago().size()>0){


                            }
                            else{
                                ((ReportesActivity)getActivity()).showbottomsheet();
                            }

                        }









                    }
                    else{

                        ((ReportesActivity)getActivity()).showbottomsheet();
                    }

                }
                else{

                    ((ReportesActivity)getActivity()).showbottomsheet();
                }

            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                if(error!=null){
                    checkSession(error);
                    ((ReportesActivity)getActivity()).showbottomsheet();
                }

            }
        });
    }

    public Profile getPerfil(){
        String jsonPerfil = mPreferences.getJSON_TypePerfil(getActivity());
        if(jsonPerfil!=null)
            return new Gson().fromJson(jsonPerfil, Profile.class);

        return null;
    }

    @Override
    public void onDestroy(){

        if(perfil!=null){
            timeend = System.currentTimeMillis();
            long finaltime= timeend-timeinit;
            int timesec = (int)finaltime/1000;

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"reportepag");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }

    @Override
    public void onClickRoot(ItemPagosrealizados dataRow, int row) {

    }
}
