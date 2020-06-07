package chadolbagi.ssamjang.util;

import java.lang.reflect.Field;

public class APIException extends Exception {
    private static final long serialVersionUID = -2786794021354326205L;

    public static final int NOT_MODIFIED_CODE = 304;
    public static final String NOT_MODIFIED_DESC = "Not Modified";

    public static final int BAD_INPUT_PARAMETER_CODE = 400;
    public static final String BAD_INPUT_PARAMETER_DESC = "Bad input parameter.";

    public static final int UNAUTHORIZED = 401;
    public static final String UNAUTHORIZED_DESC = "Access denied";

    public static final int BAD_PATH_CODE = 404;
    public static final String BAD_PATH_DESC = "Object not found at the specified path.";

    public static final int BAD_REQUEST_METHOD_CODE = 405;
    public static final String BAD_REQUEST_METHOD_DESC = "Request method not expected.";

    public static final int BAD_AUTH_REQUEST_CODE = 406;
    public static final String BAD_AUTH_REQUEST_DESC = "Bad Auth request.";

    public static final int BAD_CONFLICT_CODE = 409;
    public static final String BAD_CONFLICT_DESC = "Object conflict.";

    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String INTERNAL_SERVER_ERROR_DESC = "internal server error";

    public static final int TOO_MANY_REQUEST_CODE = 503;
    public static final String TOO_MANY_REQUEST_DESC = "Your app is making too many requests.";

    public static final int OVER_QUOTA_CODE = 507;
    public static final String OVER_QUOTA_DESC = "User is over MOSP storage quota.";


    private int code;

    public APIException(String desc) {
        super(desc);

        this.code = INTERNAL_SERVER_ERROR_CODE;

        try {
            for (Field field : APIException.class.getDeclaredFields()) {
                if (field.getName().endsWith("_DESC") && ((String)field.get(null)).equals(desc)) {
                    this.code = (Integer)(APIException.class.getDeclaredField(field.getName().substring(0, field.getName().length() - 5) + "_CODE")).get(null);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
    }

    public APIException(int code, String desc) {
        super(desc);

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}