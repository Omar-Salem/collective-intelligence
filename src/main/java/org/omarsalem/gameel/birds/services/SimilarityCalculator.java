package org.omarsalem.gameel.birds.services;

import java.util.Map;

public interface SimilarityCalculator {
    double calculateScore(Map<Integer, Double> userPreferences, Map<Integer, Double> otherUserPreferences);
}
