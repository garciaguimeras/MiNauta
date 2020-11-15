package dev.blackcat.minauta.ui.account

import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.AccountType
import dev.blackcat.minauta.ui.MyAppCompatActivity

class AccountActivity : MyAppCompatActivity() {

    lateinit var viewModel: AccountViewModel

    lateinit var accountsView: AccountRecyclerView
    lateinit var addAccountButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val dialog = AccountDialog(this)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(AccountViewModel::class.java)
        viewModel.accountsLiveData.observe(this, Observer { accounts ->
            accountsView.setStoredAccounts(accounts)
        })

        accountsView = findViewById(R.id.accountsRecyclerView)
        accountsView.initComponent()
        accountsView.setStoredAccounts(viewModel.getStoredAccounts())
        accountsView.setOnEditAccount { position ->
            val account = viewModel.getStoredAccount(position)
            account?.let { account ->
                dialog.editAccount(account) { account ->
                    viewModel.updateStoredAccount(position, account)
                }
            }
        }
        accountsView.setOnRemoveAccount { position ->
            dialog.removeAccount {
                viewModel.removeStoredAccount(position)
            }
        }
        accountsView.setOnSelectAccount { position ->
            viewModel.selectStoredAccount(position)
        }

        addAccountButton = findViewById(R.id.addAccountButton)
        addAccountButton.setOnClickListener { v ->
            dialog.createAccount { account ->
                viewModel.addStoredAccount(account)
            }
        }
    }

}