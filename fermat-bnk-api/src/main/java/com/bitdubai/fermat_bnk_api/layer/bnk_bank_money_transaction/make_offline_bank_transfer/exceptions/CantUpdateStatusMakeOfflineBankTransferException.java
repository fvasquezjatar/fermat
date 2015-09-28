package com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.make_offline_bank_transfer.exceptions;

import com.bitdubai.fermat_api.FermatException;

/**
 * Created by Yordin Alayn on 24.09.15.
 */

public class CantUpdateStatusMakeOfflineBankTransferException extends FermatException {

    public static final String DEFAULT_MESSAGE = "Falled To Update the Status Bank Transaction Make Offline Bank Transfer.";
    public CantUpdateStatusMakeOfflineBankTransferException(String message, Exception cause, String context, String possibleReason) {
        super(message, cause, context, possibleReason);
    }
}
