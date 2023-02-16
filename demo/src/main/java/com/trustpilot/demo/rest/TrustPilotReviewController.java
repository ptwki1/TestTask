package com.trustpilot.demo.rest;

import com.trustpilot.demo.model.ReviewData;
import com.trustpilot.demo.service.TrustPilotReviewParser;
import com.trustpilot.demo.service.impl.TrustpilotReviewService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class TrustPilotReviewController {
    @Autowired
    private TrustPilotReviewParser parser;

    @GetMapping("/{domain}")
    public Mono<ReviewData> getTrustPilotReviewData(@PathVariable("domain") String domain) {
        return parser.getReviewData(domain);
    }
}
