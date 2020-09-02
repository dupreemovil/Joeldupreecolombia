package com.dupreinca.dupree.mh_fragments_menu;


import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.response.ListCDR;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentReporteCdrBinding;
import com.dupreinca.dupree.mh_fragments_menu.reportes.ReportesActivity;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreinca.dupree.mh_holders.CDRHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.model_view.DataAsesora;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemCDR;
import com.dupreinca.dupree.mh_adapters.CDRListAdapter;
import com.dupreeinca.lib_api_rest.model.dto.response.TitlesCDR;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteCDRFragment extends BaseFragment implements CDRHolder.Events{

    private final String TAG = ReporteCDRFragment.class.getName();
    private ReportesController reportesController;
    private CDRListAdapter adapter_cdr;
    private List<ItemCDR> listCDR, listFilter;

    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;

    public ReporteCDRFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_reporte_cdr;
    }

    private FragmentReporteCdrBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentReporteCdrBinding) view;
        reportesController = new ReportesController(getContext());

        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
        binding.tvNombreAsesora.setText("");

        binding.rcvCDR.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.rcvCDR.setHasFixedSize(true);

        timeinit = System.currentTimeMillis();
        listCDR = new ArrayList<>();
        listFilter = new ArrayList<>();

        adapter_cdr = new CDRListAdapter(listCDR, listFilter, this);
        binding.rcvCDR.setAdapter(adapter_cdr);
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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"reportecdr");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }


    @Override
    protected void onLoadedView() {
        checkCDR();
    }

    private void checkCDR(){
        Bundle bundle;
        DataAsesora data;

        System.out.println("Check cdr ");
        if((bundle = getArguments()) != null && (data = bundle.getParcelable(ReportesActivity.TAG)) != null){

            System.out.println("Check new identi ");
            searchNewIdenty(data.getCedula());


        }
    }

    private void updateView(TitlesCDR listaCDR){
        listCDR.clear();
        listFilter.clear();
        listCDR.addAll(listaCDR.getTable());
        listFilter.addAll(listaCDR.getTable());

        binding.cardViewBackGround.setVisibility(View.VISIBLE);
        binding.tvNombreAsesora.setText(listaCDR.getAsesora());

        adapter_cdr.notifyDataSetChanged();
    }

    public void searchNewIdenty(String cedula){
        showProgress();
        reportesController.getCDR(new Identy(cedula), new TTResultListener<ListCDR>() {
            @Override
            public void success(ListCDR result) {
                dismissProgress();
                updateView(result.getResult());

                if(result!=null){

                    if(result.getResult()!=null){




                        if(result.getResult().getTable().size()>0){


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
                checkSession(error);

                ((ReportesActivity)getActivity()).showbottomsheet();
            }
        });
    }

    @Override
    public void onClickRoot(ItemCDR dataRow, int row) {

    }
}
