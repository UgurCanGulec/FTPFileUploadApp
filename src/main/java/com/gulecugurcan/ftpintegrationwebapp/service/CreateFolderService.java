package com.gulecugurcan.ftpintegrationwebapp.service;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author: UGUR CAN GULEC
 * @since: 6.02.2023
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateFolderService {

    private final DefaultSftpSessionFactory sessionFactory;

    @PostConstruct
    public void createFolders(){
        createFolder("/","/ftp");
        createFolder("/ftp", "/ftp/mto");
        createFolder("/ftp", "/ftp/mtg");
    }


    @SneakyThrows
    private boolean doesFolderExist(String parent, String path){
        SftpSession session = sessionFactory.getSession();
        ChannelSftp.LsEntry[] list = session.list(parent);
        for (ChannelSftp.LsEntry lsEntry : list) {
            String filename = lsEntry.getFilename();
            log.info("Filename found: " + filename);
            if (path.contains(filename))
                return true;
        }
        return false;
    }

    private void createFolder(String parent, String path) {
        try {
            if (doesFolderExist(parent, path)) {
                log.info("The directory already exists: " + path);
                return;
            }
            sessionFactory.getSession().mkdir(path);

        } catch (IOException e) {
            log.warn("Directory prob. already exists", e);
        }
    }

}
