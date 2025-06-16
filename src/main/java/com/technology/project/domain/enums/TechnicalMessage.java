package com.technology.project.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    INVALID_MESSAGE_ID("404", "Invalid Message ID, please verify", "messageId"),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),
    TECHNOLOGY_CREATED("201", "Technology created successfully", ""),
    ADAPTER_RESPONSE_NOT_FOUND("404-0", "invalid Technology, please verify", ""),
    TECHNOLOGY_ALREADY_EXISTS("400","The Technology already found register." ,"" ),
    NAME_TOO_LONG("404-1", "The name must not exceed the 50 characters", ""),
    DESCRIPTION_TOO_LONG("404-2", "The Description must not exceed the 90 characters", ""),
    DUPLICATE_NAMES_TECHNOLOGIES("404-3", "The names of the technologies cannot be the same",""),
    TECHNOLOGY_THREE_ASSOCIATION("404-4","A capability must be associated with at least 3 technologies" ,"" ),
    TECHNOLOGY_TWENTY_ASSOCIATION("404-5","A capability cannot have more than 20 associated technologies" ,"" ),
    TECHNOLOGY_NOT_EXISTS("400"," Some of the technologies are not registered." ,"" ),
    TECHNOLOGY_TECH_ALREADY_ASSOCIATED("404-6", "The technology or technologies are already associated with this capacity",""),
    TECHNOLOGY_CAPABILITY_CREATED("201-1", "Relations created successfully", ""),
    CAPABILITIES_NOT_EXISTS("400"," Some of the Capabilities are not registered or not have technologies registered." ,"" ),
    TECHNOLOGIES_FOUND("200","technologies found",""),
    TECHNOLOGIES_DUPLICATES_IDS("400-7","Check the input data, it is trying to save the same technologies",""),
    CAPABILITIES_TECHNOLOGY_FOUND("200","Tecnologies by Capabilities found",""),
    CAPABILITIES_TECHNOLOGIES_NOT_EXISTS("400-8"," The technologies not have or not found Capabilities associated." ,"" ),
    CAPABILITIES_FOUND("200","Capabilities found",""),
    CAPABILITIES_TECHNOLOGIES_DELETE("200"," The technologies and Capabilities was delete successfully." ,"" ),
    CAPABILITIES_TECHNOLOGIES_MORE_ONE_RELATE("400-9","The technologies is found relate with others Capabilities" ,"" );

    private final String code;
    private final String message;
    private final String param;
}
