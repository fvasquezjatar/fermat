package com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.holders;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitdubai.android_fermat_ccp_wallet_bitcoin.R;
import com.bitdubai.fermat_android_api.ui.expandableRecicler.ChildViewHolder;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.CryptoWalletTransaction;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.enums.ShowMoneyType;


import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.WalletUtils.formatBalanceString;

/**
 * Created by Matias Furszyfer on 28/10/15.
 */
public class TransactionViewHolder extends ChildViewHolder {

    private Resources res;
    private View itemView;

    private TextView txt_amount;
    private TextView txt_notes;
    private TextView txt_time;



    /**
     * Public constructor for the custom child ViewHolder
     *
     * @param itemView the child ViewHolder's view
     */
    public TransactionViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        res = itemView.getResources();

        txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
        txt_notes = (TextView) itemView.findViewById(R.id.txt_notes);
        txt_time = (TextView) itemView.findViewById(R.id.txt_time);
    }

    public void bind(CryptoWalletTransaction cryptoWalletTransaction) {

        if (cryptoWalletTransaction.getBalanceType() == null){
            txt_amount.setText(formatBalanceString(cryptoWalletTransaction.getAmount(), ShowMoneyType.BITCOIN.getCode()) + " BTC");
            txt_amount.setGravity(Gravity.CENTER_HORIZONTAL);
            txt_amount.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            txt_amount.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            txt_notes.setVisibility(View.GONE);
            txt_time.setVisibility(View.GONE);
        }else {
            txt_amount.setText(formatBalanceString(cryptoWalletTransaction.getAmount(), ShowMoneyType.BITCOIN.getCode()) + " BTC");

            txt_notes.setText((cryptoWalletTransaction.getMemo()==null) ? "No information" : cryptoWalletTransaction.getMemo());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            txt_time.setText(sdf.format(cryptoWalletTransaction.getTimestamp()));
        }
    }
}
