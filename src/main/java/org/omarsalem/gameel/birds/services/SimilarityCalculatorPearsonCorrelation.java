package org.omarsalem.gameel.birds.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimilarityCalculatorPearsonCorrelation implements SimilarityCalculator {
    @Override
    public double calculateScore(Map<Integer, Double> firstUserPreferences, Map<Integer, Double> secondUserPreferences) {
        List<Integer> commonArticles = getCommonArticles(firstUserPreferences.keySet(), secondUserPreferences.keySet());

        if (commonArticles.isEmpty()) {
            return 0;
        }

        double sum1 = 0, sum2 = 0;
        double sumSq1 = 0, sumSq2 = 0;
        double sumProduct = 0;

        for (Integer articleId : commonArticles) {
            final Double user1Rating = firstUserPreferences.get(articleId);
            final Double user2Rating = secondUserPreferences.get(articleId);

            sum1 += user1Rating;
            sum2 += user2Rating;


            sumSq1 += Math.pow(user1Rating, 2);
            sumSq2 += Math.pow(user2Rating, 2);

            final double product = user1Rating * user2Rating;
            sumProduct += product;
        }

        // Calculate Pearson score
        int n = commonArticles.size();
        double num = sumProduct - (sum1 * sum2 / n);
        double den = Math.sqrt((sumSq1 - Math.pow(sum1, 2) / n) * (sumSq2 - Math.pow(sum2, 2) / n));
        if (den == 0) {
            return 0;
        }
        return num / den;
    }

    private List<Integer> getCommonArticles(Set<Integer> firstUserPreferences, Set<Integer> secondUserPreferences) {
        List<Integer> commonArticles = new ArrayList<>();

        for (Integer entry : firstUserPreferences) {
            if (secondUserPreferences.contains(entry)) {
                commonArticles.add(entry);
            }
        }
        return commonArticles;
    }
}
