package org.motechproject.ebodac.client;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EbodacFtpsClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacFtpsClient.class);

    private static final Integer CONNECT_TIMEOUT = 10000;

    private FTPSClient ftp = new FTPSClient(false);

    public void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                LOGGER.error("IOException occurred while disconnecting: ", ioe);
            }
        }
    }

    public void connect(String hostname, Integer port, String username, String password) throws FtpException {
        try {
            ftp.setConnectTimeout(CONNECT_TIMEOUT);
            ftp.connect(hostname, port);

            Integer reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                String replyString = ftp.getReplyString();
                disconnect();
                throw new FtpException("Could not connect: " + replyString);
            }
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                String replyString = ftp.getReplyString();
                disconnect();
                throw new FtpException("Could not authenticate: " + replyString);
            }
            ftp.execPROT("P");
            ftp.execPBSZ(0);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            disconnect();
            throw new FtpException("IOException occurred while connecting: " + e.getMessage(), e);
        }
        ftp.enterLocalPassiveMode();
    }

    public List<String> listFiles(String directory) throws FtpException {
        if (ftp.isConnected()) {
            try {
                List<String> filenames = new ArrayList<String>();
                for (FTPFile f : ftp.listFiles(directory)) {
                    filenames.add(f.getName());
                }
                if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    String replyString = ftp.getReplyString();
                    throw new FtpException("Could not list files: " + replyString);
                }
                return filenames;
            } catch (IOException e) {
                throw new FtpException("Could not list files: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public void fetchFile(String location, OutputStream output) throws FtpException {
        if (ftp.isConnected() && output != null) {
            try {
                ftp.retrieveFile(location, output);
                output.close();
                if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    String replyString = ftp.getReplyString();
                    output.close();
                    throw new FtpException("Could not fetch file: " + replyString);
                }
            } catch (IOException e) {
                throw new FtpException("Could not fetch file: " + e.getMessage(), e);
            }
        }
    }

    public void sendFile(String path, InputStream is) throws FtpException, IOException {
        if (ftp.isConnected()) {
            ftp.storeFile(path, is);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                String replyString = ftp.getReplyString();
                throw new FtpException("Could not send file: " + replyString);
            }
        }
    }

    public FTPSClient getFtp() {
        return ftp;
    }

    public void setFtp(FTPSClient ftp) {
        this.ftp = ftp;
    }
}
