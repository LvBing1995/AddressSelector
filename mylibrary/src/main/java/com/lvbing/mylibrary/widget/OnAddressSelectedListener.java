package com.lvbing.mylibrary.widget;


import com.lvbing.mylibrary.bean.City;
import com.lvbing.mylibrary.bean.County;
import com.lvbing.mylibrary.bean.Province;
import com.lvbing.mylibrary.bean.Street;

public interface OnAddressSelectedListener {
    void onAddressSelected(Province province, City city, County county, Street street);
}
