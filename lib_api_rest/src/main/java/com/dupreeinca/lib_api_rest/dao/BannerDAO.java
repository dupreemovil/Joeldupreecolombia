package com.dupreeinca.lib_api_rest.dao;

import android.content.Context;

import com.dupreeinca.lib_api_rest.dao.base.TTCallback;
import com.dupreeinca.lib_api_rest.dao.base.TTGenericDAO;
import com.dupreeinca.lib_api_rest.model.base.TTError;
import com.dupreeinca.lib_api_rest.model.base.TTResultListener;
import com.dupreeinca.lib_api_rest.model.dto.request.ActualizacionSend;
import com.dupreeinca.lib_api_rest.model.dto.request.LiquidaSend;
import com.dupreeinca.lib_api_rest.model.dto.request.SuscripcionSend;
import com.dupreeinca.lib_api_rest.model.dto.response.BannerDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.LiquidaDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ProductCatalogoDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.ProductMadrugonDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.SusDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.UrlsCatalogosDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.VersionDTO;
import com.dupreeinca.lib_api_rest.model.dto.response.SuscripcionDTO;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class BannerDAO extends TTGenericDAO {

    public BannerDAO(Context context) {
        super(context);
    }

    public void getBanner(final TTResultListener<BannerDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<BannerDTO> call = rest.getBanner();
        call.enqueue(new TTCallback<BannerDTO>(new TTResultListener<BannerDTO>() {
            @Override
            public void success(BannerDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }

    public void getUrlCatalogos(final TTResultListener<UrlsCatalogosDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<UrlsCatalogosDTO> call = rest.getUrlCatalogos();
        call.enqueue(new TTCallback<UrlsCatalogosDTO>(new TTResultListener<UrlsCatalogosDTO>() {
            @Override
            public void success(UrlsCatalogosDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }

    public void obtainVersion(final TTResultListener<VersionDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<VersionDTO> call = rest.obtainVersion();
        call.enqueue(new TTCallback<VersionDTO>(new TTResultListener<VersionDTO>() {
            @Override
            public void success(VersionDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }

    public void getProductos(final String campanaActual, final TTResultListener<ProductCatalogoDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<ProductCatalogoDTO> call = rest.getProductos(new Gson().toJson(campanaActual));
        call.enqueue(new TTCallback<ProductCatalogoDTO>(new TTResultListener<ProductCatalogoDTO>() {
            @Override
            public void success(ProductCatalogoDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }


    public void getProductosmadrugon(final TTResultListener<ProductMadrugonDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<ProductMadrugonDTO> call = rest.getProductosmadrugon();
        call.enqueue(new TTCallback<ProductMadrugonDTO>(new TTResultListener<ProductMadrugonDTO>() {
            @Override
            public void success(ProductMadrugonDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }


    public void getSuscripcion(final TTResultListener<SuscripcionDTO> listener){
        iRest rest = getRetrofit().create(iRest.class);
        Call<SuscripcionDTO> call = rest.obtainSuscripcion();
        call.enqueue(new TTCallback<SuscripcionDTO>(new TTResultListener<SuscripcionDTO>() {
            @Override
            public void success(SuscripcionDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }

    public void suscripcion(SuscripcionSend data, final TTResultListener<SusDTO> listener){
        iRest userREST = getRetrofit().create(iRest.class);
        Call<SusDTO> call = userREST.suscripcionasesora(new Gson().toJson(data));
        call.enqueue(new TTCallback<SusDTO>(new TTResultListener<SusDTO>() {
            @Override
            public void success(SusDTO result) {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }


    public void actualizacion(ActualizacionSend data, final TTResultListener<SusDTO> listener){
        iRest userREST = getRetrofit().create(iRest.class);
        Call<SusDTO> call = userREST.registracorreo(new Gson().toJson(data));
        call.enqueue(new TTCallback<SusDTO>(new TTResultListener<SusDTO>() {
            @Override
            public void success(SusDTO result)
            {
                listener.success(result);
            }

            @Override
            public void error(TTError error) {
                listener.error(error);
            }
        },getRetrofit()));
    }



    public interface iRest {
        @GET("panel/banner/")
        Call<BannerDTO> getBanner();

        @GET("panel/catalogos")
        Call<UrlsCatalogosDTO> getUrlCatalogos();

        @GET("panel/version/")
        Call<VersionDTO> obtainVersion();

        @GET("panel/obtiene_suscripcion/")
        Call<SuscripcionDTO> obtainSuscripcion();


        @GET("pedidos/productos")
        Call<ProductCatalogoDTO> getProductos(@Query("filters") String campana);

        @POST("pedidos/productos_madrugon")
        Call<ProductMadrugonDTO> getProductosmadrugon();

        @FormUrlEncoded
        @POST("panel/suscripcion_asesora")
        Call<SusDTO> suscripcionasesora(@Field("Params") String jsonSuscripcion);


        @FormUrlEncoded
        @POST("panel/registra_correo")
        Call<SusDTO> registracorreo(@Field("Params") String jsonRegistro);

    }
}
