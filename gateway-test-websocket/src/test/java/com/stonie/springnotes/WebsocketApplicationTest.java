package com.stonie.springnotes;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;

public class WebsocketApplicationTest {

    public static void main(final String[] args) {
        final WebSocketClient client = new ReactorNettyWebSocketClient();
        final String url = "ws://localhost:8080/echo";
        client.execute(URI.create(url), session ->
                session.send(Flux.just(session.textMessage("World")))
                        .thenMany(session.receive().take(1).map(WebSocketMessage::getPayloadAsText))
                        .doOnNext(System.out::println)
                        .then())
                .block(Duration.ofMillis(5000));
    }
}
