package com.dupreinca.dupree.mh_fragments_menu;


import android.content.Context;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import 	androidx.appcompat.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dupreeinca.lib_api_rest.controller.ReportesController;
import com.dupreeinca.lib_api_rest.controller.UserController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.ListaPuntos;
import com.dupreeinca.lib_api_rest.model.dto.response.PtosByCamp;
import com.dupreeinca.lib_api_rest.model.dto.response.PuntosAsesoraDTO;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentIncentConsultaPtosBinding;
import com.dupreinca.dupree.mh_adapters.PuntosAsesoraListAdapter;
import com.dupreinca.dupree.mh_holders.PuntosAsesoraHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncentivosConsultaPtosFragment extends BaseFragment implements PuntosAsesoraHolder.Events{
    private final String TAG = IncentivosConsultaPtosFragment.class.getName();
    private FragmentIncentConsultaPtosBinding binding;
    private ReportesController reportesController;

    private PuntosAsesoraListAdapter listAdapter;
    private List<PtosByCamp> list, listFilter;

    private UserController userController;
    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;

    public IncentivosConsultaPtosFragment() {
        // Required empty public constructor
    }


    public void loadData(Profile perfil){
        this.perfil=perfil;
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_incent_consulta_ptos;
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentIncentConsultaPtosBinding) view;

        binding.ctnCardView.setVisibility(View.INVISIBLE);

        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.recycler.setHasFixedSize(true);

        list = new ArrayList<>();
        listFilter = new ArrayList<>();

        timeinit = System.currentTimeMillis();

        perfil = getPerfil();

        //listPtosAsesora = getPuntosAsesora();
        listFilter.addAll(list);

        listAdapter = new PuntosAsesoraListAdapter(list, listFilter, this);
        binding.recycler.setAdapter(listAdapter);

        msgToast("Por favor ingresé cedula");

    }

    @Override
    protected void onLoadedView() {
        reportesController = new ReportesController(getContext());
        checkPuntos();
    }

    private void checkPuntos(){
        if(perfil != null){
            if(perfil.getPerfil().equals(Profile.ADESORA)){
                searchNewIdenty("");
            }
        }
    }

    private void updateView(ListaPuntos listaPuntos){
        list.clear();
        listFilter.clear();
        list.addAll(listaPuntos.getList());
        listFilter.addAll(listaPuntos.getList());

        binding.ctnCardView.setVisibility(View.VISIBLE);
//        binding.profileImage.setVisibility(View.VISIBLE);
        binding.nameAsesora.setText(listaPuntos.getResume().getAsesora());

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);

        binding.ptsEfectivos.setText(getContext().getString(R.string.concat_efectivos, formatter.format(listaPuntos.getResume().getEfectivos())));
        binding.ptsRedimidos.setText(getContext().getString(R.string.concat_redimidos, formatter.format(listaPuntos.getResume().getRedimidos())));
        binding.ptsDisponibles.setText(getContext().getString(R.string.concat_disponibles, formatter.format(listaPuntos.getResume().getDisponibles())));
        binding.ptsPendientes.setText(getContext().getString(R.string.concat_pendientes, formatter.format(Float.parseFloat(listaPuntos.getResume().getPendientes_Pago()))));




        listAdapter.notifyDataSetChanged();
    }

    public void searchNewIdenty(String cedula){
        showProgress();
        reportesController.getPuntosAsesora(new Identy(cedula), new TTResultListener<PuntosAsesoraDTO>() {
            @Override
            public void success(PuntosAsesoraDTO result) {
                dismissProgress();

                updateView(result.getResult());

                if(result!=null){

                    if(result.getResult()!=null){




                        if(result.getResult().getList().size()>0){


                        }
                        else{
                            ((MenuActivity)getActivity()).showbottomsheet();
                        }






                    }
                    else{

                        ((MenuActivity)getActivity()).showbottomsheet();
                    }

                }
                else{

                    ((MenuActivity)getActivity()).showbottomsheet();
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
                if(((MenuActivity)getActivity())!=null){
                    ((MenuActivity)getActivity()).showbottomsheet();
                }

            }
        });

    }

    @Override
    public void onClickRoot(PtosByCamp dataRow, int row) {

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.cedula_asesora));

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
                searchViewTextChange(newText);
                return false;
            }
        });

        searchView.setIconified(true);//inicialmente oculto

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchMyQuery(String query){
        searchNewIdenty(query);
    }

    public void searchViewTextChange(String newText) {

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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"incentivoscon");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
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
}
