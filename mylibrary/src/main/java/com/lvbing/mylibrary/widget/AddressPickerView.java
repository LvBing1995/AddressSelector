package com.lvbing.mylibrary.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.lvbing.mylibrary.R;
import com.lvbing.mylibrary.bean.City;
import com.lvbing.mylibrary.bean.County;
import com.lvbing.mylibrary.bean.Province;
import com.lvbing.mylibrary.bean.Street;
import com.lvbing.mylibrary.beannew.CommonAddressBean;
import com.lvbing.mylibrary.db.manager.AddressDictManager;
import com.lvbing.mylibrary.utils.Lists;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by wepon on 2017/12/4.
 * 自定义仿京东地址选择器
 */

public class AddressPickerView extends RelativeLayout implements View.OnClickListener {
    // recyclerView 选中Item 的颜色
    private int defaultSelectedColor = Color.parseColor("#10305B");
    // recyclerView 未选中Item 的颜色
    private int defaultUnSelectedColor = Color.parseColor("#000000");
    // 确定字体不可以点击时候的颜色
    private int defaultSureUnClickColor = Color.parseColor("#7F7F7F");
    // 确定字体可以点击时候的颜色
    private int defaultSureCanClickColor = Color.parseColor("#50AA00");

    private static final int INDEX_TAB_PROVINCE = 0;//省份标志
    private static final int INDEX_TAB_CITY = 1;//城市标志
    private static final int INDEX_TAB_COUNTY = 2;//乡镇标志
    private static final int INDEX_TAB_STREET = 3;//街道标志
    private int tabIndex = INDEX_TAB_PROVINCE; //默认是省份

    private Context mContext;
    private int defaultTabCount = 3; //tab 的数量
    private TabLayout mTabLayout; // tabLayout
    private RecyclerView mRvList; // 显示数据的RecyclerView

    private List<CommonAddressBean> mRvData; // 用来在recyclerview显示的数据
    private AddressAdapter mAdapter;   // recyclerview 的 adapter
    private AddressDictManager addressDictManager;

    private static final int WHAT_PROVINCES_PROVIDED = 0;
    private static final int WHAT_CITIES_PROVIDED = 1;
    private static final int WHAT_COUNTIES_PROVIDED = 2;
    private static final int WHAT_STREETS_PROVIDED = 3;

    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    private List<Street> streets;

    private Province mSelectProvice; //选中 省份 bean
    private City mSelectCity;//选中 城市  bean
    private County mSelectCountry;//选中 区县  bean
    private Street mSelectStreet;//选中 街镇  bean

    private OnAddressPickerSureListener mOnAddressPickerSureListener;
    private TextView mTvSure; //确定

    public TextView getmTvSure() {
        return mTvSure;
    }

    public TabLayout getmTabLayout() {
        return mTabLayout;
    }

    @SuppressWarnings("unchecked")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_PROVINCES_PROVIDED: //更新省份列表
                    provinces = (List<Province>) msg.obj;
                    mRvData.clear();
                    mRvData.addAll(provinces);
                    mAdapter.notifyDataSetChanged();
                    updateTabsVisibility(WHAT_PROVINCES_PROVIDED);
                    updateTabsSelect();
                    break;

                case WHAT_CITIES_PROVIDED: //更新城市列表
                    cities = (List<City>) msg.obj;
                    if (Lists.notEmpty(cities)) {
                        // 以次级内容更新列表
                        mRvData.clear();
                        mRvData.addAll(cities);
                        // 更新索引为次级
                        tabIndex = INDEX_TAB_CITY;
                        updateTabsVisibility(WHAT_CITIES_PROVIDED);
                        updateTabsSelect();
                    } else {
                        // 次级无内容，回调
                        callbackInternal();
                    }

                    break;

                case WHAT_COUNTIES_PROVIDED://更新乡镇列表
                    counties = (List<County>) msg.obj;
                    if (Lists.notEmpty(counties)) {
                        mRvData.clear();
                        mRvData.addAll(counties);
                        tabIndex = INDEX_TAB_COUNTY;
                        updateTabsVisibility(WHAT_COUNTIES_PROVIDED);
                        updateTabsSelect();
                    } else {
                        callbackInternal();
                    }

                    break;

                case WHAT_STREETS_PROVIDED://更新街道列表
                    streets = (List<Street>) msg.obj;
                    if (Lists.notEmpty(streets)) {
                        mRvData.clear();
                        mRvData.addAll(streets);
                        tabIndex = INDEX_TAB_STREET;
                        updateTabsVisibility(WHAT_STREETS_PROVIDED);
                        updateTabsSelect();
                    } else {
                        callbackInternal();
                    }

                    break;
            }


            updateProgressVisibility();
            return true;
        }
    });

    private void updateTabsSelect() {
        if (mTabLayout.getTabCount() > 0)
            mTabLayout.getTabAt(mTabLayout.getTabCount() - 1).select();
    }

    private void updateProgressVisibility() {
        progressBar.setVisibility(View.GONE);
    }
    TabLayout.Tab cityTab;
    TabLayout.Tab countryTab;
    TabLayout.Tab streetTab;
    private void updateTabsVisibility(int index) {
        for (int i = 3; i >= 0; i--) {
            switch (i){
                case INDEX_TAB_PROVINCE://省
                    //tabView.setVisibility(Lists.notEmpty(provinces) ? View.VISIBLE : View.GONE);
                    break;
                case INDEX_TAB_CITY://市
                    if (Lists.notEmpty(cities)){
                        if (mTabLayout.getTabCount() < 2){//不存在的时候创建
                            mTabLayout.addTab(cityTab = mTabLayout.newTab().setText("请选择"));
                        }else {//已存在又点击了前面的tab选项
                            mTabLayout.getTabAt(index).setText("请选择");
                        }
                    }else{
                        if (mTabLayout.getTabCount() > INDEX_TAB_CITY)
                            mTabLayout.removeTab(cityTab);
                    }
                    break;
                case INDEX_TAB_COUNTY://区
                    if (Lists.notEmpty(counties)){
                        if (mTabLayout.getTabCount() < 3){
                            mTabLayout.addTab(countryTab = mTabLayout.newTab().setText("请选择"));
                        }else {//已存在又点击了前面的tab选项
                            mTabLayout.getTabAt(index).setText("请选择");
                        }
                    }else{
                        if (mTabLayout.getTabCount() > INDEX_TAB_COUNTY)
                            mTabLayout.removeTab(countryTab);
                    }
                    break;
                case INDEX_TAB_STREET://街镇
                    if (Lists.notEmpty(streets)){
                        if (mTabLayout.getTabCount() < 4){
                            mTabLayout.addTab(streetTab = mTabLayout.newTab().setText("请选择"));
                        }else {//已存在又点击了前面的tab选项
                            mTabLayout.getTabAt(index).setText("请选择");
                        }
                    }else{
                        if (mTabLayout.getTabCount() > INDEX_TAB_STREET)
                            mTabLayout.removeTab(streetTab);
                    }
                    break;

            }

        }
    }

    public AddressPickerView(Context context) {
        super(context);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private ProgressBar progressBar;
    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        addressDictManager = new AddressDictManager(context);
        mRvData = new ArrayList<>();
        // UI
        View rootView = inflate(mContext, R.layout.address_picker_view, this);
        // 确定
        mTvSure = rootView.findViewById(R.id.tvSure);
        progressBar = rootView.findViewById(R.id.progressBar);
        mTvSure.setTextColor(defaultSureUnClickColor);
        mTvSure.setOnClickListener(this);
        // tablayout初始化
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tlTabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("请选择"));

        mTabLayout.addOnTabSelectedListener(tabSelectedListener);
        updateTabsVisibility(0);
        // recyclerview adapter的绑定
        mRvList = (RecyclerView) rootView.findViewById(R.id.rvList);
        mRvList.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new AddressAdapter();
        mRvList.setAdapter(mAdapter);
        // 初始化默认的本地数据  也提供了方法接收外面数据
        mRvList.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }


    /**
     * 初始化数据
     * 拿assets下的json文件
     */
    private void initData() {
        mRvData.clear();
        retrieveProvinces();
    }

    /**
     * 开放给外部传入数据
     * 暂时就用这个Bean模型，如果数据不一致就需要各自根据数据来生成这个bean了
     */
    /*public void initData(YwpAddressBean bean) {
        if (bean != null) {
            mSelectCountry = null;
            mSelectStreet = null;
            mSelectCity = null;
            mSelectProvice = null;
            mTabLayout.getTabAt(0).select();

            mYwpAddressBean = bean;
            mRvData.clear();
            mRvData.addAll(mYwpAddressBean.getProvince());
            mAdapter.notifyDataSetChanged();

        }
    }*/


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvSure) {
            sure();
        }
    }

    //点确定
    private void sure() {
        if (mSelectProvice != null &&
                mSelectCity != null &&
                mSelectCountry != null &&
                mSelectStreet != null) {
            //   回调接口

        } else {
            Toast.makeText(mContext, "地址还没有选完整哦", Toast.LENGTH_SHORT).show();
        }

    }
    private void callbackInternal(){
        mTvSure.setEnabled(true);
        mTvSure.setTextColor(defaultSureCanClickColor);
        if (mOnAddressPickerSureListener != null) {
            mOnAddressPickerSureListener.onSureClick(mSelectProvice,mSelectCity,mSelectCountry,mSelectStreet);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        provinces = null;
        cities = null;
        counties = null;
        streets = null;
    }

    /**
     * TabLayout 切换事件
     */
    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mRvData.clear();
            switch (tab.getPosition()) {
                case INDEX_TAB_PROVINCE:
                    mRvData.addAll(provinces);
                    tabIndex = INDEX_TAB_PROVINCE;
                    break;
                case INDEX_TAB_CITY:
                    mRvData.addAll(cities);
                    tabIndex = INDEX_TAB_CITY;
                    break;
                case INDEX_TAB_COUNTY:
                    mRvData.addAll(counties);
                    tabIndex = INDEX_TAB_COUNTY;
                    break;
                case INDEX_TAB_STREET:
                    mRvData.addAll(streets);
                    tabIndex = INDEX_TAB_STREET;
                    break;
            }
            // 滚动到这个位置
            mAdapter.notifyDataSetChanged();
            mRvList.smoothScrollToPosition(getSelectPosition(tab.getPosition()));

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    private int getSelectPosition(int type) {
        switch (type){
            case INDEX_TAB_PROVINCE:
                if (mSelectProvice == null) return 0;
                for (int i = 0; i < provinces.size(); i++) {
                    if (TextUtils.equals(mSelectProvice.getCode(),provinces.get(i).getCode()))
                        return i;
                }
                return 0;
            case INDEX_TAB_CITY:
                if (mSelectCity == null) return 0;
                for (int i = 0; i < cities.size(); i++) {
                    if (TextUtils.equals(mSelectCity.getCode(),cities.get(i).getCode()))
                        return i;
                }
                return 0;
            case INDEX_TAB_COUNTY:
                if (mSelectCountry == null) return 0;
                for (int i = 0; i < counties.size(); i++) {
                    if (TextUtils.equals(mSelectCountry.getCode(),counties.get(i).getCode()))
                        return i;
                }
                return 0;
            case INDEX_TAB_STREET:
                if (mSelectStreet == null) return 0;
                for (int i = 0; i < streets.size(); i++) {
                    if (TextUtils.equals(mSelectStreet.getCode(),streets.get(i).getCode()))
                        return i;
                }
                return 0;
        }
        return 0;
    }


    /**
     * 下面显示数据的adapter
     */
    class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address_text, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int tabSelectPosition = mTabLayout.getSelectedTabPosition();
            holder.mTitle.setText(mRvData.get(position).getName());
            holder.mTitle.setTextColor(defaultUnSelectedColor);

            // 设置选中效果的颜色
            switch (tabSelectPosition) {
                case INDEX_TAB_PROVINCE:
                    if (provinces != null && provinces.size() > position &&
                            mSelectProvice != null &&
                            TextUtils.equals(provinces.get(position).getCode(),mSelectProvice.getCode())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case INDEX_TAB_CITY:
                    if (cities != null && cities.size() > position &&
                            mSelectCity != null &&
                            TextUtils.equals(cities.get(position).getCode(),mSelectCity.getCode())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case INDEX_TAB_COUNTY:
                    if (counties != null && counties.size() > position &&
                            mSelectCountry != null &&
                            TextUtils.equals(counties.get(position).getCode(),mSelectCountry.getCode())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case INDEX_TAB_STREET:
                    if (streets != null && streets.size() > position &&
                            mSelectStreet != null &&
                            TextUtils.equals(streets.get(position).getCode(),mSelectStreet.getCode())) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
            }
            // 设置点击之后的事件
            holder.mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击 分类别
                    switch (tabSelectPosition) {
                        case INDEX_TAB_PROVINCE:
                            // 清空后面三个的数据
                            mSelectCity = null;
                            cities = null;
                            mSelectCountry = null;
                            counties = null;
                            mSelectStreet = null;
                            streets = null;
                            retrieveCitiesWith(provinces.get(position).id);
                            if (mRvData.get(position) != null  && mRvData.get(position) instanceof Province)
                                mSelectProvice = (Province) mRvData.get(position);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(INDEX_TAB_PROVINCE).setText(mSelectProvice.getName());
                            // 灰掉确定按钮
                            mTvSure.setEnabled(false);
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            break;
                        case INDEX_TAB_CITY:
                            // 清空后面两个个的数据
                            mSelectCountry = null;
                            counties = null;
                            mSelectStreet = null;
                            streets = null;
                            retrieveCountiesWith(cities.get(position).id);
                            if (mRvData.get(position) != null  && mRvData.get(position) instanceof City)
                                mSelectCity = (City) mRvData.get(position);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(INDEX_TAB_CITY).setText(mSelectCity.getName());
                            // 灰掉确定按钮
                            mTvSure.setEnabled(false);
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            break;
                        case INDEX_TAB_COUNTY:
                            // 清空后面一个的数据
                            mSelectStreet = null;
                            streets = null;
                            retrieveStreetsWith(counties.get(position).id);
                            if (mRvData.get(position) != null &&  mRvData.get(position) instanceof County)
                                mSelectCountry = (County) mRvData.get(position);
                            mTabLayout.getTabAt(INDEX_TAB_COUNTY).setText(mSelectCountry.getName());
                            // 灰掉确定按钮
                            mTvSure.setEnabled(false);
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            break;
                        case INDEX_TAB_STREET:
                            if (mRvData.get(position) != null  && mRvData.get(position) instanceof Street)
                                mSelectStreet = (Street) mRvData.get(position);
                            // 没了，选完了，这个时候可以点确定了
                            mTabLayout.getTabAt(INDEX_TAB_STREET).setText(mSelectStreet.getName());
                            // 确定按钮变亮
                            mTvSure.setEnabled(true);
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            callbackInternal();
                            break;
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRvData == null ? 0 :mRvData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTitle;

            ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.itemTvTitle);
            }

        }
    }


    /**
     * 点确定回调这个接口
     */
    public interface OnAddressPickerSureListener {
        void onSureClick(CommonAddressBean province, CommonAddressBean city, CommonAddressBean country, CommonAddressBean street);
    }

    public void setOnAddressPickerSure(OnAddressPickerSureListener listener) {
        this.mOnAddressPickerSureListener = listener;
    }


    /**
     * 查询省份列表
     */
    private void retrieveProvinces() {
        progressBar.setVisibility(View.VISIBLE);
        List<Province> provinceList = addressDictManager.getProvinceList();
        handler.sendMessage(Message.obtain(handler, WHAT_PROVINCES_PROVIDED, provinceList));

    }

    /**
     * 根据省份id查询城市列表
     * @param provinceId  省份id
     */
    private void retrieveCitiesWith(int provinceId) {
        progressBar.setVisibility(View.VISIBLE);
        List<City> cityList = addressDictManager.getCityList(provinceId);
        handler.sendMessage(Message.obtain(handler, WHAT_CITIES_PROVIDED, cityList));
    }

    /**
     * 根据城市id查询乡镇列表
     * @param cityId 城市id
     */
    private void retrieveCountiesWith(int cityId){
        progressBar.setVisibility(View.VISIBLE);
        List<County> countyList = addressDictManager.getCountyList(cityId);
        handler.sendMessage(Message.obtain(handler, WHAT_COUNTIES_PROVIDED, countyList));
    }
    /**
     * 根据乡镇id查询乡镇列表
     * @param countyId 乡镇id
     */
    private void retrieveStreetsWith(int countyId) {
        progressBar.setVisibility(View.VISIBLE);
        List<Street> streetList = addressDictManager.getStreetList(countyId);
        handler.sendMessage(Message.obtain(handler, WHAT_STREETS_PROVIDED, streetList));
    }


}
