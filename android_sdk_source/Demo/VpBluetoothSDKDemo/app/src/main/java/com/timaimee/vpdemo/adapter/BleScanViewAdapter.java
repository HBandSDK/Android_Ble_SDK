package com.timaimee.vpdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.timaimee.vpdemo.R;

import java.util.List;


/**
 * Created by timaimee on 2016/7/25.
 */
public class BleScanViewAdapter extends RecyclerView.Adapter<BleScanViewAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    List<SearchResult> itemData;
    OnRecycleViewClickCallback mBleCallback;

    public BleScanViewAdapter(Context context, List<SearchResult> data) {
        this.itemData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.mBleRssi.setText(itemData.get(position).getName() + "-" + itemData.get(position).getAddress() + "-" + itemData.get(position).rssi);

    }


    @Override
    public int getItemCount() {
        return itemData == null ? 0 : itemData.size();
    }


    public void setBleItemOnclick(OnRecycleViewClickCallback bleCallback) {
        this.mBleCallback = bleCallback;
    }


    public class NormalTextViewHolder extends RecyclerView.ViewHolder {

        TextView mBleRssi;


        NormalTextViewHolder(View view) {
            super(view);
            mBleRssi = (TextView) view.findViewById(R.id.tv);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBleCallback.OnRecycleViewClick(getPosition());
                    Log.d("NormalTextViewHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}