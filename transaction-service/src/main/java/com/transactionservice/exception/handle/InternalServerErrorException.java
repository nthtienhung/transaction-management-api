package com.transactionservice.exception.handle;

import com.transactionservice.configuration.message.Labels;
import com.transactionservice.constant.ApiConstants;
import com.transactionservice.enums.MessageCode;
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

    /**
     * Constructor với mã lỗi được chỉ định.
     *
     * @param errorCode Mã lỗi
     */
    public InternalServerErrorException(MessageCode errorCode) {
        this(Labels.getLabels(errorCode.getKey()), errorCode.name(), errorCode.getKey());
    }

    /**
     * Constructor với thông điệp mặc định, mã lỗi và khóa lỗi được chỉ định.
     *
     * @param defaultMessage Thông điệp mặc định
     * @param errorCode Mã lỗi
     * @param errorKey Khóa lỗi
     */
    public InternalServerErrorException(String defaultMessage, String errorCode, String errorKey) {
        this(ApiConstants.ErrorType.DEFAULT_TYPE, defaultMessage, errorCode, errorKey, new Object[0]);
    }

    /**
     * Constructor với URI, thông điệp mặc định, mã lỗi, khóa lỗi và danh sách tham số lỗi được chỉ định.
     *
     * @param type URI của loại lỗi
     * @param defaultMessage Thông điệp mặc định
     * @param errorCode Mã lỗi
     * @param errorKey Khóa lỗi
     * @param errorParams Danh sách tham số lỗi
     */
    public InternalServerErrorException(URI type, String defaultMessage, String errorCode, String errorKey,
                                        Object[] errorParams) {
        super(type, defaultMessage, Status.INTERNAL_SERVER_ERROR, null, null, null,
                getAlertParameters(defaultMessage, errorCode, errorKey, errorParams));

        this.errorKey = errorKey;
        this.errorParams = errorParams;
    }

    /**
     * Phương thức tạo bản đồ tham số cảnh báo.
     *
     * @param message Thông điệp
     * @param errorCode Mã lỗi
     * @param errorKey Khóa lỗi
     * @param errorParams Danh sách tham số lỗi
     * @return Bản đồ tham số cảnh báo
     */
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
