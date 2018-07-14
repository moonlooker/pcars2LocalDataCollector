package com.springtest.code.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springtest.code.enums.RaceStates;
import com.springtest.code.model.Part;
import com.springtest.code.utils.MyFilesUtils;

public class StringTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        String race1 = new String(MyFilesUtils.toByteArrayWithNIO(new File("H:/Mywork/race1.json")));
        Map<String, Object> racj = (Map<String, Object>) JSON.parse(race1);
        Map<String, Object> raceParts = (Map<String, Object>) racj.get("participants");
        Map<String, Object> raceStatus = (Map<String, Object>) racj.get("gameStates");
        Map<String, Object> raceInfo = (Map<String, Object>) racj.get("eventInformation");

        String mTrackLocation = String.valueOf(raceInfo.get("mTrackLocation"));
        String mTrackVariation = String.valueOf(raceInfo.get("mTrackVariation"));
        String mTrackLength = String.valueOf(raceInfo.get("mTrackLength"));

        System.out.println(
            RaceStates.RACESTATE_FINISHED.ordinal() == Integer.valueOf(String.valueOf(raceStatus.get("mRaceState"))));
        System.out.println(raceStatus.get("mRaceState"));
        JSONArray parts = (JSONArray) raceParts.get("mParticipantInfo");
        List<Part> results = new ArrayList<>();
        for (Object t : parts) {
            JSONObject temp = (JSONObject) t;
            Part tPart = new Part();
            tPart.setmName(String.valueOf(temp.get("mName")));
            tPart.setmRacePosition(Integer.valueOf(String.valueOf(temp.get("mRacePosition"))));
            tPart.setmLapsCompleted(String.valueOf(temp.get("mLapsCompleted")));
            tPart.setmFastestLapTimes(String.valueOf(temp.get("mFastestLapTimes")));
            tPart.setmFastestSector1(String.valueOf(temp.get("mFastestSector1Times")));
            tPart.setmFastestSector2(String.valueOf(temp.get("mFastestSector2Times")));
            tPart.setmFastestSector3(String.valueOf(temp.get("mFastestSector3Times")));
            tPart.setmCarNames(String.valueOf(temp.get("mCarNames")));
            tPart.setmCarClassNames(String.valueOf(temp.get("mCarClassNames")));
            tPart.setmTrackLocation(mTrackLocation);
            tPart.setmTrackVariation(mTrackVariation);
            tPart.setmTrackLength(mTrackLength);
            results.add(tPart);
            System.out.println(JSON.toJSONString(tPart));
        }

        results.sort((Part p1, Part p2) -> p1.getmRacePosition().compareTo(p2.getmRacePosition()));

        for (Part t : results) {
            System.out.println(
                t.getmName() + "," + t.getmCarNames() + "," + t.getmLapsCompleted() + ", ," + t.getmFastestLapTimes());
        }
    }

}
