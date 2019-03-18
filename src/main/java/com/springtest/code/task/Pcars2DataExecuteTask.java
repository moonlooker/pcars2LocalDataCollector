package com.springtest.code.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springtest.code.enums.RaceStates;
import com.springtest.code.enums.SessionStates;
import com.springtest.code.model.MyCache;
import com.springtest.code.model.Part;
import com.springtest.code.service.RunTimeExeService;
import com.springtest.code.utils.MyHttpClientUtils;

/**
 * 数据采集定时任务
 * @author LL
 *
 */
@Component
public class Pcars2DataExecuteTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 是否要记录,只有在比赛的时候才记录
     */
    private volatile boolean isRecord = false;

    @Resource
    private RunTimeExeService runTimeExeService;

    private long sessionEndBegin = 0L;

    /**
     * 单位秒
     */
    private int finishWaitTime = 90;

    /**
     * 比赛结果统计
     * @throws Exception
     */
    @Scheduled(fixedDelay = 1000)
    public void execute() throws Exception {

        if (!runTimeExeService.runState()) {
            /*是否要调用CREST2*/
            runTimeExeService.runExe();
        }

        String race1 = MyHttpClientUtils.doGet("http://127.0.0.1:8180/crest2/v1/api");
        //        race1 = new String(MyFilesUtils.toByteArrayWithNIO(new File("H:/Mywork/race1.json")));
        log.debug("get pcars2 data : {}", race1);
        @SuppressWarnings("unchecked")
        Map<String, Object> racj = (Map<String, Object>) JSON.parse(race1);

        @SuppressWarnings("unchecked")
        Map<String, Object> raceStatus = (Map<String, Object>) racj.get("gameStates");
        if (raceStatus == null) {
            return;
        }

        if (RaceStates.RACESTATE_FINISHED.ordinal() > Integer.valueOf(String.valueOf(raceStatus.get("mRaceState")))
            && SessionStates.SESSION_RACE.ordinal() == Integer
                .valueOf(String.valueOf(raceStatus.get("mSessionState")))) {
            /*判断当前session状态,只有比赛阶段才处理数据,并且采集状态打开*/
            if (!isRecord) {
                isRecord = true;
                /*清理名字缓存*/
                MyCache.clearRaceList();
                MyCache.clearNameCache();
                MyCache.clearPlayers();
            }
            return;
        }

        if (!isRecord) {
            return;
        }

        log.debug("start count race result .............");

        @SuppressWarnings("unchecked")
        Map<String, Object> raceParts = (Map<String, Object>) racj.get("participants");
        @SuppressWarnings("unchecked")
        Map<String, Object> raceInfo = (Map<String, Object>) racj.get("eventInformation");

        String mTrackLocation = String.valueOf(raceInfo.get("mTrackLocation"));
        String mTrackVariation = String.valueOf(raceInfo.get("mTrackVariation"));
        String mTrackLength = String.valueOf(raceInfo.get("mTrackLength"));

        JSONArray parts = (JSONArray) raceParts.get("mParticipantInfo");

        if (parts == null) {
            return;
        }

        boolean continueSwitch = false;
        for (Object t : parts) {
            JSONObject temp = (JSONObject) t;

            /*使用玩家时间统计数据对象中的比赛状态判断是否需要统计*/
            String mName = String.valueOf(temp.get("mName"));
            Part pTime = MyCache.getPlayerTime(mName);
            String raceState = String.valueOf(temp.get("mRaceStates"));
            if (pTime != null) {
                raceState = pTime.getmRaceStates();
            }

            if (Integer.valueOf(raceState) == 3 && sessionEndBegin == 0) {
                /*只要有人完赛就设置一个下完赛时间,用来判断统计完全终止时间*/
                sessionEndBegin = System.currentTimeMillis();
            }

            if (Integer.valueOf(raceState) < 3) {
                /*只有完成比赛的玩家数据才被统计,每次获取的都是服务器实时列表,存在于当前session的玩家才能被统计*/
                continueSwitch = true;
                continue;
            }

            if (MyCache.isExistName(mName)) {
                /*如果玩家数据已经被统计过了就不再处理*/
                continue;
            }

            Part tPart = new Part();
            tPart.setmName(mName);
            tPart.setmRacePosition(Integer.valueOf(String.valueOf(temp.get("mRacePosition"))));
            tPart.setmLapsCompleted(String.valueOf(temp.get("mLapsCompleted")));
            tPart.setmFastestLapTimes(String.valueOf(temp.get("mFastestLapTimes")));
            tPart.setmFastestSector1(String.valueOf(temp.get("mFastestSector1Times")));
            tPart.setmFastestSector2(String.valueOf(temp.get("mFastestSector2Times")));
            tPart.setmFastestSector3(String.valueOf(temp.get("mFastestSector3Times")));
            tPart.setmCarNames(String.valueOf(temp.get("mCarNames")));
            tPart.setmCarClassNames(String.valueOf(temp.get("mCarClassNames")));
            tPart.setmLastLapTimes(String.valueOf(temp.get("mLastLapTimes")));
            tPart.setmRaceStates(raceState);
            /*获取玩家完成比赛后的总时间*/
            tPart.setmTotaleTime(MyCache.getPlayerTime(tPart.getmName()) != null
                ? MyCache.getPlayerTime(tPart.getmName()).getmTotaleTime()
                : "");
            tPart.setmTrackLocation(mTrackLocation);
            tPart.setmTrackVariation(mTrackVariation);
            tPart.setmTrackLength(mTrackLength);

            /*加入集合*/
            MyCache.addRaceList(tPart);
            log.info("race result : {}", JSON.toJSONString(tPart));
            MyCache.addNameCache(mName);
        }

        if (System.currentTimeMillis() - sessionEndBegin <= finishWaitTime * 1000) {
            /*从第一个完成比赛的玩家开始计算,如果已经超过预设时间则强行终止程序,
             * 因为一些掉线或者其他原因的玩家会影响统计结束*/
            continueSwitch = false;
        }
        if (continueSwitch) {
            /*如果没有统计完成不进行结果统计,直到全部完成*/
            continueSwitch = false;
            return;
        }

        List<Part> result = new ArrayList<>();
        HashSet<String> tempMap = new HashSet<>();
        for (Part tl : MyCache.getRaceList()) {
            if (!tempMap.contains(tl.getmName())) {
                result.add(tl);
            }
            tempMap.add(tl.getmName());
        }

        /*加入到缓存*/
        MyCache.addResult(result);
        sessionEndBegin = 0;
        log.info("比赛结果为：{}", JSON.toJSONString(result));
        /*停止统计数据*/
        isRecord = false;

    }

    /**
     * 玩家比赛总时间统计
     * @throws Exception
     */
    @Scheduled(fixedDelay = 2000)
    public void executeImm() throws Exception {

        String race1 = MyHttpClientUtils.doGet("http://127.0.0.1:8180/crest2/v1/api");
        log.debug("get pcars2 data : {}", race1);
        @SuppressWarnings("unchecked")
        Map<String, Object> racj = (Map<String, Object>) JSON.parse(race1);

        @SuppressWarnings("unchecked")
        Map<String, Object> raceStatus = (Map<String, Object>) racj.get("gameStates");
        if (raceStatus == null) {
            return;
        }

        /*如果是非比赛中则不统计*/
        if (RaceStates.RACESTATE_RACING.ordinal() > Integer.valueOf(String.valueOf(raceStatus.get("mRaceState")))
            && SessionStates.SESSION_RACE.ordinal() == Integer
                .valueOf(String.valueOf(raceStatus.get("mSessionState")))) {
            return;
        } else if (SessionStates.SESSION_RACE.ordinal() != Integer
            .valueOf(String.valueOf(raceStatus.get("mSessionState")))) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> raceParts = (Map<String, Object>) racj.get("participants");

        JSONArray parts = (JSONArray) raceParts.get("mParticipantInfo");
        if (parts == null) {
            log.warn("parts is null!!!");
        }
        for (Object t : parts) {
            JSONObject temp = (JSONObject) t;
            Part tPart = new Part();
            tPart.setmName(String.valueOf(temp.get("mName")));
            tPart.setmLapsCompleted(String.valueOf(temp.get("mLapsCompleted")));
            tPart.setmLastLapTimes(String.valueOf(temp.get("mLastLapTimes")));
            tPart.setmRaceStates(String.valueOf(temp.get("mRaceStates")));
            Part pre = MyCache.getPlayerTime(tPart.getmName());

            if (pre == null) {
                /*如果没有缓存就创建一个新的*/
                tPart.setmTotaleTime("0");
                tPart.setmLapsCompleted("0");
                MyCache.updatePlayer(tPart.getmName(), tPart);
                log.info("create player [{}] TotaleTime data +++++++++++", tPart.getmName());
            } else {

                if (RaceStates.RACESTATE_RACING.ordinal() == Integer.valueOf(pre.getmRaceStates())
                    && RaceStates.RACESTATE_FINISHED.ordinal() <= Integer.valueOf(tPart.getmRaceStates())) {
                    /*如果前一个记录是比赛中,当前记录是比赛结束后,则为最终结果*/
                    tPart.setRecordComplete(true);
                }
                if (pre.isRecordComplete()) {
                    /*如果缓存数据已经是完成比赛就不再计算*/
                    log.debug("已经是完成 player [{}] 不记录", tPart.getmName());
                    continue;
                }

                BigDecimal prelap = new BigDecimal(pre.getmLapsCompleted());
                BigDecimal nowlap = new BigDecimal(tPart.getmLapsCompleted());

                if (prelap.compareTo(nowlap) == -1) {
                    /*如果缓存比赛时间数据小于当前比赛时间数据就累加统计*/
                    tPart.setmTotaleTime(addDecimal(pre.getmTotaleTime(), tPart.getmLastLapTimes()));
                    MyCache.updatePlayer(tPart.getmName(), tPart);
                    log.debug("player [{}] TotaleTime data is [{}] +++++++++++", tPart.getmName(),
                        tPart.getmTotaleTime());
                }
            }

            log.debug("laptime result : {}", JSON.toJSONString(tPart));
        }

    }

    private String addDecimal(String a, String b) {

        BigDecimal biga = new BigDecimal(a);
        BigDecimal bigb = new BigDecimal(b);
        return biga.add(bigb).toString();
    }

}
