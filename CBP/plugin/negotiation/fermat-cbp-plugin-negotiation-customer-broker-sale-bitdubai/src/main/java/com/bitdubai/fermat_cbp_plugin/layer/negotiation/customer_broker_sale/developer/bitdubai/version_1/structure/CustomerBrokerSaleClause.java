package com.bitdubai.fermat_cbp_plugin.layer.negotiation.customer_broker_sale.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;

import java.util.UUID;

/**
 * Created by angel on 21/10/15.
 */
public class CustomerBrokerSaleClause implements Clause{

    private final UUID clauseId;
    private final ClauseType type;
    private final String value;
    private final ClauseStatus status;
    private final String proposedBy;


    public CustomerBrokerSaleClause(UUID clauseId, ClauseType type, String value, ClauseStatus status, String proposedBy){
        this.clauseId = clauseId;
        this.type = type;
        this.value = value;
        this.status = status;
        this.proposedBy = proposedBy;
    }

    @Override
    public UUID getClauseId() {
        return this.clauseId;
    }

    @Override
    public ClauseType getType() {
        return this.type;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public ClauseStatus getStatus() {
        return this.status;
    }

    @Override
    public String getProposedBy() {
        return this.proposedBy;
    }
}
