package org.omarsalem.gameel.birds;

import org.omarsalem.gameel.birds.dal.implementation.UserActionsRepoTextFile;
import org.omarsalem.gameel.birds.services.contract.Recommender;
import org.omarsalem.gameel.birds.services.implementation.RatingCalculatorImpl;
import org.omarsalem.gameel.birds.services.implementation.RecommenderImpl;
import org.omarsalem.gameel.birds.services.implementation.SimilarityCalculatorPearsonCorrelation;

import java.util.List;
import java.util.Map;

public class Application {
    private static Recommender recommender;

    public static void main(String[] args) {
        final UserActionsRepoTextFile userActionsRepo = new UserActionsRepoTextFile("/UserBehaviour.txt");
        final SimilarityCalculatorPearsonCorrelation similarityCalculator = new SimilarityCalculatorPearsonCorrelation();
        final RatingCalculatorImpl ratingCalculator = new RatingCalculatorImpl();

        recommender = new RecommenderImpl(userActionsRepo, similarityCalculator, ratingCalculator);

        int userId = 1;
        final List<Map.Entry<Integer, Double>> recommendations = recommender.getRecommendations(userId);
        recommendations.forEach(r -> System.out.println(String.format("%s:%s", r.getKey(), r.getValue())));
    }


}