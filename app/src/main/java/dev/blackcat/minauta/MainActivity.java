package dev.blackcat.minauta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dev.blackcat.minauta.data.Account;
import dev.blackcat.minauta.data.store.PreferencesStore;

public class MainActivity extends MyAppCompatActivity
{

    Button configureButton;
    Button startButton;
    TextView accountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initComponents();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Account account = new PreferencesStore(this).getAccount();
        if (account == null)
        {
            accountTextView.setText(R.string.configure_account_text);
            startButton.setEnabled(false);
        }
        else
        {
            if (account.getSession() == null)
            {
                accountTextView.setText(account.getUsername());
                startButton.setEnabled(true);
            }
            else
            {
                Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                startActivity(intent);
            }
        }
    }

    private void initComponents()
    {
        accountTextView = this.getViewById(R.id.accountTextView);

        configureButton = this.getViewById(R.id.configureButton);
        configureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        startButton = this.getViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                startActivity(intent);
            }
        });
    }
}
