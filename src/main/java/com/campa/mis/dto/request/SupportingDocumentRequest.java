package com.campa.mis.dto.request;

public record SupportingDocumentRequest(

    String documentType,  // KML or IMAGE

    String description

) {}