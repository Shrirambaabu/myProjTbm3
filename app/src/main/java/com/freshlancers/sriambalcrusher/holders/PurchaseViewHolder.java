package com.freshlancers.sriambalcrusher.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.model.PurchaseList;

/**
 * Created by Shriram on 16-Nov-17.
 */

public class PurchaseViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "PurchaseViewHolder";

    private TextView purchaseItemName, vendorName;

    public PurchaseViewHolder(View itemView) {
        super(itemView);
        purchaseItemName = (TextView) itemView.findViewById(R.id.purchase_amount_value);
        vendorName = (TextView) itemView.findViewById(R.id.purchase_name_value);
    }

    public void updateUI(PurchaseList purchaseList){

        vendorName.setText(purchaseList.getVendorName());
        purchaseItemName.setText(purchaseList.getPurchaseItem());
    }
}