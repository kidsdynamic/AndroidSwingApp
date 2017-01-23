package com.kidsdynamic.swing.androidswingapp;

import com.google.gson.Gson;

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


}
