package com.dupreinca.dupree.mh_fragments_menu.pedidos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ViewDataBinding;

import com.dupreeinca.lib_api_rest.controller.BannerController;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidaSend;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarMadrugon;
import com.dupreeinca.lib_api_rest.model.dto.response.BannerDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidaDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ListProductCatalogoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ListProductMadrugonDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ProductCatalogoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ProductMadrugonDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.UrlsCatalogosDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.MadCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Madrugon;
import com.dupreinca.dupree.MainActivity;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredValida;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import 	com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dupreeinca.lib_api_rest.controller.PedidosController;
import com.dupreeinca.lib_api_rest.enums.EnumLiquidar;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarProducto;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarSend;
import com.dupreeinca.lib_api_rest.model.dto.response.EstadoPedidoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidarDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ResultEdoPedido;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Catalogo;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.FullscreenActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentPedidosHacerBinding;
import com.dupreinca.dupree.mh_adapters.CatalogoListAdapter;
import com.dupreinca.dupree.mh_adapters.PedidosPagerAdapter;
import com.dupreinca.dupree.mh_adapters.base.TabViewPager.TabManagerFragment;
import com.dupreinca.dupree.mh_dial_peru.dialogo_datos_asesora;
import com.dupreinca.dupree.mh_dialogs.InputDialog;
import com.dupreinca.dupree.mh_dialogs.SimpleDialog;
import com.dupreinca.dupree.mh_holders.CatalogoHolder;
import com.dupreinca.dupree.mh_utilities.KeyBoard;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class HacerPedidoFragment extends TabManagerFragment implements BasePedido, CatalogoHolder.Events, dialogo_datos_asesora.DatosCuadroDialogo{

    public static final String TAG = HacerPedidoFragment.class.getName();
    public FragmentPedidosHacerBinding binding;
    private PedidosController pedidosController;

    public final static String PEDIDO_FACTURADO = "2";
    public final static String PEDIDO_NUEVO = "0";
    public final static String PEDIDO_MODIFICAR = "1";
    public static final String BROACAST_DATA="broacast_data";

    public ResultEdoPedido resultEdoPedido;
    private boolean enable = true;
    String dato_corr="", dato_celu="";
    int bandera=1;

    private PedidosPagerAdapter pedidosPagerAdapter;
//    private BaseViewPagerAdapter pagerAdapter;

    //FILTRO CONTROL
    RealmResults<Catalogo> querycat;
    private List<Catalogo> listFilter;//, listSelected;
    private CatalogoListAdapter adapter_catalogo;
    private Realm realm;
    //FILTRO CONTROL

    private Profile perfil;
    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    public Boolean conpedido=false;

    Animation rotation;
    private BannerController bannerController;

    private String campana;

    ListProductCatalogoDTO listaProd_catalogo;
    ListProductMadrugonDTO listaProd_madrugon;

    public void loadData(Profile perfil){
        this.perfil=perfil;
    }

    private boolean productsEditable=false;
    private boolean offersEditable=false;

       public HacerPedidoFragment() {
        // Required empty public constructor

    }

    //MARK: TabManagerFragment
    @Override
    protected ViewPager setViewPage() {
        return binding.pagerView;
    }

    //MARK: TabManagerFragment
    @Override
    protected TabLayout setTabs() {
        Log.e(TAG, "setTabs()");
        return binding.tabs;
    }

    //MARK: TabManagerFragment
    @Override
    protected FragmentStatePagerAdapter setAdapter() {
        return pedidosPagerAdapter;
    }

    //MARK: TabManagerFragment
    @Override
    protected List<ModelList> setItems() {
        List<ModelList> items = new ArrayList<>();
        items.add(new ModelList(R.drawable.ic_shopping_cart_white_24dp, getString(R.string.carrito)));
        items.add(new ModelList(R.drawable.ic_list_white_24dp, getString(R.string.ofertas)));
        items.add(new ModelList(R.drawable.baseline_assignment_white_24, getString(R.string.historical)));
        return items;
    }

    //MARK: BaseFragment
    @Override
    protected int getMainLayout() {
        return R.layout.fragment_pedidos_hacer;
    }

    //MARK: TabManagerFragment
    @Override
    protected void initViews(ViewDataBinding view) {

        binding = (FragmentPedidosHacerBinding) view;
        Log.e(TAG, "initViews()");

        timeinit = System.currentTimeMillis();
        bannerController =  new BannerController(getActivity());

        pedidosPagerAdapter = new PedidosPagerAdapter(getChildFragmentManager());

        binding.pagerView.beginFakeDrag();
        binding.pagerView.addOnPageChangeListener(mOnPageChangeListener);
        binding.pagerView.setPagingEnabled(false);
        binding.pagerView.setOffscreenPageLimit(1);

        binding.fabSendPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEnable()) {
                    msgToast(getString(R.string.pedido_no_puede_modificarse));
                    return;
                }

                if(pedidosPagerAdapter.getCarritoFragment().validate()){
                    /*
                    if (bandera==1 && dato_celu=="" && dato_corr==""){
                        new dialogo_datos_asesora(getContext(), HacerPedidoFragment.this);
                    } else{*/
                        testRefreshServer();
                    //}

                } else {
                    msgToast(getString(R.string.no_se_detectaron_cambios));
                }
            }
        });



        //CONTROL DE FILTROS Y CATALOGO
        realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        binding.ctcRcvFilter.setVisibility(View.GONE);
        binding.rcvFilterPedido.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.rcvFilterPedido.setHasFixedSize(true);

        timeinit = System.currentTimeMillis();
        //listPremios = new ArrayList<>();
        listFilter = new ArrayList<>();
        adapter_catalogo = new CatalogoListAdapter(listFilter, this);
        binding.rcvFilterPedido.setAdapter(adapter_catalogo);

        binding.swipePedidos.setOnRefreshListener(mOnRefreshListener);
        binding.swipePedidos.setEnabled(false);
    }

    @Override
    protected void onLoadedView() {
        Log.e(TAG, "onLoadedView()");
        pedidosController = new PedidosController(getContext());

        controlVisible(false, "");
        filterCatalogoDB("");//mostrar toodo el catalogo
        checkEdoPedido();
    }

    public MenuItem searchItem;
    public SearchView searchView;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pedidos, menu);
        final MenuItem searchAsesora = menu.findItem(R.id.menu_asesora);
        searchItem = menu.findItem(R.id.menu_action_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.ingresar_codigo));

        EditText txtSearch = searchView.findViewById(R.id.search_src_text);
        txtSearch.setInputType(InputType.TYPE_CLASS_NUMBER);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onCreateOptionsMenu() -> onQueryTextSubmit() -> " + query);
                searchMyQuery(query);
                searchView.clearFocus();
                return false;//habilita el serach del teclado
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onCreateOptionsMenu() -> onQueryTextChange() -> " + newText);
                filterCatalogo(newText);
                return true;
            }
        });

        searchView.setIconified(true);//inicialmente oculto

        if(dataStore.getTipoPerfil()!=null) {
            boolean isAsesora = dataStore.getTipoPerfil().getPerfil().equals(Profile.ADESORA);
            searchAsesora.setVisible(!isAsesora);
        } else {
            msgToast(getString(R.string.handled_501_se_cerro_sesion));
            gotoMain();
        }

        searchAsesora.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                obtainIdentyAsesora();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchMyQuery(String query){
        Log.e(TAG, "searchQuery() -> query: " + query);

    }


    public Profile getPerfil(){
        String jsonPerfil = mPreferences.getJSON_TypePerfil(getActivity());
        if(jsonPerfil!=null)
            return new Gson().fromJson(jsonPerfil, Profile.class);

        return null;
    }



    //MARK CatalogoHolder.Events
    @Override
    public void onAddCartClick(Catalogo dataRow, int row) {
        Log.e(TAG,"onAddCartClick: "+String.valueOf(row));
        addCart(row);//agregar sin validar
    }

    //MARK CatalogoHolder.Events
    @Override
    public void onIncreaseClick(Catalogo dataRow, int row) {
        Log.e(TAG,"onIncreaseClick, pos:  "+row);
        increaseCart(row);
    }

    //MARK CatalogoHolder.Events
    @Override
    public void onDecreaseClick(Catalogo dataRow, int row) {
        Log.e(TAG,"onDecreaseClick, pos:  "+row);
        int cantidad = dataRow.getCantidad();
        if(cantidad>0) {
            if(cantidad==1){
                testRemoveCart(row);//pregunta si quiere eliminar
            } else {
                decreaseCart(row, cantidad);
            }
        }
    }

    //MARK CatalogoHolder.Events
    @Override
    public void onAddEditableClick(boolean isEditable) {

    }

    public void testRefreshServer(){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.guardar_cambios_server), getString(R.string.desea_guardar_cambios_server));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    sendToServer();
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }


    public void sendtoServerP(){
        showProgress();

        LiquidarSend send = obtainProductsLiquidate();
        Log.e(TAG, "sendToServer() -> send: "+new Gson().toJson(send));
        pedidosController.liquidarPedido(send, new TTResultListener<LiquidarDTO>() {
            @Override
            public void success(LiquidarDTO result) {

                System.out.println("El result pedido "+result.getActiva_premio());


                dismissProgress();

                if(result.getActiva_premio().contains("1")){

                    System.out.println("El result pedido act ");

                    //          showpremio(result.getMensaje_premio());

                }

                if(result != null && result.getCodigo() != null){
                    if(result.getCodigo().equals(EnumLiquidar.OK.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        String  msg = "";
                        if(result.getTotal_pedido()!=null){

                            try {
                                msg = "Total: "
                                        .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                        .concat(". ")
                                        .concat(result.getMensaje());
                            } catch (NumberFormatException e) {

                                Toast.makeText(getActivity(),"Error al obtener Total",Toast.LENGTH_LONG).show();
                            }


                        }
                        else{

                            msg = "Total: ";
                        }


                        showSnackBarDuration(msg,15000);
                        pedidosPagerAdapter.getCarritoFragment().setvisiblemad();

                        System.out.println("Set visible mad");
                        //         pedidosPagerAdapter.getCarritoFragment().pedidoEndviadoexitosamente();
                        //       pedidosPagerAdapter.getOffersFragment().ofertaEndviadoexitosamente();

                        //       initFAB();

                        //         clearAllData();
                        pedidosPagerAdapter.getCarritoFragment().updateCarritoM();



                    } else if(result.getCodigo().equals(EnumLiquidar.DEBAJO_MONTO.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);

                        String  msg = "Total: "
                                .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                .concat(". ")
                                .concat(result.getMensaje());

                        showSnackBarDuration(msg,15000);
                    }
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void sendToServer(){
        showProgress();

        LiquidarSend send = obtainProductsLiquidate();
        Log.e(TAG, "sendToServer() -> send: "+new Gson().toJson(send));
        pedidosController.liquidarPedido(send, new TTResultListener<LiquidarDTO>() {
            @Override
            public void success(LiquidarDTO result) {

                System.out.println("El result pedido "+result.getActiva_premio());


                dismissProgress();

                if(result.getActiva_premio().contains("1")){

                    System.out.println("El result pedido act ");

          //          showpremio(result.getMensaje_premio());

                }

                if(result != null && result.getCodigo() != null){
                    if(result.getCodigo().equals(EnumLiquidar.OK.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        String  msg = "";
                        if(result.getTotal_pedido()!=null){

                            try {
                                msg = "Total: "
                                        .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                        .concat(". ")
                                        .concat(result.getMensaje());
                            } catch (NumberFormatException e) {

                                Toast.makeText(getActivity(),"Error al obtener Total",Toast.LENGTH_LONG).show();
                            }


                        }
                        else{

                            msg = "Total: ";
                        }


                        showSnackBarDuration(msg,15000);
                        pedidosPagerAdapter.getCarritoFragment().setvisiblemad();

                        System.out.println("Set visible mad");
               //         pedidosPagerAdapter.getCarritoFragment().pedidoEndviadoexitosamente();
                 //       pedidosPagerAdapter.getOffersFragment().ofertaEndviadoexitosamente();

                 //       initFAB();

               //         clearAllData();
                        pedidosPagerAdapter.getCarritoFragment().updateCarritoM();



                    } else if(result.getCodigo().equals(EnumLiquidar.DEBAJO_MONTO.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);

                        String  msg = "Total: "
                                .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                .concat(". ")
                                .concat(result.getMensaje());

                        showSnackBarDuration(msg,15000);
                    }
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
//        new Http(getActivity()).liquidarPedido(obtainProductsLiquidate(), TAG, BROACAST_LIQUIDATE_PRODUCTS);
    }


    private void sendServer(){
        showProgress();


        LiquidaSend send = obtainProductsL();
        Log.e(TAG, "sendToServer() -> send: "+new Gson().toJson(send));
        pedidosController.liquidaPedido(send, new TTResultListener<LiquidaDTO>() {
            @Override
            public void success(LiquidaDTO result) {

                System.out.println("El result pedido "+result.getActiva_premio());


                dismissProgress();

                if(result.getActiva_premio().contains("1")){

                    System.out.println("El result pedido act ");

                    //          showpremio(result.getMensaje_premio());

                }

                if(result != null && result.getCodigo() != null){
                    if(result.getCodigo().equals(EnumLiquidar.OK.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                        String  msg = "";
                        if(result.getTotal_pedido()!=null){

                            try {
                                msg = "Total: "
                                        .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                        .concat(". ")
                                        .concat(result.getMensaje());
                            } catch (NumberFormatException e) {

                                Toast.makeText(getActivity(),"Error al obtener Total",Toast.LENGTH_LONG).show();
                            }


                        }
                        else{

                            msg = "Total: ";
                        }


                        showSnackBarDuration(msg,15000);
                        pedidosPagerAdapter.getCarritoFragment().setvisiblemad();

                        System.out.println("Set visible mad");
                                 pedidosPagerAdapter.getCarritoFragment().pedidoEndviadoexitosamente();
                               pedidosPagerAdapter.getOffersFragment().ofertaEndviadoexitosamente();

                        //       initFAB();

                        //         clearAllData();
                        pedidosPagerAdapter.getCarritoFragment().updateCarritoM();



                    } else if(result.getCodigo().equals(EnumLiquidar.DEBAJO_MONTO.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);

                        String  msg = "Total: "
                                .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                .concat(". ")
                                .concat(result.getMensaje());

                        showSnackBarDuration(msg,15000);
                    }
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
//        new Http(getActivity()).liquidarPedido(obtainProductsLiquidate(), TAG, BROACAST_LIQUIDATE_PRODUCTS);
    }

    private double total_pedido = 0;
    public LiquidarSend obtainProductsLiquidate(){
        String id_pedido = resultEdoPedido!=null ? resultEdoPedido.getId_pedido() : "";
        List<String> paquetones= new ArrayList<>();
        List<LiquidarProducto> ofertas = new ArrayList<>();
        List<LiquidarProducto> productos = new ArrayList<>();
        total_pedido = 0;

        List<ItemCarrito> itemCarritoList = pedidosPagerAdapter.getCarritoFragment().getListFilterCart();
        for(ItemCarrito item : itemCarritoList) {
            switch (item.getType()){
                case ItemCarrito.TYPE_CATALOGO:
                    productos.add(new LiquidarProducto(String.valueOf(item.getId()), item.getCantidad()));
                    total_pedido = total_pedido + item.getCantidad() * Double.parseDouble(item.getValor());
                    break;
                case ItemCarrito.TYPE_OFFERTS:
                    ofertas.add(new LiquidarProducto(String.valueOf(item.getId()), item.getCantidad()));
                    total_pedido = total_pedido + item.getCantidad() * Double.parseDouble(item.getValor_descuento());
                    break;
            }

        }


        return new LiquidarSend(id_pedido, paquetones, productos, ofertas);
    }

    public LiquidaSend obtainProductsL(){
        String id_pedido = resultEdoPedido!=null ? resultEdoPedido.getId_pedido() : "";
        List<String> paquetones= new ArrayList<>();
        List<LiquidarProducto> ofertas = new ArrayList<>();
        List<LiquidarProducto> productos = new ArrayList<>();
        List<ItemCarritoM> lista = filterCatalogoDBMAD("");
        List<LiquidarMadrugon> madrugon = new ArrayList<>();
        for(int j=0;j<lista.size();j++){


            LiquidarMadrugon liq = new LiquidarMadrugon(id_pedido,lista.get(j).getCantidad(),lista.get(j).getValor(),lista.get(j).getTallasel());
            madrugon.add(liq);


        }


        total_pedido = 0;

        return new LiquidaSend(id_pedido,productos, madrugon,ofertas,"MOVIL","721652");
    }

    private void checkEdoPedido(){
        Log.e(TAG,"checkEdoPedido(A)");
        if(perfil != null){
            Log.e(TAG,"checkEdoPedido(B)");
            if(perfil.getPerfil().equals(Profile.ADESORA)){
                Log.e(TAG,"checkEdoPedido(C)");

                searchIdenty("");
            } else {
                enableEdit(false);
                obtainIdentyAsesora();
            }
        }
    }

    private void controlVisible(boolean isVisible, String text){
        //habilita tabs
        if(pedidosPagerAdapter!=null){
            setTabIcons(PedidosPagerAdapter.PAGE_CARRITO, isVisible);
            setTabIcons(PedidosPagerAdapter.PAGE_OFFERS, isVisible);
        }
        if(text!=null){
            binding.tvNombreAsesora.setText(text);
            binding.ctxNameAsesora.setVisibility(isVisible && !TextUtils.isEmpty(text) ? View.VISIBLE : View.GONE);
        }
         enableSearch(isVisible);

        //fabShow(isVisible);
        fabShow(false);
    }

    public List<ItemCarritoM> filterCatalogoDBMAD(final String textFilter){
        List<ItemCarritoM> listCarritos = new ArrayList<>();
        Log.v(TAG,"filterCatalogoDBM... ---------------filterCatalogoDBM--------------");
        try {
            realm.beginTransaction();
            RealmResults<MadCarrito> query = realm.where(MadCarrito.class)
                    .beginGroup()

                    .endGroup()
                    .contains("id",textFilter)
                    .findAllSorted("id").sort("id", Sort.DESCENDING);
            realm.commitTransaction();
            for (MadCarrito item : query) {
                listCarritos.add(new ItemCarritoM(item));
            }

        } catch(Throwable e) {
            Log.v(TAG,"filterCatalogoDBM... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }

        return listCarritos;
    }

    private void fabTitle(String title){
        binding.fabSendPedido.setTitle(title);
    }

    private void fabShow(boolean isVisible){
        binding.fabSendPedido.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setBackGroungColor(int color){
        binding.fabSendPedido.setBackgroundColor(color);
    }

    private void updateView(){
        Log.e(TAG, "updateView()");
        controlVisible(true, resultEdoPedido.getAsesora());
        //muestra o no ofertas
        pedidosPagerAdapter.getOffersFragment().setShowOffers(resultEdoPedido.getOfertas().getActivo().equals(EstadoPedidoDTO.SHOW_PRDUCTS));

        //VERIFICA SI CAMBIO LA CAMPAÑA
        String campanaActualServer = resultEdoPedido.getCampana();
        String campanaActualLocal = dataStore.getCampanaActual();

        if(campanaActualLocal==null){
            dataStore.setCampanaActual(campanaActualServer);// se respalda la campañ actual
        } else if(!campanaActualLocal.equals(campanaActualServer)){//si cambio la campaña
            // se borrar e pedido actual (prductos y ofertas) de la DB
            dataStore.setCampanaActual(campanaActualServer);// se respalda la campañ actual


            System.out.println("Hacerped campanact");
            campana = campanaActualServer;
            dataStore.setChangeCampana(true);
            if(conpedido){

            }
            else{
                deleteCatalogo();
                deleteMadrugon();
                deleteMadcarrito();
            }

         //   deleteCatalogo();
            getBanner();


            showProgress();

  //          startActivity(new Intent(getActivity(), FullscreenActivity.class));

//            getActivity().finish();
            //return;
        }
        else{
            System.out.println("Hacerped campanact else");
            campana = campanaActualServer;
            dataStore.setCampanaActual(campana);// se respalda la campañ actual
            dataStore.setChangeCampana(true);
           // deleteCatalogo();

            if(conpedido){

            }
            else{
                deleteCatalogo();
                deleteMadrugon();
            }

            getBanner();


            showProgress();

        }

        selectAction();
    }

    public void deleteMadrugon(){

        System.out.println("Delete Madr");
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.delete(Madrugon.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"deleteMadrugon... ---------------ok--------------");
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"deleteMadrugon... ---------------error--------------");
                Log.e(TAG,"deleteMadrugon... "+error.getMessage());
                //realm.close();
                dismissProgress();

            }
        });
        //realm.beginTransaction();

        //realm.commitTransaction();
    }

    public void deleteMadcarrito(){

        System.out.println("Delete Madr");
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.delete(MadCarrito.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"deleteMadrugon... ---------------ok--------------");
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"deleteMadrugon... ---------------error--------------");
                Log.e(TAG,"deleteMadrugon... "+error.getMessage());
                //realm.close();
                dismissProgress();

            }
        });
        //realm.beginTransaction();

        //realm.commitTransaction();
    }

    public void deleteCatalogo(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.delete(Catalogo.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"deleteCatalogo... ---------------ok--------------");
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"deleteCatalogo... ---------------error--------------");
                Log.e(TAG,"deleteCatalogo... "+error.getMessage());
                //realm.close();
                dismissProgress();

            }
        });
        //realm.beginTransaction();

        //realm.commitTransaction();
    }


    private void selectAction(){
        Log.e(TAG, "selectAction()");
        switch (resultEdoPedido.getEstado_pedido()) {
            case PEDIDO_NUEVO:
                // Todoel proceso desde cero, puede existir algo loca
                //borra los productos de la DB que esten pedidos con anterioridad
                Log.e(TAG, "PEDIDO NUEVO");//no llega nada de productos nuevos, se mantienen los de la db local
                enableEdit(true);
                pedidosPagerAdapter.getOffersFragment().clearEditable();
                pedidosPagerAdapter.getOffersFragment().setEnable(true);
                pedidosPagerAdapter.getOffersFragment().sincOfertasDB(resultEdoPedido.getOfertas().getProductos(), false);//prevalece la oferta local;

                pedidosPagerAdapter.getCarritoFragment().clearCartDBM();
                pedidosPagerAdapter.getCarritoFragment().sincCatalogoDBM(resultEdoPedido.getMadrugon().getProductos(), false);//prevalece la oferta local;

                pedidosPagerAdapter.getCarritoFragment().binding.layoutbtn.setVisibility(View.VISIBLE);

                pedidosPagerAdapter.getCarritoFragment().sincCatalogoDB(resultEdoPedido.getProductos().getProductos(), false);//Se da prioridad al pedido local


                pedidosPagerAdapter.getCarritoFragment().clearEditable();
                pedidosPagerAdapter.getCarritoFragment().setEnable(true);

                pedidosPagerAdapter.getCarritoFragment().cedulaasesora = this.cedula;
                pedidosPagerAdapter.getCarritoFragment().updateCarrito();//actualiza el carrito

                break;
            case PEDIDO_MODIFICAR:
                //Si, es editar se da prioridad a lo que esta en el server
                Log.e(TAG, "PEDIDO A MODIFICAR");
                enableEdit(true);
                pedidosPagerAdapter.getOffersFragment().clearEditable();
                pedidosPagerAdapter.getOffersFragment().setEnable(true);
                Log.e(TAG, "Ofertas: "+new Gson().toJson(resultEdoPedido.getOfertas().getProductos()));
                pedidosPagerAdapter.getOffersFragment().sincOfertasDB(resultEdoPedido.getOfertas().getProductos(), true);//prevalece la oferta del server
                //enableEdit(true);
                pedidosPagerAdapter.getCarritoFragment().cedulaasesora = this.cedula;
                pedidosPagerAdapter.getCarritoFragment().sincCatalogoDBM(resultEdoPedido.getMadrugon().getProductos(), true);//prevalece la oferta local;
                pedidosPagerAdapter.getCarritoFragment().binding.layoutbtn.setVisibility(View.VISIBLE);
                pedidosPagerAdapter.getCarritoFragment().sincCatalogoDB(resultEdoPedido.getProductos().getProductos(), true);//Se borra el pedido local
                pedidosPagerAdapter.getCarritoFragment().clearEditable();
                pedidosPagerAdapter.getCarritoFragment().setEnable(true);

                pedidosPagerAdapter.getCarritoFragment().updateCarrito();//actualiza el carrito

                break;
            case PEDIDO_FACTURADO:
                //ya el pedido fue facturado, no se puede hacer otro o editar, no debe haber nada local
                //se muestra los que llegan, y no se pueden modificar
                Log.e(TAG, "PEDIDO YA FACTURADO");
                enableEdit(false);
                updateTotal();
                break;
        }

    }

    private void enableEdit(boolean isEnable){
        setEnable(isEnable);
    }

    /**
     * Eventos SwipeRefreshLayout
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener
            = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {

        }
    };

    private void enableSearch(boolean isVisible){
        if(searchItem!=null) {
            searchItem.setVisible(isVisible);
            searchView.setIconified(!isVisible);
        }
    }

    private void hideSearchView(){
        if(searchView!=null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG,"PAGE_MAIN Page: "+position);
            switch (position){
                case PedidosPagerAdapter.PAGE_CARRITO:
                    enableSearch(true);
                    fabShow(false);
                    System.out.println("Show Btn");
                    pedidosPagerAdapter.getCarritoFragment().showbtn();
                    if(pedidosPagerAdapter.getCarritoFragment()!=null){
                        pedidosPagerAdapter.getCarritoFragment().setEnable(isEnable());
                        pedidosPagerAdapter.getOffersFragment().setEnable(isEnable());

                        pedidosPagerAdapter.getCarritoFragment().updateCarrito();
                    }

                    if(pedidosPagerAdapter.getCarritoFragment().carritoM){
                        pedidosPagerAdapter.getCarritoFragment().showbtn();
                    }

                    if(pedidosPagerAdapter.getCarritoFragment().resumenM){
                        pedidosPagerAdapter.getCarritoFragment().showbtn();
                        pedidosPagerAdapter.getCarritoFragment().showresumen();
                    }


                    break;
                case PedidosPagerAdapter.PAGE_OFFERS:
                    enableSearch(true);
                    fabShow(false);
                    System.out.println("Hide Btn");
                    pedidosPagerAdapter.getOffersFragment().hidebtn();
                    if(pedidosPagerAdapter.getCarritoFragment()!=null) {
                        pedidosPagerAdapter.getCarritoFragment().setEnable(isEnable());
                        pedidosPagerAdapter.getOffersFragment().setEnable(isEnable());

                        pedidosPagerAdapter.getCarritoFragment().hidebtn();
                        pedidosPagerAdapter.getOffersFragment().filterOffersDB("");
                    }
                    break;
                case PedidosPagerAdapter.PAGE_HISTORICAL:
                    enableSearch(false);
                    fabShow(false);

                    if(pedidosPagerAdapter.getHistorialFragment()!=null){
                        pedidosPagerAdapter.getHistorialFragment().hidebtn();
                        pedidosPagerAdapter.getHistorialFragment().setIdentyFacturas(cedula);
                    }


                    break;
            }

            hideSearchView();
            KeyBoard.hide(Objects.requireNonNull(getActivity()));

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };


//    private String textFilter="";
    public void filterCatalogo(String textFilter){
//        this.textFilter=textFilter;
        Log.e(TAG, "textFilter: "+textFilter+", isEnable(): "+isEnable());
        if(isEnable()) {
            if (getPageCurrent() == PedidosPagerAdapter.PAGE_CARRITO) {
                if (textFilter.length() > 2) {
                    binding.ctcRcvFilter.setVisibility(View.VISIBLE);
                    filterCatalogoDB(textFilter);
                } else {
                    binding.ctcRcvFilter.setVisibility(View.GONE);
                }
            } else if (getPageCurrent() == PedidosPagerAdapter.PAGE_OFFERS) {
                pedidosPagerAdapter.getOffersFragment().filterOffersDB(textFilter);
            }
        }

    }

    private void initFAB(){
        setBackGroungColor(getResources().getColor(R.color.green_check));
        fabTitle("");
    }

    private void clearAllData(){
        setEnable(false);

        controlVisible(false, "");

        //EN ESTE CASO SE ESTA MODO GERENTE, SE LIMPIA TODITO EL PEDIDO AL CAMBIAR DE ASESORA
        pedidosPagerAdapter.getOffersFragment().deleteOferta();
        pedidosPagerAdapter.getOffersFragment().filterOffersDB("");
        pedidosPagerAdapter.getCarritoFragment().clearCartDB();
        pedidosPagerAdapter.getCarritoFragment().updateCarrito();

//        pedidosPagerAdapter.getHistorialFragment().updateView(null);
    }

    String cedula = "";
    public void searchIdenty(String cedula){
        binding.tvNombreAsesora.setText("");
        initFAB();
        this.cedula=cedula;

        if(perfil != null) {
            if (!perfil.getPerfil().equals(Profile.ADESORA))
                setEnable(false);
        }

        showProgress();
        pedidosController.getEstadoPedido(new Identy(cedula), new TTResultListener<EstadoPedidoDTO>() {
            @Override
            public void success(EstadoPedidoDTO result) {
                dismissProgress();
                resultEdoPedido = result.getResult();

                if(result.getResult().getProductos().getProductos().size()>0){
                    conpedido=true;
                }

                if(result.getResult().getMadrugon()!=null){

                    if(result.getResult().getMadrugon().getProductos()!=null){

                        if(result.getResult().getMadrugon().getProductos().size()>0){

                        }
                        else{
                            obtainMadrugon();
                        }

                    }



                }



                System.out.println("El result estado "+new Gson().toJson(result.getResult()));

                updateView();

                dataFacturas();
            }

            @Override
            public void error(TTError error) {
                setPageCurrent(PedidosPagerAdapter.PAGE_HISTORICAL);

                dismissProgress();
                checkSession(error);
                if(error!=null){
                    if(error.getStatusCode() != null && error.getStatusCode() != null){
                        if(error.getStatusCode() != 404 || error.getStatusCode() != 501) {
                            dataFacturas();
                        }
                    }
                }

            }
        });

    }

    private void dataFacturas(){
        if (getPageCurrent() == PedidosPagerAdapter.PAGE_HISTORICAL) {
            pedidosPagerAdapter.getHistorialFragment().searchIdenty(cedula);
        }
    }
    ///////////CONTROL DEL FILTRO DE PEDIDOS////////////

    public void testRemoveCart(int row){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.remover), getString(R.string.desea_remover_pedido));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    removeCart(row);
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void obtainIdentyAsesora(){
        hideSearchView();
        InputDialog d = new InputDialog();
        d.loadData(getString(R.string.cedula_asesora), getString(R.string.cedula_asesora), new InputDialog.ResponseListener() {
            @Override
            public void result(String inputText) {
                clearAllData();
                searchIdenty(inputText);
            }
        });
        d.show(getActivity().getSupportFragmentManager(),"mDialog");
    }


    private void increaseCart(int position){
        Log.i(TAG, "increaseCart(), pos: "+position);
        if(position>-1 && position<listFilter.size()) {
            realm.getDefaultInstance();
            try {
                realm.beginTransaction();
                listFilter.get(position).setCantidad(listFilter.get(position).getCantidad()+1);
                adapter_catalogo.notifyItemChanged(position);
                pedidosPagerAdapter.getCarritoFragment().updateCarrito();
                realm.commitTransaction();
            } catch (Throwable e) {
                Log.v(TAG, "increaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                Log.v(TAG,e.getLocalizedMessage());
            }
        }
    }

    private void decreaseCart(int position, int cantidad){
        Log.i(TAG, "decreaseCart(), pos: "+position);
        if(position>-1 && position<listFilter.size()) {
            realm.beginTransaction();
            try {
                listFilter.get(position).setCantidad(cantidad - 1);
                adapter_catalogo.notifyItemChanged(position);
                pedidosPagerAdapter.getCarritoFragment().updateCarrito();
                realm.commitTransaction();
            } catch (Throwable e) {
                Log.v(TAG, "decreaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void addCart(int position){
        Log.i(TAG, "addCart(), pos: "+position);
        if(position>-1 && position<listFilter.size()) {
            realm.beginTransaction();
            try {
                listFilter.get(position).setCantidad(1);// se predetermina cantidad en 1
                listFilter.get(position).setTime(Calendar.getInstance().getTimeInMillis());//controla el ultimo item agregado a la lista (cambiar a un int y controlar incremento)
                adapter_catalogo.notifyItemChanged(position);
                realm.commitTransaction();

                hideSearchView();
                pedidosPagerAdapter.getCarritoFragment().updateCarrito();

            } catch (Throwable e) {
                Log.v(TAG, "addCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void removeCart(int position){
        Log.i(TAG, "removeCart(), pos: "+position);
        if(position>-1 && position<listFilter.size()) {
            realm.beginTransaction();
            try {
                listFilter.get(position).setCantidad(0);// se predetermina cantidad en 1
                adapter_catalogo.notifyItemChanged(position);
                pedidosPagerAdapter.getCarritoFragment().updateCarrito();
                realm.commitTransaction();
            } catch (Throwable e) {
                Log.v(TAG, "removeCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void filterCatalogoDB(final String textFilter){
        Log.v(TAG,"filterCatalogoDB... ---------------filterCatalogoDB--------------");
        realm.beginTransaction();
        try {
            listFilter.clear();
            querycat = realm.where(Catalogo.class)
                    .beginGroup()
                    .equalTo("cantidad",0)
                    .equalTo("cantidadServer",0)
                    .endGroup()
                    .contains("id",textFilter).findAll();
            listFilter.addAll(querycat);
            adapter_catalogo.notifyDataSetChanged();
            realm.commitTransaction();

            Log.e(TAG,"filterCatalogoDB... listFilter.SIZE(): "+listFilter.size());
        } catch(Throwable e) {
            Log.v(TAG,"filterCatalogoDB... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }
    }

    ///////////CONTROL DEL FILTRO DE PEDIDOS////////////

    @Override
    public void onDestroy() {

        if(perfil!=null){
            timeend = System.currentTimeMillis();
            long finaltime= timeend-timeinit;
            int timesec = (int)finaltime/1000;

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"hacerpedidos");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();

        realm.close();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }



    private void controlEditable(){
        setBackGroungColor((productsEditable | offersEditable) ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.green_check));
    }

    @Override
    public void offersEditable(boolean offersEditable) {
        this.offersEditable = offersEditable;
        controlEditable();
    }

    @Override
    public void productsEditable(boolean productsEditable) {
        this.productsEditable = productsEditable;
        controlEditable();
    }

    @Override
    public void hidesearch() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
        enableSearch(false);
    }

    @Override
    public void showsearch() {
        searchView.setQuery("", true);
        searchView.setIconified(false);
        enableSearch(true);
    }

    @Override
    public void updateTotal(){
        obtainProductsLiquidate();
        Log.e(TAG, "Update Total: "+total_pedido);

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        fabTitle("$ ".concat(formatter.format(total_pedido)));
    }

    @Override
    public void updateCarrito() {
        pedidosPagerAdapter.getCarritoFragment().updateCarrito();
    }

    @Override
    public String id_pedido() {
        return resultEdoPedido.getId_pedido();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach()");
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach()");
        setHasOptionsMenu(false);
    }

    @Override
    public void ResultadoDatos(String dato_corr, String dato_celu) {
        this.dato_corr = dato_corr;
        this.dato_celu = dato_celu;
    }

    private void getBanner(){

        responseBanner = null;

        bannerController.getBanner(new TTResultListener<BannerDTO>() {
            @Override
            public void success(BannerDTO result) {
                Log.e(TAG, "getBanner() -> success(): " + new Gson().toJson(result));
                responseBanner(result);
            }

            @Override
            public void error(TTError error) {

                dismissProgress();
                msgToast("getBanner() -> error(): "+new Gson().toJson(error));
                Log.e(TAG, "getBanner() -> error(): " + new Gson().toJson(error));
                //     errorLoadInitData();
            }
        });
    }
    BannerDTO responseBanner;
    public void responseBanner(BannerDTO responseBanner){
        if(responseBanner!=null) {
            this.responseBanner = responseBanner;
            getFilesCatalogos();
        }else{

            dismissProgress();
            msgToast("responsebanner -> error(): "+responseBanner.toString());
            //  errorLoadInitData();
        }
    }

    private void getFilesCatalogos(){
        bannerController.getUrlCatalogos(new TTResultListener<UrlsCatalogosDTO>() {
            @Override
            public void success(UrlsCatalogosDTO result) {
                Log.e(TAG, "getFilesCatalogos() -> success(): " + new Gson().toJson(result));
                responseFileCatalogos(result);
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                msgToast("getFilesCatalogos() -> error(): " + new Gson().toJson(error));
                Log.e(TAG, "getFilesCatalogos() -> error(): " + new Gson().toJson(error));
                //    errorLoadInitData();
            }
        });
    }

    UrlsCatalogosDTO responseUrlCatalogos;
    public void responseFileCatalogos(UrlsCatalogosDTO responseUrlCatalogos){
        if(responseUrlCatalogos!=null) {
            this.responseUrlCatalogos = responseUrlCatalogos;
            //SE DECIDE SI SE DESCARGA LA LISTA DE PRODUCTOS O NO
            /*if(updateOnlyBannerAndPDF)
                terminatedProcess();
            else*/
            obtainCatalogo();
        }else{
            dismissProgress();
            msgToast("responsefilecatalogo() -> error(): " + new Gson().toJson(responseUrlCatalogos));
            //   errorLoadInitData();
        }
    }

    private void obtainCatalogo(){
        bannerController.getProductos(campana, new TTResultListener<ProductCatalogoDTO>() {
            @Override
            public void success(ProductCatalogoDTO result) {
                Log.e(TAG, "obtainCatalogo()C -> success(): " + new Gson().toJson(result));
                //un detalle envia 200 con error 404
                if (result.getCode() == 404) {
                    //     errorLoadInitData();
                    dismissProgress();
                } else {
                    responseCatalogo(result.getResult());
                }
            }

            @Override
            public void error(TTError error) {
                Log.e(TAG, "obtainCatalogo() -> error(): " + new Gson().toJson(error));
                //error de Backend
                if(error!=null){
                    if(error.getStatusCode()!=null){
                        if(error.getStatusCode() == 404) {
                            try {
                                ProductCatalogoDTO resp = new Gson().fromJson(error.getErrorBody(), ProductCatalogoDTO.class);
                                responseCatalogo(resp.getResult());
                            } catch (JsonSyntaxException e) {
                                dismissProgress();
                                msgToast("obtainCatalogo() -> error():json " + e.getMessage());

                                //       errorLoadInitData();
                            }
                        } else {
                            dismissProgress();
                            msgToast("obtainCatalogo() -> error(): " + error.getMessage());

                            // errorLoadInitData();
                        }
                    }
                    else{
                        dismissProgress();
                        msgToast("obtainCatalogo() -> errorstat null: " + error.getMessage());

                        //errorLoadInitData();
                    }
                }


            }
        });


//        new Http(FullscreenActivity.this).getProductosCatalogo(false);
    }


    private void obtainMadrugon() {

        deleteMadrugon();
        System.out.println("Sinc write");
    //    deleteMadcarrito();

        showProgress();
        System.out.println("La respuesta a madr");

        bannerController.getProductosmad( new TTResultListener<ProductMadrugonDTO>() {
            @Override
            public void success(ProductMadrugonDTO result) {
                Log.e(TAG, "obtainMadrugon()C -> success(): " + new Gson().toJson(result));
                System.out.println("Prod Mad "+new Gson().toJson(result.getResult()));
                //un detalle envia 200 con error 404
                if (result.getCode() == 404) {
                    //     errorLoadInitData();
                    dismissProgress();
                } else {

                    responseMadrugon(result.getResult());

                }
            }

            @Override
            public void error(TTError error) {

                Log.e(TAG, "obtainmadrugon() -> error(): " + new Gson().toJson(error));
                //error de Backend
                if(error!=null){
                    if(error.getStatusCode()!=null){
                        if(error.getStatusCode() == 404) {
                            try {

                            } catch (JsonSyntaxException e) {
                                dismissProgress();
                                msgToast("obtainMadrugon() -> error():json " + e.getMessage());

                                //       errorLoadInitData();
                            }
                        } else {
                            dismissProgress();
                            msgToast("obtainMadrugon() -> error(): " + error.getMessage());

                            // errorLoadInitData();
                        }
                    }
                    else{
                        dismissProgress();

                        Log.e(TAG, "obtainmadrugon() -> error(): " + new Gson().toJson(error.getMessage()));
                        msgToast("obtainCatalogo() -> errorstat null: " + error.getMessage());

                        //errorLoadInitData();
                    }
                }


            }
        });


    }

    public void responseCatalogo(ListProductCatalogoDTO listaProd_catalogo){
        Log.e(TAG, "listaProd_catalogo.getOfertas(): "+new Gson().toJson(listaProd_catalogo.getOfertas()));
        Log.e(TAG, "listaProd_catalogo.getPaquetones(): "+new Gson().toJson(listaProd_catalogo.getPaquetones()));
        Log.e(TAG, "listaProd_catalogo.getProductos(): "+new Gson().toJson(listaProd_catalogo.getProductos()));

        if(listaProd_catalogo.getProductos()!=null && listaProd_catalogo.getProductos().size()>0) {
            this.listaProd_catalogo = listaProd_catalogo;
            if(listaProd_catalogo.getOfertas()!=null && listaProd_catalogo.getOfertas().size()>0) {
                writeOfertas();
            } else if(listaProd_catalogo.getPaquetones()!=null &&
                    listaProd_catalogo.getPaquetones().getLinea_1()!=null &&
                    listaProd_catalogo.getPaquetones().getLinea_2()!=null &&
                    listaProd_catalogo.getPaquetones().getLinea_3()!=null &&
                    listaProd_catalogo.getPaquetones().getLinea_4()!=null){
                writePaquetones();
            } else {
                writeCatalogo();
            }
        } else{
            dismissProgress();
            msgToast("responseCatalogo() -> error(): " + listaProd_catalogo.getProductos());


            //     errorLoadInitData();
        }
    }


    public void responseMadrugon(ListProductMadrugonDTO listaProd_madrugon){

        Log.e(TAG, "listaProd_madrugon.getMadrugon(): "+new Gson().toJson(listaProd_madrugon.getProductos()));

        if(listaProd_madrugon.getProductos()!=null ) {
            this.listaProd_madrugon = listaProd_madrugon;
            if(listaProd_madrugon.getProductos().size()>0) {
                writeMadrugon();
            }
            else{

            }
        } else{
            dismissProgress();
            msgToast("responseMadrugon() -> error(): " + listaProd_madrugon.getProductos());
            //obtainMadrugon();

            //     errorLoadInitData();
        }
    }

    public void writeOfertas(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.insert(listaProd_catalogo.getOfertas());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"writeOfertas... ---------------ok--------------");
                Log.v(TAG, ": " + new Gson().toJson(listaProd_catalogo.getOfertas()));
                if(listaProd_catalogo.getPaquetones()!=null &&
                        listaProd_catalogo.getPaquetones().getLinea_1()!=null  &&
                        listaProd_catalogo.getPaquetones().getLinea_2()!=null &&
                        listaProd_catalogo.getPaquetones().getLinea_3()!=null &&
                        listaProd_catalogo.getPaquetones().getLinea_4()!=null){
                    writePaquetones();
                } else {
                    writeCatalogo();
                }
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"writeOfertas... ---------------error--------------");
                Log.e(TAG,"writeOfertas... "+error.getMessage());
                msgToast("writeOfertas... "+error.getMessage());
                dismissProgress();
                //realm.close();
                //       errorLoadInitData();
            }
        });
    }

    public void writePaquetones(){
        mPreferences.setJASON_Paquetones(getActivity(), new Gson().toJson(listaProd_catalogo.getPaquetones()));
        Log.v(TAG,"writePaquetones... ---------------ok--------------");
        Log.v(TAG, ": " + new Gson().toJson(listaProd_catalogo.getPaquetones()));
        writeCatalogo();
    }

    public void writeCatalogo(){



        if(realm.isClosed())
            realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                //RealmList<Catalogo> dm_list_catalogo = new RealmList<Catalogo>();
                //dm_list_catalogo.addAll(listaProd_catalogo.getProductos());
                //DM_List_Catalogo dm_list_catalogo = new DM_List_Catalogo(); // <-- create unmanaged
                //RealmList<Catalogo> _catalList = new RealmList<>();
                //_catalList.addAll(listaProd_catalogo.getProductos());
                //dm_list_catalogo.setCatalogoList(_catalList);
                bgRealm.insert(listaProd_catalogo.getProductos());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"writeCatalogo... ---------------ok--------------");
                Log.v(TAG, ": " + new Gson().toJson(listaProd_catalogo.getProductos()));

                terminatedProcess();
                obtainMadrugon();
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"writeCatalogo... ---------------error--------------");
                Log.e(TAG,"writeCatalogo... "+error.getMessage());
              //  msgToast("writeCatalogo... "+error.getMessage());
                dismissProgress();
                obtainMadrugon();
                //realm.close();
                //   errorLoadInitData();
            }
        });
    }


    public void writeMadrugon(){
        if(realm.isClosed())
            realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                //RealmList<Catalogo> dm_list_catalogo = new RealmList<Catalogo>();
                //dm_list_catalogo.addAll(listaProd_catalogo.getProductos());
                //DM_List_Catalogo dm_list_catalogo = new DM_List_Catalogo(); // <-- create unmanaged
                //RealmList<Catalogo> _catalList = new RealmList<>();
                //_catalList.addAll(listaProd_catalogo.getProductos());
                //dm_list_catalogo.setCatalogoList(_catalList);
                bgRealm.insert(listaProd_madrugon.getProductos());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v(TAG,"writeMadrugon... ---------------ok--------------");
                Log.v(TAG, "madrugon: " + new Gson().toJson(listaProd_madrugon.getProductos()));

                dismissProgress();
                terminatedProcess();
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"writeMadrugon... ---------------error--------------");
                Log.e(TAG,"writeMadrugon... "+error.getMessage());
                //  msgToast("writeCatalogo... "+error.getMessage());
                dismissProgress();
                //realm.close();
                //   errorLoadInitData();
            }
        });
    }

    private void terminatedProcess(){
        if(getActivity()!=null){
            if(responseBanner!=null && responseUrlCatalogos!=null){
                mPreferences.setDataInit(getActivity(), responseBanner, responseUrlCatalogos);

//        updateOnlyBannerAndPDF =false;

                mPreferences.setVersionCatalogo(getActivity(), responseBanner.getVersion());
                Log.e(TAG, "ResponseVersion de catalogo: "+ responseBanner.getVersion());
            }


            mPreferences.setChangeCampana(getActivity(), false);
            mPreferences.setUpdateBannerAndPDF(getActivity(), false);

        }


        dismissProgress();

    }


    public void showpremio(String descripcion){


        System.out.println("Enter name men");
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.premio_layout, null);


        final android.app.AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setView(deleteDialogView);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();


        deleteDialog.setCancelable(false);


        TextView edtdes = (TextView) deleteDialogView.findViewById(R.id.txtdescripcion);
        edtdes.setText(descripcion);

        Button btnclose = deleteDialogView.findViewById(R.id.buttonclose);

        Button btncancel = deleteDialogView.findViewById(R.id.btncancel);

        Button btnok = deleteDialogView.findViewById(R.id.btnok);

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((MenuActivity)getActivity()).resetmenu();
                deleteDialog.dismiss();
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((MenuActivity)getActivity()).resethacerpedido();
                deleteDialog.dismiss();

            }
        });



        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                deleteDialog.dismiss();



            }
        });

        deleteDialog.show();

        int width = (int)(displayRectangle.width() * 7/8);
        int heigth = (int)(displayRectangle.height() * 6/8);

        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



    }

}
