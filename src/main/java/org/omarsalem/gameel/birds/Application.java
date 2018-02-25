package org.omarsalem.gameel.birds;

import org.omarsalem.gameel.birds.dal.UserActionsRepoTextFile;
import org.omarsalem.gameel.birds.services.Recommender;
import org.omarsalem.gameel.birds.services.RecommenderImpl;
import org.omarsalem.gameel.birds.services.SimilarityCalculatorPearsonCorrelation;

public class Application {
    private static Recommender recommender;

    public static void main(String[] args) {
        final UserActionsRepoTextFile userActionsRepo = new UserActionsRepoTextFile("/training.txt");
        final SimilarityCalculatorPearsonCorrelation similarityCalculator = new SimilarityCalculatorPearsonCorrelation();
        
        recommender = new RecommenderImpl(userActionsRepo, similarityCalculator);

        recommender.getRecommendations(1);
//        Map<Integer, Map<Integer, Integer>> usersArticlesRatings = getUsersArticlesRatings();
//
//        foo(usersArticlesRatings, 1);

//        System.out.println(getSimilarityScore(usersArticlesRatings.get(1), usersArticlesRatings.get(1)));
    }


}