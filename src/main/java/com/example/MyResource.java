package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.example.pi.MasterActor;
import com.example.pi.Pi;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyResource.class);

    @Inject
    ActorSystem system;

    @Inject
    ActorRef actors;

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {

        LOGGER.info("System is "+system);
        return "Got it! "+ system.toString();
    }

/*    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{value}")
    @ManagedAsync
    public void getExamples (@PathParam("value") Integer value, @Suspended final AsyncResponse res) {
        Timeout timeout = new Timeout(Duration.create(2, "seconds"));
        Future<Object> future = Patterns.ask(actors, value, timeout);

        future.onComplete(new OnComplete<Object>() {

            public void onComplete(Throwable failure, Object result) {
                if (failure != null) {

                    if (failure.getMessage() != null) {
                        HashMap<String,String> response = new HashMap<String,String>();
                        response.put("error", failure.getMessage());
                        res.resume(Response.serverError().entity(response).build());
                    } else {
                        res.resume(Response.serverError());
                    }

                } else {

                    HashMap<String,Object> response = new HashMap<String,Object>();
                    response.put("results",(Integer)result);
                    res.resume(Response.ok().entity(response).build());

                }

            }
        }, system.dispatcher());

    }*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pi/{messages}/{elements}")
    @ManagedAsync
    public void getPi (@PathParam("messages") Integer messages, @PathParam("elements") Integer elements, @Suspended final AsyncResponse res) {
        Timeout timeout = new Timeout(Duration.create(100, "seconds"));
        Pi.Calculate calculate = new Pi.Calculate(messages,elements);

        ActorRef master = system.actorOf(Props.create(MasterActor.class, system, 5));

        Future<Object> future = Patterns.ask(master, calculate, timeout);
        future.onComplete(new OnComplete<Object>() {

            public void onComplete(Throwable failure, Object result) {
                result = (Pi.PiApproximation) result;

                if (failure != null) {

                    if (failure.getMessage() != null) {
                        HashMap<String,String> response = new HashMap<String,String>();
                        response.put("error", failure.getMessage());
                        res.resume(Response.serverError().entity(response).build());
                    } else {
                        res.resume(Response.serverError());
                    }

                } else {
                    res.resume(Response.ok().entity(result).build());

                }

            }
        }, system.dispatcher());

    }
}
