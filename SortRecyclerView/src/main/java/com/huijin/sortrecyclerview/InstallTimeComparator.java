package com.huijin.sortrecyclerview;

import java.util.Comparator;

/**
 * Created by huijinh on 2016/1/22.
 */
public class InstallTimeComparator implements Comparator<SortModel> {
    public int compare(SortModel o1, SortModel o2) {
        if (o1.getInstallTime()>o2.getInstallTime()) {
            return -1;
        } else if (o1.getInstallTime()<o2.getInstallTime()) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}