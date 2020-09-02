package com.dupreinca.dupree.mh_dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import androidx.appcompat.app.AlertDialog;

import com.dupreinca.dupree.R;

/**
 * Fragmento con diálogo básico
 */
public class SimpleDialog extends DialogFragment {

    private final String TAG = SimpleDialog.class.getName();

    private String titulo="";
    private String message="";

    public SimpleDialog() {
    }

    public void loadData(String titulo, String message) {
        this.titulo = titulo;
        this.message = message;
    }

    public void setListener(ListenerResult listenerResult) {
        this.listenerResult = listenerResult;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSimpleDialog();
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(titulo)
                .setMessage(message)
                .setPositiveButton(R.string.aceptar,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acciones
                                if(listenerResult != null)
                                    listenerResult.result(true);
                                dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancelar,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acciones
                                if(listenerResult != null)
                                    listenerResult.result(false);
                                dismiss();
                            }
                        });

        return builder.create();
    }

    private ListenerResult listenerResult;
    public interface ListenerResult {
        void result(boolean status);
    }
}