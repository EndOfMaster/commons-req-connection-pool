package com.endofmaster.client;

/**
 * @author ZM.Wang
 */
public class Test {

    private final RequestHttpClient requestHttpClient;

    public Test() {
        this.requestHttpClient = new RequestHttpClient();
    }

    @org.junit.Test
    public void test() {
        BaseHttpRequest request = new BaseHttpRequest("https://idcert.market.alicloudapi.com/idcard");
        request.setArg("idCard", "370602199408070732");
        request.setArg("name", "王众民");
        request.setHeader("Authorization", "APPCODE 78c6ca530eb34cd9992433fb0be3260d");
        VerifyIdCardResponse parse = requestHttpClient.execute(request).parse(VerifyIdCardResponse.class);
        System.err.println(parse);
    }
}
