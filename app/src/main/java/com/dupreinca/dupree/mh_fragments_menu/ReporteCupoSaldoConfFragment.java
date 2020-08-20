package com.dupreinca.dupree.mh_fragments_menu;


import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.response.ListCupoSaldoConf;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentReporteCupoSaldoConfBinding;
import com.dupreinca.dupree.mh_fragments_menu.reportes.ReportesActivity;
import com.dupreinca.dupree.model_view.DataAsesora;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemCupoSaldoConf;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreinca.dupree.view.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteCupoSaldoConfFragment extends BaseFragment {
    private final String TAG = ReporteCupoSaldoConfFragment.class.getName();

    private ReportesController reportesController;
    public ReporteCupoSaldoConfFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_reporte_cupo_saldo_conf;
    }

    private FragmentReporteCupoSaldoConfBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentReporteCupoSaldoConfBinding) view;
        reportesController = new ReportesController(getContext());
        binding.tvNombreAsesora.setText("");
        binding.cardViewBackGround.setVisibility(View.INVISIBLE);
        binding.cardViewBackGround2.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLoadedView() {
        checkCupoSaldoCOnf();
    }

    private void checkCupoSaldoCOnf(){
        Bundle bundle;
        DataAsesora data;
        if((bundle = getArguments()) != null && (data = bundle.getParcelable(ReportesActivity.TAG)) != null){
            searchNewIdenty(data.getCedula());
        }
    }

    private void updateView(ItemCupoSaldoConf cupoSaldoConf){
        binding.cardViewBackGround.setVisibility(View.VISIBLE);
        binding.cardViewBackGround2.setVisibility(View.VISIBLE);
        binding.tvNombreAsesora.setText(cupoSaldoConf.getAsesora());

        binding.tvCupo.setText(cupoSaldoConf.getCupo());
        binding.tvSaldo.setText(cupoSaldoConf.getSaldo());
        binding.tvConferencia.setText(cupoSaldoConf.getConf_vent());
    }

    public void searchNewIdenty(String cedula){
        showProgress();
        reportesController.getCupoSaldoConf(new Identy(cedula), new TTResultListener<ListCupoSaldoConf>() {
            @Override
            public void success(ListCupoSaldoConf result) {
                dismissProgress();
                updateView(result.getCupoSaldoConfList().get(0));

                if(result!=null){






                        if(result.getCupoSaldoConfList().size()>0){


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

}
