package com.assignment.mylogin.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ErrorResponse {

	
	private String errorCode;
	private String errorDesc;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date timestamp;
	private List errorDetail;
	
	
	public ErrorResponse() {
	}
			
	public ErrorResponse(String errorCode, String errorDesc, Date timestamp) {
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		this.timestamp = timestamp;
	}
	public ErrorResponse(String errorCode, String errorDesc, Date timestamp, List errorDetail) {
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		this.timestamp = timestamp;
		this.errorDetail = errorDetail;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(List errorDetail) {
		this.errorDetail = errorDetail;
	}
}
