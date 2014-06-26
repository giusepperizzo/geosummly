package it.unito.geosummly;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.ext.RuntimeDelegate;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * ClusteringCorrectness class.
 *
 */
public class Server {
      
    public static final String BASE_URI = "0.0.0.0";
    public static final String APP_PATH = "/";
    public static final String API_PATH = "/api/";
    public static final String WEB_ROOT = "/webroot/app";
    public static int PORT = 8080;
    private static volatile Boolean running = true;

    public static Logger logger = Logger.getLogger(Server.class.toString());

    /**
     * Starts Grizzly HTTP server exposing static content, JAX-RS resources
     * and web sockets defined in this application.
     *
     * @param webRootPath static content root path.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String webRootPath) 
    {
        final HttpServer server = new HttpServer();
        final NetworkListener listener = new NetworkListener("grizzly", BASE_URI, PORT);

        server.addListener(listener);

        final ServerConfiguration config = server.getServerConfiguration();
        // add handler for serving static content
        config.addHttpHandler(new StaticContentHandler(webRootPath), APP_PATH);

        // add handler for serving JAX-RS resources
        config.addHttpHandler(  RuntimeDelegate
                                .getInstance()
                                .createEndpoint(new ResourceConfig().packages("it.unito.geosummly.api"),
                                                GrizzlyHttpContainer.class),
                                                API_PATH
                              );

        try {
            // Start the server.
            server.start();
        } catch (Exception ex) {
            throw new ProcessingException("Exception thrown when trying to start grizzly server", ex);
        }

        return server;
    }

    public static void main(String[] args) {
        
        try {      	
        	
        	PORT = (args.length>=1) ? Integer.parseInt(args[0]) : PORT;
        	
            final HttpServer server = startServer(null);
           
            System.out.println(String.format("Geosummly Web Server started.\n" + 
                    "Access it at %s",
                    getAppUri()));
            
            Thread warmUp = new Thread() {
                public void run() {
                    //factory.searcher().warmUp((int) (configuration.getMaxCacheSize() * 0.7));
                }
            };
            warmUp.start();
            while(running) {
                Thread.sleep(100);
            }
            
            System.in.read();
            server.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getAppUri() {
        return String.format("http://localhost:%s%s", PORT, APP_PATH);
    }

    /**
     * Simple HttpHandler for serving static content included in web root
     * directory of this application.
     */
    private static class StaticContentHandler extends HttpHandler {
        private static final HashMap<String, String> EXTENSION_TO_MEDIA_TYPE;

        static {
            EXTENSION_TO_MEDIA_TYPE = new HashMap<String, String>();
            EXTENSION_TO_MEDIA_TYPE.put("html", "text/html");
            EXTENSION_TO_MEDIA_TYPE.put("js", "application/javascript");
            EXTENSION_TO_MEDIA_TYPE.put("map", "application/javascript");
            EXTENSION_TO_MEDIA_TYPE.put("css", "text/css");
            EXTENSION_TO_MEDIA_TYPE.put("png", "image/png");
            EXTENSION_TO_MEDIA_TYPE.put("ico", "image/png");
            EXTENSION_TO_MEDIA_TYPE.put("json", "text/json");
            EXTENSION_TO_MEDIA_TYPE.put("geojson", "text/geojson");
            EXTENSION_TO_MEDIA_TYPE.put("pdf", "application/pdf");
            EXTENSION_TO_MEDIA_TYPE.put("gif", "image/gif");
        }

        private final String webRootPath;

        StaticContentHandler(String webRootPath) {
            this.webRootPath = webRootPath;
        }

        @Override
        public void service(Request request, Response response) 
        throws Exception 
        {
            String uri = request.getRequestURI();
            
            int pos = uri.lastIndexOf('.');
            String extension = uri.substring(pos + 1);
            String mediaType = EXTENSION_TO_MEDIA_TYPE.get(extension);

            if (!uri.equals("/") && ( uri.contains("..") || mediaType == null) ) {
                response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
                return;
            }
            
            final String resourcesContextPath = request.getContextPath();
            if (resourcesContextPath != null && !resourcesContextPath.isEmpty()) {
                if (!uri.startsWith(resourcesContextPath)) {
                    response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
                    return;
                }

                uri = uri.substring(resourcesContextPath.length());
            }

            uri = uri.equals("/") ? uri.concat("index.html") : uri;
            System.out.println(uri);
            InputStream fileStream;

            try {
                fileStream = webRootPath == null ?
                        Server.class.getResourceAsStream(WEB_ROOT + uri) :
                        new FileInputStream(webRootPath + uri);
            } catch (IOException e) {
                fileStream = null;
            }
            if (fileStream == null) {
                response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
            } else {
                response.setStatus(HttpStatus.OK_200);
                response.setContentType(mediaType);
                ReaderWriter.writeTo(fileStream, response.getOutputStream());
            }
        }
    }
}

