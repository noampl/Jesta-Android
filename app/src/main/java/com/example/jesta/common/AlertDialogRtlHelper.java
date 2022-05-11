package com.example.jesta.common;

import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Represents a helper for RTL direction in AlertDialog, because by default it is not showing it right.
 */
public class AlertDialogRtlHelper {

    /**
     * Makes a RTL direction AlertDialog by the specified AlertDialog.Builder.
     *
     * @param builder The builder of the AlertDialog.
     * @return Returns the instance of the AlertDialog created.
     */
    public static AlertDialog make(@NonNull AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dlg) {
                // Sets the title and message direction to RTL:
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        });
        return dialog;
    }

}
