package com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.exceptions.CantUpdateCustomerBrokerPurchaseNegotiationException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiationManager;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.database.CustomerBrokerUpdateNegotiationTransactionDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantCancelPurchaseNegotiationTransactionException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantRegisterCustomerBrokerUpdateNegotiationTransactionException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantUpdatePurchaseNegotiationTransactionException;

import java.util.UUID;

/**
 * Created by Yordin Alayn on 16.12.15.
 */
public class CustomerBrokerUpdatePurchaseNegotiationTransaction {

    /*Represent the Negotiation Purchase*/
    private CustomerBrokerPurchaseNegotiationManager        customerBrokerPurchaseNegotiationManager;

    /*Represent the Transaction database DAO */
    private CustomerBrokerUpdateNegotiationTransactionDatabaseDao   customerBrokerUpdateNegotiationTransactionDatabaseDao;


    public CustomerBrokerUpdatePurchaseNegotiationTransaction(
        CustomerBrokerPurchaseNegotiationManager                customerBrokerPurchaseNegotiationManager,
        CustomerBrokerUpdateNegotiationTransactionDatabaseDao   customerBrokerUpdateNegotiationTransactionDatabaseDao
    ) {
        this.customerBrokerPurchaseNegotiationManager               = customerBrokerPurchaseNegotiationManager;
        this.customerBrokerUpdateNegotiationTransactionDatabaseDao  = customerBrokerUpdateNegotiationTransactionDatabaseDao;
    }

    //PROCESS THE UPDATE PURCHASE NEGOTIATION TRANSACTION
    public void updatePurchaseNegotiationTranasction(CustomerBrokerPurchaseNegotiation customerBrokerPurchaseNegotiation) throws CantUpdatePurchaseNegotiationTransactionException {

        try {

            //CREATE NEGOTIATION
            this.customerBrokerPurchaseNegotiationManager.updateCustomerBrokerPurchaseNegotiation(customerBrokerPurchaseNegotiation);

            //CREATE NEGOTIATION TRANSATION
            this.customerBrokerUpdateNegotiationTransactionDatabaseDao.createRegisterCustomerBrokerUpdateNegotiationTranasction(customerBrokerPurchaseNegotiation, NegotiationType.PURCHASE);

        } catch (CantUpdateCustomerBrokerPurchaseNegotiationException e) {
            throw new CantUpdatePurchaseNegotiationTransactionException(e.getMessage(),e, CantUpdatePurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR UPDATE CUSTOMER BROKER PURCHASE NEGOTIATION, UNKNOWN FAILURE.");
        } catch (CantRegisterCustomerBrokerUpdateNegotiationTransactionException e) {
            throw new CantUpdatePurchaseNegotiationTransactionException(e.getMessage(),e, CantUpdatePurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR REGISTER CUSTOMER BROKER UPDATE PURCHASE NEGOTIATION TRANSACTION, UNKNOWN FAILURE.");
        } catch (Exception e){
            throw new CantUpdatePurchaseNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e), CantUpdatePurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR PROCESS CUSTOMER BROKER UPDATE PURCHASE NEGOTIATION, UNKNOWN FAILURE.");
        }

    }

    //PROCESS THE CANCEL PURCHASE NEGOTIATION TRANSACTION
    public void cancelPurchaseNegotiationTranasction(CustomerBrokerPurchaseNegotiation customerBrokerPurchaseNegotiation)  throws CantCancelPurchaseNegotiationTransactionException {

        try {

            //CANCEL NEGOTIATION
            this.customerBrokerPurchaseNegotiationManager.cancelNegotiation(customerBrokerPurchaseNegotiation);

            //CREATE NEGOTIATION TRANSATION
            this.customerBrokerUpdateNegotiationTransactionDatabaseDao.createRegisterCustomerBrokerUpdateNegotiationTranasction(customerBrokerPurchaseNegotiation, NegotiationType.PURCHASE);

        } catch (CantUpdateCustomerBrokerPurchaseNegotiationException e) {
            throw new CantCancelPurchaseNegotiationTransactionException(e.getMessage(),e, CantCancelPurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR CANCEL CUSTOMER BROKER PURCHASE NEGOTIATION, UNKNOWN FAILURE.");
        } catch (CantRegisterCustomerBrokerUpdateNegotiationTransactionException e) {
            throw new CantCancelPurchaseNegotiationTransactionException(e.getMessage(),e, CantCancelPurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR REGISTER CUSTOMER BROKER CANCEL PURCHASE NEGOTIATION TRANSACTION, UNKNOWN FAILURE.");
        } catch (Exception e){
            throw new CantCancelPurchaseNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e), CantCancelPurchaseNegotiationTransactionException.DEFAULT_MESSAGE, "ERROR PROCESS CUSTOMER BROKER CANCEL PURCHASE NEGOTIATION, UNKNOWN FAILURE.");
        }
    }

}
