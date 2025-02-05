package org.mangorage.mangobot.website;



import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.mangorage.mangobot.modules.tricks.TrickCommand;


public final class WebServer {
    private static TrickCommand trickCommand;

    public static TrickCommand getTrickCommand() {
        return trickCommand;
    }

    public static void startWebServer(TrickCommand trickCommand) throws Exception {
        // Create a Jetty server instance
        WebServer.trickCommand = trickCommand;

        Server server = new Server();


        // Set up Servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("webpage"); // Serve files from "webpage" directory
        context.addServlet(DefaultServlet.class, "/*"); // Serve all webpage files
        context.addServlet(new ServletHolder(new MyServlet()), "/info");
        context.addServlet(new ServletHolder(new TricksServlet()), "/trick");

        server.setHandler(context);

        // HTTP Configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);

        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setSniHostCheck(false); // Disable SNI host check
        src.setSniRequired(false);

        httpConfig.addCustomizer(src);

        // SSL Context Factory for HTTPS
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("keystore.jks"); // Path to your keystore
        sslContextFactory.setKeyStorePassword("mango12"); // Keystore password
        sslContextFactory.setKeyManagerPassword("mango12"); // Key manager password

        // HTTP/1.1 Connection Factory
        HttpConnectionFactory http1ConnectionFactory = new HttpConnectionFactory(httpConfig);

        // HTTPS Connector
        ServerConnector sslConnector = new ServerConnector(
                server,
                sslContextFactory,
                http1ConnectionFactory
        );

        sslConnector.setPort(30076); // HTTPS port

        // Set the connector
        server.addConnector(sslConnector);

        // Start the server
        server.start();
        System.out.println("Webserver Started");
        server.join();
    }
}
