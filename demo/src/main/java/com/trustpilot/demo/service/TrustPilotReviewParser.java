package com.trustpilot.demo.service;

import com.trustpilot.demo.model.ReviewData;
import reactor.core.publisher.Mono;

public interface TrustPilotReviewParser {

     Mono<ReviewData> getReviewData(String domain);
}
