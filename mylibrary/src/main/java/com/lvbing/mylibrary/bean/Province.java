package com.lvbing.mylibrary.bean;

import com.lvbing.mylibrary.beannew.CommonAddressBean;

/**
 * Created by smartTop on 2016/10/19.
 * 省份的实体类
 */

public class Province implements CommonAddressBean {
    public int id;
    public String name;
    public String code;
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
