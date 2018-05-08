package com.ifeng.meipian;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by suwx on 2018/1/9.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        TouTiaoAnalysis toutiao1 = new TouTiaoAnalysis();
        toutiao1.jiexi("59.108.15.225", 20000);
//        try {
//            ExecutorService pool = Executors.newFixedThreadPool(10);
//            Runnable target1 = () -> {
//                TouTiaoAnalysis toutiao1 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao1.jiexi("210.51.19.2", 20002);
//                }
//            };
//            Runnable target2 = () -> {
//                TouTiaoAnalysis toutiao2 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao2.jiexi("114.248.232.178", 20005);
//                }
//            };
//            Runnable target3 = () -> {
//                TouTiaoAnalysis toutiao3 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao3.jiexi("111.194.51.46", 20009);
//                }
//            };
//            Runnable target4 = () -> {
//                TouTiaoAnalysis toutiao4 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao4.jiexi("59.108.15.225", 20000);
//                }
//            };
//            Runnable target5 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("111.197.23.42", 20001);
//                }
//            };
//            Runnable target6 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("180.88.159.221", 20008);
//                }
//            };
//            Runnable target7 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("123.116.245.241", 20003);
//                }
//            };
//            Runnable target8 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("123.112.138.241", 20007);
//                }
//            };
//            Runnable target9 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("101.41.11.7", 20004);
//                }
//            };
//            Runnable target10 = () -> {
//                TouTiaoAnalysis toutiao5 = new TouTiaoAnalysis();
//                while (true) {
//                    toutiao5.jiexi("114.252.164.25", 20006);
//                }
//            };
//
//            pool.execute(target1);
//            pool.execute(target2);
//            pool.execute(target3);
//            pool.execute(target4);
//            pool.execute(target5);
//            pool.execute(target6);
//            pool.execute(target7);
//            pool.execute(target8);
//            pool.execute(target9);
//            pool.execute(target10);
//        }catch (Exception e){
//            logger.error("抓取出错");
//        }
    }
}
