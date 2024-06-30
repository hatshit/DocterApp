package com.tac.reportingDemo.pojo;

import java.io.Serializable;

public class ProductPojoItem1 implements Serializable {
    String rptid;
    String productid;
    String productname;
    String productqty;
    String productamount;
    String totamount;

    public ProductPojoItem1(String rptid,
                            String productid,
                            String productname,
                            String productqty,
                            String productamount,
                            String totamount) {
        this.rptid = rptid;
        this.productid = productid;
        this.productname = productname;
        this.productqty = productqty;
        this.productamount = productamount;
        this.totamount = totamount;
    }

    public String getRptid() {
        return rptid;
    }

    public String getProductamount() {
        return productamount;
    }

    public String getProductid() {
        return productid;
    }

    public String getProductname() {
        return productname;
    }

    public String getProductqty() {
        return productqty;
    }

    public String getTotamount() {
        return totamount;
    }
}
