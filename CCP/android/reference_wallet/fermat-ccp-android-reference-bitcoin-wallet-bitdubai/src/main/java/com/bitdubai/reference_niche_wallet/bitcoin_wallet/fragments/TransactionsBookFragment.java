package com.bitdubai.reference_niche_wallet.bitcoin_wallet.fragments;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.android_fermat_ccp_wallet_bitcoin.R;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.common.enums.TransactionType;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_resources.WalletResourcesProviderManager;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.exceptions.CantGetCryptoWalletException;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.exceptions.CantListTransactionsException;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.interfaces.CryptoWallet;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.interfaces.CryptoWalletManager;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.interfaces.CryptoWalletTransaction;
import com.bitdubai.fermat_api.layer.dmp_wallet_module.crypto_wallet.interfaces.CryptoWalletWalletContact;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.UnexpectedUIExceptionSeverity;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.Views.EntryItem;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.Views.Item;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.Views.SectionItem;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.enums.ShowMoneyType;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.WalletUtils;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.session.ReferenceWalletSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Created by Matias Furszyfer
 */
public class TransactionsBookFragment extends Fragment{

    private static final String ARG_POSITION = "position";


    String walletPublicKey = "reference_wallet";

    /**
     * DealsWithWalletModuleCryptoWallet Interface member variables.
     */
    private CryptoWalletManager cryptoWalletManager;
    private CryptoWallet cryptoWallet;

    /**
     * Deals with error manager
     */
    private ErrorManager errorManager;

    /**
     * Screen views
     */
    private View rootView;
    private ListView listViewTransactions;
    private SwipeRefreshLayout swipeRefreshLayout;


    /**
     * List transactions
     */
    private List<CryptoWalletTransaction> lstTransactions = new ArrayList<CryptoWalletTransaction>();


    /**
     * List of transactions pointers
     */
    //TODO: esto deberia ir en preference setting
    private int pointerOffset = 0;
    private int cantTransactions = 50;


    /**
     * Fragment Style
     */
    Typeface tf;

    /**
     * List of Items to show in the list
     */
    ArrayList<Item> items = new ArrayList<Item>();

    /**
     * Map of transactions ordered
     */

    Map<String,Set<CryptoWalletTransaction>> mapTransactionPerDate;

    /**
     * Wallet session
     */
    private ReferenceWalletSession walletSession;

    /**
     * Resources
     */
    WalletResourcesProviderManager walletResourcesProviderManager;


    int type=0;
    private TextView textView_transactions_type;

    private boolean isLoading;

    private EntryAdapter adapter;

    /**
     *
     * @param position
     * @param walletSession
     * @return
     */

    public static TransactionsBookFragment newInstance(int position,ReferenceWalletSession walletSession,WalletResourcesProviderManager walletResourcesProviderManager,int type) {
        TransactionsBookFragment f = new TransactionsBookFragment();
        f.setWalletSession(walletSession);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        f.setType(type);
        f.setWalletResourcesProviderManager(walletResourcesProviderManager);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        try {

            errorManager = walletSession.getErrorManager();
            tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CaviarDreams.ttf");

            cryptoWalletManager = walletSession.getCryptoWalletManager();
            cryptoWallet = cryptoWalletManager.getCryptoWallet();

            mapTransactionPerDate= new HashMap<String, Set<CryptoWalletTransaction>>();

        } catch (CantGetCryptoWalletException e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
            Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
                errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
                Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.wallets_bitcoin_fragment_transactions, container, false);
            // Get ListView object from xml
            listViewTransactions = (ListView) rootView.findViewById(R.id.transactionlist);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);



            // Create the adapter to convert the array to views


            lstTransactions=cryptoWallet.getTransactions(BalanceType.AVAILABLE, walletPublicKey, cantTransactions,pointerOffset);


            //BalanceType balanceType =BalanceType.getByCode(walletSession.getBalanceTypeSelected());

            if(type==0){
                lstTransactions=showTransactionListSelected(lstTransactions,BalanceType.BOOK);
            }else if(type==1){
                lstTransactions=showTransactionListSelected(lstTransactions,BalanceType.AVAILABLE);

            }


            // Set the emptyView to the ListView
            TextView textViewEmptyListView = (TextView) rootView.findViewById(R.id.emptyElement);

            if(lstTransactions.isEmpty()){
                textViewEmptyListView.setTypeface(tf);
                listViewTransactions.setEmptyView(textViewEmptyListView);
            }

            /**
             * Setting swipe Refresh
             */
            swipeRefreshLayout.setColorSchemeColors(R.color.green);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshTransactionsContent();
                }
            });



            listViewTransactions.setOnScrollListener(new AbsListView.OnScrollListener() {
                public int currentScrollState;
                public int currentVisibleItemCount;
                public int currentFirstVisibleItem;
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    //view.
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    //showMessage("holas");
//                    Toast.makeText(getActivity(),"visible item count:"+visibleItemCount+"\n"
//                            +"first vible item:"+firstVisibleItem+"\n"
//                            +"total item count:"+totalItemCount,Toast.LENGTH_SHORT).show();
//
//                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    this.currentFirstVisibleItem = firstVisibleItem;
                    this.currentVisibleItemCount = visibleItemCount;
                }

                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    this.currentScrollState = scrollState;
                    this.isScrollCompleted();
                }

                private void isScrollCompleted() {
                    if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
                        /*** In this way I detect if there's been a scroll which has completed ***/
                        /*** do the work for load more date! ***/
                        if (!isLoading) {
                            isLoading = true;
                            //loadMoreDAta();
                            loadNewTransactions();
                        }
                    }
                }
            });

            /**
             * Load transactions
             */
            loadTransactionMap(lstTransactions);


            convertToUIList();

            /**
             *
             */
            adapter = new EntryAdapter(getActivity(), items);
            listViewTransactions.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (CantListTransactionsException e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
            Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
            Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();

        }


//        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_test);
//
//        // ESTO ES SOLO TESTEO
//
//        byte[] image=null;
//        try {
//
//            image=walletResourcesProviderManager.getImageResource("personIcon", skinId);
//
//        } catch (CantGetResourcesException e) {
//            e.printStackTrace();
//        }

        //Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(image, 0, image.length));
        //imageView.setImageDrawable(drawable);

        return rootView;
    }

    /**
     *  Order transactions in a map
     */
    private void loadTransactionMap(List<CryptoWalletTransaction> lstTransactions){
        Set<CryptoWalletTransaction> cryptoWalletTransactionSet = new HashSet<CryptoWalletTransaction>();
        for(CryptoWalletTransaction transaction:lstTransactions){
            Date date = new Date(transaction.getBitcoinWalletTransaction().getTimestamp());
            if(!mapTransactionPerDate.containsKey(convertDateToString(date))){
                cryptoWalletTransactionSet = new HashSet<CryptoWalletTransaction>();
                cryptoWalletTransactionSet.add(transaction);
                mapTransactionPerDate.put(convertDateToString(date), cryptoWalletTransactionSet);
            }else{
                mapTransactionPerDate.get(convertDateToString(date)).add(transaction);
            }
        }
    }

    private String convertDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return sdf.format(date);
    }
    private List convertToUIList(){
        for (String date: mapTransactionPerDate.keySet()){
            //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            items.add(new SectionItem(date));
            for(CryptoWalletTransaction cryptoWalletTransaction: mapTransactionPerDate.get(date)){
                items.add(new EntryItem(cryptoWalletTransaction));
            }
        }
        return items;
    }

    /**
     *  Obtain only the transactions that the user want
     *
     * @param lstTransactions
     * @param balanceType
     * @return
     */
    private List<CryptoWalletTransaction> showTransactionListSelected(List<CryptoWalletTransaction> lstTransactions, BalanceType balanceType) {
        List<CryptoWalletTransaction> lstToShow = new ArrayList<CryptoWalletTransaction>();
        for (CryptoWalletTransaction t : lstTransactions) {
            if (t.getBitcoinWalletTransaction().getBalanceType()==(balanceType)) {
                lstToShow.add(t);
            }
        }
        return lstToShow;
    }

    /**
     * Refresh
     */
    private void refreshTransactionsContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNewTransactions();
                //transactionArrayAdapter = new TransactionArrayAdapter(getActivity(),lstTransactions);
                //listViewTransactions.setAdapter(transactionArrayAdapter);
                swipeRefreshLayout.bringToFront();
                swipeRefreshLayout.invalidate();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);

    }
    private void loadTransaction(List<CryptoWalletTransaction> lstTransactions){
        if (lstTransactions.isEmpty()){
            loadTransactionMap(lstTransactions);
            convertToUIList();
            adapter.notifyDataSetChanged();
        }
    }
    /**
     *  Update transaction list
     */
    private void loadNewTransactions(){
        try {
            if (lstTransactions.isEmpty()){

                List<CryptoWalletTransaction> lst = cryptoWallet.getTransactions(BalanceType.AVAILABLE, walletPublicKey, cantTransactions, pointerOffset);

                for (CryptoWalletTransaction transaction : lst) {
                    lstTransactions.add(0, transaction);
                }
            }
            else{

                List<CryptoWalletTransaction> lst = cryptoWallet.getTransactions(BalanceType.AVAILABLE, walletPublicKey, cantTransactions, pointerOffset);
                for (CryptoWalletTransaction transaction : lst) {
                    lstTransactions.add(0, transaction);


                }
                pointerOffset = lstTransactions.size();

                if(type==0){
                    lstTransactions=showTransactionListSelected(lstTransactions, BalanceType.BOOK);
                }else if(type==1){
                    lstTransactions=showTransactionListSelected(lstTransactions, BalanceType.AVAILABLE);
                }

                adapter.notifyDataSetChanged();

            }
        }

        catch (CantListTransactionsException e)
        {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
            Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        }
        catch(Exception ex)
        {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(ex));
            Toast.makeText(getActivity().getApplicationContext(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        }


    }

    public void setWalletResourcesProviderManager(WalletResourcesProviderManager walletResourcesProviderManager) {
        this.walletResourcesProviderManager = walletResourcesProviderManager;
    }

    public void setType(int type) {
        this.type = type;
    }


    /**
     * List Item adapter
     */

    public class EntryAdapter extends ArrayAdapter<Item> {

        private Context context;
        private ArrayList<Item> items;
        private LayoutInflater vi;

        public EntryAdapter(Context context,ArrayList<Item> items) {
            super(context,0, items);
            this.context = context;
            this.items = items;
            vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            final Item item = items.get(position);
            if (item != null) {
                if(item.isSection()){
                    SectionItem si = (SectionItem)item;
                    v = vi.inflate(R.layout.list_item_section, null);

                    v.setOnClickListener(null);
                    v.setOnLongClickListener(null);
                    v.setLongClickable(false);

                    final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                    sectionView.setText(si.getTitle());

                }else{
                    EntryItem entryItem = (EntryItem)item;
                    /**
                     * Getting Views
                     */
                    v = vi.inflate(R.layout.wallets_bitcoin_fragment_transactions_list_items2,null);
                    final TextView textView_contact_name =(TextView)v.findViewById(R.id.textView_contact_name);
                    final TextView textView_type =(TextView)v.findViewById(R.id.textView_type);
                    final TextView textView_amount =(TextView)v.findViewById(R.id.textView_amount);
                    final TextView textView_time =(TextView)v.findViewById(R.id.textView_time);
                    final ImageView imageView_contact =(ImageView)v.findViewById(R.id.imageView_contact);

                    /**
                     * Setting values and validations
                     */
                    if (textView_contact_name != null) {
                        String actorName = getActorNameProvisorio(entryItem.cryptoWalletTransaction);
                        if(actorName!=null)
                        textView_contact_name.setText(actorName);
                        else textView_contact_name.setText(R.string.nullActorName);
                    }

                    if(textView_amount != null)
                        if(type==0){
                            textView_amount.setText(WalletUtils.formatBalanceString(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getRunningBookBalance(),ShowMoneyType.BITCOIN.getCode())+"_"+WalletUtils.formatBalanceString(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getAmount(),ShowMoneyType.BITCOIN.getCode()));
                        }else if( type==1){
                            textView_amount.setText(WalletUtils.formatBalanceString(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getRunningAvailableBalance(),ShowMoneyType.BITCOIN.getCode())+"_"+WalletUtils.formatBalanceString(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getAmount(),ShowMoneyType.BITCOIN.getCode()));
                        }
                    if(textView_time!=null){
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        textView_time.setText(sdf.format(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getTimestamp()));
                    }
                    if(textView_type!=null){
                        if(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getTransactionType()==TransactionType.CREDIT){
                            textView_type.setText(R.string.credit);
                        }else if(entryItem.cryptoWalletTransaction.getBitcoinWalletTransaction().getTransactionType()==TransactionType.DEBIT){
                            textView_type.setText(R.string.debit);
                        }
                    }

                }
            }
            return v;
        }

    }

    private String getActorNameProvisorio(CryptoWalletTransaction cryptoWalletTransaction) {
        if (cryptoWalletTransaction.getContactId() != null) {
            try {
                CryptoWalletWalletContact walletContactRecord = cryptoWallet.findWalletContactById(cryptoWalletTransaction.getContactId());
                return walletContactRecord.getActorName();
            } catch (Exception e) {
                System.out.println("esta es para vos mati.");
            }
        } else if (cryptoWalletTransaction.getInvolvedActor() != null) {
            return cryptoWalletTransaction.getInvolvedActor().getName();
        }
        return "Unknow";
    }

    /**
     *  Set wallet session inside the fragment when is created
     *
     * @param walletSession
     */
    public void setWalletSession(ReferenceWalletSession walletSession) {
        this.walletSession = walletSession;
    }

}
