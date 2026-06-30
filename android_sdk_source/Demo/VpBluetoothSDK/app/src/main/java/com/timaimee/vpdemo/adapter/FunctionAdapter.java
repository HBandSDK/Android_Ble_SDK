package com.timaimee.vpdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.Oprate;


public class FunctionAdapter extends BaseAdapter {
    private final String[] functions;
    LayoutInflater mLayoutInflater;

    public FunctionAdapter(Context context, String[] functions) {
        this.functions = functions;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return functions.length;
    }

    @Override
    public Object getItem(int i) {
        return functions[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        GridHold mGridHold;
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_function, null);
            mGridHold = new GridHold();
            mGridHold.tvFunctionName = (TextView) convertView.findViewById(R.id.tvFunctionName);
            convertView.setTag(mGridHold);
        } else {
            mGridHold = (GridHold) convertView.getTag();
        }
        String functionName = functions[i];
        mGridHold.tvFunctionName.setText(functionName);
//        if (i % 4 == 0) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#FF7032"));
//        } else if (i % 4 == 1) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#3F51B5"));
//        } else if (i % 4 == 2) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#2695f0"));
//        } else if (i % 4 == 3) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#852BFE"));
//        }
//        if (mGridHold.tvFunctionName.getText().toString().equals(Oprate.SHARE_LOG)) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.RED);
//        }
//        if (mGridHold.tvFunctionName.getText().toString().equals(Oprate.READ_HEALTH_ORIGINAL)) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.RED);
//        }
//        if (mGridHold.tvFunctionName.getText().toString().equals(Oprate.JL_DEVICE)) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#e529f0"));
//        }
//
//        if (mGridHold.tvFunctionName.getText().toString().equals(Oprate.ALARM_NEW_)) {
//            mGridHold.tvFunctionName.setBackgroundColor(Color.parseColor("#f5B910"));
//        }
        return convertView;
    }


    static class GridHold {
        TextView tvFunctionName;
    }
}
