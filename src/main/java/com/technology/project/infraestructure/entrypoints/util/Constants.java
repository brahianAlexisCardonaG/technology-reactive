package com.technology.project.infraestructure.entrypoints.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String X_MESSAGE_ID = "x-message-id";
    public static final String TECHNOLOGY_ERROR = "Error on Technology - [ERROR]";

    public static final String PATH_POST_TECHNOLOGY
            = "/api/v1/technology";
    public static final String PATH_GET_TECHNOLOGIES_BY_IDS
            = "/api/v1/technology";
    public static final String PATH_POST_TECHNOLOGY_CAPABILITY
            = "/api/v1/technology-capability";
    public static final String PATH_GET_TECHNOLOGIES_BY_IDS_CAPABILITIES
            = "/api/v1/technology/by-capabilities-ids";
    public static final String PATH_DELETE_RELATE_CAPABILITIES_TECHNOLOGIES
            = "/api/v1/technology/capability/delete";
}
