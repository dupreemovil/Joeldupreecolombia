package com.dupreinca.dupree.mh_fragments_menu.pedidos.Carrito;


import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dupreeinca.lib_api_rest.controller.PedidosController;
import com.dupreeinca.lib_api_rest.enums.EnumLiquidar;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidaSend;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarMadrugon;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarProducto;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidarSend;
import com.dupreeinca.lib_api_rest.model.dto.response.DetalleMadrugon;
import com.dupreeinca.lib_api_rest.model.dto.response.DetalleProductos;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidaDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidarDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Catalogo;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarritoM;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.MadCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Madrugon;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.ItemCarrito;
import com.dupreeinca.lib_api_rest.model.dto.response.realm.Oferta;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentProductsBinding;
import com.dupreinca.dupree.mh_adapters.CarritoListAdapter;
import com.dupreinca.dupree.mh_adapters.CarritoMListAdapter;
import com.dupreinca.dupree.mh_adapters.CarritoPListAdapter;
import com.dupreinca.dupree.mh_adapters.DotsIndicatorDecoration;
import com.dupreinca.dupree.mh_adapters.LinePagerIndicatorDecoration;
import com.dupreinca.dupree.mh_adapters.MadrugonAdapter;
import com.dupreinca.dupree.mh_dialogs.SimpleDialog;
import com.dupreinca.dupree.mh_fragments_menu.pedidos.BasePedido;
import com.dupreinca.dupree.mh_holders.CarritoHolder;
import com.dupreinca.dupree.mh_holders.MadrugonHolder;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarritoFragment extends BaseFragment implements CarritoHolder.Events, MadrugonHolder.Events {

    private final String TAG = CarritoFragment.class.getName();
    public static  final String BROACAST_DATA="broacast_data";

    private List<ItemCarrito> listFilterCart = new ArrayList<>();//, listSelected;
    private List<ItemCarrito> listFilterCartM = new ArrayList<>();
    private List<ItemCarritoM> listM = new ArrayList<>();
    private CarritoListAdapter listAdapter;
    private CarritoMListAdapter listMAdapter;
    private PedidosController pedidosController;

    public Boolean carritoM=false;
    public Boolean resumenM=false;

    private CarritoPListAdapter listPAdapter;
    private CarritoPListAdapter listOAdapter;

    private MadrugonAdapter listAdapterm;

    private Realm realm;
    private boolean enable=true;

    public CarritoFragment() {
        // Required empty public constructor
    }

    public static CarritoFragment newInstance() {
        Bundle args = new Bundle();

        CarritoFragment fragment = new CarritoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_products;
    }

    public FragmentProductsBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        onAttachFragment(getParentFragment());

        binding = (FragmentProductsBinding) view;
        realm = Realm.getDefaultInstance();
        pedidosController = new PedidosController(getContext());

        binding.refresh.setEnabled(false);
        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recycler.setHasFixedSize(true);

       // binding.layoutbtn.setVisibility(View.VISIBLE);
        listFilterCart = new ArrayList<>();
        listFilterCartM = new ArrayList<>();
        listAdapter = new CarritoListAdapter(listFilterCart, this);

        listAdapterm = new MadrugonAdapter(listFilterCartM, this);

        binding.recycler.setAdapter(listAdapter);
        binding.recycler.setAdapter(listAdapterm);

        binding.buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.refresh.getVisibility()==View.VISIBLE && carritoM){


                    binding.buttonnext.setText("SIGUIENTE");
                    binding.recycler1.setVisibility(View.GONE);
                    binding.recycler.setVisibility(View.VISIBLE);
                    System.out.println("El scroll "+binding.scroll1.getScrollBarSize());
                    binding.layoutpage.setVisibility(View.GONE);
                    viewParent.showsearch();
                    returncarrito();
                    carritoM = false;
                }
                else if(resumenM==true){
                    resumenM=false;
                    binding.buttonnext.setText("SIGUIENTE");
                    binding.resumen.setVisibility(View.GONE);
                    binding.refresh.setVisibility(View.VISIBLE);
                    viewParent.showsearch();
                    sendtoServerP();
                }
                else{


                }

            }
        });

        binding.buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.refresh.getVisibility()==View.VISIBLE && carritoM){
                    showresumen();

                }
                else if(binding.recycler.getVisibility()==View.VISIBLE && !carritoM){

                    System.out.println("El rec m!");

                    sendtoServerP();
                    binding.recycler1.setVisibility(View.VISIBLE);
                    binding.recycler.setVisibility(View.GONE);
                    binding.buttonback.setVisibility(View.VISIBLE);
                }
                else{
                    System.out.println("El rec m");
                    sendServer();

                }



            }
        });

        //si es gerente limpia lo seleccionado, luego filtra

        if(!dataStore.getTipoPerfil().getPerfil().equals(Profile.ADESORA) && !dataStore.getTipoPerfil().getPerfil().equals(Profile.LIDER)){
            clearCartDB();

        } else {
            updateCarrito();//mostrar todo el catalogo
        }
    }

    //MARK: CatalogoHolder.Events
    @Override
    public void onAddCartClick(ItemCarrito dataRow, int row) {
        Log.e(TAG,"onAddCartClick: "+String.valueOf(row));
        testAddCart(row);
    }

    public void showresumen(){

        LiquidaSend send = obtainProductsL();
        List<ItemCarrito> listap =filterCatalogoDB("");
        List<ItemCarrito> listao =filterCatalogoDO("");


        viewParent.hidesearch();
        binding.buttonback.setVisibility(View.VISIBLE);

        List<ItemCarritoM> lista = filterCatalogoDBMAD("");


        resumenM = true;
        int totalm =0;
        int totalp =0;
        int totalo =0;
        for(int j=0;j<lista.size();j++){
            System.out.println("pro "+ lista.get(j).getTallasel() +" name "+lista.get(j).getName());

            totalm = totalm +(int)Double.parseDouble(lista.get(j).getValor())*lista.get(j).getCantidad();

        }

        for(int i=0;i<listap.size();i++){

            totalp = totalp + (int)Double.parseDouble(listap.get(i).getValor())*listap.get(i).getCantidad();
        }

        for(int s=0;s<listao.size();s++){

            totalo = totalo + (int)Double.parseDouble(listao.get(s).getValor())*listao.get(s).getCantidad();
        }

        binding.recyclerp.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recyclerp.setHasFixedSize(true);

        binding.recyclerm.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recyclerm.setHasFixedSize(true);

        binding.recyclero.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recyclero.setHasFixedSize(true);

        listM = new ArrayList<>();

        listMAdapter = new CarritoMListAdapter(lista);
        listPAdapter = new CarritoPListAdapter(listap);
        System.out.println("La lista o "+listao.size());
        listOAdapter = new CarritoPListAdapter(listao);
        binding.txtpage.setText("1/");


        binding.buttonnext.setText("FINALIZAR");
        binding.refresh.setVisibility(View.GONE);
        binding.resumen.setVisibility(View.VISIBLE);

        binding.recyclerm.setAdapter(listMAdapter);
        binding.recyclerp.setAdapter(listPAdapter);
        binding.recyclero.setAdapter(listOAdapter);

        binding.txttotalp.setText("Total Catalogo: $"+String.format("%,d",totalp));
        binding.txttotal.setText("Total Super Ofertas: $"+String.format("%,d",totalm));
        binding.txttotalo.setText("Total Ofertas: $"+String.format("%,d",totalo));





    }

    private void sendServer(){
        showProgress();


        LiquidaSend send = obtainProductsL();
        Log.e(TAG, "sendToServer() -> send: "+new Gson().toJson(send));
        pedidosController.liquidaPedido(send, new TTResultListener<LiquidaDTO>() {
            @Override
            public void success(LiquidaDTO result) {

                System.out.println("El result pedido "+new Gson().toJson(result));


                dismissProgress();

                if(result.getActiva_premio().contains("1")){

                    System.out.println("El result pedido act ");

                              showpremio(result.getMensaje_premio());

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

                        System.out.println("Set visible mad");
                                 pedidoEndviadoexitosamente();
                               ofertaEndviadoexitosamente();

                        //       initFAB();

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

                System.out.println("El error liq "+ error.getErrorBody());
                dismissProgress();
                checkSession(error);
            }
        });
//        new Http(getActivity()).liquidarPedido(obtainProductsLiquidate(), TAG, BROACAST_LIQUIDATE_PRODUCTS);
    }

    private void clearAllData(){
        setEnable(false);



        //EN ESTE CASO SE ESTA MODO GERENTE, SE LIMPIA TODITO EL PEDIDO AL CAMBIAR DE ASESORA

       clearCartDB();
       deleteOferta();
        updateCarrito();

        binding.layoutbtn.setVisibility(View.GONE);
    }

    public void deleteOferta(){
        Log.e(TAG, "deleteOferta2()");

            Log.v(TAG, "deleteOferta2... ---------------filterOdeleteOferta2ffersDB--------------");
            realm.beginTransaction();
            try {
                realm.delete(Oferta.class);
                realm.commitTransaction();

                filterOffersDB("");
            } catch (Throwable e) {
                Log.v(TAG, "deleteOferta2... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }

    }

    public void ofertaEndviadoexitosamente(){

            Log.v(TAG, "ofertaEndviadoexitosamente... ---------------ofertaEndviadoexitosamente--------------");
            realm.beginTransaction();
            try {
              //  listFilterOffers.clear();


                String []fieldNames={"cantidad","cantidadServer"};
                Sort sort[]={Sort.DESCENDING,Sort.DESCENDING};

                RealmResults<Oferta> querycat = realm.where(Oferta.class).contains("id", "")
                        .findAllSorted(fieldNames, sort);

                //querycat = realm.where(Oferta.class).findAllSorted("cantidad",Sort.DESCENDING);

//                updateTotal();
                realm.commitTransaction();
            } catch (Throwable e) {
                Log.v(TAG, "ofertaEndviadoexitosamente... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }

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

    public LiquidaSend obtainProductsL(){
        String id_pedido = viewParent.id_pedido();
        List<String> paquetones= new ArrayList<>();
        List<LiquidarProducto> ofertas = new ArrayList<>();
        List<LiquidarProducto> productos = new ArrayList<>();

        List<ItemCarrito> listac =filterCatalogoDB("");
        List<ItemCarritoM> lista = filterCatalogoDBMAD("");
        List<LiquidarMadrugon> madrugon = new ArrayList<>();

        List<ItemCarrito> itemCarritoList = getListFilterCart();
        for(ItemCarrito item : itemCarritoList) {
            switch (item.getType()){

                case ItemCarrito.TYPE_CATALOGO:
                    productos.add(new LiquidarProducto(String.valueOf(item.getId()), item.getCantidad()));

                    break;
                case ItemCarrito.TYPE_OFFERTS:
                    ofertas.add(new LiquidarProducto(String.valueOf(item.getId()), item.getCantidad()));

                    break;
            }

        }



        for(int j=0;j<lista.size();j++){


            LiquidarMadrugon liq = new LiquidarMadrugon(lista.get(j).getId(),lista.get(j).getCantidad(),lista.get(j).getValor(),lista.get(j).tallasel);
            madrugon.add(liq);


        }




        return new LiquidaSend(id_pedido,productos, madrugon,ofertas,"MOVIL",id_pedido);
    }


    //MARK: CatalogoHolder.Events
    @Override
    public void onIncreaseClick(ItemCarrito dataRow, int row) {
        Log.e(TAG,"onIncreaseClick, pos:  "+row);
        int cantidad = dataRow.getCantidad();
        increaseCart(row, cantidad);
    }

    //MARK: CatalogoHolder.Events
    @Override
    public void onDecreaseClick(ItemCarrito dataRow, int row) {
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

    //MARK: CatalogoHolder.Events
    @Override
    public void onAddEditableClick(boolean isEditable) {
        Log.e(TAG,"onAddEditableClick, status:  "+isEditable);
        if(viewParent != null){
            viewParent.productsEditable(isEditable);
        }
    }


    @Override
    protected void onLoadedView() {

    }

    private Profile getTypePerfil(){
        String jsonPerfil = mPreferences.getJSON_TypePerfil(getActivity());
        if(jsonPerfil != null){
            return new Gson().fromJson(jsonPerfil, Profile.class);
        }
        msgToast("Hubo un problema desconocido, se recomienda cerrar sesión e iniciar nuevamente");
        return null;
    }

    //antes de enviar
    public boolean validate(){
        if(listFilterCart.size()>0){
            for(int i=0;i<listFilterCart.size(); i++){
                if (listFilterCart.get(i).getCantidad() != listFilterCart.get(i).getCantidadServer())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void setvisiblemad(){

        System.out.println("El CARRITO recycler vis ");
      //  binding.recycler.setVisibility(View.GONE);
   //     binding.recyclermad.setVisibility(View.VISIBLE);
    }


    public void testAddCart(int row){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.agregar), getString(R.string.desea_agregar_pedido));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    addCart(row);
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void testAddCartM(int row){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.agregar), getString(R.string.desea_agregar_pedido));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    addCartM(row);
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }


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

    public void testRemoveCartm(int row){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.remover), getString(R.string.desea_remover_pedido));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    removeCartM(row);
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume()");
        //setSelectedItem(oldItem);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause()");
    }

    public void pedidoEndviadoexitosamente(){
        Log.v(TAG, "pedidoEndviadoexitosamente... -----------------------------------");
        if(listFilterCart.size()>0) {
            try {
                realm.beginTransaction();
                    RealmResults<Catalogo> query = realm.where(Catalogo.class).findAll();
                    for(int i=0; i<query.size(); i++){
                        query.get(i).setCantidadServer(query.get(i).getCantidad());
                    }
                realm.commitTransaction();
                Log.v(TAG, "pedidoEndviadoexitosamente... --------------  OK  --------------");
                updateCarrito();
            } catch (Throwable e) {
                Log.v(TAG, "pedidoEndviadoexitosamente... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                //break;
                throw e;
            }
        }
    }

    public void hidebtn(){
        binding.layoutbtn.setVisibility(View.GONE);
    }
    public void showbtn(){
        binding.layoutbtn.setVisibility(View.VISIBLE);
        binding.buttonback.setVisibility(View.VISIBLE);
    }

    private void increaseCart(int i, int cantidad){
        Log.i(TAG, "increaseCart(), pos: "+i);
        if(i>-1 && i<listFilterCart.size()) {
            try {
                realm.beginTransaction();
                switch (listFilterCart.get(i).getType()){
                    case ItemCarrito.TYPE_CATALOGO:
                        Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(catalogo!=null) {
                            catalogo.setCantidad(cantidad + 1);
                        }
                        break;
                    case ItemCarrito.TYPE_OFFERTS:
                        Oferta oferta = realm.where(Oferta.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(oferta!=null) {
                            oferta.setCantidad(cantidad + 1);
                        }
                        break;
                }
                realm.commitTransaction();

                listFilterCart.get(i).setCantidad(cantidad + 1);
                listAdapter.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "increaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void decreaseCart(int i, int cantidad){
        Log.i(TAG, "decreaseCart(), pos: "+i);
        if(i>-1 && i<listFilterCart.size()) {
            try {
                realm.beginTransaction();
                switch (listFilterCart.get(i).getType()){
                    case ItemCarrito.TYPE_CATALOGO:
                        Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(catalogo!=null) {
                            catalogo.setCantidad(cantidad - 1);
                        }
                        break;
                    case ItemCarrito.TYPE_OFFERTS:
                        Oferta oferta = realm.where(Oferta.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(oferta!=null) {
                            oferta.setCantidad(cantidad - 1);
                        }
                        break;
                }
                realm.commitTransaction();

                listFilterCart.get(i).setCantidad(cantidad - 1);
                listAdapter.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "decreaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void addCart(int i){
        Log.i(TAG, "addCart(), pos: "+i);
        if(i>-1 && i<listFilterCart.size()) {
            try {
                realm.beginTransaction();
                switch (listFilterCart.get(i).getType()){
                    case ItemCarrito.TYPE_CATALOGO:
                        Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(catalogo!=null) {
                            catalogo.setCantidad(1);
                        }
                        break;
                    case ItemCarrito.TYPE_OFFERTS:
                        Oferta oferta = realm.where(Oferta.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(oferta!=null) {
                            oferta.setCantidad(1);
                        }
                        break;
                }
                realm.commitTransaction();

                listFilterCart.get(i).setCantidad(1);// se predetermina cantidad en 1
                listAdapter.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "addCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void removeCart(int i){
        Log.i(TAG, "removeCart(), pos: "+i);
        if(i>-1 && i<listFilterCart.size()) {
            try {
                realm.beginTransaction();
                switch (listFilterCart.get(i).getType()){
                    case ItemCarrito.TYPE_CATALOGO:
                        Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(catalogo!=null) {
                            catalogo.setCantidad(0);
                            catalogo.deleteFromRealm();
                        }
                        break;
                    case ItemCarrito.TYPE_OFFERTS:
                        Oferta oferta = realm.where(Oferta.class).equalTo("id", listFilterCart.get(i).getId()).findFirst();
                        if(oferta!=null) {
                            oferta.setCantidad(0);
                            oferta.deleteFromRealm();
                        }
                        break;
                }
                realm.commitTransaction();

                listFilterCart.get(i).setCantidad(0);// se predetermina cantidad en 1
                listAdapter.notifyItemChanged(i);

//                updateTotal();

                updateCarrito();//esto se hace para que actualize la lista y quite el elemento
            } catch (Throwable e) {
                Log.v(TAG, "removeCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    //


    private void increaseCartM(int i, int cantidad){
        Log.i(TAG, "increaseCart(), pos: "+i);
        if(i>-1 && i<listFilterCartM.size()) {
            try {
                realm.beginTransaction();
                System.out.println("Sinc el id "+listFilterCartM.get(i).getId());
                Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).findFirst();
                MadCarrito madcarrito = realm.where(MadCarrito.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).equalTo("tallasel",listFilterCartM.get(i).getTalla()).findFirst();

                if(madcarrito!=null){

                    madcarrito.setCantidad(cantidad+1);
                    madcarrito.setTallasel(listFilterCartM.get(i).getTalla());
                }
                else{

                    MadCarrito madc = realm.createObject(MadCarrito.class, UUID.randomUUID().toString());
                    // mad.setId(Integer.toString(listFilterCartM.size()+1));
                    madc.setCampana("");
                    madc.setName(listFilterCartM.get(i).getName());
                    madc.setDescr("");
                    madc.setUrl_img(listFilterCartM.get(i).getUrl_img());
                    madc.setTipo_oferta("");
                    madc.setOrde_prom("");
                    madc.setPrec_vent(listFilterCartM.get(i).getValor());
                    madc.setTallasel(listFilterCartM.get(i).getTalla());
                    madc.setCodi_vent(listFilterCartM.get(i).getId());
                    madc.setCantidad(1);
                    realm.insert(mad);

                }



                if(mad!=null) {


                    mad.setTallasel(listFilterCartM.get(i).getTalla());
                    mad.setCantidad(cantidad + 1);
                }
                realm.commitTransaction();

                listFilterCartM.get(i).setCantidad(cantidad + 1);
                listFilterCartM.get(i).setTallasel(listFilterCartM.get(i).getTalla());
                listAdapterm.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "increaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void decreaseCartM(int i, int cantidad){
        Log.i(TAG, "decreaseCart(), pos: "+i+" cant "+cantidad);
        if(i>-1 && i<listFilterCartM.size()) {
            try {
                realm.beginTransaction();

                Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).findFirst();
                if(mad!=null) {
                    mad.setCantidad(cantidad - 1);
                }
                MadCarrito madcarrito = realm.where(MadCarrito.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).findFirst();

                if(madcarrito!=null){

                    madcarrito.setCantidad(cantidad-1);
                    madcarrito.setTallasel(listFilterCartM.get(i).getTalla());
                }
                else {

                }


                realm.commitTransaction();

                listFilterCartM.get(i).setCantidad(listFilterCartM.get(i).getCantidad() - 1);
                listAdapterm.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "decreaseCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void addCartM(int i){
        Log.i(TAG, "addCart(), pos: "+i);
        if(i>-1 && i<listFilterCartM.size()) {
            try {
                realm.beginTransaction();

                System.out.println("");
                        Madrugon catalogo = realm.where(Madrugon.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).equalTo("tallasel",listFilterCartM.get(i).getTalla()).findFirst();
                        MadCarrito madcarrito = realm.where(MadCarrito.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).equalTo("tallasel",listFilterCartM.get(i).getTalla()).findFirst();
                        if(catalogo!=null) {
                            catalogo.setCantidad(1);
                            System.out.println("La talla sel "+listFilterCartM.get(i).getTalla());
                            catalogo.setTallasel(listFilterCartM.get(i).getTalla());
                        }
                        if(madcarrito!=null){

                            madcarrito.setCantidad(1);
                            madcarrito.setTallasel(listFilterCartM.get(i).getTalla());
                        }
                        else{
                            MadCarrito mad = realm.createObject(MadCarrito.class, UUID.randomUUID().toString());
                           // mad.setId(Integer.toString(listFilterCartM.size()+1));
                            mad.setCampana("");
                            mad.setName(listFilterCartM.get(i).getName());
                            mad.setDescr("");
                            mad.setUrl_img(listFilterCartM.get(i).getUrl_img());
                            mad.setTipo_oferta("");
                            mad.setOrde_prom("");
                            mad.setPrec_vent(listFilterCartM.get(i).getValor());
                            mad.setTallasel(listFilterCartM.get(i).getTalla());
                            mad.setCodi_vent(listFilterCartM.get(i).getId());
                            mad.setCantidad(1);
                            realm.insert(mad);
                        }


                realm.commitTransaction();

                listFilterCartM.get(i).setCantidad(1);// se predetermina cantidad en 1
                listFilterCartM.get(i).setTallasel(listFilterCartM.get(i).getTalla());
//                catalogo.setTallasel(listFilterCartM.get(i).getTalla());
                listAdapterm.notifyItemChanged(i);

                updateTotal();
            } catch (Throwable e) {
                Log.v(TAG, "addCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    private void removeCartM(int i){
        Log.i(TAG, "removeCart(), pos: "+i);
        if(i>-1 && i<listFilterCartM.size()) {
            try {
                realm.beginTransaction();
                Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).findFirst();
                if(mad!=null) {
                    mad.setCantidad(0);
                }


                listFilterCartM.get(i).setCantidad(0);// se predetermina cantidad en 1
                MadCarrito madcarrito = realm.where(MadCarrito.class).equalTo("codi_vent", listFilterCartM.get(i).getId()).findFirst();

                if(madcarrito!=null){

                    madcarrito.deleteFromRealm();
                }
                else {

                }

                realm.commitTransaction();
                listAdapterm.notifyItemChanged(i);
//                updateTotal();

                updateCarritoM();//esto se hace para que actualize la lista y quite el elemento
            } catch (Throwable e) {
                Log.v(TAG, "removeCart... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }



    //

    public List<ItemCarrito> filterCatalogoDB(final String textFilter){
        List<ItemCarrito> listCarritos = new ArrayList<>();
        Log.v(TAG,"filterCatalogoDB... ---------------filterCatalogoDB--------------");
        try {
            realm.beginTransaction();
            RealmResults<Catalogo> query = realm.where(Catalogo.class)
                    .beginGroup()
                        .greaterThan("cantidad",0)
                        .or()
                        .greaterThan("cantidadServer",0)
                    .endGroup()
                    .contains("id",textFilter)
                    .findAllSorted("time").sort("time", Sort.DESCENDING);
            realm.commitTransaction();
            for (Catalogo item : query) {
                listCarritos.add(new ItemCarrito(item));
            }

        } catch(Throwable e) {
            Log.v(TAG,"filterCatalogoDB... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }

        return listCarritos;
    }
    public List<ItemCarrito> filterCatalogoDO(final String textFilter){
        List<ItemCarrito> listCarritos = new ArrayList<>();
        Log.v(TAG,"filterCatalogoDB... ---------------filterCatalogoDB--------------");
        try {
            realm.beginTransaction();
            RealmResults<Oferta> query = realm.where(Oferta.class)
                    .beginGroup()
                    .greaterThan("cantidad",0)
                    .or()
                    .greaterThan("cantidadServer",0)
                    .endGroup()
                    .contains("id",textFilter)
                    .findAllSorted("time").sort("time", Sort.DESCENDING);
            realm.commitTransaction();
            for (Oferta item : query) {
                listCarritos.add(new ItemCarrito(item));
            }

        } catch(Throwable e) {
            Log.v(TAG,"filterCatalogoDB... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }

        return listCarritos;
    }


    public List<ItemCarrito> filterCatalogoDBM(final String textFilter){
        List<ItemCarrito> listCarritos = new ArrayList<>();
        Log.v(TAG,"filterCatalogoDBM... ---------------filterCatalogoDBM--------------");
        try {
            realm.beginTransaction();
            RealmResults<Madrugon> query = realm.where(Madrugon.class)
                    .beginGroup()
                    .endGroup()
                    .contains("codi_vent",textFilter)
                    .findAllSorted("codi_vent").sort("codi_vent", Sort.DESCENDING);

            for (Madrugon item : query) {
                System.out.println("Sinc Car "+item.getCodi_vent()+ " cant "+item.getCantidad());

                MadCarrito madcarrito = realm.where(MadCarrito.class).equalTo("codi_vent", item.getCodi_vent()).findFirst();

                if(madcarrito!=null){
                    item.setCantidad(madcarrito.getCantidad());
                    item.setCantidadServer(madcarrito.getCantidad());
                    item.setTalla(madcarrito.getTallasel());
                    item.setTallasel(madcarrito.getTallasel());


                    System.out.println("Sinc Se encontro "+item.getCodi_vent() +" server "+item.getCantidadServer());
                    listCarritos.add(new ItemCarrito(item));
                }
                else{


                    listCarritos.add(new ItemCarrito(item));
                }

            }
            realm.commitTransaction();
        } catch(Throwable e) {
            Log.v(TAG,"filterCatalogoDBM... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }

        return listCarritos;
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

    public List<ItemCarrito> filterOffersDB(final String textFilter){
        List<ItemCarrito> listCarritos = new ArrayList<>();
        Log.e(TAG, "filterOffersDB(INIT), textFilter: "+textFilter);
//        if(showOffers) {
            Log.v(TAG, "filterOffersDB... ---------------filterOffersDB--------------");
            try {
                realm.beginTransaction();
                RealmResults<Oferta> query = realm.where(Oferta.class)
                        .beginGroup()
                            .greaterThan("cantidad",0)
                            .or()
                            .greaterThan("cantidadServer",0)
                        .endGroup()
                        .contains("id", textFilter)
                        .findAllSorted("time").sort("time", Sort.DESCENDING);
                realm.commitTransaction();

                for (Oferta item : query) {
                    listCarritos.add(new ItemCarrito(item));
                }

                Log.e(TAG, "filterOffersDB(END)");
            } catch (Throwable e) {
                Log.v(TAG, "filterOffersDB... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
//        }

        return listCarritos;
    }

    public void updateCarrito(){
        try{
            listFilterCart.clear();


            listFilterCart.addAll(filterCatalogoDB(""));

            System.out.println("Catalogo Upd "+new Gson().toJson(filterCatalogoDB("")));
            listFilterCart.addAll(filterOffersDB(""));

            listAdapter = new CarritoListAdapter(listFilterCart, this);
            binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
            binding.recycler.setHasFixedSize(true);
            binding.recycler.setAdapter(listAdapter);

           // listAdapter.notifyDataSetChanged();
            binding.buttonnext.setVisibility(View.VISIBLE);
            binding.buttonback.setVisibility(View.GONE);
            updateTotal();
        }catch (Exception ex){

        }
    }

    public void returncarrito(){


        listAdapter = new CarritoListAdapter(listFilterCart, this);
        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setAdapter(listAdapter);
        //si es gerente limpia lo seleccionado, luego filtra

        System.out.println("El perfil "+dataStore.getTipoPerfil().getPerfil());
        if(!dataStore.getTipoPerfil().getPerfil().equals(Profile.ADESORA) && !dataStore.getTipoPerfil().getPerfil().equals(Profile.LIDER) ){
            System.out.println("Se eliminar carrito");
            clearCartDB();
        }
       else{
            updateCarrito();//mostrar todo el catalogo
        }


    }


    public void updateCarritoM(){

        System.out.println("El carrito M");
        carritoM = true;


            if(listFilterCartM.size()>0){


            }
            else{

             //   listFilterCartM.clear();


                List<ItemCarrito> lista = filterCatalogoDBM("");
                if(lista.size()==0){

                    showSnackBarDuration("Se esta procesando la lista Madrugon,un momento porfavor", 3000);
                    return;
                }


                System.out.println();
                listFilterCartM.addAll(lista);
                System.out.println("Sin Carrito M "+lista.size());
                GridLayoutManager lm = new GridLayoutManager(getActivity(),4, GridLayoutManager.HORIZONTAL, false);
                binding.recycler1.setLayoutManager(lm);

                listAdapterm = new MadrugonAdapter(listFilterCartM,this);
                binding.recycler1.setAdapter(listAdapterm);
                binding.recycler1.setHasFixedSize(true);
                int total = lista.size();
                int cantidadpp = 8;
                int paginas = total/cantidadpp;
                binding.layoutpage.setVisibility(View.VISIBLE);

                binding.txtpage.setText("1/"+paginas);

                // final int radius = getResources().getDimensionPixelSize(R.dimen.minImageHeight);
                //final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.mb_aspect_ratio_card_id1);
                //final int color = ContextCompat.getColor(getContext(), R.color.azulDupree);
                //  binding.recycler.addItemDecoration(new DotsIndicatorDecoration(40, 10 * 4, 40, color, color));

                binding.buttonback.setVisibility(View.VISIBLE);

                System.out.println("El carrito M FILTER "+lista.size());

                binding.recycler1.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int visibleItemCount = lm.getChildCount();
                        int totalItemCount = lm.getItemCount();
                        int pastVisiblesItems = lm.findFirstVisibleItemPosition();

                        System.out.append("Visible "+visibleItemCount);
                        System.out.append("Visible P "+pastVisiblesItems);

                        int total = lista.size();
                        int cantidadpp = 8;
                        int paginas = total/cantidadpp;

                        int actual = pastVisiblesItems/cantidadpp +1;
                        binding.txtpage.setText(actual+"/"+paginas);

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //bottom of recyclerview
                        }
                    }
                });

            }



          //  updateTotal();

    }

    protected void showSnackBarDuration(int msg, int duration){
        Activity a = getActivity();
        if(a != null) {
            Snackbar.make(a.getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setDuration(duration).show();
        }
    }

    public void sincCatalogoDB(List<DetalleProductos> listServer, boolean edit){//en modeo edicion, no se toma en cuenta si hay o no pedidos locales
        //en modo nuevo pedido, si hay algo local se deja tal cual
        Log.v(TAG,"sincCatalogoDB... ---------------sincCatalogoDB--------------");
        if( (listFilterCart.size()<1 || edit) && listServer.size()>0) {
            try {
                realm.beginTransaction();
                //listFilter.clear();
                for (int i = 0; i < listServer.size(); i++) {
                    Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", listServer.get(i).getId()).findFirst();// se busca el id
                    if (catalogo != null && catalogo.getId().equals(listServer.get(i).getId())) {//se actualiza localmente lo que trae el API
                        catalogo.setCantidad(listServer.get(i).getCantidad());
                        catalogo.setCantidadServer(listServer.get(i).getCantidad());
                    } else if(catalogo==null){//si no existe, es xq hay que eliminarlo
                        //este caso supone que se modifico el catalogo
                    }
                }

                //debe verificar cuales no llegaron xq fieron eliminados
                for (ItemCarrito item : listFilterCart) {
                    if(item.getType() == ItemCarrito.TYPE_CATALOGO) {//solo se analizan los prosutos, NO ofertas
                        //se busca si hay algun elemento que dejo de estar disponible
                        for (int j = 0; j < listServer.size(); j++) {
                            if (listServer.get(j).getId().equals(item.getId())) {///si no existe el elemento en la lista actual del server
                                break;//si encuentra el elemento, busca el siguiente
                            }
                            if (j >= listServer.size() - 1) {//llego al final de for y no encontro el elemento
                                Catalogo catalogo = realm.where(Catalogo.class).equalTo("id", item.getId()).findFirst();
                                if(catalogo!=null) {
                                    catalogo.deleteFromRealm();
                                }
                            }
                        }
                    }

                }

                //listFilter.addAll(querycat);
                //adapter_cart.notifyDataSetChanged();
                realm.commitTransaction();
                Log.v(TAG, "sincCatalogoDB... ---------------  OK  --------------");
            } catch (Throwable e) {
                Log.v(TAG, "sincCatalogoDB... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }

    public void sincCatalogoDBM(List<DetalleMadrugon> listServer, boolean edit){//en modeo edicion, no se toma en cuenta si hay o no pedidos locales
        //en modo nuevo pedido, si hay algo local se deja tal cual
        System.out.println("Sinc catalogo M "+listServer.size());
        Log.v(TAG,"sincCatalogoDB... ---------------sincCatalogoDBM--------------");
        if( (listFilterCartM.size()<1 || edit) && listServer.size()>0) {
            try {
                realm.beginTransaction();
                //listFilter.clear();
                for (int i = 0; i < listServer.size(); i++) {


                    Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", listServer.get(i).getId()).findFirst();// se busca el id
                    if (mad != null && mad.getCodi_vent().equals(listServer.get(i).getId())) {//se actualiza localmente lo que trae el API

                        System.out.println("Sinc catalogo MI "+listServer.get(i).getNombre() +" Sinc id "+listServer.get(i).getId()+" CANT "+listServer.get(i).getCantidad());
                        mad.setCantidad(listServer.get(i).getCantidad());
                        mad.setCantidadServer(listServer.get(i).getCantidad());
                        mad.setTallasel(listServer.get(i).getTalla());
                        mad.setTalla(listServer.get(i).getTalla());
                    } else if(mad==null){//si no existe, es xq hay que eliminarlo
                        //este caso supone que se modifico el catalogo


                    }
                }


                RealmResults<MadCarrito> querycat;
                List<MadCarrito> catalogoList = new ArrayList<>();
                Log.v(TAG,"clearCartDB... ---------------clearCartDB--------------");


                    catalogoList.clear();
                    querycat = realm.where(MadCarrito.class)
                            .beginGroup()
                            .greaterThan("cantidad", 0)

                            .endGroup()
                            .findAllSorted("id").sort("id", Sort.DESCENDING);
                    //.findAll();
                    catalogoList.addAll(querycat);

                    for (int i = 0; i < catalogoList.size(); i++) {

                        Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", catalogoList.get(i).getCodi_vent()).findFirst();// se busca el id
                        if (mad != null ) {//se actualiza localmente lo que trae el API


                        } else if(mad==null){//si no existe, es xq hay que eliminarlo
                            //este caso supone que se modifico el catalogo
                            querycat.get(i).deleteFromRealm();
                        }


                    }

                for (int j = 0; j < querycat.size(); j++) {

                    System.out.println("El query cat "+querycat.get(j).getCodi_vent());

                    Boolean enc =false;
                    for (int i = 0; i < catalogoList.size(); i++) {
                        if(querycat.get(j).getCodi_vent().equals(catalogoList.get(i).getCodi_vent())){
                            enc=true;
                        }

                    }

                    if(enc==false){
                        querycat.get(j).deleteFromRealm();
                    }

                }


                //debe verificar cuales no llegaron xq fieron eliminados
                for (ItemCarrito item : listFilterCartM) {

                        //se busca si hay algun elemento que dejo de estar disponible
                        for (int j = 0; j < listServer.size(); j++) {
                            if (listServer.get(j).getId().equals(item.getId())) {///si no existe el elemento en la lista actual del server
                                break;//si encuentra el elemento, busca el siguiente
                            }
                            if (j >= listServer.size() - 1) {//llego al final de for y no encontro el elemento
                                System.out.println("Sinc delete"+item.getId());
                                Madrugon mad = realm.where(Madrugon.class).equalTo("codi_vent", item.getId()).findFirst();
                                if(mad!=null) {
                                    mad.deleteFromRealm();
                                }
                            }
                        }


                }

                //listFilter.addAll(querycat);
                //adapter_cart.notifyDataSetChanged();
                realm.commitTransaction();
                Log.v(TAG, "sincCatalogoDBm... ---------------  OK  --------------");
                System.out.println("Sinc M Ok ");
            } catch (Throwable e) {
                System.out.println("Sinc catalogo error "+e.getMessage());
                Log.v(TAG, "sincCatalogoDBm... ---------------error--------------");
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                throw e;
            }
        }
    }


    public void clearCartDB(){
        RealmResults<Catalogo> querycat;
        List<Catalogo> catalogoList = new ArrayList<>();
        Log.v(TAG,"clearCartDB... ---------------clearCartDB--------------");

        try {
            realm.beginTransaction();
            catalogoList.clear();
            querycat = realm.where(Catalogo.class)
                    .beginGroup()
                    .greaterThan("cantidad",0)
                    .or()
                    .greaterThan("cantidadServer",0)
                    .endGroup()
                    .findAllSorted("time").sort("time", Sort.DESCENDING);
            //.findAll();
            catalogoList.addAll(querycat);

            for (int i = 0; i < catalogoList.size(); i++) {
                catalogoList.get(i).setCantidad(0);
                catalogoList.get(i).setCantidadServer(0);
            }

            realm.commitTransaction();
        } catch(Throwable e) {
            Log.v(TAG,"clearCartDB... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }
    }

    public void clearCartDBM(){
        RealmResults<MadCarrito> querycat;

        Log.v(TAG,"clearCartDB... ---------------clearCartDB--------------");

        try {
            realm.beginTransaction();

            querycat = realm.where(MadCarrito.class)

                    .findAll();
            //.findAll();


            querycat.deleteAllFromRealm();

            realm.commitTransaction();
        } catch(Throwable e) {
            Log.v(TAG,"clearCartDB... ---------------error--------------");
            if(realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e;
        }
    }

    private void updateTotal(){
        if(viewParent != null){
            viewParent.updateTotal();
        }
    }




    public void sendtoServerP(){

        updateCarritoM();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        listAdapter.setEnable(enable);
        listAdapter.notifyDataSetChanged();
        updateTotal();
    }

    public List<ItemCarrito> getListFilterCart() {
        return listFilterCart;
    }

    public void clearEditable(){
        listAdapter.clearEditable();
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

    @Override
    public void onAddCartMClick(ItemCarrito dataRow, int row) {
        Log.e(TAG,"onAddCartClick: "+String.valueOf(row));
        testAddCartM(row);
    }

    @Override
    public void onIncreaseMClick(ItemCarrito dataRow, int row) {
        int cantidad = dataRow.getCantidad();
        increaseCartM(row, cantidad);
    }

    @Override
    public void onDecreaseMClick(ItemCarrito dataRow, int row) {

        Log.e(TAG,"onDecreaseClick, pos:  "+row);
        int cantidad = dataRow.getCantidad();
        if(cantidad>0) {
            if(cantidad==1){
                testRemoveCartm(row);//pregunta si quiere eliminar
            } else {
                decreaseCartM(row, cantidad);
            }
        }

    }

    @Override
    public void onAddEditableMClick(boolean isEditable) {

        if(viewParent != null){
            viewParent.productsEditable(isEditable);
        }

    }
}
