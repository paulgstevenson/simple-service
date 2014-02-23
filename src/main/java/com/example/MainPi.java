package com.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

public class MainPi {

    public static void main(String ... args){
        Client client = ClientBuilder.newClient();

        for (int i = 1 ; i <= 10; i++){
            sendFuture(client,i);
        }
        // get() waits for the response to be ready
        System.out.println("All done and waiting.");
    }

    private static void sendFuture(Client client,Integer workers) {
        System.out.println(workers);
        final Future<Response> responseFuture = client.target("http://localhost:9090/simple-service/webapi").path("myresource/pi/10000/10000/1")
                .request().async().get(new InvocationCallback<Response>() {
                    @Override
                    public void completed(Response response) {
                        System.out.println(response.readEntity(String.class));
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        System.out.println("Invocation failed.");
                        throwable.printStackTrace();
                    }
                });
    }
}
