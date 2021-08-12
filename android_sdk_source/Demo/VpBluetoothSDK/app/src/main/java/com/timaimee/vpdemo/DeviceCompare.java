package com.timaimee.vpdemo;

import com.inuker.bluetooth.library.search.SearchResult;

import java.util.Comparator;

/**
 * Created by timaimee on 2017/2/8.
 */
public class DeviceCompare implements Comparator<SearchResult> {

    @Override
    public int compare(SearchResult o1, SearchResult o2) {
        return o2.rssi - o1.rssi;
    }
}
