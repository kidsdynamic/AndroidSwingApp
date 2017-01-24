package com.kidsdynamic.swing.androidswingapp;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by weichigio on 2017/1/23.
 */

public class ServerGson {

    public static class error {
        static final class e1 {
            String message;
            String error;
        }

        public static e1 E1FromJson(String json) {
            return new Gson().fromJson(json, e1.class);
        }
    }

    static final class kidData {
        int id;
        String firstName;
        String lastName;
        String dateCreated;
        String macId;
        String profile;
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
        int distance;
        String receivedDate;
    }

    static final class eventData {
        int id;
        int userId;
        int kidId;
        String name;
        String startDate;
        String endDate;
        String color;
        String status;
        String description;
        int alert;
        String city;
        String state;
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

    static final class hostData {
        int id;
        String macId;
        int requestFromID;
        int requestToID;
        String status;
        String createdDate;
        String lastUpdated;
        List<kidForHostData> kid;
    }

    static final class kidForHostData {
        int id;
        String firstName;
        String lastName;
        String dateCreated;
        String macId;
        String profile;
        int ParentID;
    }

    public static class user {

        public static class login {
            private static class c {
                String email;
                String password;

                c(String pMail, String pPassword) {
                    email = pMail;
                    password = pPassword;
                }
            }

            static final class r {
                String access_token;
                String username;
            }

            public static String toJson(String pMail, String pPassword) {
                return new Gson().toJson(new c(pMail, pPassword));
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
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


            static final class r {
                userData user;
            }

            public static String toJson(String pFirstName, String pLastName, String pPhoneNumber, String pZipcode) {
                return new Gson().toJson(new c(pFirstName, pLastName, pPhoneNumber, pZipcode));
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class retrieveUserProfile {
            static final class r {
                List<kidData> kids;
                userData user;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class avatar {
            public static class upload {
                static final class r {
                    userData user;
                }

                public static r fromJson(String json) {
                    return new Gson().fromJson(json, r.class);
                }
            }

            public static class uploadKid {
                static final class r {
                    kidData kid;
                }

                public static r fromJson(String json) {
                    return new Gson().fromJson(json, r.class);
                }
            }
        }
    }

    public static class kids {
        public static class add {
            private static class c {
                String firstName;
                String lastName;
                String macId;

                c(String pFirstName, String pLastName, String pMacId) {
                    firstName = pFirstName;
                    lastName = pLastName;
                    macId = pMacId;
                }
            }

            public static String toJson(String pFirstName, String pLastName, String pMacId) {
                return new Gson().toJson(new c(pFirstName, pLastName, pMacId));
            }

            static final class r {
                List<kidData> kids;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class update {
            private static class c {
                String kidId;
                String firstName;
                String lastName;

                c(String pKidId, String pFirstName, String pLastName) {
                    kidId = pKidId;
                    firstName = pFirstName;
                    lastName = pLastName;
                }
            }

            public static String toJson(String pKidId, String pFirstName, String pLastName) {
                return new Gson().toJson(new c(pKidId, pFirstName, pLastName));
            }

            static final class r {
                kidData kid;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class whoRegisteredMacID {
            static final class r {
                kidData kid;
                userData user;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }
    }

    public static class activity {
        public static class uploadRawData {
            private static class c {
                String indoorActivity;
                String outdoorActivity;
                String time;
                String macId;

                c(String pIndoorActivity, String pOutdoorActivity, String pTime, String pMacId) {
                    indoorActivity = pIndoorActivity;
                    outdoorActivity = pOutdoorActivity;
                    time = pTime;
                    macId = pMacId;
                }
            }

            public static String toJson(String pIndoorActivity, String pOutdoorActivity, String pTime, String pMacId) {
                return new Gson().toJson(new c(pIndoorActivity, pOutdoorActivity, pTime, pMacId));
            }
        }

        public static class retrieveData {
            static final class r {
                List<activityData> activities;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class retrieveDataByTime {
            static final class r {
                List<activityData> activities;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }
    }

    public static class event {
        public static class add {
            private static class c {
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

                c(int pKidId, String pName, String pStartDate, String pEndDate,
                  String pColor, String pDescription, int pAlert, String pCity,
                  String pState, String pRepeat, int pTimezoneOffset, List<String> pTodo) {
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

            public static String toJson(int pKidId, String pName, String pStartDate, String pEndDate,
                                        String pColor, String pDescription, int pAlert, String pCity,
                                        String pState, String pRepeat, int pTimezoneOffset, List<String> pTodo) {
                return new Gson().toJson(new c(pKidId, pName, pStartDate, pEndDate, pColor,
                        pDescription, pAlert, pCity, pState, pRepeat, pTimezoneOffset, pTodo));
            }

            static final class r {
                eventData event;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
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
                String city;
                String state;
                String repeat;
                int timezoneOffset;
                List<String> todo;

                c(int pEventId, String pName, String pStartDate, String pEndDate, String pColor,
                  String pDescription, int pAlert, String pCity, String pState, String pRepeat,
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

            public static String toJson(int pEventId, String pName, String pStartDate, String pEndDate,
                                        String pColor, String pDescription, int pAlert, String pCity,
                                        String pState, String pRepeat, int pTimezoneOffset, List<String> pTodo) {
                return new Gson().toJson(new c(pEventId, pName, pStartDate, pEndDate, pColor,
                        pDescription, pAlert, pCity, pState, pRepeat, pTimezoneOffset, pTodo));
            }

            static final class r {
                eventData event;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class retrieveEvents {
            static final class r {
                List<eventData> events;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }

        public static class retrieveEventsWithTodo {
            static final class r {
                List<eventData> events;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
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
            static final class r {
                List<hostData> hosts;
            }

            public static r fromJson(String json) {
                return new Gson().fromJson(json, r.class);
            }
        }
    }
}
