package org.omarsalem.gameel.birds.services;

import org.omarsalem.gameel.birds.dal.UserActionsRepo;
import org.omarsalem.gameel.birds.models.Action;
import org.omarsalem.gameel.birds.models.UserAction;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class RecommenderImpl implements Recommender {
    private static Map<Action, Double> ACTION_WEIGHTS;

    static {
        ACTION_WEIGHTS = new HashMap<>();
        ACTION_WEIGHTS.put(Action.View, 1d);
        ACTION_WEIGHTS.put(Action.UpVote, 1d);
        ACTION_WEIGHTS.put(Action.DownVote, -1d);
        ACTION_WEIGHTS.put(Action.Download, 2d);
//        ACTION_WEIGHTS.put(Action.Three, 3d);
//        ACTION_WEIGHTS.put(Action.ThreeFive, 3.5d);
//        ACTION_WEIGHTS.put(Action.TwoFive, 2.5d);
//        ACTION_WEIGHTS.put(Action.OneFive, 1.5d);
//        ACTION_WEIGHTS.put(Action.FourFive, 4.5d);
//        ACTION_WEIGHTS.put(Action.Four, 4.0d);
//        ACTION_WEIGHTS.put(Action.Five, 5.0d);
    }

    private final UserActionsRepo userActionsRepo;
    private final SimilarityCalculator similarityCalculator;

    public RecommenderImpl(UserActionsRepo userActionsRepo, SimilarityCalculator similarityCalculator) {
        this.userActionsRepo = userActionsRepo;
        this.similarityCalculator = similarityCalculator;
    }

    @Override
    public List<Map.Entry<Integer, Double>> getRecommendations(int userId) {
        Map<Integer, Map<Integer, Double>> matrix = getUsersArticlesRatings();
        List<Recommendation> recommendations = new ArrayList<>(matrix.size());

        final Map<Integer, Double> userPreferences = matrix.get(userId);

        for (Map.Entry<Integer, Map<Integer, Double>> entry : matrix.entrySet()) {
            final Integer otherUserId = entry.getKey();
            if (otherUserId == userId) {
                continue;
            }

            final Map<Integer, Double> otherUserPreferences = matrix.get(otherUserId);
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

    private Map<Integer, Map<Integer, Double>> getUsersArticlesRatings() {
        //group by users
        Map<Integer, List<UserAction>> usersActions = userActionsRepo.getUserActions()
                .stream()
                .collect(groupingBy(UserAction::getUserId));
        Map<Integer, Map<Integer, Double>> usersArticlesRatings = new HashMap<>(usersActions.size());
        for (Map.Entry<Integer, List<UserAction>> entry : usersActions.entrySet()) {
            final Map<Integer, Double> articlesRatings = entry
                    .getValue()
                    .stream()
                    .collect(groupingBy(UserAction::getArticleId, summingDouble(value -> ACTION_WEIGHTS.get(value.getAction()))));
            usersArticlesRatings.put(entry.getKey(), articlesRatings);

        }
        return usersArticlesRatings;
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
