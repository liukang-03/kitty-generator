package com.hhlx.kitty.generator.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuk
 * @Date: 2021/7/20 12:07
 */
public class DictModel {
    /* 是否是字典项 */
    private boolean isDict = false;
    /* 字典项类型 */
    private String dictType;
    /* 自定义kv列表 */
    private List<DictKV> kvList;

    public DictModel(){
        this.kvList = new ArrayList<>();
    }

    public boolean isDict() {
        return isDict;
    }

    public void setDict(boolean dict) {
        isDict = dict;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public List<DictKV> getKvList() {
        return kvList;
    }

    public void setKvList(List<DictKV> kvList) {
        this.kvList = kvList;
    }
}

class DictKV {
    private String value;
    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
