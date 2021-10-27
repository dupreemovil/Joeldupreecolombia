package com.dupreinca.dupree.mh_fragments_menu;


import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.PDFActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentReporteFacturaBinding;
import com.dupreinca.dupree.mh_fragments_menu.reportes.ReportesActivity;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreinca.dupree.mh_holders.FacturasPDFHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.model_view.DataAsesora;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemFactura;
import com.dupreinca.dupree.mh_adapters.FacturaPDFListAdapter;
import com.dupreinca.dupree.mh_dialogs.SimpleDialog;
import com.dupreeinca.lib_api_rest.model.dto.response.ListFactura;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteFacturaPDFFragment extends BaseFragment implements FacturasPDFHolder.Events{

    private final String TAG = ReporteFacturaPDFFragment.class.getName();
    private ReportesController reportesController;
    private FacturaPDFListAdapter listAdapter;
    private List<ItemFactura> list, listFilter;

    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;

    public ReporteFacturaPDFFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_reporte_factura;
    }

    private FragmentReporteFacturaBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentReporteFacturaBinding) view;

        reportesController = new ReportesController(getContext());

        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
        binding.tvNombreAsesora.setText("");

        binding.rcvFactura.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.rcvFactura.setHasFixedSize(true);

        perfil = getPerfil();
        timeinit = System.currentTimeMillis();
        list = new ArrayList<>();
        listFilter = new ArrayList<>();

        listAdapter = new FacturaPDFListAdapter(list, listFilter, this);
        binding.rcvFactura.setAdapter(listAdapter);

    }

    @Override
    protected void onLoadedView() {
        checkFactura();
    }

    public void viewFactura(String urlFile){
        Intent intent = new Intent(getActivity(), PDFActivity.class);
        intent.putExtra(PDFActivity.URL_FILE, urlFile);
        startActivity(intent);
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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"reportefac");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }


    private void checkFactura(){
        Bundle bundle;
        DataAsesora data;
        if((bundle = getArguments()) != null && (data = bundle.getParcelable(ReportesActivity.TAG)) != null){
            searchNewIdenty(data.getCedula());
        }
    }

    private void updateView(ListFactura responseFactura){
        list.clear();
        listFilter.clear();
        list.addAll(responseFactura.getResult());
        listFilter.addAll(responseFactura.getResult());

        binding.cardViewBackGround.setVisibility(View.VISIBLE);
        binding.tvNombreAsesora.setText(responseFactura.getAsesora());

        listAdapter.notifyDataSetChanged();
    }

    public void testDescargar(ItemFactura dataRow, int row){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.loadData(getString(R.string.descargar), getString(R.string.desea_descargar));
        simpleDialog.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status){
                    if(row!=-1){
                        viewFactura(dataRow.getLink());
                    }else{
                        msgToast(getString(R.string.error_leyendo_archivo));
                    }
                }
            }
        });
        simpleDialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void searchNewIdenty(String cedula){
        showProgress();
        reportesController.getFacturasPDF(new Identy(cedula), new TTResultListener<ListFactura>() {
            @Override
            public void success(ListFactura result) {
                dismissProgress();
                updateView(result);

                if(result!=null){

                    if(result.getResult()!=null){




                        if(result.getResult().size()>0){


                        }
                        else{
                            ((ReportesActivity)getActivity()).showbottomsheet();
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
                }
                if((ReportesActivity)getActivity()!=null){
                    ((ReportesActivity)getActivity()).showbottomsheet();
                }


            }
        });

    }

    @Override
    public void onClickRoot(ItemFactura dataRow, int row) {
        testDescargar(dataRow, row);
    }
}
