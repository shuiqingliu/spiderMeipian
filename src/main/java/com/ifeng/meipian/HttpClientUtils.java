package com.ifeng.meipian;

import com.google.common.base.Joiner;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by whling on 2017/12/18.
 */
public final class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static int DEFAULT_TIMEOUT = 120;

    private final static int MAX_TOTAL_CONNECTIONS = 200;

    private final static int MAX_ROUTE_CONNECTIONS = 200;

    private final static int READ_TIMEOUT = 6000;

    private final static int TEST_READ_TIMEOUT = 3000;

    private final static int CONNECT_TIMEOUT = 4000;

    private final static int TEST_CONNECT_TIMEOUT = 2000;

    private final static int WAIT_TIMEOUT = 1000;

    private static CloseableHttpClient client;

    private static RequestConfig testRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(WAIT_TIMEOUT)
            .setConnectTimeout(TEST_CONNECT_TIMEOUT).setSocketTimeout(TEST_READ_TIMEOUT).build();

    static {
        SSLContext sslcontext = SSLContexts.createSystemDefault();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(WAIT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(READ_TIMEOUT).build();
        connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        client = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig).build();
    }


    public static String sendGetRequest(String url) throws Exception {
        return sendGetRequest(url, null, null);
    }

    public static String sendGetHeaderParam(String url, Map<String, String> headParams) throws Exception {
        return sendGetRequest(url, headParams, null);
    }

    /**
     * get请求
     *
     * @param url        地址
     * @param headParams 请求头
     * @param params     请求体
     * @return
     * @throws Exception
     */
    public static String sendGetParam(String url, Map<String, String> headParams, Map<String, String> params) throws Exception {
        StringBuilder builder = new StringBuilder(url);
        if (MapUtils.isNotEmpty(params)) {
            if (!url.contains("?")) {
                builder.append("?");
            }
            builder.append(Joiner.on("&").withKeyValueSeparator("=").join(params));
        }

        return sendGetRequest(builder.toString(), headParams, null);
    }


    public static String sendGetRequest(String url, Map<String, String> headParams, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        String responseContent = null;

        HttpGet httpGet = new HttpGet(url);
        if (MapUtils.isNotEmpty(headParams)) {
            for (Map.Entry<String, String> entry : headParams.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
            logger.info("调用地址{}耗时为{}毫秒", new Object[]{url, System.currentTimeMillis() - start});
        } catch (Exception e) {
            logger.error("访问" + url + "异常,信息如下", e);
            throw e;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (Exception ex) {
                logger.error("net io exception ", ex);
            }
        }

        if (responseContent == null) {
            logger.info("url[{}] failed", url);
        } else {
            //logger.info("url[{}] ret[{}]", new String[]{url, responseContent});
            logger.info("url[{}]", new String[]{url});
        }

        return responseContent;
    }

    public static String sendPostRequest(String url) throws Exception {
        return sendPostRequest(url, null, null, null);
    }

    public static String sendPostRequest(String url, Map<String, String> params) throws Exception {
        return sendPostRequest(url, null, params, null);
    }

    public static String sendPostRequest(String url, Map<String, String> headParams, Map<String, String> params, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        HttpPost post = new HttpPost(url);
        if (MapUtils.isNotEmpty(headParams)) {
            for (Map.Entry<String, String> entry : headParams.entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
        }
        List<BasicNameValuePair> postData = new ArrayList<>();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                postData.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, decodeCharset == null ? "UTF-8" : decodeCharset);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }

            logger.info("调用地址{}耗时为{}毫秒", url, System.currentTimeMillis() - start);
        } catch (Exception ex) {
            logger.error("访问" + url + "异常,信息如下", ex);
            throw ex;
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                logger.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostRequest(String url, String requestContent, String decodeCharset) {
        long start = System.currentTimeMillis();
        HttpPost post = new HttpPost(url);

        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
            post.setEntity(new StringEntity(requestContent, "UTF-8"));
            HttpResponse response = client.execute(post);
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }

            logger.info("调用地址{}耗时为{}毫秒", new Object[]{url, System.currentTimeMillis() - start});
        } catch (Exception ex) {
            logger.error("访问" + url + "异常,信息如下", ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                logger.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendTestRequest(String url, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        String responseContent = null;

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(testRequestConfig);
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() > 400) {
                logger.error("访问{}异常，状态码：{}", url, response.getStatusLine().getStatusCode());
                return null;
            }
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
            logger.info("调用地址{}耗时为{}毫秒", url, System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("访问" + url + "异常,信息如下", e);
            throw e;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (Exception ex) {
                logger.error("net io exception ", ex);
            }
        }

        return responseContent;
    }

    public static String httpPost(String url, String param) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(param, "UTF-8"));


        String result = null;
        try {
            HttpResponse response = client.execute(post);
            if (null != response) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            logger.error("Fail to get response.", e);
        }

        return result;
    }

    public static String sendTestRequest(String url) throws Exception {
        return sendTestRequest(url, null);
    }

    public static void main(String[] args) {
        try {
////            Map<String, String> param = new HashMap<>();
////            "id_card":"511424198705243134","jiedaibao_user_id":"632188584652490477","mobile":"17711220027","name":"兔子二十三",
////            "product_id":"10153305","token":"ddcde241e21bd37900e80ecf9f90c5bd"
////            param.put("id_card", "511424198705243134");
////            param.put("jiedaibao_user_id", "632188584652490477");
////            param.put("mobile", "17711220027");
////            param.put("name", "兔子二十三");
////            param.put("product_id", "10153305");
////            param.put("token", "ddcde241e21bd37900e80ecf9f90c5bd");
////
////            System.out.println(sendTestRequest("http://payapi.jiedaibao.com/decode/decode2"));
//            String param = "{\"orderNum\": \"25\",\"state\":\"SHIPMENT_NOTIFIED\",\"description\":\"Notification\"}";
//            System.out.println(httpPost("http://43.227.141.157/mybankv21/loanmarket/order/jxNotify", param));
//            HashMap<String, String> params = new HashMap<>();
//            params.put("Accept-Encoding", "gzip");
//
//            String s = sendGetHeaderParam("http://www.bilibili.com/index/rank/rookie-1-5.json",params);
//            System.out.println(s);
            String s = HttpClientUtils.sendGetParam("http://baobab.kaiyanapp.com/api/v4/categories/videoList?id=2", null, null);
            System.out.println(s);
            //Map<String, String> parameterMap = new HashMap<>();
            //parameterMap.put("position", "new");
            //parameterMap.put("appId", "20");
            //parameterMap.put("gz", "1");
            //parameterMap.put("interfaceId", "11");
            //parameterMap.put("clientV", "6.2.0");
            //parameterMap.put("ext_page", "2");
            //parameterMap.put("withComment", "1");
            //parameterMap.put("callId", "1506576094390");
            //parameterMap.put("ext_loadType", "loadMore");
            //parameterMap.put("v", "1.2");
            //parameterMap.put("separateTag", "1");
            //parameterMap.put("appVersion", "6.2.0");
            //parameterMap.put("ext_openAppNum", "1");
            //parameterMap.put("ext_size", "20");
            //parameterMap.put("deviceId", "bcd8bb4cfdac977c76af6f0cdd55b96110");
            //parameterMap.put("sig", "dd8c528162dd83c853089e8396857b6d");
            //String s = sendPostRequest("http://api.tw06.xlmc.sandai.net/api/rec/list", parameterMap,null,null);
            //System.out.println(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
