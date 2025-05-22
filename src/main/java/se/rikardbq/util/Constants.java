package se.rikardbq.util;

public class Constants {
    public static class Controller {
        public static class Path {
            public static final String AUTHENCTICATE = "/authenticate";
            public static final String AUTHORIZE = "/authorize";
            public static final String LOGIN = "/login";
        }

        public static class Header {
            public static final String AUTHORIZATION = "authorization";
            public static final String ORIGIN = "origin";
            public static final String X_API_KEY = "x-api-key";
        }
    }

    public static class Token {
        public static final String ISSUER_ID = "FFBE-X-FFFE";

        public static class Claim {
            public static final String X_UNAME = "x-uname";
            public static final String X_AID = "x-aid";

        }
    }
}
