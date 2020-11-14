package com.dupreinca.dupree.mh_fragments_menu.pedidos;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;

import com.dupreeinca.lib_api_rest.controller.BannerController;
import com.dupreeinca.lib_api_rest.model.dto.response.BannerDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ListProductCatalogoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ProductCatalogoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.UrlsCatalogosDTO;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.dupreeinca.lib_api_rest.controller.PedidosController;
import com.dupreeinca.lib_api_rest.enums.EnumLiquidar;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarProducto;
import com.dupreeinca.lib_api_rest.model.dto.request.PrePedidoSend;
import com.dupreeinca.lib_api_rest.model.dto.response.EstadoPrePedidoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidarDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ResultEdoPrePedido;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Catalogo;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.FullscreenActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentPrePedidosHacerBinding;
import com.dupreinca.dupree.mh_adapters.CatalogoListAdapter;
import com.dupreinca.dupree.mh_adapters.base.TabViewPager.PrePedidosPagerAdapter;
import com.dupreinca.dupree.mh_adapters.base.TabViewPager.TabManagerFragment;
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

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */

public class HacerPrePedidoFragment extends TabManagerFragment implements BasePedido, CatalogoHolder.Events {

    public static final String TAG = HacerPrePedidoFragment.class.getName();

    public FragmentPrePedidosHacerBinding binding;
    private PedidosController pedidosController;


    public final static String PEDIDO_NUEVO = "0";
    public final static String PEDIDO_MODIFICAR = "1";
    public static  final String BROACAST_DATA="broacast_data";

    private ResultEdoPrePedido resultEdoPedido;

    private boolean enable = true;
    private PrePedidosPagerAdapter prePedidosPagerAdapter;


    //FILTRO CONTROL
    RealmResults<Catalogo> querycat;
    private List<Catalogo> listFilter;
    private CatalogoListAdapter adapter_catalogo;
    private Realm realm;
    //FILTRO CONTROL

    private Profile perfil;
    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";
    Animation rotation;
    private BannerController bannerController;

    private String campana;

    public void loadData(Profile perfil){
        this.perfil = perfil;
    }

    private boolean productsEditable=false;
    private boolean offersEditable=false;

    public Boolean conpedido=false;


    ListProductCatalogoDTO listaProd_catalogo;

    public HacerPrePedidoFragment() {
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
        return prePedidosPagerAdapter;
    }

    //MARK: TabManagerFragment
    @Override
    protected List<ModelList> setItems() {
        List<ModelList> items = new ArrayList<>();
        items.add(new ModelList(R.drawable.ic_shopping_cart_white_24dp, getString(R.string.carrito)));
        return items;
    }

    //MARK: BaseFragment
    @Override
    protected int getMainLayout() {
        return R.layout.fragment_pre_pedidos_hacer;
    }

    //MARK: TabManagerFragment
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentPrePedidosHacerBinding) view;
        Log.e(TAG, "initViews()");
        timeinit = System.currentTimeMillis();
        bannerController =  new BannerController(getActivity());
        prePedidosPagerAdapter = new PrePedidosPagerAdapter(getChildFragmentManager());
        binding.pagerView.addOnPageChangeListener(mOnPageChangeListener);
        binding.pagerView.setOffscreenPageLimit(3);
        binding.fabSendPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEnable()) {
                    msgToast(getString(R.string.pedido_no_puede_modificarse_pre));
                    return;
                }
                if(prePedidosPagerAdapter.getCarritoFragment().validate()){
                    testRefreshServer();
                } else {
                    msgToast(getString(R.string.no_se_detectaron_cambios));
                }
            }
        });

        //CONTROL DE FILTROS Y CATALOGO
        realm = Realm.getDefaultInstance();

        binding.ctcRcvFilter.setVisibility(View.GONE);
        binding.rcvFilterPedido.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.rcvFilterPedido.setHasFixedSize(true);

        //listPremios = new ArrayList<>();
        listFilter = new ArrayList<>();
        adapter_catalogo = new CatalogoListAdapter(listFilter, this);
        binding.rcvFilterPedido.setAdapter(adapter_catalogo);

        binding.swipePedidos.setOnRefreshListener(mOnRefreshListener);
        binding.swipePedidos.setEnabled(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        timeinit = System.currentTimeMillis();

    }

    @Override
    protected void onLoadedView() {
        Log.e(TAG, "onLoadedView()");
        pedidosController = new PedidosController(getContext());

        controlVisible(false, "");
        filterCatalogoDB("");//mostrar toodo el catalogo
        checkEdoPedido();
    }

    private MenuItem searchItem;
    private SearchView searchView;
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
        simpleDialog.loadData(getString(R.string.guardar_cambios_server), getString(R.string.desea_guardar_cambios_server_pre));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    sendToServer();
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    private void sendToServer(){
        showProgress();

        PrePedidoSend send = obtieneProductos();
        Log.e(TAG, "sendToServer() -> send: "+new Gson().toJson(send));
        //Ajustar Metodo del DAO
        pedidosController.controllerPrePedido(send, new TTResultListener<LiquidarDTO>() {
            @Override
            public void success(LiquidarDTO result) {
                dismissProgress();
                if(result != null && result.getCodigo() != null){
                    if(result.getCodigo().equals(EnumLiquidar.OK.getKey())){
                        NumberFormat formatter = NumberFormat.getInstance(Locale.US);

                        String  msg = "Total: "
                                .concat("$".concat(formatter.format(Float.parseFloat(result.getTotal_pedido()))))
                                .concat(". ")
                                .concat(result.getMensaje());

                        showSnackBarDuration(msg,15000);
                        prePedidosPagerAdapter.getCarritoFragment().pedidoEndviadoexitosamente();

                        initFAB();

                        clearAllData();
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
    public PrePedidoSend obtieneProductos(){
        String id_pedido = resultEdoPedido!=null ? resultEdoPedido.getId_pedido() : "";
        String campana  = resultEdoPedido!=null ? resultEdoPedido.getCampana() : "";
        List<LiquidarProducto> productos = new ArrayList<>();
        total_pedido = 0;
        List<ItemCarrito> itemCarritoList = prePedidosPagerAdapter.getCarritoFragment().getListFilterCart();
        if(itemCarritoList != null){
            for(ItemCarrito item : itemCarritoList) {
                switch (item.getType()){
                    case ItemCarrito.TYPE_CATALOGO:
                        productos.add(new LiquidarProducto(String.valueOf(item.getId()),  item.getCantidad(),item.getValor()));
                        total_pedido = total_pedido + item.getCantidad() * Double.parseDouble(item.getValor());
                        break;
                }
            }
        }
        return new PrePedidoSend(id_pedido,campana,productos);
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
        setTabIcons(prePedidosPagerAdapter.PAGE_CARRITO, isVisible);
        //   setTabIcons(prePedidosPagerAdapter.PAGE_OFFERS, isVisible);

        binding.tvNombreAsesora.setText(text);
        binding.ctxNameAsesora.setVisibility(isVisible && !TextUtils.isEmpty(text) ? View.VISIBLE : View.GONE);
        enableSearch(isVisible);
        fabShow(isVisible);
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
        if(resultEdoPedido != null) {
            controlVisible(true, resultEdoPedido.getAsesora());
            //VERIFICA SI CAMBIO LA CAMPAÑA..

            String campanaActualServer = resultEdoPedido.getCampana();
            String campanaActualLocal = dataStore.getCampanaActual();

            System.out.println("Hacerpre llego updateview result");


            if (campanaActualLocal == null) {
                dataStore.setCampanaActualPre(campanaActualServer);// se respalda la campañ actual
            } else if (!campanaActualLocal.equals(campanaActualServer)) {//si cambio la campaña
                // se borrar e pedido actual (prductos y ofertas) de la DB
                campana = campanaActualServer;
                dataStore.setCampanaActual(campana);// se respalda la campañ actual
                dataStore.setChangeCampana(true);

                if (conpedido) {


                }
                else{

                    deleteCatalogo();
                }


                getBanner();


                showProgress();
            //    Intent intent = new Intent(getActivity(), FullscreenActivity.class);
             //   intent.putExtra("campanaActual", campanaActualServer);
             //   startActivity(intent);
             //   getActivity().finish();
              //  return;
            }
            else{

                deleteCatalogo();
                campana = campanaActualServer;
                dataStore.setCampanaActual(campana);// se respalda la campañ actual
                dataStore.setChangeCampana(true);
                getBanner();

            }
            selectAction();
        }
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



    public void startAnimation(){
        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setFillAfter(true);

    }

    private void selectAction(){
        Log.e(TAG, "selectAction()");
        switch (resultEdoPedido.getEstado_pedido()) {
            case PEDIDO_NUEVO:
                // Todoel proceso desde cero, puede existir algo loca
                //borra los productos de la DB que esten pedidos con anterioridad
                Log.e(TAG, "PEDIDO NUEVO");//no llega nada de productos nuevos, se mantienen los de la db local
                enableEdit(true);

                prePedidosPagerAdapter.getCarritoFragment().sincCatalogoDB(resultEdoPedido.getProductos().getProductos(), false);//Se da prioridad al pedido local
                prePedidosPagerAdapter.getCarritoFragment().clearEditable();
                prePedidosPagerAdapter.getCarritoFragment().setEnable(true);

                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();//actualiza el carrito

                break;
            case PEDIDO_MODIFICAR:
                //Si, es editar se da prioridad a lo que esta en el server
                Log.e(TAG, "PEDIDO A MODIFICAR");
                enableEdit(false); //Deshabilita busqqueda carro de compras (boton lupa)
                prePedidosPagerAdapter.getCarritoFragment().sincCatalogoDB(resultEdoPedido.getProductos().getProductos(), false);//Se borra el pedido local
                prePedidosPagerAdapter.getCarritoFragment().clearEditable();
                prePedidosPagerAdapter.getCarritoFragment().setEnable(false); //Deshabilita botones + y - del carro de compras

                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();//actualiza el carrito
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
            enableSearch(true);
            fabShow(true);
            prePedidosPagerAdapter.getCarritoFragment().setEnable(isEnable());
            prePedidosPagerAdapter.getCarritoFragment().updateCarrito();

            hideSearchView();
            KeyBoard.hide(getActivity());

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    //    private String textFilter="";
    public void filterCatalogo(String textFilter){
        // textFilter = "10788";
//        this.textFilter=textFilter;
        Log.e(TAG, "textFilter: "+textFilter+", isEnable(): "+isEnable());
        if(isEnable()) {
            if (getPageCurrent() == prePedidosPagerAdapter.PAGE_CARRITO) {
                if (textFilter.length() > 2) {
                    binding.ctcRcvFilter.setVisibility(View.VISIBLE);
                    filterCatalogoDB(textFilter);
                } else {
                    binding.ctcRcvFilter.setVisibility(View.GONE);
                }
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
        //prePedidosPagerAdapter.getOffersFragment().filterOffersDB("");
        prePedidosPagerAdapter.getCarritoFragment().clearCartDB();
        prePedidosPagerAdapter.getCarritoFragment().updateCarrito();

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
        pedidosController.getEstadoPrePedido(new Identy(cedula), new TTResultListener<EstadoPrePedidoDTO>() {
            @Override
            public void success(EstadoPrePedidoDTO result) {
                dismissProgress();
                resultEdoPedido = result.getResult();

                if (result.getResult().getProductos().getProductos().size()>0) {
                conpedido = true;

                }

                System.out.println("Hacerpre llego updateview");

                updateView();

            }

            @Override
            public void error(TTError error) {
                setPageCurrent(prePedidosPagerAdapter.PAGE_CARRITO);
                System.out.println("Hacerpre llego updateview erro "+error.getMessage());
                dismissProgress();
                checkSession(error);

            }
        });

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
            realm.beginTransaction();
            try {
                listFilter.get(position).setCantidad(listFilter.get(position).getCantidad()+1);
                adapter_catalogo.notifyItemChanged(position);
                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();
                realm.commitTransaction();
            } catch (Throwable e) {
                Log.v(TAG, "increaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
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
                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();
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
                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();

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
                prePedidosPagerAdapter.getCarritoFragment().updateCarrito();
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
    //Buscar Codigos del catalogo
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
    public Profile getPerfil(){
        String jsonPerfil = mPreferences.getJSON_TypePerfil(getActivity());
        if(jsonPerfil!=null)
            return new Gson().fromJson(jsonPerfil, Profile.class);

        return null;
    }
    @Override
    public void onDestroy() {

        if(perfil!=null){
            timeend = System.currentTimeMillis();
            long finaltime= timeend-timeinit;
            int timesec = (int)finaltime/1000;

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"hacerduproyecta");
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
    public void updateTotal(){
        obtieneProductos();
        Log.e(TAG, "Update Total: "+total_pedido);

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        fabTitle("$ ".concat(formatter.format(total_pedido)));
    }

    @Override
    public void updateCarrito() {
        prePedidosPagerAdapter.getCarritoFragment().updateCarrito();
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



    private void getBanner(){
        startAnimation();
        responseBanner = null;

        bannerController.getBanner(new TTResultListener<BannerDTO>() {
            @Override
            public void success(BannerDTO result) {
                Log.e(TAG, "getBanner() -> success(): " + new Gson().toJson(result));
                responseBanner(result);
            }

            @Override
            public void error(TTError error) {
                Log.e(TAG, "getBanner() -> error(): " + new Gson().toJson(error));

                dismissProgress();
                msgToast("getBanner() -> error(): " + new Gson().toJson(error));

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
            msgToast("ResponseB() -> error(): " +  new Gson().toJson(responseBanner));


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
                Log.e(TAG, "getFilesCatalogos() -> error(): " + new Gson().toJson(error));

                dismissProgress();
                msgToast("getFilesCatalogos() -> error(): " +  new Gson().toJson(error));

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
            msgToast("responseUrlCatalogo() -> error():json " + responseUrlCatalogos.toString());

            //   errorLoadInitData();
        }
    }

    private void obtainCatalogo(){
        bannerController.getProductos(campana, new TTResultListener<ProductCatalogoDTO>() {
            @Override
            public void success(ProductCatalogoDTO result) {
                Log.e(TAG, "obtainCatalogo() -> success(): " + new Gson().toJson(result));
                //un detalle envia 200 con error 404
                if (result.getCode() == 404) {
                    //     errorLoadInitData();
                    dismissProgress();
                    msgToast("obtainCatalogo() -> error():json " +  new Gson().toJson(result));


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
                                //       errorLoadInitData();
                                dismissProgress();
                                msgToast("responseCatalogo() -> error():json " + e.getMessage());

                            }
                        } else {
                            // errorLoadInitData();

                            dismissProgress();
                            msgToast("responseCatalogo() -> error():json " + error.getMessage());

                        }
                    }
                    else{
                        dismissProgress();
                        msgToast("responseCatalogo() -> error():json " + error.getMessage());

                        //errorLoadInitData();
                    }
                }


            }
        });


//        new Http(FullscreenActivity.this).getProductosCatalogo(false);
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
                //realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e(TAG,"writeCatalogo... ---------------error--------------");
                Log.e(TAG,"writeCatalogo... "+error.getMessage());
                msgToast("writeCatalogo... "+error.getMessage());
                dismissProgress();
                //realm.close();
                //   errorLoadInitData();
            }
        });
    }

    private void terminatedProcess(){
        mPreferences.setDataInit(getActivity(), responseBanner, responseUrlCatalogos);

//        updateOnlyBannerAndPDF =false;

        if(responseBanner.getVersion()!=null){
            mPreferences.setVersionCatalogo(getActivity(), responseBanner.getVersion());
            Log.e(TAG, "ResponseVersion de catalogo: "+ responseBanner.getVersion());

        }


        mPreferences.setChangeCampana(getActivity(), false);
        mPreferences.setUpdateBannerAndPDF(getActivity(), false);

        dismissProgress();
        if(mPreferences.isLoggedIn(getActivity())){
            // gotoPanel();//si esta ya logueado, es solo una actualizacion y regresa al panel
        } else {
            //gotoMain();
        }
    }
}
