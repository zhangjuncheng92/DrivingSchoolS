package com.zjc.drivingSchoolS.ui.learn;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.bigkoo.pickerview.TimePopupWindow;
import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.Util;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.ProductSubject;
import com.zjc.drivingSchoolS.db.response.UserProductResponse;
import com.zjc.drivingSchoolS.db.parser.UserProductResponseParser;
import com.zjc.drivingSchoolS.db.request.OrderCreateRequest;
import com.zjc.drivingSchoolS.eventbus.pay.PayAliAccountResultEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author Z
 * @Filename LearnApplyFragment.java
 * @Date 2016.06.26
 * @description 学车申请
 */
public class LearnApplyFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private UserProductResponse userProduct;
    private ToggleButton jpushButton;
    private TextView tv_time;
    private TextView tv_locale;
    private TextView tv_timeLength;
    private TextView tv_subject;
    private TextView tv_style;
    private TextView tv_telephone;
    private TextView tv_next;
    private TextView tv_money;
    private int index;

    private OptionsPopupWindow timeLengthOptions;
    private ArrayList<String> optionsItems = new ArrayList<>();

    private OptionsPopupWindow tvSubjectOptions;
    private ArrayList<String> optionsItemsSubject = new ArrayList<>();//培训科目集合
    private List<ProductSubject> productSubjectList = new ArrayList<>();//培训科目集合

    private OptionsPopupWindow tvStyleOptions;
    private ArrayList<String> optionsItemsStyle = new ArrayList<>();

    private OrderCreateRequest orderDetail;

    private TimePopupWindow birthOptions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, R.string.title_learn);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.learn_apply_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        initBirthOptions();
        initTimeLengthOptions();
        initStyleOptions();
        findProducts();
        orderDetail = new OrderCreateRequest();
    }

    private void initView() {
        jpushButton = (ToggleButton) rootView.findViewById(R.id.personal_center_set_frg_jpush_button2);
        tv_time = (TextView) rootView.findViewById(R.id.tv_time);
        tv_locale = (TextView) rootView.findViewById(R.id.tv_locale);
        tv_timeLength = (TextView) rootView.findViewById(R.id.tv_timeLength);
        tv_subject = (TextView) rootView.findViewById(R.id.tv_subject);
        tv_style = (TextView) rootView.findViewById(R.id.tv_style);
        tv_telephone = (TextView) rootView.findViewById(R.id.tv_telephone);
        tv_next = (TextView) rootView.findViewById(R.id.tv_next);
        tv_money = (TextView) rootView.findViewById(R.id.tv_money);

        tv_time.setOnClickListener(this);
        tv_locale.setOnClickListener(this);
        tv_timeLength.setOnClickListener(this);
        tv_subject.setOnClickListener(this);
        tv_style.setOnClickListener(this);
        tv_telephone.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        jpushButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    setIndex(1);
                    jpushButton.setChecked(true);
                    jpushButton.setBackgroundResource(R.drawable.toggle_on);
                    tv_telephone.setVisibility(View.VISIBLE);
                } else {
                    jpushButton.setChecked(false);
                    setIndex(0);
                    jpushButton.setBackgroundResource(R.drawable.toggle_off);
                    tv_telephone.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initStyleOptions() {
        tvStyleOptions = new OptionsPopupWindow(getActivity());
        optionsItemsStyle.add("VIP训练");
        optionsItemsStyle.add("普通训练");
        tvStyleOptions.setPicker(optionsItemsStyle, null, null, true);
        tvStyleOptions.setSelectOptions(0);
        tvStyleOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String tx = optionsItemsStyle.get(options1);
                tv_style.setText(tx);
                if (options1 == 0) {
                    tv_style.setTag(true);
                } else {
                    tv_style.setTag(false);
                }
            }
        });
    }

    private void initTimeLengthOptions() {
        timeLengthOptions = new OptionsPopupWindow(getActivity());
        optionsItems.add("1小时");
        optionsItems.add("2小时");
        optionsItems.add("3小时");
        timeLengthOptions.setPicker(optionsItems, null, null, true);
        timeLengthOptions.setSelectOptions(0);
        timeLengthOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String tx = optionsItems.get(options1);
                tv_timeLength.setText(tx);
                tv_timeLength.setTag(options1 + 1);
            }
        });
    }

    private void findProducts() {
        ApiHttpClient.getInstance().findProducts(SharePreferencesUtil.getInstance().readUser().getUid(), new ResultResponseHandler(getActivity()) {

            @Override
            public void onResultSuccess(String result) {
                userProduct = new UserProductResponseParser().parseResultMessage(result);
                initSubjectOptions(userProduct.getSubject());
            }
        });
    }

    private void initSubjectOptions(List<ProductSubject> productSubjects) {
        this.productSubjectList = productSubjects;
        tvSubjectOptions = new OptionsPopupWindow(getActivity());
        //后台获取数据
        for (int i = 0; i < productSubjects.size(); i++) {
            optionsItemsSubject.add(productSubjects.get(i).getSubjectname());
        }
        tvSubjectOptions.setPicker(optionsItemsSubject, null, null, true);
        tvSubjectOptions.setSelectOptions(0);
        tvSubjectOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                ProductSubject productSubject = productSubjectList.get(options1);
                tv_subject.setText(productSubject.getSubjectname());
                tv_subject.setTag(productSubject);
            }
        });
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.tv_next:
                createResident();
                break;
            case R.id.tv_time:
                //隐藏虚拟键盘
                inputmanger.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                birthOptions.showAtLocation(tv_time, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_locale://练车地点
//                LearnAddressFragment fragment = new LearnAddressFragment();
//                replaceFrg(fragment, null);
                break;
            case R.id.tv_timeLength:
                //隐藏虚拟键盘
                inputmanger.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                timeLengthOptions.showAtLocation(tv_timeLength, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_subject:
                //隐藏虚拟键盘
                inputmanger.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                tvSubjectOptions.showAtLocation(tv_subject, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_style:
                //隐藏虚拟键盘
                inputmanger.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                tvStyleOptions.showAtLocation(tv_style, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_telephone://跳转联系人

                break;
        }
    }


    private void initBirthOptions() {
        birthOptions = new TimePopupWindow(getActivity(), TimePopupWindow.Type.ALL);
        birthOptions.setRange(1900, 2050);
        birthOptions.setTime(new Date());

        birthOptions.setOnTimeSelectListener(new TimePopupWindow.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date da = new Date();
                String time = sdf.format(da);
                String timePick = sdf.format(date);
                if (Integer.valueOf(time.substring(0, 4)).intValue() > Integer.valueOf(timePick.substring(0, 4)).intValue()) {
                    Util.showCustomMsg("日期选择不正确，请重新选择");
                    return;
                } else if (Integer.valueOf(time.substring(5, 7)).intValue() > Integer.valueOf(timePick.substring(5, 7)).intValue()) {
                    Util.showCustomMsg("日期选择不正确，请重新选择");
                    return;
                } else if (Integer.valueOf(time.substring(8, 10)).intValue() > Integer.valueOf(timePick.substring(8, 10)).intValue()) {
                    Util.showCustomMsg("日期选择不正确，请重新选择");
                    return;
                } else if (Integer.valueOf(time.substring(11, 13)).intValue() > Integer.valueOf(timePick.substring(11, 13)).intValue()) {
                    Util.showCustomMsg("日期选择不正确，请重新选择");
                    return;
                } else if (Integer.valueOf(time.substring(14, 16)).intValue() > Integer.valueOf(timePick.substring(14, 16)).intValue()) {
                    Util.showCustomMsg("日期选择不正确，请重新选择");
                    return;
                } else {
                    tv_time.setText(timePick);
                }
            }
        });
    }


    private void createResident() {
        //必填
        String tvSubject = tv_subject.getText().toString().trim();//培训科目
        String tvTime = tv_time.getText().toString().trim();//预约时间
        String tvLocale = tv_locale.getText().toString().trim();//练车地点
        String tvTimeLength = tv_timeLength.getText().toString().trim();//练车时长
        String tvStyle = tv_style.getText().toString().trim();//服务类型
        String tvTelephone = tv_telephone.getText().toString().trim();//联系方式
        String tvMoney = tv_money.getText().toString().trim();//价格

        if (TextUtils.isEmpty(tvTime) || TextUtils.isEmpty(tvLocale) || TextUtils.isEmpty(tvTimeLength) || TextUtils.isEmpty(tvSubject) || TextUtils.isEmpty(tvStyle)) {
            Util.showCustomMsg("请输入完整信息");
            return;
        }

        ProductSubject productSubject = (ProductSubject) tv_subject.getTag();//培训科目
        int timeLength = (int) tv_timeLength.getTag();//练车时长

//        * 联系人姓名	contactsname	string	isreplace为true时必传
//        * 联系人电话	contactsphone	string	isreplace为true时必传
//        * 优惠券ID	vid	string	非必传，格式:多个ID用','分割
        orderDetail.setLatitude(SharePreferencesUtil.getInstance().readCity().getLatLngLocal().getLatitude());
        orderDetail.setLongitude(SharePreferencesUtil.getInstance().readCity().getLatLngLocal().getLongitude());
        orderDetail.setSubjectid(productSubject.getSid());
        orderDetail.setSubjectname(productSubject.getSubjectname());
        orderDetail.setIsvip((boolean) tv_style.getTag());
        orderDetail.setIsreplace(false);
        orderDetail.setCarsname(userProduct.getCars().get(0).getCarsname());
        orderDetail.setCarsid(userProduct.getCars().get(0).getCid());
        orderDetail.setUid(SharePreferencesUtil.getInstance().readUser().getUid());
        orderDetail.setNumber(timeLength);
        orderDetail.setStarttime(tv_time.getText().toString());
        orderDetail.setLoginname(SharePreferencesUtil.getInstance().readUser().getLoginname());
        orderDetail.setNickname(SharePreferencesUtil.getInstance().readUser().getLoginname());

        ApiHttpClient.getInstance().learnApply(orderDetail, new ResultResponseHandler(getActivity(), "正在提交，请稍等") {
            @Override
            public void onResultSuccess(String result) {
                getActivity().finish();
            }
        });
    }

    public void onEventMainThread(PayAliAccountResultEvent event) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
