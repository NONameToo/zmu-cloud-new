package com.zmu.cloud.commons.card;


import com.ztwj.DefaultClient;
import com.ztwj.ZtwjApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微妙物联卡API
 */
public class CardApi {
    private static final Logger logger = LoggerFactory.getLogger(CardApi.class);

    private CardProperties cardProperties;

    private ZtwjApiClient cardClient;

    private boolean initSuccess;

    public CardApi(CardProperties cardProperties) {
        this.cardProperties = cardProperties;
    }

    /**
     * 初始化
     */
    public void init() {
        if (initSuccess) {
            return;
        }
        cardClient = new DefaultClient(cardProperties.getUrl(), cardProperties.getAppKey(), cardProperties.getSecret());
        initSuccess = true;
        logger.info("初始化物联卡Api成功!");
    }

//    /**
//     * 客户端
//     */
//    public ZtwjApiClient  cardClient() {
//        return  new DefaultClient(cardProperties.getUrl(), cardProperties.getAppKey(), cardProperties.getSecret());
//    }

    /**
     * 客户端(单例)
     */
    public ZtwjApiClient  cardClient() {
        return  this.cardClient;
    }
}
