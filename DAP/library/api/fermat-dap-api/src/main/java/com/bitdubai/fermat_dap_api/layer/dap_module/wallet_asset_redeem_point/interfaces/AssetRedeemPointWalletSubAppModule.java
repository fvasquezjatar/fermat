package com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_redeem_point.interfaces;

import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;

import com.bitdubai.fermat_api.layer.modules.interfaces.FermatSettings;
import com.bitdubai.fermat_dap_api.layer.dap_identity.redeem_point.interfaces.RedeemPointIdentity;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWallet;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWalletList;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantCreateWalletException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;

import java.util.List;

/**
 * Created by franklin on 16/10/15.
 */
public interface AssetRedeemPointWalletSubAppModule extends ModuleManager<FermatSettings, ActiveActorIdentityInformation> {
    /**
     * (non-Javadoc)
     * @see List<AssetRedeemPointWalletList> getAssetRedeemPointWalletBalancesBook(String publicKey)
     */
    List<AssetRedeemPointWalletList> getAssetRedeemPointWalletBalances(String publicKey) throws CantLoadWalletException;

    AssetRedeemPointWallet loadAssetRedeemPointWallet(String walletPublicKey) throws CantLoadWalletException;

    void createWalletAssetRedeemPoint(String walletPublicKey) throws CantCreateWalletException;

    RedeemPointIdentity getActiveAssetRedeemPointIdentity() throws CantGetIdentityRedeemPointException;

}
