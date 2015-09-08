package com.bitdubai.fermat_dap_api.layer.module.asset_issuer.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.WalletCategory;
import com.bitdubai.fermat_api.layer.all_definition.enums.WalletType;
import com.bitdubai.fermat_dap_api.all_definition.digital_asset.DigitalAsset;

/**
 * Created by franklin on 07/09/15.
 */
public interface AssetIssuer {
    //Esta interfaz tendra que contener todos las propiedades que se van a pedir en la sub app, para que se guarde el objeto digital asset
    //en su creacion, y manipular toda la informacion y workflow respectivo.
    //Falta los seters
    String getWalletPublicKey();
    void setWalletPublicKey(String walletPublicKey);

    String getAssetUserIdentityPublicKey();
    void setAssetUserIdentityPublicKey(String assetUserIdentityPublicKey);

    DigitalAsset getDigitalAsset();
    void setDigitialAsset(DigitalAsset digitialAsset);

    WalletCategory getWalletCategory();
    void setWalletCategory(WalletCategory walletCategory);

    WalletType getWalletType();
    void setWalletType(WalletType walletType);
}
