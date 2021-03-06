package com.zjc.drivingSchoolS.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobo.mobolibrary.util.Util;
import com.mobo.mobolibrary.util.UtilPhoto;
import com.zjc.drivingSchoolS.db.models.UserInfo;
import com.zjc.drivingSchoolS.db.request.OrderCreateRequest;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

import java.io.File;

/**
 * @author Z
 * @Filename ApiHttpClient.java
 * @Date 2015-05-30
 * @description 接口管理类
 */
public class ApiHttpClient {

    private static final ApiHttpClient me;

    static {
        me = new ApiHttpClient();
    }

    private ApiHttpClient() {
    }

    public static ApiHttpClient getInstance() {
        return me;
    }

    /**
     * 接口：login
     * 用途：用户登陆
     * 参数：phone(手机号)，password(密码)
     * 调用示例：http://localhost:8080/estate/guardservices/securityGuard/login?phone=13476131467&password=888888
     */
    public void login(String phone, String password, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("username", phone);
        postRequest.addProperty("password", password);

        HttpUtilsAsync.post(Constants.BASE_URL + "login", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 接口：getVerificationCode
     * 用途：获取验证码
     * 参数：phone(手机号)type(验证码类型：0：注册 1：重置密码)
     * 调用示例：http://localhost:8080/estate/services/userinfo/getVerificationCode?phone=13026313632&type=1
     */
    public void getVerificationCode(String phone, int type, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("phone", phone);
        postRequest.addProperty("type", type);
        HttpUtilsAsync.post(Constants.BASE_URL + "student/regcode", postRequest, asyncHttpResponseHandler);
    }


    /**
     * 接口：userRegister
     * 用途：用户注册
     * 参数：phone(手机号)，password(密码)
     * 调用示例：http://localhost:8080/estate/services/userinfo/userRegister?phone=13026313632&password=888888
     */
    public void userRegister(String phone, String valcode, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("phone", phone);
        postRequest.addProperty("valcode", valcode);
        HttpUtilsAsync.post(Constants.BASE_URL + "student/register", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 接口：forgetPwd
     * 用途：用户修改密码
     * 参数：phone(手机号)，password(密码)
     * 调用示例：http://localhost:8080/estate/services/userinfo/forgetPwd?phone=13026313632&password=888866
     */
    public void forgetPwd(String phone, String password, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("password", Util.encrypt(password));
        HttpUtilsAsync.post(Constants.BASE_URL + "userinfo/forgetPwd", params, asyncHttpResponseHandler);
    }

    /**
     * 1.7接口：updatePwd
     * 用途：用户修改密码
     */
    public void updatePwd(String uid, String password, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        postRequest.addProperty("password", password);
        HttpUtilsAsync.post(Constants.BASE_URL + "password/change", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.4接口：loginout
     * 用途：用户登出
     * 参数：phone(手机号)
     * 调用示例：http://localhost:8080/estate/guardservices/securityGuard/loginout?phone=13476131467
     * 请求成功返回值类型: null
     */
    public void loginOut(String phone, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        HttpUtilsAsync.post(Constants.BASE_URL + "userinfo/loginout", params, asyncHttpResponseHandler);
    }

    /**
     * 注册极光
     */
    public void registerJPush(String userId, String pushid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("pushid", pushid);
        HttpUtilsAsync.post(Constants.BASE_URL + "pushid", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 用途：更新用户坐标
     * 参数：id(用户主键)
     * 调用示例：/school/subinfo
     */
    public void updateUserinfo(UserInfo userInfo, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userInfo.getUid());
        postRequest.addProperty("latitude", userInfo.getLatitude());
        postRequest.addProperty("longitude", userInfo.getLongitude());
        postRequest.addProperty("address", userInfo.getAddress());
        postRequest.addProperty("contacts", userInfo.getContacts());
        postRequest.addProperty("contactsphone", userInfo.getContactsphone());
        postRequest.addProperty("schoolname", userInfo.getSchoolname());
        postRequest.addProperty("synopsis", userInfo.getSynopsis());
        HttpUtilsAsync.post(Constants.BASE_URL + "subinfo", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.7.2上传个人头像
     * 调用示例：http://localhost:8080/hms/appservices/user/upLoadUserImg
     */
    public void upLoadUserImg(String id, String img, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.setForceMultipartEntityContentType(true);
        params.setHttpEntityIsRepeatable(true);
        params.put("uid", id);
        try {
            File file;
            //生成输入文件路径，进行压缩
            String filename = Constants.DIR_CACHE + Util.getPhotoFileName("1");
            UtilPhoto.getSmallFileByFile(img, filename);
            file = new File(filename);
            params.put("uploadimg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtilsAsync.postInfinite(Constants.BASE_URL + "subphoto", params, asyncHttpResponseHandler);
    }


    /**
     * 1.10接口：findPatientByUserId
     * 用途：根据用户Id获取亲友列表
     * 参数：userId(用户主键)
     * 调用示例：http://localhost:8080/medical/services/userinfo/findPatientByUserId?userId=1
     */

    public void findPatientByUserId(int userId, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        HttpUtilsAsync.post(Constants.BASE_URL + "userinfo/findPatientByUserId", params, asyncHttpResponseHandler);
    }

    /**
     * 1.13接口：addPatient
     * 用途：添加亲友资料
     * 参数：
     * userId(登录用户主键)
     * name(亲友姓名)
     * idCardType(证件类型1.居民身份证 2.居民户口簿 3.护照 4.军官证 5.驾驶证 6.港澳居民来往内地通行证 7.台湾居民来往内地通行证 99.其他法定有效证件)
     * idCardNo(证件号)
     * isDefault(可选，1.默认 2.非默认)
     * 其他(可选)
     * 调用示例：http://localhost:8080/medical/services/userinfo/addPatient
     */
    public void addPatient(int userId, String name, int idCardType, String idCardNo, int gender, int age, String city, String phone, int isDefault, String birth, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("name", name);
        params.put("idCardType", idCardType);
        params.put("idCardNo", idCardNo);
        params.put("gender", gender);
        params.put("age", age);
        params.put("city", city);
        params.put("phone", phone);
        params.put("isDefault", isDefault);
        params.put("birthday", birth);
        HttpUtilsAsync.post(Constants.BASE_URL + "userinfo/addPatient", params, asyncHttpResponseHandler);
    }


    /**
     * 6.2接口：getMyAccount
     * 用途：获取我的账户信息
     * 参数：uid(登录用户主键)
     * 调用示例：/app/student/account/balance
     */
    public void getMyAccount(String userId, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        HttpUtilsAsync.post(Constants.BASE_URL + "student/account/balance", postRequest, asyncHttpResponseHandler);
    }

    /**
     * ###########   预约学车   ############
     * 1.1.12 获取学员可预约产品
     * 参数：pagesize  uid offset   state  orderid creatdate
     * 调用示例：/app/student/product
     */
    public void findProducts(String userId, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        HttpUtilsAsync.post(Constants.BASE_URL + "student/product", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.13 学员创建学车订单
     * 开始位置经度	longitude	number	必填
     * * 开始位置纬度	latitude	number	必填
     * 项目类型ID	subjectid	string	必填
     * 项目类型名称	subjectname	string	必填
     * 是否VIP	isvip	boolean	必填
     * 车型名称	carsname	string	必填
     * 是否代人下单	isreplace	boolean	必填
     * 下单用户ID	uid	string	必填
     * 联系人姓名	contactsname	string	isreplace为true时必传
     * 联系人电话	contactsphone	string	isreplace为true时必传
     * 数量 单位：小时	number	number	必填
     * 开始时间 格式：yyyy-MM-dd hh:mm:ss	starttime	string	必填
     * 车型ID	carsid	string	必填
     * 下单用户名	loginname	string	必填
     * 下单用户昵称	nickname	string	必填
     * 优惠券ID	vid	string	非必传，格式:多个ID用','分割
     * /app/student/order/create
     */
    public void learnApply(OrderCreateRequest orderCreateRequest, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", orderCreateRequest.getUid());
        postRequest.addProperty("longitude", orderCreateRequest.getLongitude());
        postRequest.addProperty("latitude", orderCreateRequest.getLatitude());
        postRequest.addProperty("subjectid", orderCreateRequest.getSubjectid());
        postRequest.addProperty("subjectname", orderCreateRequest.getSubjectname());
        postRequest.addProperty("carsname", orderCreateRequest.getCarsname());
        postRequest.addProperty("carsid", orderCreateRequest.getCarsid());

        postRequest.addProperty("isvip", orderCreateRequest.getIsvip());
        postRequest.addProperty("isreplace", orderCreateRequest.getIsreplace());
        postRequest.addProperty("contactsname", orderCreateRequest.getContactsname());
        postRequest.addProperty("contactsphone", orderCreateRequest.getContactsphone());

        postRequest.addProperty("number", orderCreateRequest.getNumber());
        postRequest.addProperty("starttime", orderCreateRequest.getStarttime());
        postRequest.addProperty("loginname", orderCreateRequest.getLoginname());
        postRequest.addProperty("nickname", orderCreateRequest.getNickname());
        HttpUtilsAsync.post(Constants.BASE_URL + "student/order/create", postRequest, asyncHttpResponseHandler);
    }


    /**
     * 1.1.14 学员学车接单列表
     * 参数：pagesize  uid offset   state  orderid creatdate
     * 调用示例：order/take/list
     */
    public void getStudyOrders(String userId, int start, String type, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("pagesize", ConstantsParams.PAGE_SIZE);
        postRequest.addProperty("state", ConstantsParams.STUDY_ORDER_TWO);
        postRequest.addProperty("type", type);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/take/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.14 学员学车订单历史
     * 参数：pagesize  uid offset   state  orderid creatdate
     * 调用示例：order/take/list
     */
    public void getStudyOrdersHistory(String userId, int start, String state, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("pagesize", ConstantsParams.PAGE_SIZE);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.15 学员学车订单详情
     * 参数：orid  uid
     * 调用示例：/app/student/order/detail
     */
    public void getOrderDetailById(String userId, String orid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/detail", postRequest, asyncHttpResponseHandler);
    }

    /**
     * ###########驾校主页############
     * 6.2接口：detail
     * 用途：地图驾校详情
     * 参数：uid;// 用户ID  sid;// 驾校ID
     * 调用示例：/app/student/school/detail
     */
    public void getSchoolInfo(String uid, String sid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        postRequest.addProperty("sid", sid);
        HttpUtilsAsync.post(Constants.BASE_URL + "student/school/detail", postRequest, asyncHttpResponseHandler);
    }


    /**
     * ###########消息中心模块############
     * 11.1.16 消息列表请求
     * 参数   偏移量	offset	number	必填/用户ID	uid	string	必填/分页大小	pagesize	number	必填
     * 调用示例：message/list
     */
    public void getMessageByTags(String userId, int start, String noticeStatus, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("type", noticeStatus);
        postRequest.addProperty("pagesize", ConstantsParams.PAGE_SIZE);
        HttpUtilsAsync.post(Constants.BASE_URL + "message/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 未读消息数量
     * 调用示例： /app/student/message/detail
     */
    public void getNoReadMessage(String uid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        HttpUtilsAsync.post(Constants.BASE_URL + "message/noread", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.31 获取消息详情
     * 调用示例： /app/student/message/detail
     */
    public void getNoticeDetail(String uid, String mid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        postRequest.addProperty("mid", mid);
        HttpUtilsAsync.post(Constants.BASE_URL + "message/detail", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.31 删除消息
     * 调用示例：  /app/student/message/delete
     */
    public void deleteNotice(String uid, JsonArray mids, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        postRequest.add("mids", mids);
        postRequest.addProperty("rule", "id");
        HttpUtilsAsync.post(Constants.BASE_URL + "message/delete", postRequest, asyncHttpResponseHandler);
    }


    /**
     * 1.1.26 学车订单-接单
     * 参数：orid  uid
     * 调用示例：/order/take/sub
     */
    public void receiveStudyOrder(String userId, String orid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/take/sub", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.26 学车订单-分配教练
     * 参数：orid  uid tid
     * 调用示例：order/teacher/allot
     */
    public void distributionStudyOrder(String userId, String orid, String tid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        postRequest.addProperty("tid", tid);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/teacher/allot", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.26 学车订单-教练列表
     * 参数：orid  uid tid
     * 调用示例：teacher/list
     */
    public void getTeacherList(String userId, int start, int state, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("pagesize", 20);
        postRequest.addProperty("type", state);
        HttpUtilsAsync.post(Constants.BASE_URL + "teacher/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.31 教练详情
     * 参数
     * 调用示例：/app/student/teacher/collect/detail
     */
    public void getTeacherDetail(String uid, String tid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", uid);
        postRequest.addProperty("tid", tid);
        HttpUtilsAsync.post(Constants.BASE_URL + "teacher/detail", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.26 学车订单-是否同意退订
     * 参数：orid  uid
     * 调用示例：/app/school/order/refund
     */
    public void studyOrderIsAgree(String userId, String orid, boolean isAgree, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        postRequest.addProperty("result", isAgree);
        HttpUtilsAsync.post(Constants.BASE_URL + "order/refund", postRequest, asyncHttpResponseHandler);
    }


    /** ###########  报名  ############  */
    /**
     * 1.1.21 学员报名订单列表历史
     * 参数：pagesize  uid offset   state  orderid creatdate
     * 调用示例：/app/student/order/list
     */
    public void getApplyOrdersHistory(String userId, int start, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("pagesize", ConstantsParams.PAGE_SIZE);
        HttpUtilsAsync.post(Constants.BASE_URL + "signuporder/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.21 学员报名订单列表接单表
     * 参数：pagesize  uid offset   state  orderid creatdate
     * 调用示例：/app/student/order/list
     */
    public void getApplyOrders(String userId, int start, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("offset", start);
        postRequest.addProperty("pagesize", ConstantsParams.PAGE_SIZE);
        HttpUtilsAsync.post(Constants.BASE_URL + "signuporder/take/list", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.26 报名订单-接单
     * 参数：orid  uid
     * 调用示例：signuporder/take/sub
     */
    public void receiveApplyOrder(String userId, String orid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        HttpUtilsAsync.post(Constants.BASE_URL + "signuporder/take/sub", postRequest, asyncHttpResponseHandler);
    }

    /**
     * 1.1.23 学员报名订单详情
     * 参数：orid  uid
     * 调用示例：/app/student/order/detail
     */
    public void getApplyDetail(String userId, String orid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("uid", userId);
        postRequest.addProperty("orid", orid);
        HttpUtilsAsync.post(Constants.BASE_URL + "signuporder/detail", postRequest, asyncHttpResponseHandler);
    }


    /**
     * 接口：feedback
     * 用途：意见反馈
     */
    public void feedback(String content, String nickname, String title, String uid, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        JsonObject postRequest = new JsonObject();
        postRequest.addProperty("content", content);
        postRequest.addProperty("nickname", nickname);
        postRequest.addProperty("title", title);
        postRequest.addProperty("uid", uid);
        HttpUtilsAsync.post(Constants.BASE_URL + "feedback", postRequest, asyncHttpResponseHandler);
    }
}
