package com.timaimee.vpdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timaimee.vpdemo.R;

import java.util.List;
import java.util.Map;

/**
 * Created by timaimee on 2016/12/21.
 */
public class GridAdatper extends BaseAdapter {
    private List<Map<String, String>> mGridData;
    LayoutInflater mLayoutInflater;


    public GridAdatper(Context context, List<Map<String, String>> itemData) {
        this.mGridData = itemData;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mGridData.size();
    }

    @Override
    public Object getItem(int i) {

        return mGridData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        GridHold mGridHold;
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_grid, null);
            mGridHold = new GridHold();
            mGridHold.mButton = (TextView) convertView.findViewById(R.id.gridbutton);
            convertView.setTag(mGridHold);
        } else {
            mGridHold = (GridHold) convertView.getTag();
        }
        Map<String, String> map = mGridData.get(i);
        mGridHold.mButton.setText(map.get("str"));

        return convertView;
    }


    static class GridHold {
        TextView mButton;
    }
}
