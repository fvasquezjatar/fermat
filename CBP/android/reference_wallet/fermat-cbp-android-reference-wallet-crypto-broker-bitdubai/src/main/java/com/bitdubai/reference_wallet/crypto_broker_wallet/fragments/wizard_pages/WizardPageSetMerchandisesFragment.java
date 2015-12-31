package com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.wizard_pages;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatCheckBox;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankAccountNumber;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletAssociatedSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletSettingSpread;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.exceptions.CantListWalletsException;
import com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.interfaces.InstalledWallet;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.adapters.WalletsAdapter;
import com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.common.SimpleListDialogFragment;
import com.bitdubai.reference_wallet.crypto_broker_wallet.session.CryptoBrokerWalletSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nelson on 22/12/15.
 */
public class WizardPageSetMerchandisesFragment extends AbstractFermatFragment implements WalletsAdapter.OnDeleteButtonClickedListener {

    // Constants
    private static final String TAG = "WizardPageSetMerchand";

    private int spreadValue;
    private boolean automaticRestock;
    private List<InstalledWallet> stockWallets;
    private Map<String, FiatCurrency> bankCurrencies;
    private Map<String, String> bankAccounts;

    // Fermat Managers
    private CryptoBrokerWalletManager walletManager;
    private ErrorManager errorManager;
    private WalletsAdapter adapter;
    private RecyclerView recyclerView;
    private FermatTextView emptyView;


    public static WizardPageSetMerchandisesFragment newInstance() {
        return new WizardPageSetMerchandisesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spreadValue = 0;
        automaticRestock = false;
        stockWallets = new ArrayList<>();
        bankCurrencies = new HashMap<>();
        bankAccounts = new HashMap<>();

        try {
            CryptoBrokerWalletModuleManager moduleManager = ((CryptoBrokerWalletSession) appSession).getModuleManager();
            walletManager = moduleManager.getCryptoBrokerWallet(appSession.getAppPublicKey());
            errorManager = appSession.getErrorManager();

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, ex);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View layout = inflater.inflate(R.layout.cbw_wizard_step_set_merchandises, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.cbw_selected_stock_wallets_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapter = new WalletsAdapter(getActivity(), stockWallets);
        adapter.setDeleteButtonListener(this);
        recyclerView.setAdapter(adapter);

        emptyView = (FermatTextView) layout.findViewById(R.id.cbw_selected_stock_wallets_empty_view);

        final FermatTextView spreadTextView = (FermatTextView) layout.findViewById(R.id.cbw_spread_value_text);
        spreadTextView.setText(String.format("%1$s %%", spreadValue));

        final View cryptoButton = layout.findViewById(R.id.cbw_select_crypto_wallets);
        cryptoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWalletsDialog(Platforms.CRYPTO_CURRENCY_PLATFORM);
            }
        });

        final View bankButton = layout.findViewById(R.id.cbw_select_bank_wallets);
        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWalletsDialog(Platforms.BANKING_PLATFORM);
            }
        });

        final View cashButton = layout.findViewById(R.id.cbw_select_cash_wallets);
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWalletsDialog(Platforms.CASH_PLATFORM);
            }
        });

        final FermatCheckBox automaticRestockCheckBox = (FermatCheckBox) layout.findViewById(R.id.cbw_automatic_restock_check_box);
        automaticRestockCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                automaticRestock = automaticRestockCheckBox.isChecked();
            }
        });

        final SeekBar spreadSeekBar = (SeekBar) layout.findViewById(R.id.cbw_spread_value_seek_bar);
        spreadSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                spreadValue = progress;
                spreadTextView.setText(String.format("%1$s %%", spreadValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        final View nextStepButton = layout.findViewById(R.id.cbw_next_step_button);
        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettingAndGoNextStep();
            }
        });

        return layout;
    }

    private void showWalletsDialog(final Platforms platform) {
        try {
            List<InstalledWallet> installedWallets = walletManager.getInstallWallets();
            List<InstalledWallet> filteredList = new ArrayList<>();

            for (InstalledWallet wallet : installedWallets) {
                if (wallet.getPlatform().equals(platform))
                    filteredList.add(wallet);
            }

            final SimpleListDialogFragment<InstalledWallet> dialogFragment = new SimpleListDialogFragment<>();
            dialogFragment.configure("Select a Wallet", filteredList);
            dialogFragment.setListener(new SimpleListDialogFragment.ItemSelectedListener<InstalledWallet>() {
                @Override
                public void onItemSelected(InstalledWallet selectedItem) {
                    if (!platform.equals(Platforms.BANKING_PLATFORM)) {

                        if (!containWallet(selectedItem)) {
                            stockWallets.add(selectedItem);
                            adapter.changeDataSet(stockWallets);
                            showOrHideNoSelectedWalletsView();
                        }

                    } else {
                        showBankAccountsDialog(selectedItem);
                    }
                }
            });

            dialogFragment.show(getFragmentManager(), "WalletsDialog");

        } catch (CantListWalletsException ex) {
            Toast.makeText(WizardPageSetMerchandisesFragment.this.getActivity(), "Oops a error occurred...", Toast.LENGTH_SHORT).show();

            Log.e(TAG, ex.getMessage(), ex);
            if (errorManager != null) {
                errorManager.reportUnexpectedWalletException(
                        Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT,
                        ex);
            }
        }
    }

    private void showBankAccountsDialog(final InstalledWallet selectedWallet) {
        try {
            List<BankAccountNumber> accounts = walletManager.getAccounts(selectedWallet.getWalletPublicKey());

            SimpleListDialogFragment<BankAccountNumber> accountsDialog = new SimpleListDialogFragment<>();
            accountsDialog.configure("Select an Account", accounts);
            accountsDialog.setListener(new SimpleListDialogFragment.ItemSelectedListener<BankAccountNumber>() {
                @Override
                public void onItemSelected(BankAccountNumber selectedAccount) {

                    FiatCurrency currency = selectedAccount.getCurrencyType();
                    String account = selectedAccount.getAccount();

                    bankCurrencies.put(selectedWallet.getWalletPublicKey(), currency);
                    bankAccounts.put(selectedWallet.getWalletPublicKey(), account);

                    if (!containWallet(selectedWallet)) {
                        stockWallets.add(selectedWallet);
                        adapter.changeDataSet(stockWallets);
                        showOrHideNoSelectedWalletsView();
                    }
                }
            });

            accountsDialog.show(getFragmentManager(), "accountsDialog");

        } catch (FermatException ex) {
            Toast.makeText(WizardPageSetMerchandisesFragment.this.getActivity(), "Oops a error occurred...", Toast.LENGTH_SHORT).show();

            Log.e(TAG, ex.getMessage(), ex);
            if (errorManager != null) {
                errorManager.reportUnexpectedWalletException(
                        Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT,
                        ex);
            }
        }
    }

    private void saveSettingAndGoNextStep() {

        if (stockWallets.isEmpty()) {
            Toast.makeText(getActivity(), R.string.select_stock_wallets_warning_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            CryptoBrokerWalletSettingSpread walletSetting = walletManager.newEmptyCryptoBrokerWalletSetting();
            walletSetting.setId(null);
            walletSetting.setBrokerPublicKey(appSession.getAppPublicKey());
            walletSetting.setSpread((float) spreadValue / 100.0f);
            walletSetting.setRestockAutomatic(automaticRestock);
            walletManager.saveWalletSetting(walletSetting, appSession.getAppPublicKey());


            for (InstalledWallet wallet : stockWallets) {
                String walletPublicKey = wallet.getWalletPublicKey();
                Platforms platform = wallet.getPlatform();

                CryptoBrokerWalletAssociatedSetting associatedSetting = walletManager.newEmptyCryptoBrokerWalletAssociatedSetting();
                associatedSetting.setBrokerPublicKey(appSession.getAppPublicKey());
                associatedSetting.setId(UUID.randomUUID());
                associatedSetting.setWalletPublicKey(walletPublicKey);
                associatedSetting.setPlatform(platform);

                if (platform.equals(Platforms.BANKING_PLATFORM)) {
                    associatedSetting.setCurrencyType(CurrencyType.BANK_MONEY);
                    associatedSetting.setMerchandise(bankCurrencies.get(walletPublicKey));
                    associatedSetting.setBankAccount(bankAccounts.get(walletPublicKey));

                } else if (platform.equals(Platforms.CRYPTO_CURRENCY_PLATFORM)) {
                    associatedSetting.setCurrencyType(CurrencyType.CRYPTO_MONEY);
                    associatedSetting.setMerchandise(wallet.getCryptoCurrency());

                } else {
                    FiatCurrency cashCurrency = walletManager.getCashCurrency(walletPublicKey);
                    associatedSetting.setMerchandise(cashCurrency);
                    associatedSetting.setCurrencyType(CurrencyType.CASH_ON_HAND_MONEY);
                }

                walletManager.saveWalletSettingAssociated(associatedSetting, appSession.getAppPublicKey());
            }

        } catch (FermatException ex) {
            Toast.makeText(WizardPageSetMerchandisesFragment.this.getActivity(), "Oops a error occurred...", Toast.LENGTH_SHORT).show();

            Log.e(TAG, ex.getMessage(), ex);
            if (errorManager != null) {
                errorManager.reportUnexpectedWalletException(
                        Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT,
                        ex);
            }
        }

        //TODO: agregar esta instruccion en el try/catch cuando funcione el saveSettingSpread
        changeActivity(Activities.CBP_CRYPTO_BROKER_WALLET_SET_EARNINGS, appSession.getAppPublicKey());
    }

    @Override
    public void deleteButtonClicked(InstalledWallet data, int position) {
        stockWallets.remove(position);
        adapter.changeDataSet(stockWallets);

        showOrHideNoSelectedWalletsView();
    }

    private void showOrHideNoSelectedWalletsView() {
        if (stockWallets.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private boolean containWallet(InstalledWallet selectedWallet) {
        if (stockWallets.isEmpty())
            return false;

        for (InstalledWallet wallet : stockWallets) {
            String walletPublicKey = wallet.getWalletPublicKey();
            String selectedWalletPublicKey = selectedWallet.getWalletPublicKey();

            if (walletPublicKey.equals(selectedWalletPublicKey))
                return true;
        }

        return false;
    }
}
