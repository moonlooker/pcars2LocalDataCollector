package com.springtest.code.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据缓存
 * @author LL
 *
 */
public class MyCache {

    private static Logger log = LoggerFactory.getLogger(MyCache.class);

    /**
     * 比赛结果数据缓存
     */
    private static TreeMap<Long, List<Part>> raceResultCache = new TreeMap<>();

    /**
     * 一场比赛的结果缓存
     */
    private static List<Part> raceResultList = new ArrayList<>();

    /**
     * 玩家比赛总时间统计缓存
     */
    private static Map<String, Part> players = new HashMap<>();
    /**
     * 玩家防重复缓存
     */
    private static Set<String> nameCache = new HashSet<>();

    public static void addRaceList(Part part) {

        raceResultList.add(part);
    }

    public static void clearRaceList() {

        raceResultList.clear();
    }

    public static List<Part> getRaceList() {

        return raceResultList;
    }

    /**
     * 缓存一个比赛结果，记录时间为key
     * @param r
     */
    public static void addResult(List<Part> r) {

        raceResultCache.put(System.currentTimeMillis(), r);
    }

    /**
     * 获取比赛结果数据
     * @return
     */
    public static Map<Long, List<Part>> getCache() {

        return raceResultCache;
    }

    /**
     * 清理比赛结果数据
     */
    public static void clearResult() {

        raceResultCache.clear();
    }

    /**
     * 更新或者添加一个玩家统计数据
     * @param player
     * @param time
     */
    public static void updatePlayer(String player, Part time) {

        players.put(player, time);
    }

    /**
     * player rules : mName
     * @param player
     * @return
     */
    public static Part getPlayerTime(String player) {

        return players.get(player);
    }

    /**
     * 清理玩家比赛总时间统计数据缓存
     */
    public static void clearPlayers() {

        log.info("clean player count data xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        players.clear();
    }

    /**
     * 增加名字
     * @param name
     */
    public static void addNameCache(String name) {

        nameCache.add(name);
    }

    /**
     * 验证名字是否已经存在
     * @param name
     * @return
     */
    public static boolean isExistName(String name) {

        return nameCache.contains(name);
    }

    /**
     * 清理
     */
    public static void clearNameCache() {

        log.info("clean playerName Cache xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        nameCache.clear();
    }

}
