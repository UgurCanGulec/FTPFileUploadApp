package com.gulecugurcan.ftpintegrationwebapp.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author: UGUR CAN GULEC
 * @since: 6.02.2023
 */

@Configuration
@Slf4j
public class SFTPSyncConfiguration {

    public DefaultSftpSessionFactory getDefaultSftpSession() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost("0.0.0.0");
        factory.setPort(22);
        factory.setAllowUnknownKeys(true);
        factory.setUser("reborn_user");
        factory.setPassword("password");
        return factory;
    }

    @Bean(name = "defaultSynchronizer")
    public SftpInboundFileSynchronizer synchronizer() {
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(this.getDefaultSftpSession());
        synchronizer.setDeleteRemoteFiles(true);
        synchronizer.setRemoteDirectory("/ftp/mto");
        synchronizer.setFilter(new SftpSimplePatternFileListFilter("*.xml"));
        return synchronizer;
    }

    @Bean(name = "SftpMessageSource")
    @InboundChannelAdapter(channel = "fileuploaded", poller = @Poller(fixedDelay = "3000"))
    public MessageSource<File> sftpMessageSource() {
        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(this.synchronizer());
        source.setLocalDirectory(new File("tmp/incoming"));
        source.setAutoCreateLocalDirectory(true);
        source.setMaxFetchSize(1);
        return source;
    }

    @ServiceActivator(inputChannel = "fileuploaded")
    public void handleIncomingFile(File file) throws IOException {
        log.info(String.format("handleIncomingFile BEGIN %s", file.getName()));
        String content = FileUtils.readFileToString(file, "UTF-8");
        log.info(String.format("Content: %s", content));
        log.info(String.format("handleIncomingFile END %s", file.getName()));
    }
}
