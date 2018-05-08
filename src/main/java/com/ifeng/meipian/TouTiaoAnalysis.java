package com.ifeng.meipian;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.util.Elements;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by suwx on 2017/12/26.
 *
 * @author suwx
 */
public class TouTiaoAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(TouTiaoAnalysis.class);
    private static final String sql = "select * from frm_30438 where  status = 9";
    private Map<String, String> map = new HashMap<>();

    public TouTiaoAnalysis() {
    }

    /**
     * 解析数据
     */
    public void jiexi(String ip,int port) {
        //美篇用户
        for (int i = 100001; i < 41689619 ; i++) {
            String url = "https://www.meipian.cn/c/" + i;
            String result = "";
            try {
                map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
//                result = HttpClientProxyUtils.sendGetRequestWithProxy("http://www.baidu.com", map, "utf-8",ip,port);
                result = HttpClientUtils.sendGetRequest(url,map,"utf-8");
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result != null && !result.isEmpty()) {
                filterData(result);
            }
        }
        //美篇官方账号
        for (int i = 10001; i < 10017 ; i++){
            String url = "https://www.meipian.cn/c/" + i;
            String result = "";
            try {
                result = HttpClientProxyUtils.sendGetRequestWithProxy(url, map, "utf-8",ip,port);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result != null && !result.isEmpty()) {
                filterData(result);
            }
        }
        //

    }

    /**
     * 过滤数据
     * 1、tag
     * 2、group_id 通过group_id去换取页面进行换取发布时间，发布时间要在五天之前
     * 3、video_play_count 播放数要大于100
     */
    private void filterData(String result) {
        try {
            Document doc = Jsoup.parse(result);
            Element singerListDiv = doc.getElementsByAttributeValue("class", "info").first();
            org.jsoup.select.Elements userName = singerListDiv.getElementById("h2");
            for (Element lineInfo : userName) {
                String lineInfoContent = lineInfo.select("td").last().text().trim();
                System.out.println("jsoup is :" + lineInfoContent);
            }

            JSONArray datas = JSON.parseObject(result).getJSONArray("data");
            logger.info("抓取到" + datas.size() + "条数据");
            for (int i = 0; i < datas.size(); i++) {
                JSONObject item = datas.getJSONObject(i);
                String hasVideo = item.getString("has_video");
                if ("true".equals(hasVideo)) {
                    //TODO 记录抓取到的数目

                    //0、以下三个字段为将要进行过滤的条件
                    String tag = item.getString("tag");
                    String group_id = item.getString("group_id");
                    String videoPlayCount = item.getString("video_play_count");
                    //1、换取发布时间
//                    String publishTime = getPublishTime(group_id,ip,port);
//                    long now = System.currentTimeMillis();
//                    long mistiming = 0L;
//                    //System.out.println(publishTime);
//                    if (!publishTime.isEmpty() && publishTime!=null && !"".equals(publishTime)) {
//                        //现在时间
//                        mistiming = now - DateUtil.parseDateTime(publishTime).getTime();
//                    } else {
//                        mistiming = 0L;
//                    }
                    //过滤条件
//                    if (mistiming >= 432000000 && Integer.parseInt(videoPlayCount) >= 100) {
//                        //如果符合要求，扔到队列
//                        //TODO 记录符合过滤条件的数目
////                        json2Object(item, publishTime);
//                    } else {
//                        logger.info("不符合过滤要求");
//                    }
                } else {
                    logger.info("接口数据has_video：" + hasVideo + "  不符合规则无法获取时间");
                }

                Thread.sleep(5000);
            }
        }catch (Exception e){
            logger.error("时间有问题");
        }
    }

    /**
     * 将json对象转换成TouTiao对象
     */
//    private TouTiao json2Object(JSONObject item, String publishTime) {
//        String display_url = "http://www.toutiao.com" + item.getString("source_url");
//        //去数据库判重
//        SpiderRepeatDao dao = new SpiderUrlDaoImpl();
//        if (dao.selectArticleUrl(display_url) != null) {
//            logger.info("抓取重复");
//        } else {
//            SpiderUrl url = new SpiderUrl();
//            url.setSourceLink(display_url);
//            url.setStatus(1);
//            dao.insertUrl(url);
//            //入完库  封装成实体对象
//            ToutiaoSync toutiao = new ToutiaoSync();
//            toutiao.setVid(item.getString("video_id"));
//            toutiao.setOwnerName(item.getString("source"));
//            toutiao.setPageUrl(display_url);
//            String image_url = item.getString("image_url");
//            image_url = image_url.replace("list/190x124", "origin");
//            toutiao.setPosterUrl(image_url);
//            toutiao.setAbstracts(item.getString("abstract"));
//            toutiao.setTitle(item.getString("title"));
//            toutiao.setOwnerId(item.getString("media_url"));//是个字符串，其中包含了uid，但是没有截取
//            toutiao.setCreateTime(DateUtil.now());
//            toutiao.setPublishTime(publishTime);
//            toutiao.setCreator("今日头条");
//            toutiao.setCpName("今日头条");
//            toutiao.setTags(item.getString("tag"));
//            toutiao.setPlayCount(item.getString("video_play_count"));
//            toutiao.setCommentCounts(item.getString("comments_count"));
//            toutiao.setGroupId(item.getString("group_id"));
//
//            String toutiao_xml = XmlUtil.convertObjToXmlWithoutEscape(toutiao);
//            String res = syncCmppJob.doPost(toutiao_xml);
//            logger.info("res",res);
//        }
//
//
//        return null;
//    }

    /**
     * 通过链接地址去页面中正则出时间来
     */
    private String getPublishTime(String group_id,String ip,int port) {
        String url = "http://www.365yg.com/group/" + group_id;
        String backInfo = "";
        String publishTime = "";
        try {
            backInfo = HttpClientProxyUtils.sendGetRequestWithProxy(url,map,"utf-8",ip,port);
            if (!backInfo.isEmpty()) {
                //(?<=(siblingList \= ))[\s\S]*?\}\](?=;\r\n)
                //String regex = "(?<=(var abstract=))[\\s\\S]*?time: \'[\\s\\S]*?\'";
                String regex = "(?<=(time: \'))[\\s\\S]{10}";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(backInfo);
                if (matcher.find()) {
                    publishTime = matcher.group(0);
                }
            }
        } catch (Exception e) {
            //logger.error(e.toString());
            logger.error("时间转换失败");
        }
        if (!publishTime.isEmpty()) {
            publishTime = publishTime.replace("/", "-") + " 00:00:00";
        }else{
            publishTime = DateUtil.now();
        }
        return publishTime;
    }
}
