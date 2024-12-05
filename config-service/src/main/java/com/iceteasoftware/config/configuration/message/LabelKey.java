package com.iceteasoftware.config.configuration.message;

public interface LabelKey {

    public static final String SUCCESS_CREATE_CONFIG = "success.create-config";
    public static final String SUCCESS_UPDATE_CONFIG = "success.update-config";
    public static final String SUCCESS_GET_CONFIG = "success.get-config";
    public static final String SUCCESS_SOFT_DELETE_CONFIG = "success.soft-delete-config";

    public static final String ERROR_GROUP_REQUIRED = "error.group-required";
    public static final String ERROR_TYPE_REQUIRED = "error.type-required";
    public static final String ERROR_KEY_REQUIRED = "error.key-required";
    public static final String ERROR_VALUE_REQUIRED = "error.value-required";
    public static final String ERROR_UPDATE_DB = "error.update-database-failed";
    public static final String ERROR_INSERT_DB = "error.insert-database-failed";
    public static final String ERROR_CONFIG_NOT_FOUND = "error.configuration-not-found";
    public static final String ERROR_QUERY_DB = "error.query-database-failed";
    public static final String ERROR_CONFIG_ID_NOT_FOUND = "error.configuration-id-not-found";
    public static final String ERROR_DELETE_DB = "error.delete-database-failed";
    public static final String ERROR_EXIST_CONFIG = "error.exist-configuration";

}
