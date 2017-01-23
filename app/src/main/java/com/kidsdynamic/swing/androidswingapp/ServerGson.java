package com.kidsdynamic.swing.androidswingapp;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weichigio on 2017/1/23.
 */

public class ServerGson {
    private static class userLogin {
        String email;
        String password;
        userLogin(String pMail, String pPassword) {
            email = pMail;
            password = pPassword;
        }
    }
    public static String getUserLogin(String pMail, String pPassword) {
        return new Gson().toJson(new userLogin(pMail, pPassword));
    }

    private static class userIsTokenValid {
        String email;
        String token;
        userIsTokenValid(String pMail, String pToken) {
            email = pMail;
            token = pToken;
        }
    }
    public static String getUserIsTokenValid(String pMail, String pToken) {
        return new Gson().toJson(new userIsTokenValid(pMail, pToken));
    }

    private static class userRegister {
        String email;
        String password;
        String firstName;
        String lastName;
        String phoneNumber;
        String zipCode;
        userRegister(String pMail, String pPassword, String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
            email = pMail;
            password = pPassword;
            firstName = pFirstName;
            lastName = pLastName;
            phoneNumber = pPhoneNumber;
            zipCode = pZipcode;
        }
    }

    public static String getUserRegister(String pMail, String pPassword, String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
        return new Gson().toJson(new userRegister(pMail, pPassword, pFirstName, pLastName, pPhoneNumber, pZipcode));
    }

    private static class userUpdateProfile {
        String firstName;
        String lastName;
        String phoneNumber;
        String zipCode;
        userUpdateProfile(String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
            firstName = pFirstName;
            lastName = pLastName;
            phoneNumber = pPhoneNumber;
            zipCode = pZipcode;
        }
    }
    public static String getUserUpdateProfile(String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
        return new Gson().toJson(new userUpdateProfile(pFirstName, pLastName, pPhoneNumber, pZipcode));
    }

    private static class kidsAdd {
        String firstName;
        String lastName;
        String macId;
        kidsAdd(String pFirstName, String pLastName, String pMacId) {
            firstName = pFirstName;
            lastName = pLastName;
            macId = pMacId;
        }
    }
    public static String getKidsAdd(String pFirstName, String pLastName, String pMacId) {
        return new Gson().toJson(new kidsAdd(pFirstName, pLastName, pMacId));
    }

    private static class kidsUpdate {
        String kidId;
        String firstName;
        String lastName;
        kidsUpdate(String pKidId, String pFirstName, String pLastName) {
            kidId = pKidId;
            firstName = pFirstName;
            lastName = pLastName;
        }
    }
    public static String getKidsUpdate(String pKidId, String pFirstName, String pLastName) {
        return new Gson().toJson(new kidsUpdate(pKidId, pFirstName, pLastName));
    }

    private static class activityUploadRawData {
        String indoorActivity;
        String outdoorActivity;
        String time;
        String macId;
        activityUploadRawData(String pIndoorActivity, String pOutdoorActivity, String pTime, String pMacId) {
            indoorActivity = pIndoorActivity;
            outdoorActivity = pOutdoorActivity;
            time = pTime;
            macId = pMacId;
        }
    }
    public static String getActivityUploadRawData(String pIndoorActivity, String pOutdoorActivity, String pTime, String pMacId) {
        return new Gson().toJson(new activityUploadRawData(pIndoorActivity, pOutdoorActivity, pTime, pMacId));
    }

    private static class eventAdd {
        int kidId;
        String name;
        String startDate;
        String endDate;
        String color;
        String description;
        int alert;
        String city;
        String state;
        String repeat;
        int timezoneOffset;
        List<String> todo;
        eventAdd(int pKidId, String pName, String pStartDate, String pEndDate,
                 String pColor, String pDescription, int pAlert, String pCity, String pState, String pRepeat,
                 int pTimezoneOffset, List<String> pTodo) {
            kidId = pKidId;
            name = pName;
            startDate = pStartDate;
            endDate = pEndDate;
            color = pColor;
            description = pDescription;
            alert = pAlert;
            city = pCity;
            state = pState;
            repeat = pRepeat;
            timezoneOffset = pTimezoneOffset;
            todo = pTodo;
        }
    }
    public static String getEventAdd(int pKidId, String pName, String pStartDate, String pEndDate,
                                     String pColor, String pDescription, int pAlert, String pCity, String pState, String pRepeat,
                                     int pTimezoneOffset, List<String> pTodo) {
        return new Gson().toJson(new eventAdd(pKidId, pName, pStartDate, pEndDate,
                pColor, pDescription, pAlert, pCity, pState, pRepeat, pTimezoneOffset, pTodo));
    }

    private static class eventUpdate {
        int eventId;
        String name;
        String startDate;
        String endDate;
        String color;
        String description;
        int alert;
        String city;
        String state;
        String repeat;
        int timezoneOffset;
        List<String> todo;
        eventUpdate(int pEventId, String pName, String pStartDate, String pEndDate,
                 String pColor, String pDescription, int pAlert, String pCity, String pState, String pRepeat,
                 int pTimezoneOffset, List<String> pTodo) {
            eventId = pEventId;
            name = pName;
            startDate = pStartDate;
            endDate = pEndDate;
            color = pColor;
            description = pDescription;
            alert = pAlert;
            city = pCity;
            state = pState;
            repeat = pRepeat;
            timezoneOffset = pTimezoneOffset;
            todo = pTodo;
        }
    }
    public static String getEventUpdate(int pEventId, String pName, String pStartDate, String pEndDate,
                                     String pColor, String pDescription, int pAlert, String pCity, String pState, String pRepeat,
                                     int pTimezoneOffset, List<String> pTodo) {
        return new Gson().toJson(new eventUpdate(pEventId, pName, pStartDate, pEndDate,
                pColor, pDescription, pAlert, pCity, pState, pRepeat, pTimezoneOffset, pTodo));
    }

    private static class subHostAdd {
        int hostId;
        subHostAdd(int pHostId) {
            hostId = pHostId;
        }
    }
    public static String getSubHostAdd(int pHostId) {
        return new Gson().toJson(new subHostAdd(pHostId));
    }

    private static class subHostAccept {
        int subHostId;
        List<Integer> KidId;
        subHostAccept(int pSubHostId, List<Integer> pKidId) {
            subHostId = pSubHostId;
            KidId = pKidId;
        }
    }
    public static String getSubHostAccept(int pHostId, List<Integer> pKidId) {
        return new Gson().toJson(new subHostAccept(pHostId, pKidId));
    }

    private static class subHostDeny {
        int subHostId;
        subHostDeny(int pSubHostId) {
            subHostId = pSubHostId;
        }
    }
    public static String getSubHostDeny(int pSubHostId) {
        return new Gson().toJson(new subHostDeny(pSubHostId));
    }

}
