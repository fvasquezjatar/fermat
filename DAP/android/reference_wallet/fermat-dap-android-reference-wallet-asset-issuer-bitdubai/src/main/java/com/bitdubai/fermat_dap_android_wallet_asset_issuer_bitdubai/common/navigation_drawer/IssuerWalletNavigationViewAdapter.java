package com.bitdubai.fermat_dap_android_wallet_asset_issuer_bitdubai.common.navigation_drawer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem;
import com.bitdubai.fermat_dap_android_wallet_asset_issuer_bitdubai.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by frank on 12/9/15.
 */
public class IssuerWalletNavigationViewAdapter extends FermatAdapter<MenuItem, IssuerWalletNavigationItemMenuViewHolder> {

    Typeface tf;

    protected IssuerWalletNavigationViewAdapter(Context context) {
        super(context);
    }

    public IssuerWalletNavigationViewAdapter(Context context, List<MenuItem> dataSet) {
        super(context, dataSet);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    protected IssuerWalletNavigationItemMenuViewHolder createHolder(View itemView, int type) {
        return new IssuerWalletNavigationItemMenuViewHolder(itemView);
    }

    @Override
    protected int getCardViewResource() {
        return R.layout.dap_navigation_drawer_issuer_wallet_navigation_row;
    }

    @Override
    protected void bindHolder(IssuerWalletNavigationItemMenuViewHolder holder, MenuItem data, int position) {
        try {

            holder.getLabel().setText(data.getLabel());

            if (data.isSelected()) {

                holder.getRow_container().setBackgroundResource(R.color.black_overlay_2);

                switch (position) {
                    case 0:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_home_active);

                        Picasso.with(context).load(R.drawable.ic_nav_home_active).into(holder.getIcon());
                        break;
                    case 1:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_history_active);

                        Picasso.with(context).load(R.drawable.ic_nav_history_active).into(holder.getIcon());
                        break;
                    case 2:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_stadistics_active);

                        Picasso.with(context).load(R.drawable.ic_nav_stadistics_active).into(holder.getIcon());
                        break;
                    case 3:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_users_active);

                        Picasso.with(context).load(R.drawable.ic_nav_users_active).into(holder.getIcon());
                        break;
                }
            } else {
                switch (position) {
                    case 0:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_home_normal);

                        Picasso.with(context).load(R.drawable.ic_nav_home_normal).into(holder.getIcon());
                        break;
                    case 1:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_history_normal);

                        Picasso.with(context).load(R.drawable.ic_nav_history_normal).into(holder.getIcon());
                        break;
                    case 2:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_stadistics_normal);

                        Picasso.with(context).load(R.drawable.ic_nav_stadistics_normal).into(holder.getIcon());
                        break;
                    case 3:
//                        holder.getIcon().setImageResource(R.drawable.ic_nav_users_normal);

                        Picasso.with(context).load(R.drawable.ic_nav_users_normal).into(holder.getIcon());
                        break;
                }
            }

            if (position == 4) {
                holder.getNavigation_row_divider().setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
