package com.bitdubai.fermat_bch_plugin_layer.watch_only_vault.developer.bitdubai.version_1.exceptions;

import com.bitdubai.fermat_api.FermatException;

/**
 * Created by rodrigo on 12/30/15.
 */
public class CantExecuteDatabaseOperationException extends FermatException {
    public static final String DEFAULT_MESSAGE = "There was an error trying to execute a database operation.";
    public CantExecuteDatabaseOperationException(String message, Exception cause, String context, String possibleReason) {
        super(message, cause, context, possibleReason);
    }
}
