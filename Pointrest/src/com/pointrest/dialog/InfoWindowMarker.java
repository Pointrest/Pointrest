package com.pointrest.dialog;

public class InfoWindowMarker {
	private String mTitle;
	private String mDescription;
	private Double mLatitude;
	private Double mLongitude;
	private String mId;

	public InfoWindowMarker(String description, String id, String title) {
		mDescription = description;
		mId = id;
		mTitle = title;
	}

	public String getmLabel() {
		return mDescription;
	}

	public void setmLabel(String mLabel) {
		this.mDescription = mLabel;
	}

	public Double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(Double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public Double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(Double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}
}