package com.huijin.sortrecyclerview;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.eowise.recyclerview.stickyheaders.OnHeaderClickListener;
import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnHeaderClickListener {
    private static final String TAG = "testfastscroll";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SortModel> myDataset;
    private StickyHeadersAdapter mStickyHeadersAdapter;
    private StickyHeadersItemDecoration mStickyHeadersItemDecoration;
    private FastScrollRecyclerViewItemDecoration decoration;
    private final int SORT_NAME = 1;
    private final int SORT_LAST_TIME = 2;
    private final int SORT_APK_SIZE = 3;
    private final int SORT_INSTALL_TIME = 4;
    private int type;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        type = SORT_NAME;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_sort);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i + 1;
                initSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // specify an adapter (see also next example)
//        myDataset = new ArrayList<String>();
//        for(int i=0; i<26; i++) {
//            myDataset.add(Character.toString((char)(65 + i)) + " Row item");
//        }
//        HashMap<String, Integer> mapIndex = calculateIndexesForName(myDataset);
//        sourceDateList = filledData(myDataset);

        initSort();
    }

    private void initSort() {
        initDatas();
        switch (type) {
            case SORT_NAME:
                // 根据a-z进行排序源数据
                Collections.sort(myDataset, new PinyinComparator());
                break;
            case SORT_APK_SIZE:
                Collections.sort(myDataset, new ApkSizeComparator());
                break;
            case SORT_INSTALL_TIME:
                Collections.sort(myDataset, new InstallTimeComparator());
                break;
            case SORT_LAST_TIME:
                Collections.sort(myDataset, new LastTimeComparator());
                break;
        }
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        setupHeaders();
        if (decoration != null) {
            mRecyclerView.removeItemDecoration(decoration);
        }
        if (type == SORT_NAME) {
            decoration = new FastScrollRecyclerViewItemDecoration(this);
            mRecyclerView.addItemDecoration(decoration);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void initDatas() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        if (myDataset != null && myDataset.size() > 0) {
            myDataset.clear();
        } else {
            myDataset = new ArrayList<SortModel>();
        }
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                SortModel model = new SortModel();
                String name = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                model.setName(name);
                model.setInstallTime(packageInfo.firstInstallTime);
                String dir = packageInfo.applicationInfo.publicSourceDir;
                model.setApkSize(new File(dir).length());
                if (type == SORT_NAME) {
                    //实例化汉字转拼音类
                    characterParser = CharacterParser.getInstance();
                    //汉字转换成拼音
                    String pinyin = characterParser.getSelling(name);
                    String sortString = pinyin.substring(0, 1).toUpperCase();

                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        model.setSortLetters(sortString);
                    } else {
                        model.setSortLetters("#");
                    }
                    model.setSortTitle(model.getSortLetters());
                } else if (type == SORT_APK_SIZE) {
                    String sortIdFromSize = getSortIdFromSize(model.getApkSize());
                    if (sortIdFromSize != null) {
                        String[] split = sortIdFromSize.split(",");
                        model.setSortLetters(split[0]);
                        model.setSortTitle(split[1]);
                    }
                } else if (type == SORT_INSTALL_TIME) {
                    String sortIdFromInstall = getSortIdFromInstallTime(model.getInstallTime());
                    if (sortIdFromInstall != null) {
                        String[] split = sortIdFromInstall.split(",");
                        model.setSortLetters(split[0]);
                        model.setSortTitle(split[1]);
                    }
                } else if (type == SORT_LAST_TIME) {
                    List<UsageStats> statsList = getUsageStatsList();
                    if (statsList == null || statsList.size() == 0) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                        break;
                    } else {
                        for (UsageStats u : statsList) {
                            if (packageInfo.packageName.equals(u.getPackageName())) {
                                model.setLastTime(u.getLastTimeUsed());
                                break;
                            }
                        }
                        String sortFromLastTime = getSortFromLastTime(model.getLastTime());
                        if (sortFromLastTime != null) {
                            String[] split = sortFromLastTime.split(",");
                            model.setSortLetters(split[0]);
                            model.setSortTitle(split[1]);
                        }
                    }
                }

                myDataset.add(model);

//                Log.d(TAG, "initDatas: myDataset +i = "+i+"name = "+packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            }
        }
    }

    private String getSortIdFromSize(long fileSize) {
        long m10 = 1024 * 1024 * 10;
        long m20 = m10 * 2;
        long m50 = m10 * 5;
        long m100 = m50 * 2;
        long m500 = m100 * 5;
        if (fileSize > m500) {
            return "A,500M以上";
        } else if (fileSize > m100 && fileSize <= m500) {
            return "B,100M-500M";
        } else if (fileSize > m50 && fileSize <= m100) {
            return "C,50M-100M";
        } else if (fileSize > m20 && fileSize <= m50) {
            return "D,20M-50M";
        } else if (fileSize > m10 && fileSize <= m20) {
            return "E,10M-20M";
        } else if (fileSize > 0 && fileSize < m10) {
            return "F,0-10M";
        }
        return null;
    }

    private String getSortIdFromInstallTime(long installTime) {
        long diff = System.currentTimeMillis() - installTime;
        long day = 1000 * 60 * 60 * 24;
        long week = day * 7;
        long mounth = week * 4;
        long mounth3 = mounth * 3;
        if (diff > 0 && diff <= day) {
            return "A,今天";
        } else if (diff > day && diff <= week) {
            return "B,一周内";
        } else if (diff > week && diff <= mounth) {
            return "C,一个月内";
        } else if (diff > mounth) {
            return "D,三个月内";
        }
        return null;
    }

    private String getSortFromLastTime(long lastTime) {
        long week = 1000 * 60 * 60 * 24 * 7;
        long month = 1000 * 60 * 60 * 24 * 30;
        long diff = System.currentTimeMillis() - lastTime;
        if (diff > 0 && diff < week) {
            return "A,常用应用";
        } else if (diff >= week && week < month) {
            return "B,7天未使用";
        } else if (diff >= month) {
            return "C,30天未使用";
        }
        return null;
    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<String> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i);
            String index = name.substring(0, 1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    private List<SortModel> filledData(ArrayList<String> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i));
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupHeaders() {
        mStickyHeadersAdapter = new HeadersAdapter();
        ((HeadersAdapter) mStickyHeadersAdapter).replaceItems(myDataset);

        if (mStickyHeadersItemDecoration != null) {
            mRecyclerView.removeItemDecoration(mStickyHeadersItemDecoration);
        }
        mStickyHeadersItemDecoration = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(mStickyHeadersAdapter)
                .setOnHeaderClickListener(this)
                .build();

        mRecyclerView.addItemDecoration(mStickyHeadersItemDecoration);
    }


    @Override
    public void onHeaderClick(View header, long headerId) {
        if (!(mStickyHeadersAdapter instanceof HeadersAdapter)) return;
        final HeadersAdapter headersAdapter = (HeadersAdapter) mStickyHeadersAdapter;
    }

    @SuppressWarnings("ResourceType")
    public List<UsageStats> getUsageStatsList() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
//        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
//        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime);
        return usageStatsList;
    }
}
