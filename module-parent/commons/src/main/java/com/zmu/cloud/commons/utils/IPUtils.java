package com.zmu.cloud.commons.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: IPUtils
 * @Author
 * @Date 2018-11-02 9:19
 */

@Slf4j
public class IPUtils {

    public static final String ADDR = "0:0:0:0:0:0:0:1";

    public static String getClientIp(ServerHttpRequest request) {
        try {
            String ipAddress = null;
            ipAddress = request.getHeaders().getFirst("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeaders().getFirst("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeaders().getFirst("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddress().getAddress().getHostAddress();
                if (ipAddress.equals("127.0.0.1")) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
            return ipAddress;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "- -";
    }

    /**
     * 获取客户端IP
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        try {
            String ipAddress = null;
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if ("127.0.0.1".equals(ipAddress)) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
            return StringUtils.isBlank(ipAddress) ? "" : ipAddress;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getClientIp(WebRequest webRequest) {
        try {
            String ipAddress = null;
            ipAddress = webRequest.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = webRequest.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = webRequest.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = webRequest.getRemoteUser();
                if ("127.0.0.1".equals(ipAddress)) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
            return StringUtils.isBlank(ipAddress) ? "" : ipAddress;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static final Map<String, String> IP_ADDR_MAP = new ConcurrentHashMap<>();

    private static final String PC_ONLINE_URL = "https://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";

    private static final Pattern PATTERN = Pattern.compile("^(192\\.168|172\\.(1[6-9]|2\\d|3[0,1]))(\\.(2[0-4]\\d|25[0-5]|[0,1]?\\d?\\d)){2}$|^10(\\.([2][0-4]\\d|25[0-5]|[0,1]?\\d?\\d)){3}$");

    public static boolean isInNet(String ip) {
        if (StringUtils.isBlank(ip) || ADDR.equalsIgnoreCase(ip))
            return true;
        Matcher matcher = PATTERN.matcher(ip);
        return matcher.find();
    }

    public static String addr(String ip) {
        if (isInNet(ip))
            return "局域网 " + ip;
        try {
            if (IP_ADDR_MAP.containsKey(ip)) {
                return IP_ADDR_MAP.get(ip);
            }
            String result = restTemplate.getForObject(PC_ONLINE_URL + ip, String.class);
            String addr = JSON.parseObject(result).getString("addr");
            IP_ADDR_MAP.put(ip, addr);
            return addr;
        } catch (Exception e) {
            log.error("解析IP地址失败：{}", e.getMessage());
            return ip;
        }
    }

    private static RestTemplate restTemplate;

    static {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(100);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(50);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setConnectTimeout(1000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                //从连接池中获取连接的超时时间，超过该时间未拿到可用连接，
                // 会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(1500)
                .build();
        HttpClient build = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(build));
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }
}
