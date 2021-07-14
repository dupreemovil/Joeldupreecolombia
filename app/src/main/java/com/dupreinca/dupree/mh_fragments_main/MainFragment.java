package com.dupreinca.dupree.mh_fragments_main;


import android.graphics.Color;
import android.os.Bundle;

import com.dupreeinca.lib_api_rest.model.dto.response.ListaOfertas;
import com.dupreinca.dupree.mh_adapters.SliderAdapter;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.model_view.Slideritem;
import com.google.android.material.appbar.AppBarLayout;


import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import 	androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.dupreeinca.lib_api_rest.model.dto.response.ImgBannerDTO;
import com.dupreinca.dupree.MainActivity;

import com.dupreinca.dupree.R;
import com.dupreinca.dupree.mh_adapters.BannerSliderAdapter;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.dupreinca.dupree.mh_utilities.PinchZoomImageView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private String TAG="MainFragment";
    SliderView slider1;

    ImageLoader img;
    public MainFragment() {
        // Required empty public constructor
    }

    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        AppBarLayout AppBarL_FragMain = (AppBarLayout) v.findViewById(R.id.AppBarL_FragMain);
        toolbar = (Toolbar) AppBarL_FragMain.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //SliderLayout slider = (SliderLayout) v.findViewById(R.id.mh_slider);

        slider1 = (SliderView)v.findViewById(R.id.imageSlider);

        SlidePresentacion1(slider1);

        ImageView imgVuelveteAsesora, imgSolicitaAsesora, imgCatalogos, imgLogin;
        imgVuelveteAsesora = (ImageView) v.findViewById(R.id.imgVuelveteAsesora);
        imgSolicitaAsesora = (ImageView) v.findViewById(R.id.imgAtencionCliente);

        imgCatalogos = (ImageView) v.findViewById(R.id.imgCatalogos);
        img = ImageLoader.getInstance();
        img.init(PinchZoomImageView.configurarImageLoader(getActivity()));
        img.displayImage(mPreferences.getImageCatalogo(getActivity()), imgCatalogos);


        timeinit = System.currentTimeMillis();
        imgLogin = (ImageView) v.findViewById(R.id.imgLogin);

        imgVuelveteAsesora.setOnClickListener(clickListener);
        imgSolicitaAsesora.setOnClickListener(clickListener);
        imgCatalogos.setOnClickListener(clickListener);
        imgLogin.setOnClickListener(clickListener);

        //Log.e(TAG,"Token: "+FirebaseInstanceId.getInstance().getToken());

        return v;
    }

    public static MainFragment newInstance() {

        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    private View.OnClickListener clickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgVuelveteAsesora:
                case R.id.imgAtencionCliente:
                case R.id.imgCatalogos:
                    ((MainActivity) getActivity()).changePage(view.getId());
                    break;
                case R.id.imgLogin:
                    ((MainActivity) getActivity()).setSelectedItem(R.id.navigation_login);
                    //((MainActivity) getActivity()).showLoginDialog();
                    break;
            }

        }
    };

    @Override
    public void onDestroy() {

        Log.i(TAG,"onDestroy()");

        timeend = System.currentTimeMillis();
        long finaltime= timeend-timeinit;
        int timesec = (int)finaltime/1000;

        RequiredVisit req = new RequiredVisit("",Integer.toString(timesec),"inicio");
        //   System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

        new Http(getActivity()).Visit(req);

        super.onDestroy();
    }

    public void SlidePresentacion(SliderLayout slider) {
        try{
            String objetcImge = mPreferences.getJSONImageBanner(getActivity());
            ImgBannerDTO.Resolution list_image = new Gson().fromJson(objetcImge, ImgBannerDTO.Resolution.class);

            if(list_image != null){
                HashMap<String, String> file_maps = new HashMap<String, String>();
                file_maps.put("1", list_image.getImg1());
                file_maps.put("2", list_image.getImg2());
                file_maps.put("3", list_image.getImg3());


                BannerSliderAdapter SliderView=null;
                for (String name : file_maps.keySet()) {

                    SliderView = new BannerSliderAdapter(getContext());
                    // initialize a SliderLayout
                    SliderView
                            //.description(name)
                            .image(file_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit);
                    //.setOnSliderClickListener(this);

                    SliderView.bundle(new Bundle());
                    SliderView.getBundle()
                            .putString("extra", name);

                    slider.addSlider(SliderView);
                }


                slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                slider.setCustomAnimation(new DescriptionAnimation());
                slider.setDuration(400000);
                slider.addOnPageChangeListener(SliderView.getListenerSlider());
            }
        }catch (Exception ex){

        }


    }


    public void SlidePresentacion1(SliderView slider) {

            String objetcImge = mPreferences.getJSONImageBanner(getActivity());
            ImgBannerDTO.Resolution list_image = new Gson().fromJson(objetcImge, ImgBannerDTO.Resolution.class);

            List<Slideritem> lista = new ArrayList<>();

            if(list_image != null){
                HashMap<String, String> file_maps = new HashMap<String, String>();
                file_maps.put("1", list_image.getImg1());
                file_maps.put("2", list_image.getImg2());
                file_maps.put("3", list_image.getImg3());


                lista.add(new Slideritem(list_image.getImg1()));
                lista.add(new Slideritem(list_image.getImg2()));
                lista.add(new Slideritem(list_image.getImg3()));


                SliderAdapter adapter = new SliderAdapter(getActivity(),lista);


                slider.setSliderAdapter(adapter);


          //      slider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!


                slider.setSliderAnimationDuration(1000);
                slider.setScrollTimeInSec(6); //set scroll delay in seconds :
                slider.startAutoCycle();



            }



    }

}
