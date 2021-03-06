package com.lvbing.addressselector;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvbing.mylibrary.bean.City;
import com.lvbing.mylibrary.bean.County;
import com.lvbing.mylibrary.bean.Province;
import com.lvbing.mylibrary.bean.Street;
import com.lvbing.mylibrary.beannew.CommonAddressBean;
import com.lvbing.mylibrary.db.manager.AddressDictManager;
import com.lvbing.mylibrary.utils.LogUtil;
import com.lvbing.mylibrary.widget.AddressPickerDialogFragment;
import com.lvbing.mylibrary.widget.AddressSelector;
import com.lvbing.mylibrary.widget.BottomDialog;
import com.lvbing.mylibrary.widget.OnAddressSelectedListener;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnAddressSelectedListener, AddressSelector.OnDialogCloseListener, AddressSelector.onSelectorAreaPositionListener {
    private TextView tv_selector_area;
    private BottomDialog dialog;
    private String provinceCode;
    private String cityCode;
    private String countyCode;
    private String streetCode;
    private int provincePosition;
    private int cityPosition;
    private int countyPosition;
    private int streetPosition;
    private LinearLayout content;
    private AddressDictManager addressDictManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_selector_area = (TextView) findViewById(R.id.tv_selector_area);
        content = (LinearLayout) findViewById(R.id.content);
        tv_selector_area.setOnClickListener(this);
        AddressSelector selector = new AddressSelector(this);
        //获取地址管理数据库
        addressDictManager = selector.getAddressDictManager();

        selector.setTextSize(14);//设置字体的大小
//        selector.setIndicatorBackgroundColor("#00ff00");
        selector.setIndicatorBackgroundColor(android.R.color.holo_orange_light);//设置指示器的颜色
//        selector.setBackgroundColor(android.R.color.holo_red_light);//设置字体的背景

        selector.setTextSelectedColor(android.R.color.holo_orange_light);//设置字体获得焦点的颜色

        selector.setTextUnSelectedColor(android.R.color.holo_blue_light);//设置字体没有获得焦点的颜色

//        //获取数据库管理
        AddressDictManager addressDictManager = selector.getAddressDictManager();
        selector.setOnAddressSelectedListener(new OnAddressSelectedListener() {
            @Override
            public void onAddressSelected(Province province, City city, County county, Street street) {

            }
        });
        View view = selector.getView();
        content.addView(view);
    }


    @Override
    public void onClick(View view) {
        AddressPickerDialogFragment dialogFragment = new AddressPickerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),"ADDRESS_PICK");
        dialogFragment.setOnAddressSure(new AddressPickerDialogFragment.OnAddressListener() {
            @Override
            public void onSureClick(CommonAddressBean province, CommonAddressBean city, CommonAddressBean country, CommonAddressBean street) {
                Toast.makeText(MainActivity.this,province.getName(),Toast.LENGTH_SHORT).show();
            }
        });
        /*if (dialog != null) {
            dialog.show();
        } else {
            dialog = new BottomDialog(this);
            dialog.setOnAddressSelectedListener(this);
            dialog.setDialogDismisListener(this);
            dialog.setTextSize(14);//设置字体的大小
            dialog.setIndicatorBackgroundColor(android.R.color.holo_orange_light);//设置指示器的颜色
            dialog.setTextSelectedColor(android.R.color.holo_orange_light);//设置字体获得焦点的颜色
            dialog.setTextUnSelectedColor(android.R.color.holo_blue_light);//设置字体没有获得焦点的颜色
//            dialog.setDisplaySelectorArea("31",1,"2704",1,"2711",0,"15582",1);//设置已选中的地区
            dialog.setSelectorAreaPositionListener(this);
            dialog.show();
        }*/
    }

    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {
        provinceCode = (province == null ? "" : province.code);
        cityCode = (city == null ? "" : city.code);
        countyCode = (county == null ? "" : county.code);
        streetCode = (street == null ? "" : street.code);
        LogUtil.d("数据", "省份id=" + provinceCode);
        LogUtil.d("数据", "城市id=" + cityCode);
        LogUtil.d("数据", "乡镇id=" + countyCode);
        LogUtil.d("数据", "街道id=" + streetCode);
        String s = (province == null ? "" : province.name) + (city == null ? "" : city.name) + (county == null ? "" : county.name) +
                (street == null ? "" : street.name);
        tv_selector_area.setText(s);
        if (dialog != null) {
            dialog.dismiss();
        }
//        getSelectedArea();
    }

    @Override
    public void dialogclose() {
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    /**
     * 根据code 来显示选择过的地区
     */
    private void getSelectedArea(){
        String province = addressDictManager.getProvince(provinceCode);
        String city = addressDictManager.getCity(cityCode);
        String county = addressDictManager.getCounty(countyCode);
        String street = addressDictManager.getStreet(streetCode);
        tv_selector_area.setText(province+city+county+street);
        LogUtil.d("数据", "省份=" + province);
        LogUtil.d("数据", "城市=" + city);
        LogUtil.d("数据", "乡镇=" + county);
        LogUtil.d("数据", "街道=" + street);
    }

    @Override
    public void selectorAreaPosition(int provincePosition, int cityPosition, int countyPosition, int streetPosition) {
        this.provincePosition = provincePosition;
        this.cityPosition = cityPosition;
        this.countyPosition = countyPosition;
        this.streetPosition = streetPosition;
        LogUtil.d("数据", "省份位置=" + provincePosition);
        LogUtil.d("数据", "城市位置=" + cityPosition);
        LogUtil.d("数据", "乡镇位置=" + countyPosition);
        LogUtil.d("数据", "街道位置=" + streetPosition);
    }
}