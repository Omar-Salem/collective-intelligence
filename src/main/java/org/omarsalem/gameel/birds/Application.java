package org.omarsalem.gameel.birds;

import org.omarsalem.gameel.birds.dal.UserActionsRepoTextFile;
import org.omarsalem.gameel.birds.models.UserAction;
import org.omarsalem.gameel.birds.services.contract.Recommender;
import org.omarsalem.gameel.birds.services.implementation.RatingCalculatorImpl;
import org.omarsalem.gameel.birds.services.implementation.RecommenderImpl;
import org.omarsalem.gameel.birds.services.implementation.SimilarityCalculatorPearsonCorrelation;

import java.util.List;
import java.util.Map;

public class Application {
    private static Recommender recommender;

    public static void main(String[] args) {
        final UserActionsRepoTextFile userActionsRepo = new UserActionsRepoTextFile("/training.txt");
        final SimilarityCalculatorPearsonCorrelation similarityCalculator = new SimilarityCalculatorPearsonCorrelation();
        final RatingCalculatorImpl ratingCalculator = new RatingCalculatorImpl();

        recommender = new RecommenderImpl(userActionsRepo, similarityCalculator, ratingCalculator);


        final List<UserAction> userActions = new UserActionsRepoTextFile("/test.txt").getUserActions();
        final Map<Integer, Map<Integer, Double>> actualUsersArticlesRatings = recommender.getUsersArticlesRatings(userActions);

        for (Map.Entry<Integer, Map<Integer, Double>> users : actualUsersArticlesRatings.entrySet()) {
            final Integer userId = users.getKey();
            System.out.println(String.format("User %s, ", userId));
            final Map<Integer, Double> actual = users.getValue();
            final List<Map.Entry<Integer, Double>> recommendations = recommender.getRecommendations(userId);
            recommendations.forEach(integerDoubleEntry -> {
                final Integer articleId = integerDoubleEntry.getKey();
                if (actual.containsKey(articleId)) {
                    System.out.println(String.format("actual: %s, recommended: %s", actual.get(articleId), integerDoubleEntry.getValue()));
                } else {
//                    System.out.println("miss");
                }
            });

        }
    }


}