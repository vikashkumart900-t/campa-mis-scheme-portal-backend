package com.campa.mis.dto.response;

public record DocumentUploadResponse(
	    String fileName,
	    String filePath,
	    String message
	) {}