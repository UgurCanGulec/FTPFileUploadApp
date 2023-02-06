package com.gulecugurcan.ftpintegrationwebapp.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.io.File;

/**
 * @author: UGUR CAN GULEC
 * @since: 6.02.2023
 */
@MessagingGateway
public interface UploadMessagingGateway {

    @Gateway(requestChannel = "uploadFile")
    void uploadFile(File file);
}
