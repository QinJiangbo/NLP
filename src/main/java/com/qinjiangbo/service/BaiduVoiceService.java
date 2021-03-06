package com.qinjiangbo.service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qinjiangbo.vojo.VoiceData;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BaiduVoiceService {

    private String token = "";
    private StringBuffer result = new StringBuffer();

    /**
     * 转换语音
     *
     * @param voiceUrl 语音在线地址
     * @return
     */
    public String convertVoice(String voiceUrl) {
        //每次请求都获取令牌
        token = getAccessToken();
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet post = new HttpGet(voiceUrl);
            HttpResponse response = client.execute(post);
            byte[] voiceBuffer = EntityUtils.toByteArray(response.getEntity());
            ArrayList<byte[]> buffers = splitBuffer(voiceBuffer,
                    voiceBuffer.length, 720000);// 每次上传45s以内的文件 720000, 经过多次测试，这个条件下效果最优
            for (byte[] buf : buffers) {
                voiceConvert(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 分割wav音频
     *
     * @param buffer
     * @param length
     * @param spsize 每块分割区大小
     * @return
     */
    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length,
                                          int spsize) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }

    /**
     * 语音转换文字
     *
     * @param voiceBuffer 缓冲块数组
     */
    private void voiceConvert(byte[] voiceBuffer) {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://vop.baidu.com/server_api");

        VoiceData data = new VoiceData();
        data.setFormat("wav");
        data.setRate(16000);
        data.setChannel(1);
        data.setCuid("T6mVWXuftApc2D9sXIujO2Vu");
        data.setToken(token);// 正式上线时候，判断超时时间，不超时，则不需要到百度去认证
        data.setLan("en");
        try {

            data.setSpeech(DatatypeConverter
                    .printBase64Binary(voiceBuffer));
            data.setLen(voiceBuffer.length);
            ObjectMapper om = new ObjectMapper();
            StringEntity entity = new StringEntity(
                    om.writeValueAsString(data));
            entity.setContentType("application/json; charset=utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            String buff = EntityUtils
                    .toString(response.getEntity());
            if (buff.length() > 0) {
                JSONObject obj = JSON.parseObject(buff.toString());
                if ("0".compareTo(obj.getString("err_no")) == 0) {// 调用成功
                    result.append(obj.getJSONArray(
                            "result").get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取访问令牌
     *
     * @return
     */
    private String getAccessToken() {
        String res = "";
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null,
                            new TrustStrategy() {
                                // 信任所有
                                public boolean isTrusted(
                                        X509Certificate[] chain,
                                        String authType)
                                        throws CertificateException {
                                    return true;
                                }
                            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext);

            HttpClient client = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();
            HttpPost post = new HttpPost(
                    "https://openapi.baidu.com/oauth/2.0/token");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("grant_type",
                    "client_credentials"));
            pairs.add(new BasicNameValuePair("client_id",
                    "T6mVWXuftApc2D9sXIujO2Vu"));
            pairs.add(new BasicNameValuePair("client_secret",
                    "f8dd2b866e8f3b584167fce9d2b09f6d"));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    pairs);
            post.setEntity(formEntity);
            HttpResponse response = client.execute(post);
            String buff = EntityUtils
                    .toString(response.getEntity());
            if (buff != null && buff.length() > 0) {
                JSONObject obj = JSON.parseObject(buff.toString());
                res = obj.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
