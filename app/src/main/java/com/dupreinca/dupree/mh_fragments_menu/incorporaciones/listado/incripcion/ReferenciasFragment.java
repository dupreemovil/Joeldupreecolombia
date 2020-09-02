package com.dupreinca.dupree.mh_fragments_menu.incorporaciones.listado.incripcion;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.InscripcionController;
import com.dupreeinca.lib_api_rest.controller.UploadFileController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.InscriptionDTO;
import com.dupreeinca.lib_api_rest.model.dto.request.Referencia;
import com.dupreeinca.lib_api_rest.model.dto.response.GenericDTO;
import com.dupreeinca.lib_api_rest.util.models.ModelList;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentReferenciasBinding;
import com.dupreinca.dupree.files.LoadJsonFile;
import com.dupreinca.dupree.files.ManagerFiles;
import com.dupreinca.dupree.mh_dialogs.SingleListDialog;
import com.dupreinca.dupree.mh_utilities.Validate;
import com.dupreinca.dupree.view.activity.BaseActivityListener;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferenciasFragment extends BaseFragment implements View.OnClickListener {

    public static String TAG = ReferenciasFragment.class.getName();

    private FragmentReferenciasBinding binding;
    private InscriptionDTO model;
    private Referencia modelRefPersonal;
    private Referencia modelRefFamiliar;

    private UploadFileController uploadFileController;
    private InscripcionController inscripController;

    private List<ModelList> listParentezco;

    public ReferenciasFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_referencias;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if((bundle = getArguments()) != null && (model = bundle.getParcelable(TAG)) != null) {
            isOnCreate = true;
            modelRefPersonal = model.getReferencia().get(0);
            modelRefFamiliar = model.getReferencia().get(1);
        } else {
            onBack();
        }
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentReferenciasBinding) view;
        binding.setCallback(this);
        binding.setModel(model);
        binding.setModelRefPersonal(modelRefPersonal);
        binding.setModelRefFamiliar(modelRefFamiliar);
    }

    @Override
    protected void onLoadedView() {
        inscripController = new InscripcionController(getContext());
        uploadFileController = new UploadFileController(getContext());

        LoadJsonFile jsonFile = new LoadJsonFile(getContext());
        listParentezco = jsonFile.getParentezcos(ManagerFiles.PARENTEZCOS.getKey());

        if(isOnCreate){
            isOnCreate = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtSpnParentesco1://
                showList(view.getId(), getString(R.string.parentesco), listParentezco, model.getReferencia().get(0).getName_parentesco());
                break;
            case R.id.txtSpnParentesco2://
                showList(view.getId(), getString(R.string.parentesco), listParentezco, model.getReferencia().get(1).getName_parentesco());
                break;
            case R.id.btnContinuar:
                if(validate()) {
                    nextPage();
                }
                break;
        }
    }

    public void showList(int id, String title, List<ModelList> data, String itemSelected){
        SingleListDialog dialog = new SingleListDialog();
        dialog.loadData(title, data, itemSelected, new SingleListDialog.ListenerResponse() {
            @Override
            public void result(ModelList item) {
                switch(id){
                    case R.id.txtSpnParentesco1:
                        binding.txtSpnParentesco1.setError(null);
                        modelRefPersonal.setParentesco(String.valueOf(item.getId()));
                        modelRefPersonal.setName_parentesco(item.getName());
                        break;
                    case R.id.txtSpnParentesco2:
                        binding.txtSpnParentesco2.setError(null);
                        modelRefFamiliar.setParentesco(String.valueOf(item.getId()));
                        modelRefFamiliar.setName_parentesco(item.getName());
                        break;
                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    private boolean validate(){
        return validatePersonal() && validateFamiliar();
    }

    private boolean validatePersonal(){
        Validate valid = new Validate();

        if(modelRefPersonal.getNombre().isEmpty()){
            msgToast("Nombre de ref. personal... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtName1);
            return false;
        }
        if(modelRefPersonal.getApellido().isEmpty()){
            msgToast("Apellido de ref. personal... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtLastName1);
            return false;
        }
        if (!modelRefPersonal.getTelefono().isEmpty() && !modelRefPersonal.getCelular().isEmpty() && modelRefPersonal.getTelefono().equals(modelRefPersonal.getCelular()))
        {
            msgToast("Teléfonos deben ser diferentes... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtCellPhone1);
            return false;
        }
        if (!modelRefPersonal.getCelular().isEmpty() && modelRefPersonal.getCelular().length() < 10)
        {
            msgToast("Teléfono movil (10 números)... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtCellPhone1);
            return false;
        }

        if(modelRefPersonal.getName_parentesco().isEmpty()){
            msgToast("Parentesco de ref. personal... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtSpnParentesco1);
            return false;
        }


        return true;
    }

    private boolean validateFamiliar(){
        Validate valid = new Validate();

        if(modelRefFamiliar.getNombre().isEmpty()){
            msgToast("Nombre de ref. familiar... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtName2);
            return false;
        }
        if(modelRefFamiliar.getApellido().isEmpty()){
            msgToast("Apellido de ref. familiar... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtLastName2);
            return false;
        }
        if (!modelRefFamiliar.getTelefono().isEmpty() && !modelRefFamiliar.getCelular().isEmpty() && modelRefFamiliar.getTelefono().equals(modelRefFamiliar.getCelular()))
        {
            msgToast("Teléfonos deben ser diferentes... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtCellPhone2);
            return false;
        }
        if (!modelRefFamiliar.getCelular().isEmpty() && modelRefFamiliar.getCelular().length() < 10)
        {
            msgToast("Teléfono movil (10 números)... Verifique");
            valid.setLoginError(getResources().getString(R.string.campo_requerido), binding.txtCellPhone2);
            return false;
        }
        if(modelRefFamiliar.getName_parentesco().isEmpty()){
            msgToast("Parentesco de ref. familiar... Verifique");
            valid.setLoginError(getString(R.string.campo_requerido),binding.txtSpnParentesco2);
            return false;
        }

        return true;
    }

    public void nextPage(){
        showProgress();
        if(model.isModeEdit()) {
            if(!model.containHttp(model.getCedula_frontal())){
                sendImageMultiPart(model.getCedula_frontal(), R.id.imgCedFrontal);
            } else if(!model.containHttp(model.getCedula_adverso())){
                sendImageMultiPart(model.getCedula_adverso(), R.id.imgCedAdverso);
            } else if(!model.containHttp(model.getPagare_frontal())){
                sendImageMultiPart(model.getPagare_frontal(), R.id.imgPagFrontal);
            } else if(!model.containHttp(model.getPagare_adverso())){
                sendImageMultiPart(model.getPagare_adverso(), R.id.imgPagAdverso);
            } else {
                postInscipcion(obtainData());
            }
        } else {
            sendImageMultiPart(model.getCedula_frontal(), R.id.imgCedFrontal);
        }
    }

    private void sendImageMultiPart(String pathFile, int id){
//        showProgress();
        uploadFileController.uploadImage(pathFile, new TTResultListener<GenericDTO>() {
            @Override
            public void success(GenericDTO result) {
//                dismissProgress();
                switch (id){
                    case R.id.imgCedFrontal:
                        Log.i(TAG, "BROACAST_INSCRIP_TYPE_IMG_CED_FRONT_URL");
                        model.setCedula_frontal(result.getResult());

                        if(!model.isModeEdit() || !model.containHttp(model.getCedula_adverso())){//en modo edicion si file == null, no se ha modificado y se llama al sigueinet caso
                            sendImageMultiPart(model.getCedula_adverso(), R.id.imgCedAdverso);
                        } else {
                            if(!model.containHttp(model.getCedula_adverso())){
                                sendImageMultiPart(model.getPagare_frontal(), R.id.imgPagFrontal);
                            } else if(!model.containHttp(model.getPagare_adverso())){
                                sendImageMultiPart(model.getPagare_adverso(), R.id.imgPagAdverso);
                            } else {
                                postInscipcion(obtainData());
                            }
                        }
                        break;
                    case R.id.imgCedAdverso:
                        Log.i(TAG, "BROACAST_INSCRIP_TYPE_IMG_CED_ADVER_URL");
                        model.setCedula_adverso(result.getResult());

                        if(!model.isModeEdit() || !model.containHttp(model.getPagare_frontal())){//en modo edicion si file == null, no se ha modificado y se llama al sigueinet caso
                            sendImageMultiPart(model.getPagare_frontal(), R.id.imgPagFrontal);
                        } else if(!model.containHttp(model.getPagare_adverso())){
                            sendImageMultiPart(model.getPagare_adverso(), R.id.imgPagAdverso);
                        } else {
                            postInscipcion(obtainData());
                        }
                        break;
                    case R.id.imgPagFrontal:
                        Log.i(TAG, "BROACAST_INSCRIP_TYPE_IMG_PAG_FRONT_URL");
                        model.setPagare_frontal(result.getResult());

                        if(!model.isModeEdit() || !model.containHttp(model.getPagare_adverso())){//en modo edicion si file == null, no se ha modificado y se llama al sigueinet caso
                            sendImageMultiPart(model.getPagare_adverso(), R.id.imgPagAdverso);
                        } else {
                            postInscipcion(obtainData());
                        }
                        break;
                    case R.id.imgPagAdverso:
                        Log.i(TAG, "BROACAST_INSCRIP_TYPE_IMG_PAG_ADVER_URL");
                        model.setPagare_adverso(result.getResult());

                        postInscipcion(obtainData());
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

    private InscriptionDTO obtainData(){
        Log.e(TAG,"obtainData() -> init()");

        List<Referencia> referenciaList = new ArrayList<>();
        referenciaList.add(modelRefPersonal);
        referenciaList.add(modelRefFamiliar);

        model.setReferencia(referenciaList);

        List<String> listCedula = new ArrayList<>();
        listCedula.add(model.getCedula_frontal());
        listCedula.add(model.getCedula_adverso());

        List<String> listPagare = new ArrayList<>();
        listPagare.add(model.getPagare_frontal());
        listPagare.add(model.getPagare_adverso());

        model.setImg_cedula(listCedula);
        model.setPagare(listPagare);

        Log.e(TAG, "obtainData() -> model: "+new Gson().toJson(model));
        return model;
    }

    private void postInscipcion(InscriptionDTO data){
//        showProgress();
        inscripController.postIncripcion(data, new TTResultListener<GenericDTO>() {
            @Override
            public void success(GenericDTO result) {
                dismissProgress();
                msgToast(result.getResult());
//                clearData();
                updateList();
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void updateList(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "hola");

        Activity a = getActivity();
        if(a != null) {
            a.setResult(Activity.RESULT_OK, returnIntent);
            a.finish();
        }
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
