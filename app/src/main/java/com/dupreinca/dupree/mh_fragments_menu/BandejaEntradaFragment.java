package com.dupreinca.dupree.mh_fragments_menu;

import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;

import com.dupreeinca.lib_api_rest.controller.UserController;
import com.dupreeinca.lib_api_rest.controller.MensajesController;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.Identy;
import com.dupreeinca.lib_api_rest.model.dto.response.DataUser;
import com.dupreeinca.lib_api_rest.model.dto.response.GenericDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ItemBandeja;
import com.dupreeinca.lib_api_rest.model.dto.response.PerfilDTO;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.MenuActivity;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.databinding.LayoutRecyclerFabBinding;
import com.dupreinca.dupree.mh_adapters.MensajesListAdapter;
import com.dupreinca.dupree.mh_dialogs.SimpleDialog;
import com.dupreinca.dupree.mh_holders.MensajesHolder;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_local_data.Data;
import com.dupreeinca.lib_api_rest.model.dto.request.IdMessages;
import com.dupreeinca.lib_api_rest.model.dto.response.BandejaDTO;
import com.dupreinca.dupree.mh_required_api.RequiredAuth;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.ImageZoomActivity_Scroll;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.view.fragment.BaseFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BandejaEntradaFragment extends BaseFragment implements MensajesHolder.Events{
    private String TAG = BandejaEntradaFragment.class.getName();
    private LayoutRecyclerFabBinding binding;
    private MensajesController mensajesController;
    private MensajesListAdapter listAdapter;
    private UserController userController;
    private String userName;
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    private Profile perfil;

    public BandejaEntradaFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getMainLayout() {
        return R.layout.layout_recycler_fab;
    }

    @Override
    protected void initViews(ViewDataBinding view) {
        binding = (LayoutRecyclerFabBinding) view;

        timeinit = System.currentTimeMillis();

        perfil = getPerfil();
        timeinit = System.currentTimeMillis();

        binding.recycler.recycler.setHasFixedSize(true);
        //QUITA ANIMACIONES AL ACTUALIZAR VISTA.... POR TSNTO NO SE VE PARPADEO AL ACTUALIZAR PROGRESSBAR
        ((SimpleItemAnimator) binding.recycler.recycler.getItemAnimator()).setSupportsChangeAnimations(false);

        Data.msg.clear();
        listAdapter = new MensajesListAdapter(Data.msg, this);
        binding.recycler.recycler.setAdapter(listAdapter);
        binding.recycler.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recycler.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recycler.refresh.setRefreshing(false);
                obtainFilesFolder();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogoDelete();
            }
        });
    }

    @Override
    protected void onLoadedView() {
        mensajesController = new MensajesController(getContext());

        obtainFilesFolder();
    }

    private DataUser checkPerfil(Identy identy){
        String jsonPerfil = mPreferences.getJSON_PerfilUser(getActivity());
        DataUser perfilUser = null;

        if(jsonPerfil!=null){
            perfilUser = new Gson().fromJson(jsonPerfil, DataUser.class);
            if(perfilUser!=null) {
                return perfilUser;
            }
        } else {
            getDataUser(identy);
        }
        return perfilUser;
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


    private void setData(DataUser dataUser){
        this.userName = dataUser.getNombre()+"-"+dataUser.getApellido();
    }

    @Override
    public void onClickRoot(ItemBandeja dataRow, int row, boolean force) {
        Log.e(TAG,"onClickItem: "+String.valueOf(row)+", getNumItemSelected(): "+String.valueOf(listAdapter.getNumItemSelected())+", force: "+force);
        if(listAdapter.getNumItemSelected() > 0){
            listAdapter.calculateItemsSelected(row);
        }
        listAdapter.notifyItemChanged(row);

        if((listAdapter.getLastNumItemSelected()==1 && listAdapter.getNumItemSelected()==0) || listAdapter.getNumItemSelected()>0){
            updateView(listAdapter.getNumItemSelected());                // Actualiza Toolbar
        }
        listAdapter.setLastNumItemSelected(listAdapter.getNumItemSelected());
    }

    @Override
    public void onLongClickRoot(ItemBandeja dataRow, int row) {
        Log.e(TAG,"onLongClick: "+String.valueOf(row));
        //update data
        listAdapter.calculateItemsSelected(row);
        listAdapter.notifyItemChanged(row);

        updateView(listAdapter.getNumItemSelected());                // Actualiza Toolbar
        listAdapter.setLastNumItemSelected(listAdapter.getNumItemSelected());
    }

    @Override
    public void onClickImage(ItemBandeja dataRow, int row) {
        if(listAdapter.getNumItemSelected() > 0){
            onLongClickRoot(dataRow, row);
        } else {
            String[] array_images = new String[1];
            array_images[0] = Data.msg.get(row).getImagen();
            gotoZoomImage_Scroll(array_images);
        }
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

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"bandeja");
            System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());
            new Http(getActivity()).Visit(req);
        }

        super.onDestroy();
    }

    private void gotoZoomImage_Scroll(String[] images_array){
        Intent intent = new Intent(getActivity(), ImageZoomActivity_Scroll.class);
        intent.putExtra(ImageZoomActivity_Scroll.ARRAY_IMAGES, images_array);
        intent.putExtra(ImageZoomActivity_Scroll.POSITION_IMAGES, 0);
        startActivity(intent);
    }

    private void deleteMessages(final List<Integer> idMessages){
        showProgress();
        mensajesController.deleteMessages(new IdMessages(idMessages), new TTResultListener<GenericDTO>() {
            @Override
            public void success(GenericDTO result) {
                dismissProgress();
                filesFolderDeleted(idMessages);
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void readMessages(List<Integer> idMessages){
        showProgress();
        mensajesController.readMessages(new IdMessages(idMessages), new TTResultListener<GenericDTO>() {
            @Override
            public void success(GenericDTO result) {
                dismissProgress();
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);
            }
        });
    }

    private void obtainFilesFolder(){
        showProgress();
        mensajesController.getMessages(new TTResultListener<BandejaDTO>() {
            @Override
            public void success(BandejaDTO result) {
                dismissProgress();

                Data.msg.clear();
                Data.msg.addAll(result.getResult());

                listAdapter.notifyDataSetChanged();

                System.out.println("El result "+result.getResult().size());
                if(result.getResult().size()>0){

                }
                else{
                    ((MenuActivity)getActivity()).showbottomsheet();
                }


            }

            @Override
            public void error(TTError error) {
                dismissProgress();
                checkSession(error);

                ((MenuActivity)getActivity()).showbottomsheet();
                System.out.println("El result erro ");
            }
        });
    }

    public void filesFolderDeleted(List<Integer> arrayIdImages){
        listAdapter.clearItemSelected();
        updateView(0);
        //obtainFilesFolder();
        //este procedimiento evita la consulta anterior
        refreshAll(DELETED, arrayIdImages);
    }

    private int DELETED=0;
    private int ADDED=1;
    private void refreshAll(int action, List<Integer> arrayIdImages){
        listAdapter.clearItemSelected();
        updateView(0);

        if(action == DELETED){
            for(int i=0; i<arrayIdImages.size(); i++) {
                for(int j=0; j<Data.msg.size(); j++){
                    if(arrayIdImages.get(i) == Integer.parseInt(Data.msg.get(j).getId_mensaje())){
                        Data.msg.remove(j);
                    }
                }

            }
        } else if(action == ADDED){// esta requiere que backend envie el id de la imagen al agregar, lo cual evita la consulta luego de agregar

        }

        listAdapter.notifyDataSetChanged();
    }


    public void showDialogoDelete() {
        SimpleDialog d = new SimpleDialog();
        d.loadData(getString(R.string.eliminar), getString(R.string.desea_remover_mensajes));
        d.setListener(new SimpleDialog.ListenerResult() {
            @Override
            public void result(boolean status) {
                if(status)
                    deleteMessages(arrayIdFiles());
            }
        });
        d.show(getActivity().getSupportFragmentManager(),"mDialog");
    }

    public List<Integer> arrayIdFiles(){
        List<Integer> requiredIds=new ArrayList<>();
        if(Data.msg.size()>0){
            for(int i=0;i<Data.msg.size();i++){
                if(Data.msg.get(i).getItemSelected()){
                    requiredIds.add(Integer.parseInt(Data.msg.get(i).getId_mensaje()));
                }
            }

        }
        return requiredIds;
    }

    public void updateView(int numItemSelected){
        updateToolbar(numItemSelected);
    }

    public Status updateToolbar(int numItemSelected){
        Log.e(TAG, "updateToolbar() -> numItemSelected: "+numItemSelected);
        if(numItemSelected==0){
            if(mStatus == Status.OPEN) {
                mStatus = Status.CLOSE;
                binding.fab.hide();
            }
        } else {
            if(mStatus == Status.CLOSE){
                mStatus = Status.OPEN;
                binding.fab.show();
            }
        }

        return mStatus;
    }

    private Status mStatus = Status.CLOSE;
    private enum Status {
        OPEN,
        CLOSE
    }
}
