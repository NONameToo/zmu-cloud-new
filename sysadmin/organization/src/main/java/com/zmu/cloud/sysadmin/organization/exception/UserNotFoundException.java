package com.zmu.cloud.sysadmin.organization.exception;

import com.zmu.cloud.common.core.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(OrganizationErrorType.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(OrganizationErrorType.USER_NOT_FOUND, message);
    }
}
