package com.springtest.code.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件辅助工具类
 * @author LL
 *
 */
public class MyFilesUtils {

    private static Logger log = LoggerFactory.getLogger(MyFilesUtils.class);
    private static final String DIGEST_TYPE = "MD5";
    private static final int BUF_SIZE = 8192;

    /**
     * 获取一个文件MD5
     * @param file
     * @return MD5可见字符串
     */
    public static String getDigest(MultipartFile file) {

        String digest = null;
        InputStream fis = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_TYPE);
            fis = file.getInputStream();
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = fis.read(buf)) != -1) {
                messageDigest.update(buf, 0, len);
            }
            fis.close();
            //            messageDigest.update(file.getBytes());

            digest = bytesToHexString(messageDigest.digest());
            messageDigest.reset();

        } catch (Exception e) {
            log.error("文件MD5获取失败", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("文件MD5获取流关闭失败", e);
                }
            }
        }
        return digest;
    }

    /**
     * 获取一个文件MD5
     * @param file
     * @return MD5可见字符串
     */
    public static String getDigest(File file) {

        String digest = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_TYPE);

            messageDigest.update(toByteArrayWithNIO(file));

            digest = bytesToHexString(messageDigest.digest());
            messageDigest.reset();

        } catch (Exception e) {
            log.error("文件MD5获取失败", e);
        }

        return digest;
    }

    /**
     * 取得不同文件的contentType,用于HttpHeader
     * @param bucketName
     * @param key
     */
    public static String contentType(String key) {

        if (key.endsWith(".BMP") || key.endsWith(".bmp")) {
            return "image/bmp";
        } else if (key.endsWith(".GIF") || key.endsWith(".gif")) {
            return "image/gif";
        } else if (key.endsWith(".JPEG") || key.endsWith(".jpeg") || key.endsWith(".JPG") || key.endsWith(".jpg")
            || key.endsWith(".PNG") || key.endsWith(".png")) {
            return "image/jpeg";
        } else if (key.endsWith(".HTML") || key.endsWith(".html")) {
            return "text/html";
        } else if (key.endsWith(".TXT") || key.endsWith(".txt")) {
            return "text/plain";
        } else if (key.endsWith(".VSD") || key.endsWith(".vsd")) {
            return "application/vnd.visio";
        } else if (key.endsWith(".PPTX") || key.endsWith(".pptx") || key.endsWith(".PPT") || key.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (key.endsWith(".DOCX") || key.endsWith(".docx") || key.endsWith(".DOC") || key.endsWith(".doc")) {
            return "application/msword";
        } else if (key.endsWith(".XML") || key.endsWith(".xml")) {
            return "text/xml";
        } else if (key.endsWith(".MP4") || key.endsWith(".mp4")) {
            return "video/mpeg4";
        } else if (key.endsWith(".MP3") || key.endsWith(".mp3")) {
            return "audio/mp3";
        }
        return "unknow";
    }

    /**
     * byte[]转换成字符串
     * @param src
     * @return 返回16进制可见字符串
     */
    public static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * File转换为byte[],文件限制,大小不能超过Integer.MAX_VALUE
     * @param file
     * @return byte[]
     * @throws IOException 如果有错会有IO异常
     */
    public byte[] getContent(File file) throws IOException {

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            extracted(file);
        }
        fi.close();
        return buffer;
    }

    private void extracted(File file) throws IOException {

        throw new IOException("Could not completely read file " + file.getName());
    }

    /**
     * 使用字节流方式将文件转换为byte[]
     *
     * @param filename
     * @return byte[]
     * @throws IOException 如果有错会有IO异常
     */
    public static byte[] toByteArray(File file) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("toByteArray处理出错!!", e);
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                log.error("文件流关闭失败", e);
            }
            bos.close();
        }
    }

    /**
     * 使用NIO方式将文件转换为byte[]
     *
     * @param File
     * @return byte[]
     * @throws IOException 如果有错会有IO异常
     */
    public static byte[] toByteArrayWithNIO(File file) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            log.error("toByteArrayWithNIO处理出错!!", e);
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                log.error("文件流关闭失败", e);
            }
            try {
                fs.close();
            } catch (IOException e) {
                log.error("文件流关闭失败", e);
            }
        }
    }

    /**
     * 使用NIO方式将文件转换为byte[]
     *
     * @param FileInputStream
     * @return byte[]
     * @throws IOException 如果有错会有IO异常
     */
    public static byte[] toByteArrayWithNIO(FileInputStream fs) throws IOException {

        if (fs == null) {
            throw new FileNotFoundException();
        }

        FileChannel channel = null;
        try {
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            log.error("toByteArrayWithNIO处理出错!!", e);
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                log.error("文件流关闭失败", e);
            }

        }
    }

    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     *
     * @param filename
     * @return byte[]
     * @throws IOException 如果有错会有IO异常
     */
    @SuppressWarnings("resource")
    public static byte[] toByteArrayBigFile(String filename) throws IOException {

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
            //            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            log.error("toByteArrayBigFile处理出错!!", e);
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                log.error("文件流关闭失败", e);
            }
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名称
     * @return 文件扩展名,如果没有就返回空字符
     */
    public static String getExtention(String fileName) {

        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            return fileName.substring(pos);
        }
        return "";
    }

}
