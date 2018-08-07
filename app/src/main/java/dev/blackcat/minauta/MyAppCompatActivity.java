package dev.blackcat.minauta;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyAppCompatActivity extends AppCompatActivity
{

    public <T extends View> T getViewById(int resId)
    {
        return (T) this.findViewById(resId);
    }

    public AlertDialog showDialogWithText(int resId)
    {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(this.getString(resId));
        dialog.show();

        return dialog;
    }

    public AlertDialog showOneButtonDialogWithText(int resId)
    {
        String acceptText = getString(R.string.button_accept);

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(this.getString(resId));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, acceptText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        dialog.show();

        return dialog;
    }

}
