package dev.blackcat.minauta.ui.account

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.blackcat.minauta.R
import dev.blackcat.minauta.data.AccountType
import dev.blackcat.minauta.data.StoredAccount


class AccountRecyclerViewAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val EMPTY_VIEW = -1000;
    }

    class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val usernameCheckBox = view.findViewById<CheckBox>(R.id.usernameCheckBox)
        val accountTypeTextView = view.findViewById<TextView>(R.id.accountTypeTextView)
        val editImageView = view.findViewById<ImageView>(R.id.editImageView)
        val removeImageView = view.findViewById<ImageView>(R.id.removeImageView)

    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    var accounts: List<StoredAccount> = listOf()
    var onEditAccount: ((position: Int) -> Unit)? = null
    var onRemoveAccount: ((position: Int) -> Unit)? = null
    var onSelectAccount: ((position: Int) -> Unit)? = null

    fun setStoredAccounts(accounts: List<StoredAccount>) {
        this.accounts = accounts;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (accounts.isNotEmpty()) accounts.size else 1

    override fun getItemViewType(position: Int) = if (accounts.isEmpty()) EMPTY_VIEW else super.getItemViewType(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context);
        if (viewType == EMPTY_VIEW) {
            val view = inflater.inflate(R.layout.row_empty_account, parent, false)
            return EmptyViewHolder(view)
        }
        val view = inflater.inflate(R.layout.row_account, parent, false)
        return AccountViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AccountViewHolder) {
            val account = accounts[position]

            holder.accountTypeTextView.text = when (account.accountType) {
                AccountType.NATIONAL -> context.getString(R.string.national_account_text)
                AccountType.INTERNATIONAL -> context.getString(R.string.international_account_text)
            }

            holder.usernameCheckBox.text = "${account.username}"
            holder.usernameCheckBox.isChecked = account.selected
            holder.usernameCheckBox.setOnClickListener { v ->
                onSelectAccount?.let { onSelectAccount ->
                    onSelectAccount(position)
                }
            }

            holder.editImageView.setOnClickListener { v ->
                onEditAccount?.let { onEditAccount ->
                    onEditAccount(position)
                }
            }

            holder.removeImageView.setOnClickListener { v ->
                onRemoveAccount?.let { onRemoveAccount ->
                    onRemoveAccount(position)
                }
            }
        }
    }


}