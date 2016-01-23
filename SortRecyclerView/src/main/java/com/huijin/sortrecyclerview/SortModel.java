package com.huijin.sortrecyclerview;

public class SortModel {

	private String name;//显示的数据
	private String sortLetters;//显示数据拼音的首字母
	private String sortTitle;
	private long lastTime; //上次打开时间
	private long installTime; //安装时间
	private long apkSize; //应用大小
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getSortTitle() {
		return sortTitle;
	}

	public void setSortTitle(String sortTitle) {
		this.sortTitle = sortTitle;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public long getInstallTime() {
		return installTime;
	}

	public void setInstallTime(long installTime) {
		this.installTime = installTime;
	}

	public long getApkSize() {
		return apkSize;
	}

	public void setApkSize(long apkSize) {
		this.apkSize = apkSize;
	}
}
