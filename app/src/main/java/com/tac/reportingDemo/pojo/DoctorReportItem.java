package com.tac.reportingDemo.pojo;

import java.util.ArrayList;
import java.util.List;

public class DoctorReportItem {
    String rptid;
    String loginid;
    String areaname;
    String doctorname;
    String reportdate;
    String withwhom;
    String remark;
    String latitude;
    String longitude;

    List<ProductPojoItem1> itemList = new ArrayList<>();

    public DoctorReportItem(String rptid,
                            String loginid,
                            String areaname,
                            String doctorname,
                            String reportdate,
                            String withwhom,
                            String remark,
                            String latitude,
                            String longitude,
                            List<ProductPojoItem1> itemList) {
        this.rptid = rptid;
        this.loginid = loginid;
        this.areaname = areaname;
        this.doctorname = doctorname;
        this.reportdate = reportdate;
        this.withwhom = withwhom;
        this.remark = remark;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemList = itemList;
    }

    public List<ProductPojoItem1> getItemList() {
        return itemList;
    }

    public String getAreaname() {
        return areaname;
    }

    public String getDoctorname() {
        return doctorname;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLoginid() {
        return loginid;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRemark() {
        return remark;
    }

    public String getReportdate() {
        return reportdate;
    }

    public String getRptid() {
        return rptid;
    }

    public String getWithwhom() {
        return withwhom;
    }

}

