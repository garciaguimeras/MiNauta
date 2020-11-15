package dev.blackcat.minauta.ui.account

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.blackcat.minauta.data.StoredAccount

class AccountRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    lateinit var listAdapter: AccountRecyclerViewAdapter

    fun initComponent() {
        listAdapter = AccountRecyclerViewAdapter(context)
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter
    }

    fun setStoredAccounts(accounts: List<StoredAccount>) {
        listAdapter.setStoredAccounts(accounts)
    }

    fun setOnEditAccount(onEditAccount: (position: Int) -> Unit) {
        listAdapter.onEditAccount = onEditAccount
    }

    fun setOnRemoveAccount(onRemoveAccount: (position: Int) -> Unit) {
        listAdapter.onRemoveAccount = onRemoveAccount
    }

    fun setOnSelectAccount(onSelectAccount: (position: Int) -> Unit) {
        listAdapter.onSelectAccount = onSelectAccount
    }
}