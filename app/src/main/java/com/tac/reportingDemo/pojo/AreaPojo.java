package com.tac.reportingDemo.pojo;

public class AreaPojo {

    String name,id, rate, mrp;
    public  AreaPojo(String name,String id, String rate, String mrp){
        this.name = name;
        this.id = id;
        this.rate = rate;
        this.mrp = mrp;
    }
    public  AreaPojo(){
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }
}
