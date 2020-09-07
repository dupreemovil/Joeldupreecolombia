package com.dupreinca.dupree.mh_fragments_menu.panel_asesoras;


import android.app.AlertDialog;
import android.content.Context;
import androidx.databinding.ViewDataBinding;

import com.dupreeinca.lib_api_rest.model.dto.response.ItemEncuesta;
import com.dupreeinca.lib_api_rest.model.dto.response.ListEncuesta;
import com.dupreeinca.lib_api_rest.util.preferences.DataStore;
import com.dupreinca.dupree.mh_adapters.CheckAdapter;
import com.dupreinca.dupree.mh_adapters.RadioAdapter;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.model_view.Opciones;
import com.dupreinca.dupree.model_view.Respuesta;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;

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


    public int pos_cues=0;

    public List<ItemEncuesta> listaenc;
    public List<Respuesta> listares;

    public List<Opciones> listarestemp;

    public CheckAdapter checkadapter;
    public RadioAdapter radioadapter;

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

                    System.out.println("Get encuesta "+result.getPanelAsesora().getActiva_encuesta());
                    if(result.getPanelAsesora().getActiva_encuesta().contains("1")){

                        getencuesta();
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

    public void getencuesta(){
        DataStore dataStore  ;
        dataStore =  DataStore.getInstance(getContext());
        String token = dataStore.getTokenSesion();

        reportesController.getEncuesta( token,new TTResultListener<ListEncuesta>() {
            @Override
            public void success(ListEncuesta result) {
                dismissProgress();
                System.out.println("El Json encuesta "+new Gson().toJson(result));
                System.out.println("El Json encuesta "+new Gson().toJson(result.getLista()));
                if(result.getLista().size()>0){
                    listaenc = result.getLista();
                    showcuestionario();
                }

            }

            @Override
            public void error(TTError error) {

                System.out.println("El Json err "+error.getMessage());
            }
        });
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


    public void showcuestionario(){


        System.out.println("Nuevo se muestra");

        //  ColorDrawable cd = new ColorDrawable(0xFFe3e3e3);
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.encuesta_layout, null);

        listares = new ArrayList<>();

        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setView(deleteDialogView);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();


        deleteDialog.setCancelable(false);
        pos_cues = 0;

        TextView txtpregunta = (TextView)deleteDialogView.findViewById(R.id.txtpreguntac);

        Button btnback = deleteDialogView.findViewById(R.id.btnback);
        Button btnnext = deleteDialogView.findViewById(R.id.btnnext);

        LinearLayout layoutcheck = (LinearLayout)deleteDialogView.findViewById(R.id.layoutcheck);
        LinearLayout layoutradio = (LinearLayout)deleteDialogView.findViewById(R.id.layoutcradio);
        LinearLayout layoutanswer = (LinearLayout)deleteDialogView.findViewById(R.id.layoutanswer);

        EditText editanswer = (EditText)deleteDialogView.findViewById(R.id.edtrespuesta);

        RecyclerView listcheck = (RecyclerView)deleteDialogView.findViewById(R.id.listacheck);
        RecyclerView listradio = (RecyclerView)deleteDialogView.findViewById(R.id.listaradio);

        btnback.setVisibility(View.INVISIBLE);
        ArrayList<Opciones> listaop = new ArrayList<>();

        txtpregunta.setText(listaenc.get(pos_cues).getDescripcionpregunta());

        for(int j=0;j<listaenc.get(pos_cues).getOpciones().size();j++){
            listaop.add(new Opciones(listaenc.get(pos_cues).getOpciones().get(j).getId(),listaenc.get(pos_cues).getOpciones().get(j).getOpcion()));
        }

        System.out.println("El tip preg "+listaenc.get(pos_cues).getTipo_pregunta()+ " pregun"+listaenc.get(pos_cues).getDescripcionpregunta());

        listarestemp = new ArrayList<>();

        if(listaenc.get(pos_cues).getTipo_pregunta().contains("1")){

            System.out.println("El tip preg es 1 "+listaop.size());
            layoutcheck.setVisibility(View.GONE);
            layoutanswer.setVisibility(View.GONE);
            layoutradio.setVisibility(View.VISIBLE);

            radioadapter = new RadioAdapter(getActivity(), listaop, new RadioAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(Opciones item) {

                    listarestemp.add(item);

                }

                @Override
                public void onItemUncheck(Opciones item) {

                    for(int i=0;i<listarestemp.size();i++){

                        if(Integer.parseInt(listarestemp.get(i).getId())==Integer.parseInt(item.getId())){

                            listarestemp.remove(i);

                        }

                    }

                }
            });

            listradio.setAdapter(radioadapter);
            listradio.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            listradio.setClickable(true);
            listradio.setHasFixedSize(true);

            listradio.setAdapter(radioadapter);

        }
        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("2")){
            layoutcheck.setVisibility(View.VISIBLE);
            layoutanswer.setVisibility(View.GONE);
            layoutradio.setVisibility(View.GONE);




            checkadapter = new CheckAdapter(getActivity(), listaop, new CheckAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(Opciones item) {


                    listarestemp.add(item);


                }

                @Override
                public void onItemUncheck(Opciones item) {

                    for(int i=0;i<listarestemp.size();i++){

                        if(Integer.parseInt(listarestemp.get(i).getId())==Integer.parseInt(item.getId())){

                            listarestemp.remove(i);

                        }

                    }

                }
            });
            listcheck.setAdapter(checkadapter);


        }
        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){
            layoutcheck.setVisibility(View.GONE);
            layoutanswer.setVisibility(View.VISIBLE);
            layoutradio.setVisibility(View.GONE);
            editanswer.setText("");

        }



        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos_cues>0){

                    ArrayList<Opciones> listaop1 = new ArrayList<>();




                    if(pos_cues==1){
                        pos_cues=pos_cues-1;
                        btnback.setVisibility(View.INVISIBLE);

                        for(int j=0;j<listaenc.get(pos_cues).getOpciones().size();j++){
                            listaop1.add(new Opciones(listaenc.get(pos_cues).getOpciones().get(j).getId(),listaenc.get(pos_cues).getOpciones().get(j).getOpcion()));
                        }


                        if(listaenc.get(pos_cues).getTipo_pregunta().contains("1")){
                            layoutcheck.setVisibility(View.VISIBLE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.GONE);

                            checkadapter = new CheckAdapter(getActivity(), listaop1, new CheckAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {

                                }

                                @Override
                                public void onItemUncheck(Opciones item) {

                                }
                            });
                            listcheck.setAdapter(checkadapter);


                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("2")){
                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.VISIBLE);
                            radioadapter = new RadioAdapter(getActivity(),listaop1, new RadioAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {

                                }

                                @Override
                                public void onItemUncheck(Opciones item) {

                                }
                            });

                            listradio.setAdapter(radioadapter);


                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){
                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.VISIBLE);
                            layoutradio.setVisibility(View.GONE);
                            editanswer.setText("");

                        }
                    }
                    else{
                        pos_cues=pos_cues-1;

                        for(int j=0;j<listaenc.get(pos_cues).getOpciones().size();j++){
                            listaop1.add(new Opciones(listaenc.get(pos_cues).getOpciones().get(j).getId(),listaenc.get(pos_cues).getOpciones().get(j).getOpcion()));
                        }


                        if(listaenc.get(pos_cues).getTipo_pregunta().contains("1")){
                            layoutcheck.setVisibility(View.VISIBLE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.GONE);

                            checkadapter = new CheckAdapter(getActivity(),listaop1,new CheckAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {


                                    listarestemp.add(item);


                                }

                                @Override
                                public void onItemUncheck(Opciones item) {

                                    for(int i=0;i<listarestemp.size();i++){

                                        if(Integer.parseInt(listarestemp.get(i).getId())==Integer.parseInt(item.getId())){

                                            listarestemp.remove(i);

                                        }

                                    }

                                }
                            });
                            listcheck.setAdapter(checkadapter);


                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("2")){
                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.VISIBLE);
                            radioadapter = new RadioAdapter(getActivity(),listaop1, new RadioAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {

                                }

                                @Override
                                public void onItemUncheck(Opciones item) {

                                }
                            });
                            listradio.setAdapter(radioadapter);


                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){
                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.VISIBLE);
                            layoutradio.setVisibility(View.GONE);
                            editanswer.setText("");

                        }
                    }


                }
                else{


                }

            }
        });


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){


                    if(editanswer.getText().toString().length()==0){


                        Toast.makeText(getActivity(),"Debe llenar su respuesta",Toast.LENGTH_LONG).show();

                        return;
                    }

                    else{

                    }
                }
                else{

                    if(listarestemp.size()==0){
                        Toast.makeText(getActivity(),"Debe escoger su respuesta",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {

                    }



                }


                ArrayList<Opciones> listaop1 = new ArrayList<>();

                if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){



                    List<String> listastringtemp = new ArrayList();
                    listastringtemp.add(editanswer.getText().toString());
                    Respuesta resp = new Respuesta(listaenc.get(pos_cues).getIdpregunta(),listastringtemp);
                    listares.add(resp);

                    //INITIALIZE

                    listarestemp = new ArrayList<>();


                }
                else{

                    //GUARDAR
                    List<String> listastringtemp = new ArrayList();
                    for(int j=0;j<listarestemp.size();j++){



                        listastringtemp.add(listarestemp.get(j).getId());

                    }
                    Respuesta resp = new Respuesta(listaenc.get(pos_cues).getIdpregunta(),listastringtemp);
                    listares.add(resp);

                    //INITIALIZE

                    listarestemp = new ArrayList<>();
                }




                if(listaenc.size()>0){

                    if(pos_cues+1==listaenc.size()){

                        //SEND RESPUESTAS

                        System.out.println("´La res El id 1 "+listares.get(0).id);
                        System.out.println("La resp a enviar "+new Gson().toJson(listares).toString());


                        deleteDialog.dismiss();
                        new Http(getActivity()).gencuesta(getActivity(),listares);




                    }
                    else{
                        pos_cues = pos_cues+1;
                        //     btnback.setVisibility(View.VISIBLE);

                        txtpregunta.setText(listaenc.get(pos_cues).getDescripcionpregunta());

                        for(int j=0;j<listaenc.get(pos_cues).getOpciones().size();j++){
                            listaop1.add(new Opciones(listaenc.get(pos_cues).getOpciones().get(j).getId(),listaenc.get(pos_cues).getOpciones().get(j).getOpcion()));
                        }

                        System.out.println("Cantidad de opciones");

                        if(listaenc.get(pos_cues).getTipo_pregunta().contains("1")){

                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.VISIBLE);
                            radioadapter = new RadioAdapter(getActivity(),listaop1,new RadioAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {

                                    listarestemp.add(item);
                                }
                                @Override
                                public void onItemUncheck(Opciones item) {

                                    for(int i=0;i<listarestemp.size();i++){

                                        if(Integer.parseInt(listarestemp.get(i).getId())==Integer.parseInt(item.getId())){

                                            listarestemp.remove(i);

                                        }

                                    }

                                }
                            });
                            listradio.setAdapter(radioadapter);
                            listradio.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            listradio.setClickable(true);
                            listradio.setHasFixedSize(true);

                            listradio.setAdapter(radioadapter);




                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("2")){



                            layoutcheck.setVisibility(View.VISIBLE);
                            layoutanswer.setVisibility(View.GONE);
                            layoutradio.setVisibility(View.GONE);

                            listcheck.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            listcheck.setClickable(true);
                            listcheck.setHasFixedSize(true);


                            checkadapter = new CheckAdapter(getActivity(),listaop1,new CheckAdapter.OnItemCheckListener() {
                                @Override
                                public void onItemCheck(Opciones item) {


                                    listarestemp.add(item);




                                }

                                @Override
                                public void onItemUncheck(Opciones item) {

                                    for(int i=0;i<listarestemp.size();i++){

                                        if(Integer.parseInt(listarestemp.get(i).getId())==Integer.parseInt(item.getId())){

                                            listarestemp.remove(i);

                                        }

                                    }

                                }
                            });
                            listcheck.setAdapter(checkadapter);


                        }
                        else if(listaenc.get(pos_cues).getTipo_pregunta().contains("3")){
                            layoutcheck.setVisibility(View.GONE);
                            layoutanswer.setVisibility(View.VISIBLE);
                            layoutradio.setVisibility(View.GONE);
                            editanswer.setText("");

                        }

                    }



                }
                else{


                }

            }
        });


        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);



        //  deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        deleteDialog.show();


        System.out.println("El width de window "+displayRectangle.width());

        System.out.println("El heigth de window "+displayRectangle.height());

        int width = (int)(displayRectangle.width() * 7/8);
        int heigth = (int)(displayRectangle.height() * 7/8);

        deleteDialog.getWindow().setLayout(width, heigth);






    }


}
