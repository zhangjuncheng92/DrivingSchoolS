/**
 * BCPay.java
 *
 * Created by xuanzhui on 2015/7/27.
 * Copyright (c) 2015 BeeCloud. All rights reserved.
 */
package cn.beecloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCPayPalSyncObserver;
import cn.beecloud.entity.BCObjectIdResult;
import cn.beecloud.entity.BCPayReqParams;
import cn.beecloud.entity.BCPayResult;
import cn.beecloud.entity.BCQRCodeResult;
import cn.beecloud.entity.BCRefundResult;
import cn.beecloud.entity.BCReqParams;
import cn.beecloud.entity.BCRestfulCommonResult;
import cn.beecloud.entity.BCSmsResult;
import cn.beecloud.entity.BCSubscription;
import cn.beecloud.entity.BCSubscriptionResult;

/**
 * 支付类
 * 单例模式
 */
public class BCPay {
    private static final String TAG = "BCPay";

    /**
     * 保留callback实例
     */
    static BCCallback payCallback;

    static Activity mContextActivity;

    // IWXAPI 是第三方app和微信通信的openapi接口
    static IWXAPI wxAPI = null;

    static BCPayPalSyncObserver payPalSyncObserver;

    private static BCPay instance;

    private BCPay() {}

    /**
     * 唯一获取BCPay实例的入口
     * @param context   留存context
     * @return          BCPay实例
     */
    public synchronized static BCPay getInstance(Context context) {

        if (instance == null) {
            instance = new BCPay();
            payCallback = null;
        }

        if (context != null)
            mContextActivity = (Activity)context;

        return instance;
    }

    /**
     * 初始化微信支付，必须在需要调起微信支付的Activity的onCreate函数中调用，例如：
     * BCPay.initWechatPay(XXActivity.this);
     * 微信支付只有经过初始化才能成功调起，其他支付渠道无此要求。
     *
     * @param context      需要在某Activity里初始化微信支付，此参数需要传递该Activity.this，不能为null
     * @return             返回出错信息，如果成功则为null
     */
    public static String initWechatPay(Context context, String wechatAppID) {
        String errMsg = null;

        if (context == null) {
            errMsg = "Error: initWechatPay里，context参数不能为null.";
            Log.e(TAG, errMsg);
            return errMsg;
        }

        if (wechatAppID == null || wechatAppID.length() == 0) {
            errMsg = "Error: initWechatPay里，wx_appid必须为合法的微信AppID.";
            Log.e(TAG, errMsg);
            return errMsg;
        }

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxAPI = WXAPIFactory.createWXAPI(context, null);

        BCCache.getInstance().wxAppId = wechatAppID;

        try {
            if (isWXPaySupported()) {
                // 将该app注册到微信
                wxAPI.registerApp(wechatAppID);
            } else {
                errMsg = "Error: 安装的微信版本不支持支付.";
                Log.e(TAG, errMsg);
            }
        } catch (Exception ignored) {
            errMsg = "Error: 无法注册微信 " + wechatAppID + ". Exception: " + ignored.getMessage();
            Log.e(TAG, errMsg);
        }

        return errMsg;
    }

    /**
     * 释放BCPay占据的context, callback, observer引用
     */
    public static void clear() {
        mContextActivity = null;
        payCallback = null;
        payPalSyncObserver = null;
    }

    /**
     * 释放微信占据的context
     */
    public static void detachWechat() {
        if (wxAPI != null) {
            wxAPI.detach();
            wxAPI = null;
        }
    }


    public enum PAYPAL_PAY_TYPE {
        SANDBOX, LIVE
    }

    /**
     * 初始化paypal, 仅在需要paypal支付的时候需要调用
     * @param clientId  PayPal APP Client ID
     * @param secret    PayPal APP Secret
     * @param type      paypay pay type, SANDBOX for test before online, LIVE is for online
     * @param retrieveShippingAddresses set true then it will enable PayPal Shipping Addresses Retrieval, but it sometimes may cause 'shipping address invalid' error during payment
     */
    public static void initPayPal(String clientId, String secret, PAYPAL_PAY_TYPE type, Boolean retrieveShippingAddresses){
        BCCache instance = BCCache.getInstance();
        instance.paypalClientID = clientId;
        instance.paypalSecret = secret;
        instance.paypalPayType = type;
        instance.retrieveShippingAddresses = retrieveShippingAddresses;
    }

    /**
     * 判断微信是否支持支付
     * @return true表示支持
     */
    public static boolean isWXPaySupported() {
        boolean isPaySupported = false;
        if (wxAPI != null) {
            isPaySupported = wxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        }
        return isPaySupported;
    }

    /**
     * 判断微信客户端是否安装并被支持
     * @return true表示支持
     */
    public static boolean isWXAppInstalledAndSupported() {
        boolean isWXAppInstalledAndSupported = false;
        if (wxAPI != null) {
            isWXAppInstalledAndSupported = wxAPI.isWXAppInstalled() && wxAPI.isWXAppSupportAPI();
        }
        return isWXAppInstalledAndSupported;
    }

    /**
     * 支付调用总接口
     *
     * @param channelType     支付类型  对于支付手机APP端目前只支持WX_APP, ALI_APP, UN_APP, BD_APP
     *                        @see cn.beecloud.entity.BCReqParams.BCChannelTypes
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param billTimeout     订单超时时间，以秒为单位，可以为null
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param analysis        为扩展参数，用于后期的分析，当前仅支持以category为key的分类分析
     * @param callback        支付完成后的回调函数
     */
    private void reqPaymentAsync(final BCReqParams.BCChannelTypes channelType,
                                 final String billTitle, final Integer billTotalFee,
                                 final String billNum, final Integer billTimeout,
                                 final Map<String, String> optional,
                                 final Map<String, String> analysis,
                                 final BCCallback callback) {

        if (callback == null) {
            Log.w(TAG, "请初始化callback");
            return;
        }

        payCallback = callback;

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {

                //校验并准备公用参数
                BCPayReqParams parameters;
                try {
                    parameters = new BCPayReqParams(channelType);
                } catch (BCException e) {
                    callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                            BCPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                            BCPayResult.FAIL_EXCEPTION,
                            e.getMessage()));
                    return;
                }

                String paramValidRes = BCValidationUtil.prepareParametersForPay(billTitle, billTotalFee,
                        billNum, optional, parameters);

                if (paramValidRes != null) {
                    callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                            BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                            BCPayResult.FAIL_INVALID_PARAMS,
                            paramValidRes));
                    return;
                }

                parameters.billTimeout = billTimeout;
                parameters.analysis = analysis;

                String payURL = BCHttpClientUtil.getBillPayURL();

                BCHttpClientUtil.Response response = BCHttpClientUtil
                        .httpPost(payURL, parameters.transToBillReqMapParams());

                if (response.code == 200 || (response.code >= 400 && response.code < 500)) {
                    String ret = response.content;

                    //反序列化json串
                    Gson res = new Gson();

                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> responseMap;
                    try {
                        responseMap = res.fromJson(ret, type);
                    } catch (JsonSyntaxException ex) {
                        callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                                        BCPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                                        BCPayResult.FAIL_EXCEPTION,
                                        "JsonSyntaxException or Network Error:" + response.code + " # " + response.content));
                        return;
                    }

                    //判断后台返回结果
                    Integer resultCode = ((Double) responseMap.get("result_code")).intValue();
                    if (resultCode == 0) {

                        if (mContextActivity != null) {

                            BCCache.getInstance().billID = (String)responseMap.get("id");

                            //如果是测试模式
                            if (BCCache.getInstance().isTestMode) {
                                reqTestModePayment(billTitle, billTotalFee);
                                return;
                            }

                            //针对不同的支付渠道调用不同的API
                            switch (channelType) {
                                case WX_APP:
                                    reqWXPaymentViaAPP(responseMap);
                                    break;
                                case ALI_APP:
                                    reqAliPaymentViaAPP(responseMap);
                                    break;
                                case UN_APP:
                                case BC_APP:
                                    reqUnionPaymentViaAPP(responseMap);
                                    break;
                                case BD_APP:
                                    break;
                                default:
                                    callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                                            BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                                            BCPayResult.FAIL_INVALID_PARAMS,
                                            "channelType参数不合法"));
                            }

                        } else {
                            callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                                    BCPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                                    BCPayResult.FAIL_EXCEPTION,
                                    "Context-Activity NP-Exception"));
                        }
                    } else {
                        //返回后端传回的错误信息
                        String serverMsg = String.valueOf(responseMap.get("result_msg"));
                        String serverDetail = String.valueOf(responseMap.get("err_detail"));

                        callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                                resultCode,
                                serverMsg,
                                serverDetail));
                    }

                } else {
                    callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                            BCPayResult.APP_INTERNAL_NETWORK_ERR_CODE,
                            BCPayResult.FAIL_NETWORK_ISSUE,
                            "Network Error:" + response.code + " # " + response.content));
                }

            }
        });
    }

    /**
     * 支付调用总接口
     *
     * @param payParam        支付参数
     * @param callback        支付完成后的回调函数
     */
    public void reqPaymentAsync(final PayParams payParam, final BCCallback callback) {
        if (payParam.channelType == null)
            return;

        if (payParam.channelType == BCReqParams.BCChannelTypes.PAYPAL_SANDBOX ||
                    payParam.channelType == BCReqParams.BCChannelTypes.PAYPAL_LIVE) {
            reqPayPalPaymentAsync(payParam.billTitle,
                    payParam.billTotalFee,
                    payParam.currency,
                    (HashMap<String, String>) payParam.optional,
                    callback);
        } else {
            reqPaymentAsync(payParam.channelType,
                    payParam.billTitle,
                    payParam.billTotalFee,
                    payParam.billNum,
                    payParam.billTimeout,
                    payParam.optional,
                    payParam.analysis,
                    callback);
        }
    }

    /**
     * 与服务器交互后下一步进入微信app支付
     *
     * @param responseMap     服务端返回参数
     */
    private void reqWXPaymentViaAPP(final Map<String, Object> responseMap) {

        //获取到服务器的订单参数后，以下主要代码即可调起微信支付。
        PayReq request = new PayReq();
        request.appId = String.valueOf(responseMap.get("app_id"));
        request.partnerId = String.valueOf(responseMap.get("partner_id"));
        request.prepayId = String.valueOf(responseMap.get("prepay_id"));
        request.packageValue = String.valueOf(responseMap.get("package"));
        request.nonceStr = String.valueOf(responseMap.get("nonce_str"));
        request.timeStamp = String.valueOf(responseMap.get("timestamp"));
        request.sign = String.valueOf(responseMap.get("pay_sign"));

        if (wxAPI != null) {
            wxAPI.sendReq(request);
        } else {
            payCallback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                    BCPayResult.FAIL_EXCEPTION,
                    "Error: 微信API为空, 请确认已经在需要调起微信支付的Activity中[成功]调用了BCPay.initWechatPay"));
        }
    }

    /**
     * 与服务器交互后下一步进入支付宝app支付
     *
     * @param responseMap     服务端返回参数
     */
    private void reqAliPaymentViaAPP(final Map<String, Object> responseMap) {

        String orderString = (String) responseMap.get("order_string");

        PayTask aliPay = new PayTask(mContextActivity);
        String aliResult = aliPay.pay(orderString);

        //解析ali返回结果
        Pattern pattern = Pattern.compile("resultStatus=\\{(\\d+?)\\}");
        Matcher matcher = pattern.matcher(aliResult);
        String resCode = "";
        if (matcher.find())
            resCode = matcher.group(1);

        String result;
        int errCode;
        String errMsg;

        //9000-订单支付成功, 8000-正在处理中, 4000-订单支付失败, 6001-用户中途取消, 6002-网络连接出错
        String errDetail;
        if (resCode.equals("9000")) {
            result = BCPayResult.RESULT_SUCCESS;
            errCode = BCPayResult.APP_PAY_SUCC_CODE;
            errMsg = BCPayResult.RESULT_SUCCESS;
            errDetail = errMsg;
        } else if (resCode.equals("6001")) {
            result = BCPayResult.RESULT_CANCEL;
            errCode = BCPayResult.APP_PAY_CANCEL_CODE;
            errMsg = BCPayResult.RESULT_CANCEL;
            errDetail = errMsg;
        } else if (resCode.equals("8000")) {
            result = BCPayResult.RESULT_UNKNOWN;
            errCode = BCPayResult.APP_INTERNAL_THIRD_CHANNEL_ERR_CODE;
            errMsg = BCPayResult.RESULT_PAYING_UNCONFIRMED;
            errDetail = "订单正在处理中，无法获取成功确认信息";
        } else {
            result = BCPayResult.RESULT_FAIL;
            errCode = BCPayResult.APP_INTERNAL_THIRD_CHANNEL_ERR_CODE;
            errMsg = BCPayResult.FAIL_ERR_FROM_CHANNEL;

            if (resCode.equals("4000"))
                errDetail = "订单支付失败";
            else
                errDetail = "网络连接出错";
        }

        payCallback.done(new BCPayResult(result, errCode, errMsg,
                errDetail, BCCache.getInstance().billID));
    }

    /**
     * 与服务器交互后下一步进入银联app支付
     *
     * @param responseMap     服务端返回参数
     */
    private void reqUnionPaymentViaAPP(final Map<String, Object> responseMap) {

        String TN = (String) responseMap.get("tn");

        Intent intent = new Intent();
        intent.setClass(mContextActivity, BCUnionPaymentActivity.class);
        intent.putExtra("tn", TN);
        mContextActivity.startActivity(intent);
    }


    private void reqTestModePayment(String billTitle, Integer billTotalFee) {
        Intent intent = new Intent(mContextActivity, BCMockPayActivity.class);
        intent.putExtra("id", BCCache.getInstance().billID);
        intent.putExtra("billTitle", billTitle);
        intent.putExtra("billTotalFee", billTotalFee);
        mContextActivity.startActivity(intent);
    }

    /**
     * 微信支付调用接口
     * 如果您申请的是新版本(V3)的微信支付，请使用此接口发起微信支付.
     * 您在BeeCloud控制台需要填写“微信Partner ID”、“微信Partner KEY”、“微信APP ID”.
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param callback        支付完成后的回调函数
     */
    public void reqWXPaymentAsync(final String billTitle, final Integer billTotalFee,
                                  final String billNum,
                                  final Map<String, String> optional, final BCCallback callback) {
        this.reqPaymentAsync(BCReqParams.BCChannelTypes.WX_APP, billTitle, billTotalFee,
                billNum, null, optional, null, callback);
    }

    /**
     * 支付宝支付
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param callback        支付完成后的回调函数
     */
    public void reqAliPaymentAsync(final String billTitle, final Integer billTotalFee,
                                   final String billNum,
                                   final Map<String, String> optional,
                                   final BCCallback callback) {
        this.reqPaymentAsync(BCReqParams.BCChannelTypes.ALI_APP, billTitle, billTotalFee,
                billNum, null, optional, null, callback);
    }

    /**
     * 银联在线支付
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param callback        支付完成后的回调函数
     */
    public void reqUnionPaymentAsync(final String billTitle, final Integer billTotalFee,
                                     final String billNum,
                                     final Map<String, String> optional,
                                     final BCCallback callback) {
        this.reqPaymentAsync(BCReqParams.BCChannelTypes.UN_APP, billTitle, billTotalFee,
                billNum, null, optional, null, callback);
    }

    /**
     * 百度钱包支付
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param callback        支付完成后的回调函数
     */
    public void reqBaiduPaymentAsync(final String billTitle, final Integer billTotalFee,
                                   final String billNum,
                                     final Map<String, String> optional,
                                   final BCCallback callback) {
        this.reqPaymentAsync(BCReqParams.BCChannelTypes.BD_APP, billTitle, billTotalFee,
                billNum, null, optional, null, callback);
    }

    /**
     * PayPal支付
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param currency        支付货币，如USD
     * @param optional        为扩展参数HashMap，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param callback        支付完成后的回调函数
     */
    public void reqPayPalPaymentAsync(final String billTitle, final Integer billTotalFee,
                                          final String currency, final HashMap<String, String> optional,
                                          final BCCallback callback) {
        if (BCCache.getInstance().isTestMode) {
            callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                    BCPayResult.FAIL_INVALID_PARAMS,
                    "PayPal支付暂不支持通过BeeCloud.setSandbox设置测试模式，你可以在BCPay.initPayPal中设置其原生的sandbox模式"));
            return;
        }

        payCallback = callback;

        if (BCCache.getInstance().paypalClientID == null ||
                BCCache.getInstance().paypalSecret == null ||
                BCCache.getInstance().paypalPayType == null) {
            callback.done(new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                    BCPayResult.FAIL_INVALID_PARAMS,
                    "使用PayPal支付需要设置client id，PayPal应用secret和PayPal支付类型"));
            return;
        }

        Intent intent = new Intent(mContextActivity, BCPayPalPaymentActivity.class);
        intent.putExtra("billTitle", billTitle);
        intent.putExtra("billTotalFee", billTotalFee);
        intent.putExtra("currency", currency);
        Gson gson = new Gson();
        intent.putExtra("optional", gson.toJson(optional));
        mContextActivity.startActivity(intent);

    }

    private String getPayPalAccessToken() {

        BCHttpClientUtil.Response response = BCHttpClientUtil
                .getPayPalAccessToken();

        String accessToken = null;

        if (response.code == 200) {
            String ret = response.content;

            //反序列化json
            Gson res = new Gson();

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> responseMap = res.fromJson(ret, type);

            //判断后台返回结果
            accessToken = String.valueOf(responseMap.get("access_token"));

        }

        return accessToken;
    }

    /**
     * sync with server to verify the payment
     *
     * @return if result equals BCPayResult.RESULT_SUCCESS then it means sync successfully and payment is valid
     */
    public BCPayResult syncPayPalPayment(final String billTitle, final Integer billTotalFee, final String billNum,
                               final String currency, final String optional) {
        if (BCCache.getInstance().isTestMode) {
            return new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                    BCPayResult.FAIL_INVALID_PARAMS,
                    "PayPal支付暂不支持通过BeeCloud.setSandbox设置测试模式，你可以在BCPay.initPayPal中设置其原生的sandbox模式");
        }

        //verify params
        BCPayReqParams parameters;
        try {
            parameters = new BCPayReqParams(BCCache.getInstance().paypalPayType == PAYPAL_PAY_TYPE.LIVE ?
                    BCReqParams.BCChannelTypes.PAYPAL_LIVE : BCReqParams.BCChannelTypes.PAYPAL_SANDBOX);
        } catch (BCException e) {
            return new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                    BCPayResult.FAIL_INVALID_PARAMS,
                    e.getMessage());
        }

        Map<String, String> optionalMap = null;

        if (optional != null) {
            Gson gson = new Gson();
            optionalMap = gson.fromJson(optional, new TypeToken<Map<String,String>>() {}.getType());
        }

        String paramValidRes = BCValidationUtil.prepareParametersForPay(billTitle, billTotalFee,
                billNum, optionalMap, parameters);

        if (paramValidRes != null) {
            return new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                    BCPayResult.FAIL_INVALID_PARAMS,
                    paramValidRes);
        }

        String accessToken = getPayPalAccessToken();

        //Log.w("BCPay", accessToken);

        if (accessToken == null) {
            Log.w(TAG, "fail to retrieve PayPal token");
            return new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_NETWORK_ERR_CODE,
                    BCPayResult.FAIL_NETWORK_ISSUE,
                    "Can't get access Token");
        }

        parameters.currency=currency;
        parameters.accessToken = "Bearer " + accessToken;

        String payURL = BCHttpClientUtil.getBillPayURL();

        BCHttpClientUtil.Response response = BCHttpClientUtil
                .httpPost(payURL, parameters.transToBillReqMapParams());

        if (response.code == 200 || (response.code >= 400 && response.code < 500)) {
            String ret = response.content;

            Gson res = new Gson();

            Type type = new TypeToken<Map<String,Object>>() {}.getType();
            Map<String, Object> responseMap;
            try {
                responseMap = res.fromJson(ret, type);
            } catch (JsonSyntaxException ex) {
                return new BCPayResult(BCPayResult.RESULT_FAIL,
                        BCPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                        BCPayResult.FAIL_EXCEPTION,
                        "JsonSyntaxException or Network Error:" + response.code + " # " + response.content);
            }

            //check result
            Double resultCode = (Double) responseMap.get("result_code");
            //String errDetail = (String)responseMap.get("err_detail");

            if (resultCode == 0) {

                BCCache.getInstance().billID=String.valueOf(responseMap.get("id"));

                return new BCPayResult(BCPayResult.RESULT_SUCCESS,
                        BCPayResult.APP_PAY_SUCC_CODE,
                        BCPayResult.RESULT_SUCCESS,
                        BCPayResult.RESULT_SUCCESS,
                        String.valueOf(responseMap.get("id")));
            /*
            } else if (resultCode == 7 && errDetail != null && errDetail.startsWith("PAYPAL")) {

                Log.w("BCPay", "not a valid payment: " + errDetail);

                //here RESULT_SUCCESS only means sync success, not means successful pay
                //for example, you paid 1 USD, but the PayPal server just received 0.1 USD
                //then it is not a valid payment, it is rare but to prevent fraud

                return BCPayResult.RESULT_SUCCESS;
            */
            } else {
                //return error info from server
                String serverMsg = String.valueOf(responseMap.get("result_msg"));
                String serverDetail = String.valueOf(responseMap.get("err_detail"));

                return new BCPayResult(BCPayResult.RESULT_FAIL,
                        resultCode.intValue(),
                        serverMsg,
                        serverDetail);
            }

        } else {
            return new BCPayResult(BCPayResult.RESULT_FAIL,
                    BCPayResult.APP_INTERNAL_NETWORK_ERR_CODE,
                    BCPayResult.FAIL_NETWORK_ISSUE,
                    "Network Error");
        }
    }

    /**
     * sync with server to verify the payment
     *
     * @return if result equals BCPayResult.RESULT_SUCCESS then it means sync successfully and payment is valid
     */
    public BCPayResult syncPayPalPayment(final String syncJson) {
        Gson gson = new Gson();
        Map<String, String> syncItem = gson.fromJson(syncJson, new TypeToken<Map<String,String>>() {}.getType());

        Integer billTotalFee;
        try{
            billTotalFee = Integer.valueOf(syncItem.get("billTotalFee"));
        } catch (Exception e){
            Log.e(TAG, e.getMessage() == null ? "Exception" : e.getMessage());
            billTotalFee = -1;
        }

        return syncPayPalPayment(syncItem.get("billTitle"), billTotalFee,
                syncItem.get("billNum"), syncItem.get("currency"), syncItem.get("optional"));
    }

    /**
     * 生成支付宝内嵌支付二维码
     *
     * @param billTitle       商品描述, 32个字节内, 汉字以2个字节计
     * @param billTotalFee    支付金额，以分为单位，必须是正整数
     * @param billNum         商户自定义订单号
     * @param optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求
     * @param returnUrl       同步返回页面, 必填
     * @param qrPayMode       支付宝内嵌二维码类型, 可为null
     *                        @see BCPayReqParams
     * @param callback        支付完成后的回调函数
     */
    public void reqAliInlineQRCodeAsync(final String billTitle, final Integer billTotalFee,
                                  final String billNum, final Map<String, String> optional,
                                  final String returnUrl,
                                  final String qrPayMode,
                                  final BCCallback callback) {
        if (callback == null) {
            Log.w(TAG, "请初始化callback");
            return;
        }

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (BCCache.getInstance().isTestMode) {
                    callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL, "该功能暂不支持测试模式"));
                    return;
                }

                //校验并准备公用参数
                BCPayReqParams parameters;
                try {
                    parameters = new BCPayReqParams(BCReqParams.BCChannelTypes.ALI_QRCODE,
                            BCReqParams.ReqType.QRCODE);
                } catch (BCException e) {
                    callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL, e.getMessage()));
                    return;
                }

                String paramValidRes = BCValidationUtil.prepareParametersForPay(billTitle, billTotalFee,
                        billNum, optional, parameters);

                if (paramValidRes != null) {
                    callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL,
                            paramValidRes));
                    return;
                }

                //添加ALI_QRCODE参数
                if (returnUrl == null || !BCValidationUtil.isStringValidURL(returnUrl)) {
                    callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL,
                            "returnUrl为ALI_QRCODE的必填参数，并且需要以http://或https://开始"));
                    return;
                }

                parameters.returnUrl = returnUrl;
                parameters.qrPayMode = qrPayMode;

                String qrCodeReqURL = BCHttpClientUtil.getQRCodeReqURL();

                BCHttpClientUtil.Response response = BCHttpClientUtil
                        .httpPost(qrCodeReqURL, parameters.transToBillReqMapParams());

                if (response.code == 200 || (response.code >= 400 && response.code < 500)) {
                    String ret = response.content;

                    //反序列化json
                    Gson res = new Gson();

                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> responseMap;
                    try {
                        responseMap = res.fromJson(ret, type);
                    } catch (JsonSyntaxException ex) {
                        callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                                BCRestfulCommonResult.APP_INNER_FAIL,
                                "JsonSyntaxException or Network Error:" + response.code + " # " + response.content));
                        return;
                    }

                    //判断后台返回结果
                    Integer resultCode = ((Double) responseMap.get("result_code")).intValue();
                    if (resultCode == 0) {

                        String content = null;
                        String aliQRCodeHtml;

                        if (responseMap.get("url") != null) {
                            content = (String) responseMap.get("url");
                        }
                        aliQRCodeHtml = String.valueOf(responseMap.get("html"));

                        callback.done(new BCQRCodeResult(resultCode,
                                String.valueOf(responseMap.get("result_msg")),
                                String.valueOf(responseMap.get("err_detail")),
                                null, null,
                                content, null,
                                aliQRCodeHtml));

                    } else {
                        //返回服务端传回的错误信息
                        callback.done(new BCQRCodeResult(resultCode,
                                String.valueOf(responseMap.get("result_msg")),
                                String.valueOf(responseMap.get("err_detail"))));
                    }

                } else {
                    callback.done(new BCQRCodeResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL,
                            "Network Error:" + response.code + " # " + response.content));
                }

            }
        });
    }

    /**
     * 发起预退款
     * @param params 预退款参数
     * @param callback  回调入口
     */
    public void prefund(final RefundParams params, final BCCallback callback) {
        if (callback == null) {
            Log.e(TAG, "请初始化callback");
            return;
        }

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (BCCache.getInstance().isTestMode) {
                    callback.done(new BCRefundResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL, "该功能暂不支持测试模式"));
                    return;
                }

                if (params.channelType != null &&
                        params.channelType != BCReqParams.BCChannelTypes.WX &&
                        params.channelType != BCReqParams.BCChannelTypes.ALI &&
                        params.channelType != BCReqParams.BCChannelTypes.UN &&
                        params.channelType != BCReqParams.BCChannelTypes.BD &&
                        params.channelType != BCReqParams.BCChannelTypes.KUAIQIAN &&
                        params.channelType != BCReqParams.BCChannelTypes.JD &&
                        params.channelType != BCReqParams.BCChannelTypes.YEE) {
                    callback.done(new BCRefundResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL, "选择的渠道类型不正确"));
                    return;
                }

                Map<String, Object> reqMap = new HashMap<String, Object>();

                BCCache mCache = BCCache.getInstance();
                String appId = mCache.appId;
                Long timestamp = (new Date()).getTime();
                String appSign = BCSecurityUtil.getMessageMD5Digest(appId +
                        timestamp + mCache.secret);
                reqMap.put("app_id", appId);
                reqMap.put("timestamp", timestamp);
                reqMap.put("app_sign", appSign);
                if (params.channelType != null)
                    reqMap.put("channel", params.channelType.name());
                reqMap.put("refund_no", params.refundNum);
                reqMap.put("bill_no", params.billNum);
                reqMap.put("refund_fee", params.refundFee);
                reqMap.put("optional", params.optional);
                reqMap.put("need_approval", Boolean.TRUE);

                String reqURL = BCHttpClientUtil.getRefundUrl();

                BCHttpClientUtil.Response response = BCHttpClientUtil
                        .httpPost(reqURL, reqMap);

                if (response.code == 200 || (response.code >= 400 && response.code < 500)) {
                    //反序列化json
                    Gson gson = new Gson();

                    try {
                        callback.done(gson.fromJson(response.content, BCRefundResult.class));
                    } catch (JsonSyntaxException ex) {
                        callback.done(new BCRefundResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                                BCRestfulCommonResult.APP_INNER_FAIL,
                                "JsonSyntaxException or Network Error:" + response.code + " # " + response.content));
                    }
                } else {
                    callback.done(new BCRefundResult(BCRestfulCommonResult.APP_INNER_FAIL_NUM,
                            BCRestfulCommonResult.APP_INNER_FAIL,
                            "Network Error:" + response.code + " # " + response.content));
                }
            }
        });
    }

    /**
     * 发送验证码
     * @param phone 结束验证码的手机号
     * @param callback  回调入口
     */
    public void sendSmsCode(final String phone, final BCCallback callback) {
        if (callback == null) {
            Log.e(TAG, "请初始化callback");
            return;
        }

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> reqMap = new HashMap<String, Object>();
                reqMap.put("phone", phone);

                callback.done(BCHttpClientUtil.addRestObject(BCHttpClientUtil.getSmsCodeUrl(),
                        reqMap, BCSmsResult.class, true));
            }
        });
    }

    /**
     * 发起订阅
     * @param params 订阅参数
     * @param smsId 通过sendSmsCode获取的验证码标识符
     * @param smsCode   用户手机收到的验证码
     * @param couponCode    优惠码，可以为null
     * @param callback  回调入口
     */
    public void subscribe(final BCSubscription params, final String smsId, final String smsCode,
                          final String couponCode, final BCCallback callback) {
        if (callback == null) {
            Log.e(TAG, "请初始化callback");
            return;
        }

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> reqMap = BCHttpClientUtil.objectToMap(params);
                reqMap.put("sms_id", smsId);
                reqMap.put("sms_code", smsCode);
                if (couponCode != null)
                    reqMap.put("coupon_code", couponCode);

                callback.done(BCHttpClientUtil.addRestObject(BCHttpClientUtil.getSubscriptionUrl(),
                        reqMap, BCSubscriptionResult.class, true));
            }
        });
    }

    /**
     * 取消订阅
     * @param sid   订阅记录的标识符id
     * @param callback  回调入口
     */
    public void cancelSubscription(final String sid, final BCCallback callback) {
        if (callback == null) {
            Log.e(TAG, "请初始化callback");
            return;
        }

        BCCache.executorService.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> reqMap = new HashMap<>();
                callback.done(BCHttpClientUtil.deleteRestObject(BCHttpClientUtil.getSubscriptionUrl(),
                        sid, reqMap, BCObjectIdResult.class, true));
            }
        });
    }

    /**
     * 外部支付参数实例
     */
    public static class PayParams {
        /**
         *  只允许
         *  BCReqParams.BCChannelTypes.WX_APP，
         *  BCReqParams.BCChannelTypes.ALI_APP，
         *  BCReqParams.BCChannelTypes.UN_APP，
         *  BCReqParams.BCChannelTypes.BD_APP，
         *  BCReqParams.BCChannelTypes.PAYPAL_SANDBOX，
         *  BCReqParams.BCChannelTypes.PAYPAL_LIVE
         *
         */
        public BCReqParams.BCChannelTypes channelType;

        /**
         * 商品描述, 32个字节内, 汉字以2个字节计
         */
        public String billTitle;

        /**
         * 支付金额，以分为单位，必须是正整数
         */
        public Integer billTotalFee;

        /**
         * 商户自定义订单号，PayPal不需要该参数
         */
        public String billNum;

        /**
         * 支付货币，如CNY、USD，目前仅PayPal用到
         */
        public String currency;

        /**
         * 订单超时时间，以秒为单位，建议不小于360, 可以为null
         */
        public Integer billTimeout;

        /**
         * 扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求，可以为null，
         * 对于PayPal请以HashMap实例化
         */
        public Map<String, String> optional;

        /**
         * 扩展参数，用于分析，可以为null
         * 目前key只有是"category"时才会进行分析
         */
        public Map<String, String> analysis;
    }

    /**
     * 外部预退款参数实例
     */
    public static class RefundParams {
        /**
         *  选填参数
         *  BCReqParams.BCChannelTypes.WX，
         *  BCReqParams.BCChannelTypes.ALI，
         *  BCReqParams.BCChannelTypes.UN，
         *  BCReqParams.BCChannelTypes.BD，
         *  BCReqParams.BCChannelTypes.KUAIQIAN，
         *  BCReqParams.BCChannelTypes.JD，
         *  BCReqParams.BCChannelTypes.YEE
         */
        public BCReqParams.BCChannelTypes channelType;

        /**
         * 商户退款单号
         */
        public String refundNum;

        /**
         * 发起支付时填写的订单号
         */
        public String billNum;

        /**
         * 退款金额，必须为正整数，单位为分
         */
        public Integer refundFee;

        /**
         * 扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求，可以为null，
         * 对于PayPal请以HashMap实例化
         */
        public Map<String, String> optional;
    }
}
