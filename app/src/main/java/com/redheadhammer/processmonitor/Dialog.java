package com.redheadhammer.processmonitor;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

public class Dialog {
    public static void createDialog(Context context, String title, String message, String btn, long timeInSeconds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(btn, (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button NegativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            assert(NegativeButton != null);

            NegativeButton.setText(String.format("%s %s", btn, " (5)"));
            NegativeButton.setEnabled(false);

            new CountDownTimer(timeInSeconds*1000, 1000) {
                @Override
                public void onTick(long l) {
                    NegativeButton.setText(String.format("%s %s", btn, ((l+1000)/1000)));
                }

                @Override
                public void onFinish() {
                    NegativeButton.setText(btn);
                    NegativeButton.setEnabled(true);
                }
            }.start();
        });

        alertDialog.show();
    }
}
