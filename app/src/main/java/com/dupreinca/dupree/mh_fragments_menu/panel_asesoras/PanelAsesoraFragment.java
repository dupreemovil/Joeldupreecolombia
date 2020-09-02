package com.dupreinca.dupree.mh_fragments_menu.panel_asesoras;


import android.content.Context;
import androidx.databinding.ViewDataBinding;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.response.PanelAsesoraDTO;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.MenuListener;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentPanelAsesoraBinding;
import com.dupreinca.dupree.mh_adapters.PanelAsesoraPagerAdapter;
import com.dupreeinca.lib_api_rest.model.dto.response.PanelAsesora;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.mh_adapters.base.TabViewPager.TabManagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanelAsesoraFragment extends TabManagerFragment {

    private final String TAG = PanelAsesoraFragment.class.getName();
    private FragmentPanelAsesoraBinding binding;
    private ReportesController reportesController;
    private PanelAsesoraPagerAdapter pagerAdapter;

    private Profile perfil;
    public void loadData(Profile perfil){
        this.perfil=perfil;
    }

    public PanelAsesoraFragment() {
        // Required empty public constructor
    }

    @Override
    protected ViewPager setViewPage() {
        return binding.viewPager;
    }

    @Override
    protected TabLayout setTabs() {
        return binding.tabs;
    }

    @Override
    protected FragmentStatePagerAdapter setAdapter() {
        return pagerAdapter;
    }

    @Override
    protected List<ModelList> setItems() {
        List<ModelList> items = new ArrayList<>();
        items.add(new ModelList(R.drawable.ic_track_changes_white_24dp, getString(R.string.edo_pedido)));
        items.add(new ModelList(R.drawable.ic_info_outline_white_24dp, getString(R.string.faltantes)));
        return items;
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_panel_asesora;
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentPanelAsesoraBinding) view;

        binding.swipePanelAsesora.setOnRefreshListener(mOnRefreshListener);
        binding.swipePanelAsesora.setEnabled(false);


        pagerAdapter = new PanelAsesoraPagerAdapter(getFragmentManager());
        binding.viewPager.addOnPageChangeListener(mOnPageChangeListener);
        binding.viewPager.setOffscreenPageLimit(2);

        //SOlicitaron quitar foto de perfil por temas legales
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(perfil!=null && perfil.getImagen_perfil()!=null && !perfil.getImagen_perfil().isEmpty())
                //    gotoZoomImage(perfil.getImagen_perfil());
            }
        });
        binding.profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //imageSelected=BROACAST_INSCRIP_IMG_PROFILE;
                //permissionImage();
                return false;
            }
        });

       /* if(perfil.getImagen_perfil()!=null && !perfil.getImagen_perfil().isEmpty()){
            Log.e(TAG, "JSON IMAGE" + perfil.getImagen_perfil());
            //imageProfile(perfil.getImagen_perfil());
        }*/

        binding.fabMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuListener != null)
                    menuListener.gotoMenuPage(R.id.menu_lat_bandeja_entrada);
            }
        });


    }

    //43
    @Override
    protected void onLoadedView() {
        reportesController = new ReportesController(getContext());

        setData(false, null);
        checkDetailPanel();
    }

    private void checkDetailPanel(){
        showProgress();
        reportesController.getPanelAsesora(new TTResultListener<PanelAsesoraDTO>() {
            @Override
            public void success(PanelAsesoraDTO result) {
                dismissProgress();
                if(result.getPanelAsesora() != null) {
                    updateView(result.getPanelAsesora());
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void setData(boolean isVisible, PanelAsesora data){

        if(data!=null){

            if(data.getCampana()!=null){

                String campana =  "Campaña:" +" "+ data.getCampana();
                if(campana!=null && campana.length()>0){
                    binding.tvCamp.setText(campana);
                }
                else {
                    binding.tvCamp.setText("");
                }


            }
            else{

                binding.tvCamp.setText("");
            }

            if(data.getNombre_asesora()!=null){

                binding.tvNameAsesora.setText(data.getNombre_asesora());
            }
            else{

                binding.tvNameAsesora.setText( "");
            }

            if(data.getSaldo()!=null){

                String saldof = data.getSaldo();
                String txtsaldo = "Saldo: $ "+saldof;

                binding.tvSaldoAsesora.setText(txtsaldo);
            }
            else{
                binding.tvSaldoAsesora.setText("Saldo: $ 0");
            }

            if(data.getCupo_credito()!=null){

                String cupocred = "Cupo crédito: $ ";

                if(data.getCupo_credito().length()>0){


                    String monto = data.getCupo_credito();
                    String cupo = cupocred+monto;
                    binding.tvCupoAsesora.setText(cupo);
                }
                else{
                    binding.tvCupoAsesora.setText("Cupo crédito: $ 0");
                }


            }
            else{
                binding.tvCupoAsesora.setText("Cupo crédito: $ 0");
            }


        }
        else{
            binding.tvCamp.setText("");
            binding.tvNameAsesora.setText("");
            binding.tvSaldoAsesora.setText("");
            binding.tvCupoAsesora.setText("");
        }








        if(data!=null && data.getCantidad_mensajes()!=null)
            binding.fabMessages.setTitle(getString(R.string.concat_mensajes, data.getCantidad_mensajes()));

        ///PÁGINAS
        binding.appBarLayout.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        binding.tabs.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        binding.profileImage.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateView(PanelAsesora data){
        setData(true, data);

        updateTracking(data);
        updateFaltanteConf(data);
    }

    private void updateTracking(PanelAsesora data){
        if(data!=null) {
            pagerAdapter.getTrackingFragment().setData(data.getTracking());
        }
    }

    private void updateFaltanteConf(PanelAsesora data){
        if(data!=null) {
            pagerAdapter.getFaltantesAsesoraFragment().setData(data.getFaltantes());
        }
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

    private ViewPager.OnPageChangeListener mOnPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG,"onPageSelected Page: "+position);
            switch (position){
                case PanelAsesoraPagerAdapter.PAGE_TRACKING:
//                    updateTracking();
                    break;
                case PanelAsesoraPagerAdapter.PAGE_FALTANTES_Y_CONF:
//                    updateFaltanteConf();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    MenuListener menuListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuActivity) {
            menuListener = (MenuActivity) context;
        } else
            throw new RuntimeException(context.toString().concat(" is not OnInteractionActivity"));
    }

//    @Override
//    public void onAttachFragment(Fragment childFragment) {
//        super.onAttachFragment(childFragment);
//        if (childFragment instanceof IncorporacionesVPages) {
//            viewParent = (IncorporacionesVPages) childFragment;
//        } else {
//            //throw new RuntimeException(childFragment.toString().concat(" is not OnInteractionActivity"));
//            Log.e(TAG, "is not OnInteractionActivity");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        menuListener = null;
    }
}
