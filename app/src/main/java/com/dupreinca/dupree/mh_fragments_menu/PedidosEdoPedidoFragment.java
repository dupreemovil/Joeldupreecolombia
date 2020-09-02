package com.dupreinca.dupree.mh_fragments_menu;


import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.PedidosController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.response.EstadoPedidoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ResultEdoPedido;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentPedidosEdoPedidoBinding;
import com.dupreinca.dupree.mh_adapters.CDRListAdapter;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.ListaProductos;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosEdoPedidoFragment extends BaseFragment {
    private final String TAG = PedidosEdoPedidoFragment.class.getName();
    private FragmentPedidosEdoPedidoBinding binding;
    private PedidosController pedidosController;

    private CDRListAdapter adapter_cdr;
    private ListaProductos listProducts, listPaquetones, listOfertas, listFilter;

    public PedidosEdoPedidoFragment() {
        // Required empty public constructor
    }

    private Profile perfil;


    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    public void loadData(Profile perfil){
        this.perfil=perfil;
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_pedidos_edo_pedido;
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentPedidosEdoPedidoBinding) view;

        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
        binding.tvNombreAsesora.setText("");

        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recycler.setHasFixedSize(true);

        perfil = getPerfil();
        timeinit = System.currentTimeMillis();
        listProducts = new ListaProductos();
        listPaquetones = new ListaProductos();
        listOfertas = new ListaProductos();
        listFilter = new ListaProductos();

        binding.recycler.setAdapter(adapter_cdr);

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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"pedidosedo");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }

    @Override
    protected void onLoadedView() {
        pedidosController =  new PedidosController(getContext());
        checkEdoPedido();
    }

    private void checkEdoPedido(){
        if(perfil != null){
            if(perfil.getPerfil().equals(Profile.ADESORA)){
                searchIdenty("");
            }
        }
    }

    private void updateView(ResultEdoPedido resultEdoPedido){
        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
    }

    public void searchIdenty(String cedula){
        showProgress();
        pedidosController.getEstadoPedido(new Identy(cedula), new TTResultListener<EstadoPedidoDTO>() {
            @Override
            public void success(EstadoPedidoDTO result) {
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

}
