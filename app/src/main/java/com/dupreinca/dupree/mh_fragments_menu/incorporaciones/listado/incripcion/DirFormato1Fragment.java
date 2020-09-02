package com.dupreinca.dupree.mh_fragments_menu.incorporaciones.listado.incripcion;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.InscripcionController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.InscriptionDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.BarrioDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.CiudadDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.DepartamentoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ListBarrioDTO;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentDireccionFormato1Binding;
import com.dupreinca.dupree.files.LoadJsonFile;
import com.dupreinca.dupree.files.ManagerFiles;
import com.dupreinca.dupree.mh_dialogs.ListCity;
import com.dupreinca.dupree.mh_dialogs.ListDpto;
import com.dupreinca.dupree.mh_dialogs.MH_Dialogs_Barrio;
import com.dupreinca.dupree.mh_dialogs.SingleListDialog;
import com.dupreinca.dupree.mh_utilities.Validate;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.activity.BaseActivityListener;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class DirFormato1Fragment extends BaseFragment implements View.OnClickListener {

    public static String TAG = DirFormato1Fragment.class.getName();
    private FragmentDireccionFormato1Binding binding;
    private InscriptionDTO model;
    private List<ModelList> listBis, listDirSend, listLetra, listPCardinal, listTipoVia;

    private InscripcionController inscripController;

    private List<DepartamentoDTO> listDpto=null;
    private List<CiudadDTO> listCiudad=null, listCiudad_2=null;
    private List<BarrioDTO> listBarrio=null, listBarrio_2=null;
    public List<DepartamentoDTO> getListDpto(String data){
        Type listType = new TypeToken<ArrayList<DepartamentoDTO>>(){}.getType();
        return new Gson().fromJson(data, listType);
    }

    public DirFormato1Fragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_direccion_formato1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if((bundle = getArguments()) != null && (model = bundle.getParcelable(TAG)) != null) {
            isOnCreate = true;
            Log.e(TAG, "model.getFormato_direccion(): "+model.getFormato_direccion());
        } else {
            onBack();
        }
    }


    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentDireccionFormato1Binding) view;
        binding.setCallback(this);
        binding.setModel(model);
    }

    @Override
    protected void onLoadedView() {
        inscripController = new InscripcionController(getContext());

        LoadJsonFile jsonFile = new LoadJsonFile(getContext());
        listBis = jsonFile.getParentezcos(ManagerFiles.BIS.getKey());
        listDirSend = jsonFile.getParentezcos(ManagerFiles.DIR_SEND.getKey());
        listLetra = jsonFile.getParentezcos(ManagerFiles.LETRA.getKey());
        listPCardinal = jsonFile.getParentezcos(ManagerFiles.PCARDINAL.getKey());
        listTipoVia = jsonFile.getParentezcos(ManagerFiles.TIPO_VIA.getKey());
        listDpto = getListDpto(mPreferences.getDpto(getActivity()));

        if(isOnCreate){
            isOnCreate = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //DIRECCION DE RESIDENCIA
            case R.id.txt_spn_tipo_via1://
                showList(view.getId(), getString(R.string.tipo_via), listTipoVia, model.getTipo_via1());
                break;
            case R.id.txt_spn_letra1://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra1());
                break;
            case R.id.txt_spn_bis1://
                showList(view.getId(), getString(R.string.bis), listBis, model.getBis1());
                break;
            case R.id.txt_spn_letra2://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra2());
                break;
            case R.id.txt_spn_pcardinal1://
                showList(view.getId(), getString(R.string.sur_este), listPCardinal, model.getPcardinal1());
                break;
            case R.id.txt_spn_letra3://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra3());
                break;
            case R.id.txt_spn_pcardinal2://
                showList(view.getId(), getString(R.string.sur_este), listPCardinal, model.getPcardinal2());
                break;
            case R.id.txt_spn_departamento://
                if(listDpto!=null)
                    showDpto(view.getId(), getString(R.string.departamento), listDpto, model.getDepartamento());
                break;
            case R.id.txt_spn_name_ciudad://
                if(listCiudad!=null)
                    showCity(view.getId(), getString(R.string.ciudad), listCiudad, model.getName_ciudad());
                break;
            case R.id.txt_spn_barrio://
                if(listBarrio!=null) {
                    List<BarrioDTO> barrioList = new ArrayList<>(listBarrio);
                    List<BarrioDTO> filterBarrio = new ArrayList<>(listBarrio);
                    showBarrio(view.getId(), getString(R.string.barrio), filterBarrio, barrioList);
                }
                break;

            //DIRECCION DE ENVIO
            case R.id.txtSpnDirSend://
                showList(view.getId(), getString(R.string.direccion_de_envio_pedido), listDirSend, model.getSpnDirEnvio());
                break;
            case R.id.txt_spn_tipo_via_env_1://
                showList(view.getId(), getString(R.string.tipo_via), listTipoVia, model.getTipo_via_env_1());
                break;
            case R.id.txt_spn_letra_env_1://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra_env_1());
                break;
            case R.id.txt_spn_bis_env_1://
                showList(view.getId(), getString(R.string.bis), listBis, model.getBis_env_1());
                break;
            case R.id.txt_spn_letra_env_2://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra_env_2());
                break;
            case R.id.txt_spn_pcardinal_env_1://
                showList(view.getId(), getString(R.string.sur_este), listPCardinal, model.getPcardinal1());
                break;
            case R.id.txt_spn_letra_env_3://
                showList(view.getId(), getString(R.string.letra), listLetra, model.getLetra_env_3());
                break;
            case R.id.txt_spn_pcardinal_env_2://
                showList(view.getId(), getString(R.string.sur_este), listPCardinal, model.getPcardinal2());
                break;
            case R.id.txt_spn_departamento_env://
                if(listDpto!=null)
                    showDpto(view.getId(), getString(R.string.departamento), listDpto, model.getDepartamento_env());
                break;
            case R.id.txt_spn_name_ciudad_env://
                if(listCiudad_2!=null)
                    showCity(view.getId(), getString(R.string.ciudad), listCiudad_2, model.getName_ciudad_env());
                break;
            case R.id.txt_spn_barrio_env://
                if(listBarrio_2!=null) {
                    List<BarrioDTO> barrioList = new ArrayList<>(listBarrio_2);
                    List<BarrioDTO> filterBarrio = new ArrayList<>(listBarrio_2);
                    showBarrio(view.getId(), getString(R.string.barrio), filterBarrio, barrioList);
                }
                break;
            case R.id.btnContinuar:
                if(validate()) {
                    nextPage();
                }
                break;
        }
    }

    public boolean validate() {
        return validateDirResidencia() && validateDirEnvio();
    }

    public boolean validateDirResidencia(){
        Validate valid = new Validate();
        //DIRECCION DE RESIDENCIA
        if (model.getTipo_via1().isEmpty())
        {
            msgToast("Dir. Res. > Tipo de vía... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnTipoVia1);
            return false;
        }

        if (!model.getTipo_via1().toUpperCase().equals("Otro".toUpperCase()))
        {
            if(model.getNumero1().isEmpty()){
                msgToast("Dir. Res. > Número 1... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumero1);
                return false;
            }
            if(model.getNumero2().isEmpty()){
                msgToast("Dir. Res. > Número 2... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumero2);
                return false;
            }
            if(model.getNumero3().isEmpty()){
                msgToast("Dir. Res. > Número 2... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumero3);
                return false;
            }
        }

        if (model.getTipo_via1().toUpperCase().equals("Otro".toUpperCase()))
        {
            if(model.getComplemento().isEmpty()){
                msgToast("Dir. Res. > Datos adicionales...");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtComplemento);
                return false;
            }
        }

        //departamento
        if (model.getDepartamento().isEmpty())
        {
            msgToast("Dir. Res. > Dpto... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnDepartamento);
            return false;
        }

        if (model.getName_ciudad().isEmpty())
        {
            msgToast("Dir. Res. > Ciudad... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnNameCiudad);
            return false;
        }

        if (model.getBarrio().isEmpty())
        {
            msgToast("Dir. Res. > Barrio... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnBarrio);
            return false;
        }

        return true;
    }

    public boolean validateDirEnvio(){
        Validate valid = new Validate();
        //DIRECCION DE ENVIO
        if (model.isShowDirEnvio()) {
            if (model.getTipo_via_env_1().isEmpty()) {
                msgToast("Dir. Envío > Tipo de vía... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnTipoViaEnv1);
                return false;
            }

            if (!model.getTipo_via_env_1().toUpperCase().equals("Otro".toUpperCase())) {
                if (model.getNumero_env_1().isEmpty()) {
                    msgToast("Dir. Envío > Número 1... Verifique");
                    valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumeroEnv1);
                    return false;
                }
                if (model.getNumero_env_2().isEmpty()) {
                    msgToast("Dir. Envío > Número 2... Verifique");
                    valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumeroEnv2);
                    return false;
                }
                if (model.getNumero_env_3().isEmpty()) {
                    msgToast("Dir. Envío > Número 2... Verifique");
                    valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtNumeroEnv3);
                    return false;
                }
            }

            if (model.getTipo_via_env_1().toUpperCase().equals("Otro".toUpperCase())) {
                if (model.getComplemento_env().isEmpty()) {
                    msgToast("Dir. Envío > Datos adicionales...");
                    valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtComplementoEnv);
                    return false;
                }
            }

            //departamento
            if (model.getDepartamento_env().isEmpty()) {
                msgToast("Dir. Envío > Dpto... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnDepartamentoEnv);
                return false;
            }

            if (model.getName_ciudad_env().isEmpty()) {
                msgToast("Dir. Envío > Ciudad... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnNameCiudadEnv);
                return false;
            }

            if (model.getId_barrio_env().isEmpty()) {
                msgToast("Dir. Envío > Barrio... Verifique");
                valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtSpnBarrioEnv);
                return false;
            }

        }
        return true;
    }

    public void showList(int id, String title, List<ModelList> data, String itemSelected){
        SingleListDialog dialog = new SingleListDialog();
        dialog.loadData(title, data, itemSelected, new SingleListDialog.ListenerResponse() {
            @Override
            public void result(ModelList item) {
                switch(id){
                    //DIRECCION DE RESIDENCIA
                    case R.id.txt_spn_tipo_via1:
                        binding.txtSpnTipoVia1.setError(null);
                        model.setTipo_via1(item.getName());
                        break;
                    case R.id.txt_spn_letra1:
                        binding.txtSpnLetra1.setError(null);
                        model.setLetra1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_bis1:
                        binding.txtSpnBis1.setError(null);
                        model.setBis1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_letra2:
                        binding.txtSpnLetra2.setError(null);
                        model.setLetra2(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_pcardinal1:
                        binding.txtSpnPcardinal1.setError(null);
                        model.setPcardinal1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_letra3:
                        binding.txtSpnLetra3.setError(null);
                        model.setLetra3(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_pcardinal2:
                        binding.txtSpnPcardinal2.setError(null);
                        model.setPcardinal2(item.getId() == -1 ? "" : item.getName());
                        break;

                    //DIRECCION DE ENVIO
                    case R.id.txtSpnDirSend:
                        binding.txtSpnDirSend.setError(null);
                        model.setSpnDirEnvio(item.getName());
                        model.setShowDirEnvio(item.getId() > 0);
                        break;

                    case R.id.txt_spn_tipo_via_env_1:
                        binding.txtSpnTipoViaEnv1.setError(null);
                        model.setTipo_via_env_1(item.getName());
                        break;
                    case R.id.txt_spn_letra_env_1:
                        binding.txtSpnLetraEnv1.setError(null);
                        model.setLetra_env_1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_bis_env_1:
                        binding.txtSpnBisEnv1.setError(null);
                        model.setBis_env_1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_letra_env_2:
                        binding.txtSpnLetraEnv2.setError(null);
                        model.setLetra_env_2(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_pcardinal_env_1:
                        binding.txtSpnPcardinalEnv1.setError(null);
                        model.setPcardinal_env_1(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_letra_env_3:
                        binding.txtSpnLetraEnv3.setError(null);
                        model.setLetra_env_3(item.getId() == -1 ? "" : item.getName());
                        break;
                    case R.id.txt_spn_pcardinal_env_2:
                        binding.txtSpnPcardinalEnv2.setError(null);
                        model.setPcardinal_env_2(item.getId() == -1 ? "" : item.getName());
                        break;
                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void showDpto(int id, String title, List<DepartamentoDTO> data, String itemSelected){
        ListDpto dialogDpto = new ListDpto();
        dialogDpto.loadData(title, data, itemSelected, new ListDpto.ListenerResponse() {
            @Override
            public void result(DepartamentoDTO item) {
                switch(id) {
                    case R.id.txt_spn_departamento:
                        clearCity();

                        binding.txtSpnDepartamento.setError(null);
                        model.setId_departamento(item.getId_dpto());
                        model.setDepartamento(item.getName_dpto());
                        listCiudad = item.getCiudades();
                        break;
                    case R.id.txt_spn_departamento_env:
                        clearCity_2();

                        binding.txtSpnDepartamentoEnv.setError(null);
                        model.setId_departamento_env(item.getId_dpto());
                        model.setDepartamento_env(item.getName_dpto());
                        listCiudad_2 = item.getCiudades();
                        break;
                }
            }
        });
        dialogDpto.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void showCity(int id, String title, List<CiudadDTO> data, String itemSelected){
        ListCity dialogCity = new ListCity();
        dialogCity.loadData(title, data, itemSelected, new ListCity.ListenerResponse() {
            @Override
            public void result(CiudadDTO item) {
                switch(id) {
                    case R.id.txt_spn_name_ciudad:
                        clearBarrio();

                        binding.txtSpnNameCiudad.setError(null);
                        model.setName_ciudad(item.getName_ciudad());
                        model.setId_ciudad(item.getId_ciudad());

                        //Buscar barrios
                        getBarrios(id, item.getId_ciudad());
                        break;
                    case R.id.txt_spn_name_ciudad_env:
                        clearBarrio_2();

                        binding.txtSpnNameCiudadEnv.setError(null);
                        model.setName_ciudad_env(item.getName_ciudad());
                        model.setId_ciudad_env(item.getId_ciudad());

                        //Buscar barrios
                        getBarrios(id, item.getId_ciudad());
                        break;
                }
            }
        });
        dialogCity.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void showBarrio(int id, String title, List<BarrioDTO> datafilter, List<BarrioDTO> data){
        MH_Dialogs_Barrio dialogBarrio = new MH_Dialogs_Barrio();
        dialogBarrio.loadData(datafilter, data, new MH_Dialogs_Barrio.ListenerResponse() {
            @Override
            public void result(BarrioDTO item) {
                switch (id){
                    case R.id.txt_spn_barrio:
                        binding.txtSpnBarrio.setError(null);
                        model.setBarrio(item.getName_barrio());
                        model.setId_barrio(item.getId_barrio());
                        break;
                    case R.id.txt_spn_barrio_env:
                        binding.txtSpnBarrioEnv.setError(null);
                        model.setBarrio_env(item.getName_barrio());
                        model.setId_barrio_env(item.getId_barrio());
                        break;
                }
            }
        });
        dialogBarrio.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public void getBarrios(int id, String id_ciudad){
        showProgress();
        inscripController.getBarrios(id_ciudad, new TTResultListener<ListBarrioDTO>() {
            @Override
            public void success(ListBarrioDTO result) {
                dismissProgress();
                switch (id){
                    case R.id.txt_spn_name_ciudad:
                        listBarrio = new ArrayList<>();
                        listBarrio.addAll(result.getBarrios());
                        break;
                    case R.id.txt_spn_name_ciudad_env:
                        listBarrio_2 = new ArrayList<>();
                        listBarrio_2.addAll(result.getBarrios());
                        break;
                }
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void clearCity(){
        listCiudad=null;
        model.setId_ciudad("");
        model.setName_ciudad("");

        clearBarrio();
    }

    private void clearBarrio(){
        listBarrio=null;
        model.setId_barrio("");
        model.setBarrio("");
    }

    private void clearCity_2(){
        listCiudad_2=null;
        model.setId_ciudad_env("");
        model.setName_ciudad_env("");

        clearBarrio_2();
    }

    private void clearBarrio_2(){
        listBarrio_2=null;
        model.setId_barrio_env("");
        model.setBarrio_env("");
    }

    public void nextPage(){
        Bundle bundle = new Bundle();
        bundle.putParcelable(DatosContactoFragment.TAG, model);

        baseActivityListener.replaceFragmentWithBackStack(DatosContactoFragment.class, true, bundle);
    }

    BaseActivityListener baseActivityListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivityListener) {
            baseActivityListener = (BaseActivityListener) context;
        } else
            throw new RuntimeException(context.toString().concat(" is not OnInteractionActivity"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        baseActivityListener = null;
    }
}
