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
    private long start;

    private ActorRef sender;
    private final ActorRef workerRouter;
    private int nrOfMessages;

    public MasterActor(ActorSystem system, final int nrOfWorkers) {
        workerRouter = system.actorOf(new RoundRobinPool(nrOfWorkers).props(Props.create(PiActor.class)));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterActor.class);


    @Override
    public void preStart() {
    }

    public void onReceive(Object message) {
        if (message instanceof Pi.Calculate) {
            start = System.currentTimeMillis();
            Pi.Calculate calculate = (Pi.Calculate) message;
            nrOfMessages = calculate.getNrOfMessages();
            sender = getSender();
            for (int start = 0; start < calculate.getNrOfMessages(); start++) {
                workerRouter.tell(new Pi.Work(start, calculate.getNrOfElements()), getSelf());
            }
        } else if (message instanceof Pi.Result) {
            Pi.Result result = (Pi.Result) message;
            pi += result.getValue();
            nrOfResults += 1;
            if (nrOfResults == nrOfMessages) {
                Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                sender.tell(new Pi.PiApproximation(pi, duration.toMillis()), getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
