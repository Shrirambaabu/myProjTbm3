package com.freshlancers.sriambalcrusher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.model.PurchaseList;
import com.freshlancers.sriambalcrusher.holders.PurchaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shriram on 16-Nov-17.
 */

public class RecyclerAdapterPurchaseList extends RecyclerView.Adapter<PurchaseViewHolder> {

    private ArrayList<PurchaseList> purchaseLists;

    public RecyclerAdapterPurchaseList(ArrayList<PurchaseList> purchaseLists) {
        this.purchaseLists = purchaseLists;
    }

    @Override
    public PurchaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_purchase_list, parent, false);
        return new PurchaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PurchaseViewHolder holder, int position) {

        PurchaseList purchaseList = purchaseLists.get(position);

        holder.updateUI(purchaseList);



    }

    @Override
    public int getItemCount() {
        return purchaseLists.size();
    }


}
