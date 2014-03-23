package it.unito.geosummly.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;

@Path("/fingerprint")
public class Fingerprint {
    
    public static Logger logger = Logger.getLogger(Fingerprint.class.toString());

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response compute(  
                              @Context Request request,
                              @QueryParam("north") Double north,
                              @QueryParam("est") Double est,
                              @QueryParam("south") Double south,
                              @QueryParam("west") Double west
                            ) 
    {       
        
        logger.log(Level.INFO, "Compute fingerprint of the BBox contained within "
                + "with the following delimiters: "
                + "north=" + north +",est=" + est +",south=" + south + ",west=" + west); 
        
        //launch process
        
        return Response
                .status(Response.Status.OK)
                .entity("OK")
                .build();
    }
//    
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response compute(  
//                              @Context Request request,
//                              @QueryParam("nelat") Double neLat,
//                              @QueryParam("nelng") Double neLng,
//                              @QueryParam("swlat") Double swLat,
//                              @QueryParam("swlng") Double swLng,
//                              @QueryParam("nwlat") Double nwLat,
//                              @QueryParam("nwlng") Double nwLng,
//                              @QueryParam("selat") Double seLat,
//                              @QueryParam("selng") Double seLng
//                            ) 
//    {       
//        
//        logger.log(Level.INFO, "Compute fingerprint of the BBox with the "
//                + "following vertices: ne=(" + neLat + "," + neLng + ")," +
//                                      "se=(" + seLat + "," + seLng + ")," + 
//                                      "nw=(" + nwLat + "," + nwLng + ")," + 
//                                      "sw=(" + swLat + "," + swLng + ")");
//        
//        //launch process
//        
//        return Response
//                .status(Response.Status.OK)
//                .entity("OK")
//                .build();
//    }
}
