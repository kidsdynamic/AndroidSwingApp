package com.kidsdynamic.swing.androidswingapp;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 和backend的JSON定義及轉換
 * 參考API的方式
 * 以/user/login為例:
 *  ServerGson.user.login
 *  產生上傳JSON -> ServerGson.user.login.toJson
 *  分析接收JSON -> ServerGson.user.login.fromJson
 *
 * 其它的API依此類推，而若該API無上傳JSON或無回的JSON，則無對應之method.
 */

public class ServerGson {

    static final class kidData {
        int id;
        String name;
        String dateCreated;
        String macId;
        String profile;
        userData parent;
    }

    static final class userData {
        int id;
        String email;
        String firstName;
        String lastName;
        String lastUpdate;
        String dateCreated;
        String zipCode;
        String phoneNumber;
        String profile;
    }

    static final class activityData {
        int id;
        String macId;
        String kidId;
        String type;
        int steps;
        String receivedDate;
    }

    static final class eventData {
        int id;
        userData user;
        List<kidData> kid;
        String name;
        String startDate;
        String endDate;
        String color;
        String status;
        String description;
        int alert;
        String repeat;
        int timezoneOffset;
        String dateCreated;
        String lastUpdated;
        List<todoData> todo;
    }

    static final class todoData {
        int id;
        String text;
        String status;
        String dateCreated;
        String lastUpdated;
    }

    static final class requestUser {
        int id;
        String email;
        String firstName;
        String lastName;
        String lastUpdate;
        String dateCreated;
        String zipCode;
        String phoneNumber;
        String profile;
        String registrationId;
    }

    static final class hostData {
        int id;
        requestUser requestFromUser;
        requestUser requestToUser;
        String status;
        String createdDate;
        String lastUpdated;
        List<kidData> kids;
    }

    public static class user {

        public static class login {
            // 參考backend的定義，此階層為/user/login
            private static class c {
                String email;
                String password;

                c(String pMail, String pPassword) {
                    email = pMail;
                    password = pPassword;
                }
            }

            static final class response {
                String access_token;
                String username;
            }

            public static String toJson(String pMail, String pPassword) {
                return new Gson().toJson(new c(pMail, pPassword));
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class register {
            private static class c {
                String email;
                String password;
                String firstName;
                String lastName;
                String phoneNumber;
                String zipCode;

                c(String pMail, String pPassword, String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
                    email = pMail;
                    password = pPassword;
                    firstName = pFirstName;
                    lastName = pLastName;
                    phoneNumber = pPhoneNumber;
                    zipCode = pZipcode;
                }
            }

            public static String toJson(String pMail, String pPassword, String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
                return new Gson().toJson(new c(pMail, pPassword, pFirstName, pLastName, pPhoneNumber, pZipcode));
            }
        }

        public static class isTokenValid {
            private static class c {
                String email;
                String token;

                c(String pMail, String pToken) {
                    email = pMail;
                    token = pToken;
                }
            }

            public static String toJson(String pMail, String pToken) {
                return new Gson().toJson(new c(pMail, pToken));
            }
        }

        public static class updateProfile {
            private static class c {
                String firstName;
                String lastName;
                String phoneNumber;
                String zipCode;

                c(String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
                    firstName = pFirstName;
                    lastName = pLastName;
                    phoneNumber = pPhoneNumber;
                    zipCode = pZipcode;
                }
            }

            public static String toJson(String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
                return new Gson().toJson(new c(pFirstName, pLastName, pPhoneNumber, pZipcode));
            }

            public static userData fromJson(String json) {
                return new Gson().fromJson(json, userData.class);
            }
        }

        public static class retrieveUserProfile {
            static final class response {
                List<kidData> kids;
                userData user;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class findByEmail {
            public static userData fromJson(String json) {
                return new Gson().fromJson(json, userData.class);
            }
        }

        public static class avatar {
            public static class upload {
                static final class response {
                    userData user;
                }

                public static response fromJson(String json) {
                    return new Gson().fromJson(json, response.class);
                }
            }

            public static class uploadKid {
                static final class response {
                    kidData kid;
                }

                public static response fromJson(String json) {
                    return new Gson().fromJson(json, response.class);
                }
            }
        }
    }

    public static class kids {
        public static class add {
            private static class c {
                String name;
                String macId;

                c(String pName, String pMacId) {
                    name = pName;
                    macId = pMacId;
                }
            }

            public static String toJson(String pName, String pMacId) {
                return new Gson().toJson(new c(pName, pMacId));
            }

            public static kidData fromJson(String json) {
                return new Gson().fromJson(json, kidData.class);
            }
        }

        public static class update {
            private static class c {
                int kidId;
                String name;

                c(int pKidId, String pName) {
                    kidId = pKidId;
                    name = pName;
                }
            }

            public static String toJson(int pKidId, String pName) {
                return new Gson().toJson(new c(pKidId, pName));
            }

            static final class response {
                kidData kid;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class whoRegisteredMacID {
            static final class response {
                kidData kid;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }
    }

    public static class activity {
        public static class uploadRawData {
            private static class c {
                String indoorActivity;
                String outdoorActivity;
                int time;
                String macId;
                int timeZoneOffset;

                c(String pIndoorActivity, String pOutdoorActivity, int pTime, String pMacId) {
                    indoorActivity = pIndoorActivity;
                    outdoorActivity = pOutdoorActivity;
                    time = pTime;
                    macId = pMacId;
                    Calendar now = Calendar.getInstance();
                    int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
                    timeZoneOffset = offset / 60 / 1000;
                }
            }

            public static String toJson(String pIndoorActivity, String pOutdoorActivity, int pTime, String pMacId) {
                return new Gson().toJson(new c(pIndoorActivity, pOutdoorActivity, pTime, pMacId));
            }
        }

        public static class retrieveData {
            static final class response {
                List<activityData> activities;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class retrieveDataByTime {
            static final class response {
                List<activityData> activities;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }
    }

    public static class event {
        public static class add {
            private static class c {
                List<Integer> kidId;
                String name;
                String startDate;
                String endDate;
                String color;
                String description;
                int alert;
                String repeat;
                int timezoneOffset;
                List<String> todo;

                c(List<Integer> pKidId, String pName, String pStartDate, String pEndDate,
                  String pColor, String pDescription, int pAlert, String pRepeat,
                  int pTimezoneOffset, List<String> pTodo) {
                    kidId = pKidId;
                    name = pName;
                    startDate = pStartDate;
                    endDate = pEndDate;
                    color = pColor;
                    description = pDescription;
                    alert = pAlert;
                    repeat = pRepeat;
                    timezoneOffset = pTimezoneOffset;
                    todo = pTodo;
                }
            }

            public static String toJson(List<Integer> pKidId, String pName, String pStartDate, String pEndDate,
                                        String pColor, String pDescription, int pAlert, String pRepeat,
                                        int pTimezoneOffset, List<String> pTodo) {
                return new Gson().toJson(new c(pKidId, pName, pStartDate, pEndDate, pColor,
                        pDescription, pAlert, pRepeat, pTimezoneOffset, pTodo));
            }

            static final class response {
                eventData event;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class update {
            private static class c {
                int eventId;
                String name;
                String startDate;
                String endDate;
                String color;
                String description;
                int alert;
                String repeat;
                int timezoneOffset;
                List<String> todo;

                c(int pEventId, String pName, String pStartDate, String pEndDate, String pColor,
                  String pDescription, int pAlert, String pRepeat,
                  int pTimezoneOffset, List<String> pTodo) {
                    eventId = pEventId;
                    name = pName;
                    startDate = pStartDate;
                    endDate = pEndDate;
                    color = pColor;
                    description = pDescription;
                    alert = pAlert;
                    repeat = pRepeat;
                    timezoneOffset = pTimezoneOffset;
                    todo = pTodo;
                }
            }

            public static String toJson(int pEventId, String pName, String pStartDate, String pEndDate,
                                        String pColor, String pDescription, int pAlert, String pRepeat,
                                        int pTimezoneOffset, List<String> pTodo) {
                return new Gson().toJson(new c(pEventId, pName, pStartDate, pEndDate, pColor,
                        pDescription, pAlert, pRepeat, pTimezoneOffset, pTodo));
            }

            static final class response {
                eventData event;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class retrieveEvents {
            static final class response {
                List<eventData> event;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class retrieveAllEventsWithTodo {

            public static List<eventData> fromJson(String json) {
                eventData[] data = new Gson().fromJson(json, eventData[].class);
                return data == null ? null : new ArrayList<>(Arrays.asList(data));
            }
        }

        public static class retrieveAllEventsById {

            public static List<eventData> fromJson(String json) {
                eventData[] data = new Gson().fromJson(json, eventData[].class);
                return new ArrayList<>(Arrays.asList(data));
            }
        }

        public static class todo {
            public static class done {
                private static class c {
                    int eventId;
                    int todoId;

                    c(int pEventId, int pTodoId) {
                        eventId = pEventId;
                        todoId = pTodoId;
                    }
                }

                public static String toJson(int pEventId, int pTodoId) {
                    return new Gson().toJson(new c(pEventId, pTodoId));
                }

            }
        }
    }

    public static class subHost {
        public static class add {
            private static class c {
                int hostId;

                c(int pHostId) {
                    hostId = pHostId;
                }
            }

            public static String toJson(int pHostId) {
                return new Gson().toJson(new c(pHostId));
            }

            public static hostData fromJson(String json) {
                return new Gson().fromJson(json, hostData.class);
            }
        }

        public static class accept {
            private static class c {
                int subHostId;
                List<Integer> KidId;

                c(int pSubHostId, List<Integer> pKidId) {
                    subHostId = pSubHostId;
                    KidId = pKidId;
                }
            }

            public static String toJson(int pHostId, List<Integer> pKidId) {
                return new Gson().toJson(new c(pHostId, pKidId));
            }

            public static hostData fromJson(String json) {
                return new Gson().fromJson(json, hostData.class);
            }
        }

        public static class deny {
            private static class c {
                int subHostId;

                c(int pSubHostId) {
                    subHostId = pSubHostId;
                }
            }

            public static String toJson(int pSubHostId) {
                return new Gson().toJson(new c(pSubHostId));
            }

            public static hostData fromJson(String json) {
                return new Gson().fromJson(json, hostData.class);
            }
        }

        public static class list {
            static final class response {
                List<hostData> requestFrom;
                List<hostData> requestTo;
            }

            public static response fromJson(String json) {
                return new Gson().fromJson(json, response.class);
            }
        }

        public static class removeKid {
            private static class c {
                int subHostId;
                int kidId;

                c(int pSubHostId, int pKidId) {
                    subHostId = pSubHostId;
                    kidId = pKidId;
                }
            }

            public static String toJson(int pSubHostId, int pKidId) {
                return new Gson().toJson(new c(pSubHostId, pKidId));
            }

            public static hostData fromJson(String json) {
                return new Gson().fromJson(json, hostData.class);
            }
        }
    }
}
