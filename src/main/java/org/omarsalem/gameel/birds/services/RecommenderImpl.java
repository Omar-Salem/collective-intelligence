package org.omarsalem.gameel.birds.services;

import org.omarsalem.gameel.birds.dal.UserActionsRepo;
import org.omarsalem.gameel.birds.models.Action;
import org.omarsalem.gameel.birds.models.UserAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class RecommenderImpl implements Recommender {
    private static Map<Action, Integer> ACTION_WEIGHTS;

    static {
        ACTION_WEIGHTS = new HashMap<>();
        ACTION_WEIGHTS.put(Action.View, 1);
        ACTION_WEIGHTS.put(Action.UpVote, 1);
        ACTION_WEIGHTS.put(Action.DownVote, -1);
        ACTION_WEIGHTS.put(Action.Download, 2);
    }

    private final UserActionsRepo userActionsRepo;
    private final SimilarityCalculator similarityCalculator;

    public RecommenderImpl(UserActionsRepo userActionsRepo, SimilarityCalculator similarityCalculator) {
        this.userActionsRepo = userActionsRepo;
        this.similarityCalculator = similarityCalculator;
    }

    @Override
    public void getRecommendations(int userId) {
        Map<Integer, Map<Integer, Integer>> matrix = getUsersArticlesRatings();
        List<Recommendation> recommendations = new ArrayList<>(matrix.size());

        final Map<Integer, Integer> userPreferences = matrix.get(userId);

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : matrix.entrySet()) {
            final Integer otherUserId = entry.getKey();
            if (otherUserId == userId) {
                continue;
            }

            final Map<Integer, Integer> otherUserPreferences = matrix.get(otherUserId);
            final double sim = similarityCalculator.calculateScore(userPreferences, otherUserPreferences);
            recommendations.add(new Recommendation(otherUserId, sim, otherUserPreferences));
        }

        Map<Integer, Aggregates> totalRatings = new HashMap<>();
        recommendations
                .stream()
                .forEach(recommendation -> recommendation
                        .getOtherUserSimilarity()
                        .entrySet()
                        .forEach(articleIdRating -> {
                            final Integer articleId = articleIdRating.getKey();
                            if (!totalRatings.containsKey(articleId)) {
                                totalRatings.put(articleId, new Aggregates(0d, 0d));
                            }
                            final Aggregates aggregates = totalRatings.get(articleId);

                            final double total = aggregates.getRatingSum() + articleIdRating.getValue();
                            aggregates.setRatingSum(total);

                            if (recommendation.otherUserSimilarity.containsKey(articleId)) {
                                aggregates.setSimSum(aggregates.getSimSum() + recommendation.getSimilarity());
                            }

                            totalRatings.put(articleId, aggregates);
                        }));
    }

    private Map<Integer, Map<Integer, Integer>> getUsersArticlesRatings() {
        //group by users
        Map<Integer, List<UserAction>> usersActions = userActionsRepo.getUserActions()
                .stream()
                .collect(groupingBy(UserAction::getUserId));
        Map<Integer, Map<Integer, Integer>> usersArticlesRatings = new HashMap<>(usersActions.size());
        for (Map.Entry<Integer, List<UserAction>> entry : usersActions.entrySet()) {
            final Map<Integer, Integer> articlesRatings = entry
                    .getValue()
                    .stream()
                    .collect(groupingBy(UserAction::getArticleId, summingInt(value -> ACTION_WEIGHTS.get(value.getAction()))));
            usersArticlesRatings.put(entry.getKey(), articlesRatings);

        }
        return usersArticlesRatings;
    }

    private static class Recommendation {
        private final int otherUserId;
        private final double similarity;
        private final Map<Integer, Double> otherUserSimilarity;

        public Recommendation(int otherUserId, double similarity, Map<Integer, Integer> otherUserPreferences) {
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

    private static class Aggregates {
        private double ratingSum;
        private double simSum;

        public Aggregates(double ratingSum, double simSum) {
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
    }
}
