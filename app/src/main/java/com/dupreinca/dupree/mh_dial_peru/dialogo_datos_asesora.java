package com.dupreinca.dupree.mh_dial_peru;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dupreinca.dupree.R;

import java.util.regex.Pattern;

public class dialogo_datos_asesora {

    public  interface DatosCuadroDialogo{
        void ResultadoDatos(String dato_corr, String dato_celu);
    }

    private DatosCuadroDialogo interfaz;

    private final dialogo_datos_asesora contexto;

    public dialogo_datos_asesora(final Context Contexto, DatosCuadroDialogo actividad) {

        interfaz = actividad;
        contexto = this;

        final Dialog dialogo = new Dialog(Contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);

        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(R.layout.dialogo_datos_asesora);
        ImageView guardar = (ImageView)dialogo.findViewById(R.id.img_guar_dato);
        ImageView cerrar  = (ImageView)dialogo.findViewById(R.id.img_cerr_dial);
        final TextInputEditText tie_dato_corr = (TextInputEditText) dialogo.findViewById(R.id.tie_dato_corr);
        final TextInputEditText tie_dato_celu = (TextInputEditText) dialogo.findViewById(R.id.tie_dato_celu);
        TextView txt_mens_modi =  (TextView) dialogo.findViewById(R.id.txt_mens_modi);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tie_dato_corr.getText().toString().trim().equalsIgnoreCase("")){
                    txt_mens_modi.setText("Ingrese correo electronico");
                } else if(!validarEmail(tie_dato_corr.getText().toString())){
                    txt_mens_modi.setText("Correo incorrecto");
                } else if(tie_dato_celu.getText().toString().trim().equalsIgnoreCase("")){
                    txt_mens_modi.setText("Ingrese celular");
                } else if(tie_dato_celu.getText().toString().trim().length()!=9){
                    txt_mens_modi.setText("Celular incorrecto");
                } else if (!tie_dato_corr.getText().toString().trim().equalsIgnoreCase("") && !tie_dato_celu.getText().toString().trim().equalsIgnoreCase("")){
                    interfaz.ResultadoDatos(tie_dato_corr.getText().toString(), tie_dato_celu.getText().toString());
                    dialogo.dismiss();
                }
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });


        dialogo.show();
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
