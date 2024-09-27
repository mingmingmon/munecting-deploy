package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

public class EntityNotFoundException extends GeneralException{

    public EntityNotFoundException() {
        super(Status.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(Status errorStatus) {
        super(errorStatus);
    }
}
