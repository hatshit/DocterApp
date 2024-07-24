    package com.tac.reportingDemo.storage;

public class    ENDPOINTS {


    public static final String BASE_URL = "http://api.wellpetpharma.com/"; // vipul vipul@123#
// "http://mrreporting.osrinfotech.com/"; //
//    public static final String BASE_URL ="http://mrreporting.osrinfotech.com/"; // amit amit@123#
    public static final String LOGIN = BASE_URL + "app/islogin";
    public static final String AREA_LIST = BASE_URL + "app/getareamaster";
    public static final String DOCTOR_LIST = BASE_URL + "app/getdoctormaster";

    public static final String EMPLOYEE_LIST = BASE_URL + "app/allemplists/";
    public static final String REPORT_DOCTOR_DESIG_LIST = BASE_URL + "app/allreportbyemp/";
    public static final String REPORT_CHEMIST_DESIG_LIST = BASE_URL + "app/allchemistreportemp/";
    public static final String GET_PRODUCTS = BASE_URL + "app/getproductmaster";
    public static final String GET_GIFTS = BASE_URL + "app/getgiftmaster";
    public static final String SUBMIT_CHEMIST_DCR = BASE_URL + "app/addchemistreport/";
    public static final String CHEMIST_LIST = BASE_URL + "app/getchemistmaster/";
    public static final String SUBMIT_REPORTS = BASE_URL + "app/addtour/";
    public static final String TOUR_REPORT = BASE_URL + "app/gettourreport/";
    public static final String ADD_EXPENSE = BASE_URL + "app/addexpense/";
    public static final String EXPENSE_REPORT = BASE_URL + "app/getexpensereport/";
    public static final String DASHBOARD = BASE_URL + "app/dashboard/";
    public static final String GET_CHEMIST_DCR = BASE_URL + "app/allchemistreport/";
    public static final String ADD_DOCTOR = BASE_URL + "app/adddoctor/";
    public static final String ADD_PLAN = BASE_URL + "app/addplan/";
    public static final String ADD_INATTENDANCE = BASE_URL + "app/Inattendance/";
    public static final String ADD_OUTATTENDANCE = BASE_URL + "app/Outattendance/";
    public static final String ADD_AREA = BASE_URL + "app/addarea/";
    public static final String ADD_CHEMIST = BASE_URL + "app/addchemist/";
    public static final String CHANGE_PASSWORD = BASE_URL + "app/changesecuritycode/";
    public static final String MY_DOCTORS = BASE_URL + "app/mydoctor/";
    public static final String MY_VIEW_PLAN = BASE_URL + "app/allplanlists/";
    public static final String MY_DAILY_VIEW_PLAN = BASE_URL + "app/dailyactivity/";
    public static final String MY_CHEMISTS = BASE_URL + "app/mychemist/";
    public static final String DELETE_DOCTOR_REPORT = BASE_URL + "app/deleteDoctorDCR/";
    public static final String DELETE_CHEMIST_REPORT = BASE_URL + "app/deletechemistDCR/";
    public static final String DELETE_CHEMIST = BASE_URL + "app/deletechemist/";
    public static final String DELETE_DOCTOR = BASE_URL + "app/deletedoctor/";
    public static final String DELETE_PLAN = BASE_URL + "app/deleteplan/";


    public static String SUBMIT_DCR = BASE_URL + "app/addreport";
    public static String GET_DCR_REPORT = BASE_URL + "app/allreport";
    public static String LEAVE_APPLICATION = BASE_URL + "app/leaveappication/";
    public static String SEND_LOCATION = BASE_URL + "app/addlocation/";

    public static String GET_CHECKIN = BASE_URL + "app/getCheckin";
    public static String SEND_CHECKIN = BASE_URL + "app/checkin";
    public static String SEND_CHECKOUT = BASE_URL + "app/checkout";
    public static String GET_CHEMIST_MISSED_REPORTS = BASE_URL + "app/getchemistmiss";
    public static String GET_DOCTOR_MISSED_REPORTS = BASE_URL + "app/getdoctormiss";


}