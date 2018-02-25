package org.omarsalem.gameel.birds.services.implementation;

import org.omarsalem.gameel.birds.dal.UserActionsRepo;
import org.omarsalem.gameel.birds.models.UserAction;
import org.omarsalem.gameel.birds.services.contract.RatingCalculator;
import org.omarsalem.gameel.birds.services.contract.Recommender;
import org.omarsalem.gameel.birds.services.contract.SimilarityCalculator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class RecommenderImpl implements Recommender {

    private final UserActionsRepo userActionsRepo;
    private final SimilarityCalculator similarityCalculator;
    private final RatingCalculator ratingCalculator;
    private final List<UserAction> userActions;
    private final Map<Integer, Map<Integer, Double>> matrix;

    public RecommenderImpl(UserActionsRepo userActionsRepo, SimilarityCalculator similarityCalculator, RatingCalculator ratingCalculator) {
        this.userActionsRepo = userActionsRepo;
        this.similarityCalculator = similarityCalculator;
        this.ratingCalculator = ratingCalculator;

        userActions = userActionsRepo.getUserActions();
        matrix = getUsersArticlesRatings(userActions);
    }

    @Override
    public List<Map.Entry<Integer, Double>> getRecommendations(int userId) {

        List<Recommendation> scoreMatrix = createScoreMatrix(userId, matrix);

        Map<Integer, WeightedScore> totalRatings = createWeightedScore(scoreMatrix);

        return sortByWeight(totalRatings);
    }

    @Override
    public Map<Integer, Map<Integer, Double>> getUsersArticlesRatings(List<UserAction> userActions) {
        //group by users
        Map<Integer, List<UserAction>> usersActions = userActions
                .stream()
                .collect(groupingBy(UserAction::getUserId));

        //group articles by id, calculate sum of ratings
        Map<Integer, Map<Integer, Double>> usersArticlesRatings = new HashMap<>(usersActions.size());
        for (Map.Entry<Integer, List<UserAction>> entry : usersActions.entrySet()) {
            final Map<Integer, Double> articlesRatings = entry
                    .getValue()
                    .stream()
                    .collect(groupingBy(UserAction::getArticleId, ratingCalculator.getCollector()));
            usersArticlesRatings.put(entry.getKey(), articlesRatings);

        }
        return usersArticlesRatings;
    }

    private List<Recommendation> createScoreMatrix(int userId, Map<Integer, Map<Integer, Double>> matrix) {
        Map<Integer, Double> userPreferences = matrix.get(userId) == null ? new HashMap<>() : matrix.get(userId);
        List<Recommendation> recommendations = new ArrayList<>(matrix.size());

        for (Map.Entry<Integer, Map<Integer, Double>> entry : matrix.entrySet()) {
            final Integer otherUserId = entry.getKey();
            if (otherUserId == userId) {
                continue;
            }

            final Map<Integer, Double> otherUserPreferences = matrix.get(otherUserId);
            final double sim = similarityCalculator.calculateScore(userPreferences, otherUserPreferences);
            if (sim > 0) {
                recommendations.add(new Recommendation(otherUserId, sim, otherUserPreferences));
            }
        }
        return recommendations;
    }

    private Map<Integer, WeightedScore> createWeightedScore(List<Recommendation> scoreMatrix) {
        Map<Integer, WeightedScore> totalRatings = new HashMap<>();
        scoreMatrix
                .forEach(recommendation -> recommendation
                        .getOtherUserSimilarity()
                        .forEach((articleId, rating) -> {
                            if (!totalRatings.containsKey(articleId)) {
                                totalRatings.put(articleId, new WeightedScore(0d, 0d));
                            }
                            final WeightedScore weightedScore = totalRatings.get(articleId);

                            final double total = weightedScore.getRatingSum() + rating;
                            weightedScore.setRatingSum(total);

                            if (recommendation.otherUserSimilarity.containsKey(articleId)) {
                                weightedScore.setSimSum(weightedScore.getSimSum() + recommendation.getSimilarity());
                            }

                            totalRatings.put(articleId, weightedScore);
                        }));
        return totalRatings;
    }

    private List<Map.Entry<Integer, Double>> sortByWeight(Map<Integer, WeightedScore> totalRatings) {
        final Map<Integer, Double> collect = totalRatings
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, c -> c.getValue().getWeight()));
        final List<Map.Entry<Integer, Double>> collect1 = collect
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
        Collections.reverse(collect1);
        return collect1;
    }

    private static class Recommendation {
        private final int otherUserId;
        private final double similarity;
        private final Map<Integer, Double> otherUserSimilarity;

        public Recommendation(int otherUserId, double similarity, Map<Integer, Double> otherUserPreferences) {
            this.otherUserId = otherUserId;
            this.similarity = similarity;
            this.otherUserSimilarity = otherUserPreferences
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() * similarity));
        }

        public int getOtherUserId() {
            return otherUserId;
        }

        public double getSimilarity() {
            return similarity;
        }

        public Map<Integer, Double> getOtherUserSimilarity() {
            return otherUserSimilarity;
        }
    }

    private static class WeightedScore {
        private double ratingSum;
        private double simSum;

        public WeightedScore(double ratingSum, double simSum) {
            this.ratingSum = ratingSum;
            this.simSum = simSum;
        }

        public double getRatingSum() {
            return ratingSum;
        }

        public double getSimSum() {
            return simSum;
        }

        public void setRatingSum(double ratingSum) {
            this.ratingSum = ratingSum;
        }

        public void setSimSum(double simSum) {
            this.simSum = simSum;
        }

        public double getWeight() {
            return ratingSum / simSum;
        }

        @Override
        public String toString() {
            return "{" +
                    "ratingSum=" + ratingSum +
                    ", simSum=" + simSum +
                    ", weight=" + getWeight() +
                    '}';
        }
    }
}
