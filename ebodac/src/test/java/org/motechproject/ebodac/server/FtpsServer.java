package org.motechproject.ebodac.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;
import java.net.ServerSocket;

public class FtpsServer {

    private FtpServer server;
    private Integer port;

    public void start() throws Exception {
        ServerSocket s = new ServerSocket(0);
        port = s.getLocalPort();
        s.close();

        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setServerAddress("localhost");
        factory.setPort(port);

        SslConfigurationFactory ssl = new SslConfigurationFactory();

        ssl.setKeystoreFile(new File("src/test/resources/keystore.jks"));
        ssl.setKeystorePassword("password");

        factory.setSslConfiguration(ssl.createSslConfiguration());
        factory.setImplicitSsl(false);

        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("src/test/resources/ftpusers.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());

        server = serverFactory.createServer();
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public Integer getPort() {
        return port;
    }

}
