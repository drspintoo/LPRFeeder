package com.panoptech.mmc.server.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NotAuthorisedServerException extends AbstractException {
	private static final long serialVersionUID = 1L;
	private boolean logoutRequired = false;

	public NotAuthorisedServerException() {
	}

	public NotAuthorisedServerException(String message, boolean logoutRequired) {
		super(message);
		this.logoutRequired = logoutRequired;
	}

	public boolean isLogoutRequired() {
		return this.logoutRequired;
	}
}
