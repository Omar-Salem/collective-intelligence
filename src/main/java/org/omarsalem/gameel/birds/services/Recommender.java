package org.omarsalem.gameel.birds.services;

import java.util.List;
import java.util.Map;

public interface Recommender {
    List<Map.Entry<Integer, Double>> getRecommendations(int userId);
}
