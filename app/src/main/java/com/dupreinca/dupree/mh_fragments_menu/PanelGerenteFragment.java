package com.dupreinca.dupree.mh_fragments_menu;


import android.app.AlertDialog;
import android.content.Context;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dupreeinca.lib_api_rest.controller.CampanaController;
import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemEncuesta;
import com.dupreeinca.lib_api_rest.model.dto.response.ListEncuesta;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreeinca.lib_api_rest.util.preferences.DataStore;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemCampana;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemPanelGte;
import com.dupreeinca.lib_api_rest.model.dto.response.ListItemPanelGte;
import com.dupreeinca.lib_api_rest.model.dto.response.ListPanelGte;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.MenuListener;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentPanelGerenteBinding;
import com.dupreinca.dupree.mh_adapters.CheckAdapter;
import com.dupreinca.dupree.mh_adapters.PanelGteListAdapter;
import com.dupreinca.dupree.mh_adapters.RadioAdapter;
import com.dupreinca.dupree.mh_dialogs.SingleListDialog;
import com.dupreinca.dupree.mh_holders.PanelGteHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.model_view.Opciones;
import com.dupreinca.dupree.model_view.Respuesta;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanelGerenteFragment extends BaseFragment implements PanelGteHolder.Events{
    private final String TAG = PanelGerenteFragment.class.getName();

    private ReportesController reportesController;
    private CampanaController campanaController;

    private List<ItemCampana> campHttp;
    private List<ItemPanelGte> panelGteDetails;


    private PanelGteListAdapter adapter_panelGte;
    protected DataStore dataStore;

    public PanelGerenteFragment() {
        // Required empty public constructor
    }

    private Profile perfil;

    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    public int pos_cues=0;

    public List<ItemEncuesta> listaenc;
    public List<Respuesta> listares;

    public List<Opciones> listarestemp;

    public CheckAdapter checkadapter;
    public RadioAdapter radioadapter;

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_panel_gerente;
    }

    private FragmentPanelGerenteBinding binding;
    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentPanelGerenteBinding) view;

        reportesController = new ReportesController(getContext());
        campanaController = new CampanaController(getContext());

        binding.rcvPanelGrnte.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.rcvPanelGrnte.setHasFixedSize(true);
        ((SimpleItemAnimator) binding.rcvPanelGrnte.getItemAnimator()).setSupportsChangeAnimations(false);

        binding.fabMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuListener != null) {
                    menuListener.gotoMenuPage(R.id.menu_lat_bandeja_entrada);
                }
            }
        });
        campHttp = new ArrayList<>();
        listaenc = new ArrayList<>();
        panelGteDetails =new ArrayList<>();
        perfil = getPerfil();
        timeinit = System.currentTimeMillis();
        adapter_panelGte = new PanelGteListAdapter(panelGteDetails, this);
        binding.rcvPanelGrnte.setAdapter(adapter_panelGte);

        listares = new ArrayList<>();
        //refresh pantalla
        binding.rcvPanelGrnte.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                obtainDetailCamp();
            }
        });

        dataStore = new DataStore(getActivity());

        binding.rcvPanelGrnte.setOnClickListener(mListenerClick);
    }

    @Override
    protected void onLoadedView() {
        obtainDetailCamp();
    }

    View.OnClickListener mListenerClick =
            new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.rcvPanelGrnte:
                            showList(getString(R.string.campana_two_points), getNameCampana(campHttp));
                            break;
                    }
                }
            };

    public void showList(String title, List<ModelList> data){
        SingleListDialog d = new SingleListDialog();
        d.loadData(title, data, "", new SingleListDialog.ListenerResponse() {
            @Override
            public void result(ModelList item) {
                obtainDetailCamp();
            }
        });
        d.show(getActivity().getSupportFragmentManager(),"mDialog");
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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"panelge");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }

    private void addDetailCampana(ListItemPanelGte items){

        panelGteDetails.clear();
        if(items != null) {
            if(items.getFechaCierre() != null){
                dataStore.setCampaniaCierre(items.getFechaCierre());
            }else{
                dataStore.setCampaniaCierre(String.valueOf(items.getCampana()));
            }

            panelGteDetails.addAll(items.getPanelGteDetails());

            if(items.getCantidadMensajes()!=null)
                binding.fabMessages.setTitle(items.getCantidadMensajes()+" Mensajes");

            binding.txtCorte.setText(items.getFechaCorte());

            binding.txtAnimation.setVisibility(View.VISIBLE);
            binding.txtAnimation.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            binding.txtAnimation.setSelected(true);
            binding.txtAnimation.setSingleLine(true);
            binding.txtAnimation.setText(items.getFechaCierre());

            adapter_panelGte.notifyDataSetChanged();
        }
    }

    private void obtainDetailCamp(){
        showProgress();
        DataStore dataStore  ;
        dataStore =  DataStore.getInstance(getContext());
        String token = dataStore.getTokenSesion();

        reportesController.getPanelGrte(token, new TTResultListener<ListPanelGte>() {
            @Override
            public void success(ListPanelGte result) {
                dismissProgress();
                System.out.println("El Json panel "+new Gson().toJson(result.getListDetail()));
                System.out.println("El Json panel "+result.listDetail.getActiva_encuesta());
                addDetailCampana(result.getListDetail());

                if(result.listDetail.getActiva_encuesta().contains("1")){
                    getencuesta();
                }
                binding.refresh.setRefreshing(false);
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
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

    public List<ModelList> getNameCampana(List<ItemCampana> campHttp){
        List<ModelList> respose = new ArrayList<>();

        for(int i=0; i<campHttp.size(); i++){
            respose.add(new ModelList(i, campHttp.get(i).getNombre()));
        }

        return respose;
    }

    MenuListener menuListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuActivity) {
            menuListener = (MenuActivity) context;
        } else {
            throw new RuntimeException(context.toString().concat(" is not OnInteractionActivity"));
        }

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        menuListener = null;
    }

    @Override
    public void onClickRoot(ItemPanelGte dataRow, int row) {

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
