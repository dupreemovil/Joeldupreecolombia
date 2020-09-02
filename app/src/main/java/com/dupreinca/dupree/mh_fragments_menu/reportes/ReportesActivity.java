package com.dupreinca.dupree.mh_fragments_menu.reportes;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.ActivityBaseBinding;
import com.dupreeinca.lib_api_rest.enums.EnumReportes;
import com.dupreinca.dupree.mh_fragments_menu.ReporteCDRFragment;
import com.dupreinca.dupree.mh_fragments_menu.ReporteCupoSaldoConfFragment;
import com.dupreinca.dupree.mh_fragments_menu.ReporteFacturaPDFFragment;
import com.dupreinca.dupree.mh_fragments_menu.ReportePagosRealizadosFragment;
import com.dupreinca.dupree.mh_fragments_menu.ReporteSegPetQueRecFragment;
import com.dupreinca.dupree.model_view.DataAsesora;
import com.dupreinca.dupree.view.activity.BaseActivity;

public class ReportesActivity extends BaseActivity {
    public static String TAG = ReportesActivity.class.getName();

    LinearLayout layoutBottomSheet;

    BottomSheetBehavior bottomSheetBehavior;

    Button btnbshome;
    Button btnbscancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityBaseBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_base);

        setSupportActionBar(binding.toolbar);
        final ActionBar actionBar = getSupportActionBar();

        DataAsesora data;
        Intent intent = getIntent();
        if(actionBar != null && (intent != null) && (data = intent.getParcelableExtra(TAG)) != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_white_24);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            Bundle bundle = new Bundle();
            bundle.putParcelable(TAG, data);
            if(data.getId() == EnumReportes.REPORTE_CDR.getKey()){

                if(bottomSheetBehavior!=null){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                actionBar.setTitle(getString(R.string.canjes_y_devoluciones_cdr));
                replaceFragmentWithBackStack(ReporteCDRFragment.class, false, bundle);
            } else if(data.getId() == EnumReportes.REPORTE_SEGUIMIENTO.getKey()){

                if(bottomSheetBehavior!=null){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                actionBar.setTitle(getString(R.string.seguimiento_servicios));
                replaceFragmentWithBackStack(ReporteSegPetQueRecFragment.class, false, bundle);
            } else if(data.getId() == EnumReportes.REPORTE_FACTURAS.getKey()){

                if(bottomSheetBehavior!=null){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                actionBar.setTitle(getString(R.string.detalle_factura_pdf));
                replaceFragmentWithBackStack(ReporteFacturaPDFFragment.class, false, bundle);
            } else if(data.getId() == EnumReportes.REPORTE_PAGOS.getKey()){
                if(bottomSheetBehavior!=null){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                actionBar.setTitle(getString(R.string.pagos_realizados));
                replaceFragmentWithBackStack(ReportePagosRealizadosFragment.class, false, bundle);
            } else if(data.getId() == EnumReportes.REPORTE_CONFERENCIA.getKey()){

                if(bottomSheetBehavior!=null){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                actionBar.setTitle(getString(R.string.cupo_saldo_y_conferencia_asesora));
                replaceFragmentWithBackStack(ReporteCupoSaldoConfFragment.class, false, bundle);
            } else {
                finish();
            }

        } else {
            finish();
        }



        layoutBottomSheet = (LinearLayout)findViewById(R.id.bottomsheet);

        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

// change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// set the peek height
        bottomSheetBehavior.setPeekHeight(0);

// set hideable or not
        bottomSheetBehavior.setHideable(true);

        btnbscancel = (Button)findViewById(com.dupreinca.dupree.R.id.bscancel);

        btnbshome = (Button)findViewById(com.dupreinca.dupree.R.id.bshome);

        btnbscancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showbottomsheet();
            }
        });

        btnbshome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();

            }
        });

// set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });



    }
    @Override
    protected int getFragmentLayout() {
        return R.id.fragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showbottomsheet(){

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }

    }

}
