package com.bitdubai.desktop.wallet_manager.preference_settings;

import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantGetDefaultLanguageException;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantGetDefaultSkinException;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantLoadSubAppSettings;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantSaveSubAppSettings;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantSetDefaultLanguageException;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.exceptions.CantSetDefaultSkinException;
import com.bitdubai.fermat_pip_api.all_definition.sub_app_module.settings.interfaces.SubAppSettings;

import java.util.UUID;

/**
 * Created by mati on 2015.08.24..
 */
public class WalletManagerPreferenceSettings implements SubAppSettings {
    /**
     * This method let us know the default language of a wallet
     *
     * @return the identifier of the default language of the wallet
     * @throws CantGetDefaultLanguageException
     */
    @Override
    public UUID getDefaultLanguage() throws CantGetDefaultLanguageException {
        return null;
    }

    /**
     * This method let us know the default skin of a wallet
     *
     * @return the identifier of the default skin of the wallet
     * @throws CantGetDefaultSkinException
     */
    @Override
    public UUID getDefaultSkin() throws CantGetDefaultSkinException {
        return null;
    }

    /**
     * This method let us set the default language for a wallet
     *
     * @param languageId the identifier of the language to set as default
     * @throws CantSetDefaultLanguageException
     */
    @Override
    public void setDefaultLanguage(UUID languageId) throws CantSetDefaultLanguageException {

    }

    /**
     * This method let us set the default skin for a wallet
     *
     * @param skinId the identifier of the skin to set as default
     * @throws CantSetDefaultSkinException
     */
    @Override
    public void setDefaultSkin(UUID skinId) throws CantSetDefaultSkinException {

    }

    /**
     * This method let us set the preference settings for a wallet
     *
     * @param walletPreferenceSettings
     * @param walletPublicKey
     * @throws CantSetDefaultSkinException
     */
    @Override
    public void setPreferenceSettings(String walletPreferenceSettings, String walletPublicKey) throws CantSaveSubAppSettings {

    }

    /**
     * This method let us get the preference settings for a wallet
     *
     * @param walletPublicKey
     * @return preference settings of a wallet
     * @throws CantGetDefaultSkinException
     */
    @Override
    public String getPreferenceSettings(String walletPublicKey) throws CantLoadSubAppSettings {
        return null;
    }
}
