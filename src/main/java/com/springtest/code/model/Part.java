package com.springtest.code.model;

import java.math.BigDecimal;

public class Part {

    /**
     * 玩家数据
     */
    //名字
    private String mName;
    //位置
    private Integer mRacePosition;
    //完成圈数
    private String mLapsCompleted;
    //最快圈速
    private String mFastestLapTimes;
    private String mFastestSector1;
    private String mFastestSector2;
    private String mFastestSector3;
    //车辆名字
    private String mCarNames;
    //车辆级别
    private String mCarClassNames;
    //最后一圈时间
    private String mLastLapTimes;
    //总比赛时间
    private String mTotaleTime;
    //状态  see RaceStates
    private String mRaceStates;

    /**
     * 赛道信息
     */
    private String mTrackLocation;
    private String mTrackVariation;
    private String mTrackLength;

    public String getmName() {

        return mName;
    }

    public void setmName(String mName) {

        this.mName = mName;
    }

    public Integer getmRacePosition() {

        return mRacePosition;
    }

    public void setmRacePosition(Integer mRacePosition) {

        this.mRacePosition = mRacePosition;
    }

    public String getmLapsCompleted() {

        return mLapsCompleted;
    }

    public void setmLapsCompleted(String mLapsCompleted) {

        this.mLapsCompleted = mLapsCompleted;
    }

    public String getmFastestLapTimes() {

        return mFastestLapTimes;
    }

    public void setmFastestLapTimes(String mFastestLapTimes) {

        this.mFastestLapTimes = mFastestLapTimes;
    }

    public String getmFastestSector1() {

        return mFastestSector1;
    }

    public void setmFastestSector1(String mFastestSector1) {

        this.mFastestSector1 = mFastestSector1;
    }

    public String getmFastestSector2() {

        return mFastestSector2;
    }

    public void setmFastestSector2(String mFastestSector2) {

        this.mFastestSector2 = mFastestSector2;
    }

    public String getmFastestSector3() {

        return mFastestSector3;
    }

    public void setmFastestSector3(String mFastestSector3) {

        this.mFastestSector3 = mFastestSector3;
    }

    public String getmCarNames() {

        return mCarNames;
    }

    public void setmCarNames(String mCarNames) {

        this.mCarNames = mCarNames;
    }

    public String getmCarClassNames() {

        return mCarClassNames;
    }

    public void setmCarClassNames(String mCarClassNames) {

        this.mCarClassNames = mCarClassNames;
    }

    public String getmTrackLocation() {

        return mTrackLocation;
    }

    public void setmTrackLocation(String mTrackLocation) {

        this.mTrackLocation = mTrackLocation;
    }

    public String getmTrackVariation() {

        return mTrackVariation;
    }

    public void setmTrackVariation(String mTrackVariation) {

        this.mTrackVariation = mTrackVariation;
    }

    public String getmTrackLength() {

        return mTrackLength;
    }

    public void setmTrackLength(String mTrackLength) {

        this.mTrackLength = mTrackLength;
    }

    public String getmLastLapTimes() {

        return mLastLapTimes;
    }

    public void setmLastLapTimes(String mLastLapTimes) {

        this.mLastLapTimes = mLastLapTimes;
    }

    public String getmTotaleTime() {

        return mTotaleTime;
    }

    public void setmTotaleTime(String mTotaleTime) {

        this.mTotaleTime = mTotaleTime;
    }

    public String getmRaceStates() {

        return mRaceStates;
    }

    public void setmRaceStates(String mRaceStates) {

        this.mRaceStates = mRaceStates;
    }

    public String formatTime(String time) {

        BigDecimal times = new BigDecimal(time);
        BigDecimal min = times.divide(new BigDecimal("60"), 0, BigDecimal.ROUND_DOWN);
        BigDecimal sec = times.subtract(min.multiply(new BigDecimal("60")));
        if (sec.compareTo(new BigDecimal("10")) == -1) {
            return min.toString() + ":0" + sec.toString();
        }
        return min.toString() + ":" + sec.toString();
    }

}
