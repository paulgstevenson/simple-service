package com.example;

import akka.actor.ActorSystem;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Injections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PgsApplication extends Application {

    private ActorSystem system;

    private static final Logger LOGGER = LoggerFactory.getLogger(PgsApplication.class);

    @Inject
    public PgsApplication(ServiceLocator serviceLocator) {
        LOGGER.info("Setting up Actor system");
        system = ActorSystem.create("PgsSystem");

        DynamicConfiguration dc = Injections.getConfiguration(serviceLocator);
        Injections.addBinding(Injections.newBinder(system).to(ActorSystem.class), dc);
        dc.commit();

        LOGGER.info("Completed binding");
    }

    @PreDestroy
    private void shutdown() {
        system.shutdown();
        system.awaitTermination(Duration.create(15, TimeUnit.SECONDS));
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(MyResource.class);
        return classes;
    }
}
