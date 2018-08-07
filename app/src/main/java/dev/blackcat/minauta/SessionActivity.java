package dev.blackcat.minauta;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dev.blackcat.minauta.async.SessionAvailableTimeAsyncTask;
import dev.blackcat.minauta.async.SessionLogoutAsyncTask;
import dev.blackcat.minauta.async.SessionUsedTimeTask;
import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.net.Connection;
import dev.blackcat.minauta.net.ConnectionFactory;
import dev.blackcat.minauta.data.store.PreferencesStore;
import dev.blackcat.minauta.net.JNautaConnection;

public class SessionActivity extends MyAppCompatActivity
{

    Button closeButton;
    TextView availableTimeTextView;
    TextView usedTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        this.initializeComponents();
    }

    protected void initializeComponents()
    {
        closeButton = this.getViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                closeSession();
            }
        });

        availableTimeTextView = this.getViewById(R.id.availableTimeTextView);
        usedTimeTextView = this.getViewById(R.id.usedTimeTextView);

        final String availableTimeText = getString(R.string.available_time_text);
        new SessionAvailableTimeAsyncTask(this, new SessionAvailableTimeAsyncTask.OnTaskResult() {
            @Override
            public void onResult(Connection.AvailableTimeResult result)
            {
                if (result.state != Connection.State.OK)
                    availableTimeTextView.setText(availableTimeText + " --:--:--");
                else
                    availableTimeTextView.setText(availableTimeText + " " + result.availableTime);
            }
        }).execute();

        final String usedTimeText = getString(R.string.used_time_text);
        new SessionUsedTimeTask(this, new SessionUsedTimeTask.OnTaskResult() {
            @Override
            public void onResult(final String time)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        usedTimeTextView.setText(usedTimeText + " " + time);
                    }
                });
            }
        }).execute();
    }

    protected void closeSession()
    {
        final PreferencesStore store = new PreferencesStore(this);
        final AlertDialog closingDialog = this.showDialogWithText(R.string.closing_text);

        new SessionLogoutAsyncTask(this, new SessionLogoutAsyncTask.OnTaskResult() {
            @Override
            public void onResult(Connection.LogoutResult result)
            {
                closingDialog.dismiss();

                if (result.state != Connection.State.OK)
                {
                    SessionActivity.this.showOneButtonDialogWithText(R.string.logout_error);
                }

                store.setSession("", "", 0);
                finish();
            }
        }).execute();
    }

}
