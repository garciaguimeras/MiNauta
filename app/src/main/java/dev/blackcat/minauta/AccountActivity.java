package dev.blackcat.minauta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.store.PreferencesStore;

public class AccountActivity extends MyAppCompatActivity
{

    EditText usernameEdit;
    EditText passwordEdit;
    Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        this.initComponents();
    }

    private void initComponents()
    {
        final PreferencesStore prefsData = new PreferencesStore(this);

        Account account = prefsData.getAccount();
        usernameEdit = this.getViewById(R.id.usernameEdit);
        passwordEdit = this.getViewById(R.id.passwordEdit);
        if (account != null)
        {
            usernameEdit.setText(account.getUsername());
            passwordEdit.setText(account.getPassword());
        }

        acceptButton = this.getViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (!username.contains("@"))
                    username += "@nauta.com.cu";
                prefsData.setAccount(username, password);

                finish();
            }
        });
    }

}
