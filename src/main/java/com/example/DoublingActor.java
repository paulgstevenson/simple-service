package com.example;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoublingActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoublingActor.class);

    @Override
    public void preStart() {
        LOGGER.debug("starting actor");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Integer) {
            LOGGER.debug("received message: " + (Integer)message);
            getSender().tell((Integer)message*2, getSelf());
        } else {
            unhandled(message);
        }

    }

}
