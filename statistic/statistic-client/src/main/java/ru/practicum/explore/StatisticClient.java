package ru.practicum.explore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Service
public class StatisticClient extends BaseClient {

    private static final String API_HIT = "/hit";

    private static final String API_STATS = "/stats";

    @Autowired
    public StatisticClient(@Value("${statistic-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build());
    }

    public ResponseEntity<Object> create(EndpointHit endpointHit) {
        return post(API_HIT, endpointHit);
    }

    public ResponseEntity<Object> readAll(LocalDateTime start, LocalDateTime end,
                                          Collection<String> uris, Boolean unique) {
        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return get(API_STATS + "?start={starts}&end={end}&uris={uris}&unique={unique}", params);
    }
}