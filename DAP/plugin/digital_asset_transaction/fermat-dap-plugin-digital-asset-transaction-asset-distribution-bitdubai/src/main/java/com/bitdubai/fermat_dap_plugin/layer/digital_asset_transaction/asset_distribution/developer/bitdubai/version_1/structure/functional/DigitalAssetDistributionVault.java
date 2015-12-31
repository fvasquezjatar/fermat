package com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_distribution.developer.bitdubai.version_1.structure.functional;

import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantGetAssetIssuerActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.AssetIssuerWalletTransactionRecordWrapper;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.interfaces.AbstractDigitalAssetVault;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWallet;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletBalance;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletManager;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetTransactionsException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.UUID;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 04/10/15.
 */
public class DigitalAssetDistributionVault extends AbstractDigitalAssetVault {

    ErrorManager errorManager;
    //private final String LOCAL_STORAGE_PATH="digital-asset-transmission/";
    //private final String digitalAssetFileName="digital-asset.xml";
    //private final String digitalAssetMetadataFileName="digital-asset-metadata.xml";
    //String digitalAssetFileStoragePath;

    public DigitalAssetDistributionVault(UUID pluginId,
                                         PluginFileSystem pluginFileSystem,
                                         ErrorManager errorManager,
                                         AssetIssuerWalletManager assetIssuerWalletManager,
                                         ActorAssetIssuerManager actorAssetIssuerManager,
                                         BitcoinNetworkManager bitcoinNetworkManager) throws CantSetObjectException {
        setPluginFileSystem(pluginFileSystem);
        setPluginId(pluginId);
        setErrorManager(errorManager);
        LOCAL_STORAGE_PATH = "digital-asset-transmission/";
        setAssetIssuerWalletManager(assetIssuerWalletManager);
        setActorAssetIssuerManager(actorAssetIssuerManager);
        setBitcoinCryptoNetworkManager(bitcoinNetworkManager);
    }

    public void setErrorManager(ErrorManager errorManager) throws CantSetObjectException {
        if (errorManager == null) {
            throw new CantSetObjectException("ErrorManager is null");
        }
        this.errorManager = errorManager;
    }

    public void updateIssuerWalletBalance(DigitalAssetMetadata digitalAssetMetadata, CryptoTransaction genesisTransaction, BalanceType balanceType, String actorToPublicKey, boolean credit) throws CantLoadWalletException, CantGetTransactionsException, CantRegisterDebitException, CantGetAssetIssuerActorsException, CantRegisterCreditException {
        AssetIssuerWallet assetIssuerWallet = this.assetIssuerWalletManager.loadAssetIssuerWallet(this.walletPublicKey);
        AssetIssuerWalletBalance assetIssuerWalletBalance = assetIssuerWallet.getBookBalance(balanceType);
        ActorAssetIssuer actorAssetIssuer = this.actorAssetIssuerManager.getActorAssetIssuer();
        String actorFromPublicKey;
        if (actorAssetIssuer == null) {
            System.out.println("ASSET DISTRIBUTION Actor Issuer is null");
            actorFromPublicKey = "UNDEFINED";
        } else {
            actorFromPublicKey = actorAssetIssuer.getActorPublicKey();
        }
        System.out.println("ASSET DISTRIBUTION Actor Issuer public key:" + actorFromPublicKey);
        AssetIssuerWalletTransactionRecordWrapper assetIssuerWalletTransactionRecordWrapper = new AssetIssuerWalletTransactionRecordWrapper(
                digitalAssetMetadata,
                genesisTransaction,
                actorFromPublicKey,
                actorToPublicKey
        );
        System.out.println("ASSET DISTRIBUTION AssetIssuerWalletTransactionRecordWrapper:" + assetIssuerWalletTransactionRecordWrapper.getDescription());
        System.out.println("ASSET DISTRIBUTION Balance Type:" + balanceType);

        if (credit) {
            assetIssuerWalletBalance.credit(assetIssuerWalletTransactionRecordWrapper, balanceType);
        } else {
            assetIssuerWalletBalance.debit(assetIssuerWalletTransactionRecordWrapper, balanceType);
        }
    }

    public AssetIssuerWallet getIssuerWallet() throws CantLoadWalletException {
        return this.assetIssuerWalletManager.loadAssetIssuerWallet(this.walletPublicKey);
    }

}
