package com.mlcloud.fusioncloud.exception;

import com.mlcloud.common.code.CodeEnum;
import com.mlcloud.common.exception.BaseException;

/**
 * fusioncloud的内部异常
 * @author ：hf
 * @date ：Created in 2020/12/3 1:57 下午
 * @modified By：
 * @version: $
 */
public abstract class BaseFusionCloudException extends BaseException {

    private final String message;

    protected BaseFusionCloudException(String message, CodeEnum code) {
        super(code);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
