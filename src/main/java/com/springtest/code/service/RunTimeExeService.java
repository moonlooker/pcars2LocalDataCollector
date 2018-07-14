package com.springtest.code.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 数据采集应用
 * 因为使用的是CREST2,所以需要独立的调用程序
 * @author LL
 *
 */
@Service
public class RunTimeExeService {

    private static Logger log = LoggerFactory.getLogger(RunTimeExeService.class);

    @Value("${exe.path}")
    private String path;

    private boolean running = false;

    private Process pr;

    public void runExe() {

        String jarPath = RunTimeExeService.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            jarPath = java.net.URLDecoder.decode(jarPath, "UTF-8");
            log.info("file path is {}", jarPath);
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        String myPath = new File(jarPath).getParentFile().getAbsolutePath().replaceAll("file.*", "");

        log.info("jar path is {}", jarPath);
        Runtime rt = Runtime.getRuntime();
        pr = null;
        try {
            //打包后调用外部的程序
            pr = rt.exec(myPath + "crest2/CREST2.exe");
            //本地测试的时候使用这个调用
            //            pr = rt.exec(RunTimeExeService.class.getResource("/").getPath() + "crest2/CREST2.exe");

            running = true;
        } catch (IOException e) {
            log.error("start CREST2.exe error!!!", e);
        }

    }

    public boolean runState() {

        return running;
    }

    public void destroy() {

        running = false;
        if (pr != null) {
            //            log.info("stop exe !!!!!");
            //            pr.destroy();
        }

    }

}
