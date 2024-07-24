package com.tac.reportingDemo.pojo;

import androidx.annotation.NonNull;

public class MyViewPlanPojo {

    private String planId, areaname, Name,year, month,week,day,remark,Plandate,type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MyViewPlanPojo() {
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPlandate() {
        return Plandate;
    }

    public void setPlandate(String plandate) {
        Plandate = plandate;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }
}
