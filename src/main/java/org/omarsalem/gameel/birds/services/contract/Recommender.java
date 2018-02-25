package org.omarsalem.gameel.birds.services.contract;

import org.omarsalem.gameel.birds.models.UserAction;

import java.util.List;
import java.util.Map;

public interface Recommender {
    List<Map.Entry<Integer, Double>> getRecommendations(int userId);

    Map<Integer, Map<Integer, Double>> getUsersArticlesRatings(List<UserAction> userActions);
}
