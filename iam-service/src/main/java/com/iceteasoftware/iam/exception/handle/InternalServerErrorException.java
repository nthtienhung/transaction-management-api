package com.iceteasoftware.iam.exception.handle;

import com.iceteasoftware.common.message.Labels;
import com.iceteasoftware.iam.constant.ApiConstants;
import com.iceteasoftware.iam.enums.MessageCode;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InternalServerErrorException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    private final String errorKey;

    private final Object[] errorParams;

    public InternalServerErrorException(MessageCode errorCode) {
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
