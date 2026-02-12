package com.instantsolutions.larimarpharma.exceptions;


import com.instantsolutions.larimarpharma.DTOs.UserIdentityDto;
import lombok.Getter;
import lombok.Setter;

public class PortalLockedException extends RuntimeException {

    private final UserIdentityDto userIdentity;

    public PortalLockedException(String message, UserIdentityDto userIdentity) {
        super(message);
        this.userIdentity = userIdentity;
    }

    public UserIdentityDto getUserIdentity() {
        return userIdentity;
    }
}
