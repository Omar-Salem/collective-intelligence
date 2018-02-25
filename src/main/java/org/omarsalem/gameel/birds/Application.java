package org.omarsalem.gameel.birds;

import org.omarsalem.gameel.birds.dal.UserActionsRepoTextFile;
import org.omarsalem.gameel.birds.models.UserAction;
import org.omarsalem.gameel.birds.services.contract.Recommender;
import org.omarsalem.gameel.birds.services.implementation.RatingCalculatorImpl;
import org.omarsalem.gameel.birds.services.implementation.RecommenderImpl;
import org.omarsalem.gameel.birds.services.implementation.SimilarityCalculatorPearsonCorrelation;

import java.util.List;

public class Application {
    private static Recommender recommender;

    public static void main(String[] args) {
        final UserActionsRepoTextFile userActionsRepo = new UserActionsRepoTextFile("/training.txt");
        final SimilarityCalculatorPearsonCorrelation similarityCalculator = new SimilarityCalculatorPearsonCorrelation();
        final RatingCalculatorImpl ratingCalculator = new RatingCalculatorImpl();

        recommender = new RecommenderImpl(userActionsRepo, similarityCalculator, ratingCalculator);

        recommender.getRecommendations(1);
        final List<UserAction> userActions = new UserActionsRepoTextFile("/test.txt").getUserActions();
    }


}