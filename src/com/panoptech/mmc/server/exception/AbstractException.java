package com.panoptech.mmc.server.exception;

public class AbstractException extends Exception {
	private static final long serialVersionUID = 1L;
	private String messageKey = "";

	public AbstractException() {
	}

	public AbstractException(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return this.messageKey;
	}
}
