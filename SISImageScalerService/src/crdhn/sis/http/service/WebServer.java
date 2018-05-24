package crdhn.sis.http.service;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crdhn.sis.configuration.Configuration;
import crdhn.sis.imagescaler.ImageWorker;
import java.io.File;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public void start() throws Exception {

        Server server = new Server();
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(100);
        threadPool.setMaxThreads(2000);
        server.setThreadPool(threadPool);
        logger.info("Listening on port {}", Configuration.SERVICE_PORT);

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Configuration.SERVICE_PORT);
        connector.setMaxIdleTime(100000);
        connector.setConfidentialPort(8443);
        connector.setStatsOn(false);
        connector.setLowResourcesConnections(20000);
        connector.setLowResourcesMaxIdleTime(5000);

        server.setConnectors(new Connector[]{connector});

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping("crdhn.sis.http.servlet.UploadImageController", Configuration.UPLOADIMAGE_CONTROLLER_PATH);
        server.setStopAtShutdown(true);
        server.setSendServerVersion(true);
        for (int i = 0; i < Configuration.number_worker; i++) {
            new ImageWorker(i).start();
        }

        File directoryOriginal = new File(Configuration.HOME_PATH + File.separator + Configuration.ORIGINAL_IMAGE_DIRECTORY);
        File directoryScale = new File(Configuration.HOME_PATH + File.separator + Configuration.SCALED_IMAGE_DIRECTORY);
        if (!directoryOriginal.exists()) {
            directoryOriginal.mkdirs();
            System.out.println("create folder success!" + Configuration.ORIGINAL_IMAGE_DIRECTORY);
        }
        if (!directoryScale.exists()) {
            directoryScale.mkdirs();
            System.out.println("Configuration.SCALED_IMAGE_DIRECTORY=" + Configuration.SCALED_IMAGE_DIRECTORY);
        }
        server.start();
        server.join();
    }
}
