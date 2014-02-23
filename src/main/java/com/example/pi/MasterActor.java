package com.example.pi;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class MasterActor extends UntypedActor {

    private double pi;
    private int nrOfResults;
    private final long start = System.currentTimeMillis();

    private final ActorRef workerRouter;
    private int nrOfMessages;

    public MasterActor(ActorSystem system, final int nrOfWorkers) {
        workerRouter = system.actorOf(new RoundRobinPool(nrOfWorkers).props(Props.create(PiActor.class)));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterActor.class);


    @Override
    public void preStart() {
        LOGGER.debug("starting master actor");
    }

    public void onReceive(Object message) {
        if (message instanceof Pi.Calculate) {
            Pi.Calculate calculate = (Pi.Calculate) message;
            nrOfMessages = calculate.getNrOfMessages();
            for (int start = 0; start < calculate.getNrOfMessages(); start++) {
                workerRouter.tell(new Pi.Work(start, calculate.getNrOfElements()), getSelf());
            }
        } else if (message instanceof Pi.Result) {
            Pi.Result result = (Pi.Result) message;
            pi += result.getValue();
            nrOfResults += 1;
            if (nrOfResults == nrOfMessages) {
                LOGGER.debug("returning result");
                Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                LOGGER.debug("sender "+ getSender().toString());

                getSender().tell(new Pi.PiApproximation(pi, duration), getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
