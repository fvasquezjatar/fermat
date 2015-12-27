package com.bitdubai.fermat_android_api.engine;

import android.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;

/**
 * Created by Matias Furszyfer on 2015.09.01..
 */

public interface PaintActivityFeatures {

    public void paintComboBoxInActionBar(ArrayAdapter adapter, ActionBar.OnNavigationListener listener);

    public android.support.v7.widget.Toolbar getToolbar();

    public RelativeLayout getToolbarHeader();

    public void invalidate();

    public void addCollapseAnimation(ElementsWithAnimation elementsWithAnimation);

    // Esto no deberia estar acá
    public void addDesktopCallBack(DesktopHolderClickCallback desktopHolderClickCallback);

}
