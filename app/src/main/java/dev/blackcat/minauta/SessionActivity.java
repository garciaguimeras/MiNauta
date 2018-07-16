package dev.blackcat.minauta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.net.Connection;
import dev.blackcat.minauta.net.ConnectionFactory;
import dev.blackcat.minauta.net.FakeConnection;
import dev.blackcat.minauta.data.store.PreferencesStore;

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
    }

    protected void closeSession()
    {
        PreferencesStore store = new PreferencesStore(this);
        Account account = store.getAccount();

        // TODO: Change for a real connection
        Connection connection = ConnectionFactory.createSessionProducer(FakeConnection.class);
        Connection.LogoutResult result = connection.logout(account.getSession());

        if (result.state != Connection.State.OK)
        {
            this.showDialogWithText(R.string.logout_error);
        }

        store.setSession("", "", 0);
        finish();
    }

}
