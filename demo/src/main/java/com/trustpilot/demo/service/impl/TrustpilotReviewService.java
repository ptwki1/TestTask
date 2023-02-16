package com.trustpilot.demo.service.impl;

import com.trustpilot.demo.model.ReviewData;
import com.trustpilot.demo.service.TrustPilotReviewParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Service
public class TrustpilotReviewService implements TrustPilotReviewParser {

    @Autowired
    private WebClient webClient;
    private Map<String, Mono<ReviewData>> cache = new HashMap<String, Mono<ReviewData>>();
    private static final String URL_TEMPLATE = "https://www.trustpilot.com/review/%s";

    @Override
    public Mono<ReviewData> getReviewData(String domain) {
        Mono<ReviewData> cachedReviewData = cache.get(domain);
        if (cachedReviewData != null) {
            return cachedReviewData;
        }

        String url = String.format(URL_TEMPLATE, domain);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(Jsoup::parse)
                .map(doc -> {
                    Element ratingElement = doc.getElementsByAttributeValue("class", "typography_body-l__KUYFJ typography_appearance-subtle__8_H2l styles_text__W4hWi").first();
                    String rating = null;
                    if (ratingElement != null) {
                        rating = Arrays.stream(ratingElement.text().split("•")).findFirst().get().trim();
                    }

                    Element reviewsCountElement = doc.getElementsByAttributeValue("class", "typography_body-l__KUYFJ typography_appearance-subtle__8_H2l").first();
                    String reviewCount = null;
                    if (reviewsCountElement != null) {
                        reviewCount = reviewsCountElement.text().trim();
                    }

                    if (rating == null || reviewCount == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain not found");
                    }

                    ReviewData reviewData = new ReviewData(reviewCount, rating);
                    cache.put(domain, Mono.just(reviewData));
                    return reviewData;
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain not found")));
    }
}
