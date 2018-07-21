package com.springtest.code.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springtest.code.enums.RaceStates;
import com.springtest.code.model.MyCache;
import com.springtest.code.model.Part;

/**
 * web服务
 * @author LL
 *
 */
@RestController
@RequestMapping("/result")
public class MyTestController {

    //    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 展示比赛结果数据
     * @return
     */
    @RequestMapping("/raceRank")
    public String rankList() {

        StringBuilder sb = new StringBuilder();

        int i = 0;

        sb.append("<html><body>");

        for (Entry<Long, List<Part>> e : MyCache.getCache().entrySet()) {
            Date raceTime = new Date(Long.valueOf(e.getKey()));
            sb.append("<h4>").append(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(raceTime)).append("比赛")
                .append(++i).append("结果</h4>");
            sb.append("<table border=\"1\">");
            sb.append("<tr>");
            sb.append("<th>名次</th>");
            sb.append("<th>比赛状态</th>");
            sb.append("<th>级别</th>");
            sb.append("<th>玩家</th>");
            sb.append("<th>车型</th>");
            sb.append("<th>完成圈数</th>");
            sb.append("<th>总时间</th>");
            sb.append("<th>最快圈速</th>");

            sb.append("</tr>");

            List<Part> result = e.getValue();

            /*按照名次排序*/
            result.sort((Part p1, Part p2) -> p1.getmRacePosition().compareTo(p2.getmRacePosition()));

            for (Part t : result) {
                sb.append("<tr>");
                sb.append("<td>").append(t.getmRacePosition()).append("</td>");
                sb.append("<td>").append(RaceStates.getDesc(t.getmRaceStates())).append("</td>");
                sb.append("<td>").append(t.getmCarClassNames()).append("</td>");
                sb.append("<td>").append(t.getmName()).append("</td>");
                sb.append("<td>").append(t.getmCarNames()).append("</td>");
                sb.append("<td>").append(t.getmLapsCompleted()).append("</td>");
                sb.append("<td>").append(t.formatTime(t.getmTotaleTime())).append("</td>");
                sb.append("<td>").append(t.formatTime(t.getmFastestLapTimes())).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            sb.append("<br>").append("<br>").append("<br>").append("<br>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }

    /**
     * 查看玩家最快圈速
     * @return
     */
    @RequestMapping("/fastRank")
    public String fastestRank() {

        StringBuilder sb = new StringBuilder();
        int i = 0;

        sb.append("<html><body>");

        for (Entry<Long, List<Part>> e : MyCache.getCache().entrySet()) {
            Date raceTime = new Date(Long.valueOf(e.getKey()));
            sb.append("<h4>").append(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(raceTime)).append("比赛")
                .append(++i).append("最快圈速排行</h4>");
            sb.append("<table border=\"1\">");
            sb.append("<tr>");
            sb.append("<th>名次</th>");
            sb.append("<th>玩家</th>");
            sb.append("<th>车型</th>");
            sb.append("<th>Sector1</th>");
            sb.append("<th>Sector2</th>");
            sb.append("<th>Sector3</th>");
            sb.append("<th>最快圈速</th>");
            sb.append("</tr>");

            List<Part> temp = e.getValue();
            temp.sort((Part a, Part b) -> a.getmFastestLapTimes().compareTo(b.getmFastestLapTimes()));
            int j = 1;
            for (Part t : e.getValue()) {
                sb.append("<tr>");
                sb.append("<td>").append(j++).append("</td>");
                sb.append("<td>").append(t.getmName()).append("</td>");
                sb.append("<td>").append(t.getmCarNames()).append("</td>");
                sb.append("<td>").append(t.getmFastestSector1()).append("</td>");
                sb.append("<td>").append(t.getmFastestSector2()).append("</td>");
                sb.append("<td>").append(t.getmFastestSector3()).append("</td>");
                sb.append("<td>").append(t.getmFastestLapTimes()).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            sb.append("<br>").append("<br>").append("<br>").append("<br>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }

    @RequestMapping("/rlist")
    public String recordList() {

        StringBuilder sb = new StringBuilder();

        int i = 0;

        sb.append("<html><body>");

        for (Entry<Long, List<Part>> e : MyCache.getCache().entrySet()) {
            Date raceTime = new Date(Long.valueOf(e.getKey()));
            sb.append("<h4>").append(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(raceTime)).append("比赛")
                .append(++i).append("结果</h4>");

            for (Part t : e.getValue()) {

                sb.append(t.getmName()).append(",");
                sb.append(t.getmCarNames()).append(",");
                sb.append(t.getmLapsCompleted()).append(",");
                sb.append(t.getmTotaleTime()).append(",");
                sb.append(t.getmFastestLapTimes()).append("<br>");
            }

            sb.append("<br>").append("<br>").append("<br>").append("<br>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }
}
