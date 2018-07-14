package com.springtest.code.enums;

/**
 * 比赛状态
 * @author LL
 *
 */
public enum RaceStates {
    RACESTATE_INVALID(""),
    RACESTATE_NOT_STARTED("等待中"),
    RACESTATE_RACING("比赛中"),
    RACESTATE_FINISHED("完赛"),
    RACESTATE_DISQUALIFIED("黑旗"),
    RACESTATE_RETIRED("放弃"),
    RACESTATE_DNF("未完成"),
    RACESTATE_MAX("");

    private String name;

    RaceStates(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public static String getDesc(String state) {

        String name = "unknown";

        RaceStates[] ary = RaceStates.values();
        for (RaceStates t : ary) {
            if (t.ordinal() == Integer.valueOf(state)) {
                return t.getName();
            }
        }

        return name;
    }

}
