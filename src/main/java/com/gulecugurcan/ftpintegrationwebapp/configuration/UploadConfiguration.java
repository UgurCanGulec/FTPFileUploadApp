package com.gulecugurcan.ftpintegrationwebapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

import java.time.LocalDateTime;

/**
 * @author: UGUR CAN GULEC
 * @since: 6.02.2023
 */

@Configuration
public class UploadConfiguration {


    @Bean
    public DefaultSftpSessionFactory getDefaultSftpSession() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost("0.0.0.0");
        factory.setPort(22);
        factory.setAllowUnknownKeys(true);
        factory.setUser("reborn_user");
        factory.setPassword("password");
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "uploadfile")
    MessageHandler uploadHandler(DefaultSftpSessionFactory factory){
        SftpMessageHandler messageHandler = new SftpMessageHandler(factory);
        messageHandler.setRemoteDirectoryExpression(new LiteralExpression("/ftp/mtg"));
        messageHandler.setFileNameGenerator(message -> String.format("mytextfile_%s.txt", LocalDateTime.now()));
        return messageHandler;
    }
}
