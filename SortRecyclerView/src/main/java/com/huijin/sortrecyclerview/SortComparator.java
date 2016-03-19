
package com.huijin.sortrecyclerview;

import java.util.Comparator;

/**
 * 分类比较器
 * Created by huijinh on 2016/1/22.
 */
public class SortComparator implements Comparator<SortModel> {
    private int type;
    private final int SORT_NAME = 1;
    private final int SORT_LAST_TIME = 2;
    private final int SORT_APK_SIZE = 3;
    private final int SORT_INSTALL_TIME = 4;

    public SortComparator(int type) {
        this.type = type;
    }

    public int compare(SortModel o1, SortModel o2) {
        int result = 0;
        switch (type) {
            case SORT_NAME:
                if (o1.getSortLetters().equals("@")
                        || o2.getSortLetters().equals("#")) {
                    result = -1;
                } else if (o1.getSortLetters().equals("#")
                        || o2.getSortLetters().equals("@")) {
                    result = 1;
                } else {
                    result = o1.getSortLetters().compareTo(o2.getSortLetters());
                }
                break;
            case SORT_APK_SIZE:
                if (o1.getApkSize() > o2.getApkSize()) {
                    result = -1;
                } else if (o1.getApkSize() < o2.getApkSize()) {
                    result = 1;
                } else {
                    result = o1.getSortLetters().compareTo(o2.getSortLetters());
                }
                break;
            case SORT_INSTALL_TIME:
                if (o1.getInstallTime() > o2.getInstallTime()) {
                    result = -1;
                } else if (o1.getInstallTime() < o2.getInstallTime()) {
                    result = 1;
                } else {
                    result = o1.getSortLetters().compareTo(o2.getSortLetters());
                }
                break;
            case SORT_LAST_TIME:
                if (o1.getLastTime() > o2.getLastTime()) {
                    result = -1;
                } else if (o1.getLastTime() < o2.getLastTime()) {
                    result = 1;
                } else {
                    result = o1.getSortLetters().compareTo(o2.getSortLetters());
                }
                break;
        }
        return result;
    }
}
