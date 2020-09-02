package com.dupreinca.dupree.mh_fragments_menu.pedidos.historial;

import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.HistorialDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemFacturaDTO;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentProductsBinding;
import com.dupreinca.dupree.mh_adapters.FacturasListAdapter;
import com.dupreinca.dupree.mh_fragments_menu.pedidos.BasePedido;
import com.dupreinca.dupree.mh_fragments_menu.pedidos.historial.detalle_factura.DetalleFacturaActivity;
import com.dupreinca.dupree.mh_holders.FacturasHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacturasFragment extends BaseFragment implements FacturasHolder.Events{

    private final String TAG = FacturasFragment.class.getName();
    private ReportesController reportesController;

    private List<ItemFacturaDTO> list;
    private FacturasListAdapter adapterList;

    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;


    public FacturasFragment() {
        // Required empty public constructor
    }

    public static FacturasFragment newInstance() {
        Bundle args = new Bundle();

        FacturasFragment fragment = new FacturasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_products;
    }

    private FragmentProductsBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        onAttachFragment(getParentFragment());

        binding = (FragmentProductsBinding) view;

        timeinit = System.currentTimeMillis();

        binding.refresh.setEnabled(true);
        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recycler.setHasFixedSize(true);

        list = new ArrayList<>();
        adapterList = new FacturasListAdapter(list, this);
        binding.recycler.setAdapter(adapterList);

        timeinit =  System.currentTimeMillis();
        perfil = getPerfil();

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refresh.setRefreshing(false);
                searchIdenty(lastIdenty);
            }
        });
    }

    @Override
    protected void onLoadedView() {
        reportesController = new ReportesController(getContext());

        checkData();
    }

    private String lastIdenty = "";
    private void checkData(){
        if(dataStore.getTipoPerfil().getPerfil() != null){
            if(dataStore.getTipoPerfil().getPerfil().equals(Profile.ADESORA)){
                searchIdenty("");
            }
        }
    }

    public void setIdentyFacturas(String cedula){
        if(!this.lastIdenty.equals(cedula)){
            this.lastIdenty = cedula;
            searchIdenty(lastIdenty);
        }
    }

    public void searchIdenty(String cedula){
        this.lastIdenty = cedula;

        showProgress();
        reportesController.getHistorialPedido(new Identy(cedula), new TTResultListener<HistorialDTO>() {
            @Override
            public void success(HistorialDTO result) {
                dismissProgress();
                updateView(result.getResult());
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"facturas");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }

    public void updateView(List<ItemFacturaDTO> data){
        list.clear();
        if(data!=null){
            list.addAll(data);
        }
        adapterList.notifyDataSetChanged();
    }
    //MARK: HistorialHolder.Events
    @Override
    public void onClickRoot(ItemFacturaDTO dataRow, int row) {
        Intent intent = new Intent(getActivity(), DetalleFacturaActivity.class);
        intent.putExtra(DetalleFacturaActivity.TAG, dataRow);
        gotoActivity(intent);
    }

    //Parent Fragment
    private BasePedido viewParent;
    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof BasePedido) {
            viewParent = (BasePedido) childFragment;
        } else {
            Log.e(TAG, "is not OnInteractionActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewParent = null;
    }

}
