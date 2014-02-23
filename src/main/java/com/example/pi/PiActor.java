package com.example.pi;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PiActor.class);


    @Override
    public void preStart() {
    }

    public void onReceive(Object message) {
        if (message instanceof Pi.Work) {
            Pi.Work work = (Pi.Work) message;
            double result = calculatePiFor(work.getStart(), work.getNrOfElements());
            getSender().tell(new Pi.Result(result), getSelf());
        } else {
            unhandled(message);
        }
    }

    private double calculatePiFor(int start, int nrOfElements) {
        double acc = 0.0;
        for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;
    }
}
