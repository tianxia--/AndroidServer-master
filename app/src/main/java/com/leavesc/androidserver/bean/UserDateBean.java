package com.leavesc.androidserver.bean;

import android.util.Base64;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDateBean implements Serializable {
//    name string 是人员名称
//    avatarFile file 是 人员头像
//    icNumber string 否IC卡号
//    jobNumber string 否 人员工号
//    mobile string 否手机号
//    company_id int 是公司ID，默认传1
//    remark string 否 备注
//    groups array 否人员组列表，如 1,2,3
//    force int 否强制加入标记：默认是0, 强制是1

    public String name;
    public Base64 avatarFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Base64 getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(Base64 avatarFile) {
        this.avatarFile = avatarFile;
    }
}
