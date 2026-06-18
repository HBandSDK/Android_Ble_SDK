package com.timaimee.vpdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.timaimee.vpdemo.R;

import java.text.MessageFormat;
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
        holder.mBleRssi.setText(MessageFormat.format("{0}-{1}-{2}", itemData.get(position).getName(), itemData.get(position).getAddress(), itemData.get(position).rssi));
        holder.itemView.setOnClickListener(view -> mBleCallback.OnRecycleViewClick(position));
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
            mBleRssi = view.findViewById(R.id.tv);
//            view.setOnClickListener(v -> {
//                mBleCallback.OnRecycleViewClick(getPosition());
//                Log.d("NormalTextViewHolder", "onClick--> position = " + getPosition());
//            });
        }
    }
}