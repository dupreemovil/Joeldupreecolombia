package com.dupreinca.dupree.mh_fragments_menu;


import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.UserController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.DataAuth;
import com.dupreeinca.lib_api_rest.model.dto.response.DataUser;
import com.dupreeinca.lib_api_rest.model.dto.response.PerfilDTO;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.FragmentConfigModPerfiBinding;
import com.dupreinca.dupree.mh_utilities.Validate;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigModPerfilFragment extends BaseFragment {
    private final String TAG = ConfigModPerfilFragment.class.getName();
    private FragmentConfigModPerfiBinding binding;
    private UserController userController;

    public ConfigModPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_config_mod_perfi;
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (FragmentConfigModPerfiBinding) view;


        binding.btnEditPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validatePerfil()){
                    DataUser user = new DataUser(
                            binding.txtNamePerfil.getText().toString(),
                            binding.txtLastnamePerfil.getText().toString(),
                            binding.txtPhonePerfil.getText().toString(),
                            binding.txtCellphonePerfil.getText().toString(),
                            binding.txtEmail.getText().toString()
                    );

                    editarUsuario(user);
                }
            }
        });

        binding.txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.txtEmail.removeTextChangedListener(this);
                String email_digitado = binding.txtEmail.getText().toString();
                String email_modificado = email_digitado.replaceAll("[^a-zA-Z0-9@.#$%^&*_&?$()]", "");

                //Log.e("TECLA", "presente:"+email_digitado+". Modificado:" + email_modificado);
                binding.txtEmail.setText(email_modificado);
                binding.txtEmail.setSelection(email_modificado.length());
                binding.txtEmail.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editarUsuario(DataUser user){
        showProgress();
        userController.putPerfil(user, new TTResultListener<DataAuth>() {
            @Override
            public void success(DataAuth result) {
                dismissProgress();
                msgToast(result.getResult());
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    @Override
    protected void onLoadedView() {
        userController = new UserController(getContext());
        checkPerfil();
    }

    private void checkPerfil(){
        String jsonPerfil = mPreferences.getJSON_PerfilUser(getActivity());
        if(jsonPerfil!=null){
            DataUser perfilUser = new Gson().fromJson(jsonPerfil, DataUser.class);
            if(perfilUser!=null) {
                setData(perfilUser);
            }
        } else {
            getDataUser(new Identy(""));
        }
    }


    private void getDataUser(Identy data){
        showProgress();
        userController.getPerfilUser(data,new TTResultListener<PerfilDTO>() {
            @Override
            public void success(PerfilDTO result) {
                dismissProgress();
                setData(result.getPerfilList().get(0));
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }
    private boolean validatePerfil(){
        Validate validate = new Validate();

        if(binding.txtNamePerfil.getText().toString().isEmpty()){
            validate.setLoginError(getString(R.string.campo_requerido), binding.txtNamePerfil);
            return false;
        }else if(binding.txtLastnamePerfil.getText().toString().isEmpty()){
            validate.setLoginError(getString(R.string.campo_requerido), binding.txtLastnamePerfil);
            return false;
        }else if(binding.txtPhonePerfil.getText().toString().isEmpty()){
            validate.setLoginError(getString(R.string.campo_requerido), binding.txtPhonePerfil);
            return false;
        }else if(binding.txtCellphonePerfil.getText().toString().length()<10){
            validate.setLoginError(getString(R.string.campo_requerido), binding.txtCellphonePerfil);
            return false;
        }else if(binding.txtPhonePerfil.getText().toString().equals(binding.txtCellphonePerfil.getText().toString())){
            validate.setLoginError(getString(R.string.telefonos_iguales), binding.txtCellphonePerfil);
            return false;
        }else if(validate.isValidEmail(binding.txtEmail.getText().toString())){
            validate.setLoginError(getString(R.string.campo_requerido), binding.txtEmail);
            return false;
        }

        return  true;
    }

    private void
    setData(DataUser dataUser){
        binding.txtNamePerfil.setText(dataUser.getNombre());
        binding.txtLastnamePerfil.setText(dataUser.getApellido());
        binding.txtPhonePerfil.setText(dataUser.getTelefono());
        binding.txtCellphonePerfil.setText(dataUser.getCelular());
        binding.txtEmail.setText(dataUser.getCorreo());
    }

}
