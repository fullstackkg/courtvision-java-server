package com.khalilgayle.courtvisionserver.players;

import com.khalilgayle.courtvisionserver.players.playerexceptions.PlayerException;
import com.khalilgayle.courtvisionserver.players.playerexceptions.PlayerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayerService {
    private final WebClient webClient;

    public PlayerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PlayerListDTOResponse> getPlayers(int page, int playersPerPage) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/players")
                        .queryParam("page", page)
                        .queryParam("players_per_page", playersPerPage)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new PlayerNotFoundException("No players found"));
                    } else if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return clientResponse.bodyToMono(PlayerListDTOResponse.class);
                    } else {
                        return Mono.error(new PlayerException("Internal service error occurred while fetching players"));
                    }
                });
    }
}
