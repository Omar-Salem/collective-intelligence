package org.omarsalem.gameel.birds.services;

import java.util.Map;

public interface SimilarityCalculator {
    double calculateScore(Map<Integer, Integer> userPreferences, Map<Integer, Integer> otherUserPreferences);
}
