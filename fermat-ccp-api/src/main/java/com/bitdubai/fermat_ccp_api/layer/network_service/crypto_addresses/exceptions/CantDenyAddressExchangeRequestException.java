package com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions;

import com.bitdubai.fermat_api.FermatException;

/**
 * The exception <code>CantDenyAddressExchangeRequestException</code>
 * is thrown when there is an error trying to deny an address exchange request.
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 23/09/2015.
 */
public class CantDenyAddressExchangeRequestException extends FermatException {

    private static final String DEFAULT_MESSAGE = "CAN'T DENY ADDRESS EXCHANGE REQUEST EXCEPTION";

    public CantDenyAddressExchangeRequestException(String message, Exception cause, String context, String possibleReason) {
        super(message, cause, context, possibleReason);
    }

    public CantDenyAddressExchangeRequestException(Exception cause, String context, String possibleReason) {
        this(DEFAULT_MESSAGE, cause, context, possibleReason);
    }

}
