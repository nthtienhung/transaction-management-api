package com.example.iamservice.exception.handler;

import com.example.iamservice.configuration.message.Labels;
import com.example.iamservice.constant.ApiConstants;
import com.example.iamservice.enums.ErrorCode;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class InternalServerErrorException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    private final String errorKey;

    private final Object[] errorParams;

    public InternalServerErrorException(ErrorCode errorCode) {
        this(Labels.getLabels(errorCode.getKey()), errorCode.name(), errorCode.getKey());
    }

    public InternalServerErrorException(String defaultMessage, String errorCode, String errorKey) {
        this(ApiConstants.ErrorType.DEFAULT_TYPE, defaultMessage, errorCode, errorKey, new Object[0]);
    }

    public InternalServerErrorException(URI type, String defaultMessage, String errorCode, String errorKey,
                                        Object[] errorParams) {
        super(type, defaultMessage, Status.INTERNAL_SERVER_ERROR, null, null, null,
                getAlertParameters(defaultMessage, errorCode, errorKey, errorParams));

        this.errorKey = errorKey;
        this.errorParams = errorParams;
    }

    private static Map<String, Object> getAlertParameters(String message, String errorCode, String errorKey,
                                                          Object[] errorParams) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put(ApiConstants.ErrorKey.MESSAGE, message);
        parameters.put(ApiConstants.ErrorKey.ERROR_CODE, errorCode);
        parameters.put(ApiConstants.ErrorKey.ERROR_KEY, errorKey);
        parameters.put(ApiConstants.ErrorKey.PARAMS, Arrays.asList(errorParams));

        return parameters;
    }

}
