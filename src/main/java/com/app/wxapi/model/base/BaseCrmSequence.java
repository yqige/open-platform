package com.app.wxapi.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseCrmSequence<M extends BaseCrmSequence<M>> extends Model<M> implements IBean {

    public M setName(java.lang.String name) {
        set("name", name);
        return (M) this;
    }

    public java.lang.String getName() {
        return get("name");
    }

    public M setCurrentValue(java.lang.Long currentValue) {
        set("current_value", currentValue);
        return (M) this;
    }

    public java.lang.Long getCurrentValue() {
        return get("current_value");
    }

    public M setIncrementValue(java.lang.Integer incrementValue) {
        set("increment_value", incrementValue);
        return (M) this;
    }

    public java.lang.Integer getIncrementValue() {
        return get("increment_value");
    }

}
