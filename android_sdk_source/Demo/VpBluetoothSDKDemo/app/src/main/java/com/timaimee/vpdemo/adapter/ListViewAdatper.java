package com.timaimee.vpdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.timaimee.vpdemo.R;

import java.util.List;

/**
 * Created by timaimee on 2016/12/21.
 */
public class ListViewAdatper extends BaseAdapter {
    private List<SearchResult> mData;
    LayoutInflater mLayoutInflater;


    public ListViewAdatper(Context context, List<SearchResult> itemData) {
        this.mData = itemData;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {

        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextViewHold textViewHold;
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_main, null);
            textViewHold = new TextViewHold();
            textViewHold.mTView = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(textViewHold);
        } else {
            textViewHold = (TextViewHold) convertView.getTag();
        }

        textViewHold.mTView.setText(mData.get(i).getName() + "-" + mData.get(i).getAddress() + "-" + mData.get(i).rssi);
        return convertView;
    }


    static class TextViewHold {
        TextView mTView;
    }
}
