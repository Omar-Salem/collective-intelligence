//package org.omarsalem.gameel.birds.services;
//
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.omarsalem.gameel.birds.dal.UserActionsRepo;
//import org.omarsalem.gameel.birds.models.Action;
//import org.omarsalem.gameel.birds.models.UserAction;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class RecommenderImplTest {
//
//    UserActionsRepo userActionsRepoMock;
//
//    private SimilarityCalculator similarityCalculator = new SimilarityCalculatorPearsonCorrelation();
//
//    private Recommender target;
//
//    @Before
//    public void init() {
//        userActionsRepoMock = mock(UserActionsRepo.class);
//        target = new RecommenderImpl(userActionsRepoMock, similarityCalculator);
//    }
//
//    @Test
//    public void getRecommendations() {
//        //Arrange
//        final ArrayList<UserAction> userActions = new ArrayList<>();
//
//        userActions.add(new UserAction(1, Action.FourFive, 6, "Toby", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.View, 6, "Toby", 500, "Dupree"));
//        userActions.add(new UserAction(1, Action.Four, 6, "Toby", 600, "Superman"));
//
//
//        userActions.add(new UserAction(1, Action.Three, 1, "Rose", 100, "Night"));
//        userActions.add(new UserAction(1, Action.TwoFive, 1, "Rose", 200, "Lady"));
//        userActions.add(new UserAction(1, Action.Three, 1, "Rose", 300, "Luck"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 1, "Rose", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 1, "Rose", 600, "Superman"));
//        userActions.add(new UserAction(1, Action.TwoFive, 1, "Rose", 500, "Dupree"));
//
//        userActions.add(new UserAction(1, Action.Three, 2, "Seymour", 100, "Night"));
//        userActions.add(new UserAction(1, Action.Three, 2, "Seymour", 200, "Lady"));
//        userActions.add(new UserAction(1, Action.OneFive, 2, "Seymour", 300, "Luck"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 2, "Seymour", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.Five, 2, "Seymour", 600, "Superman"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 2, "Seymour", 500, "Dupree"));
//
//
//        userActions.add(new UserAction(1, Action.FourFive, 3, "Puig", 100, "Night"));
//        userActions.add(new UserAction(1, Action.Three, 3, "Puig", 300, "Luck"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 3, "Puig", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.Four, 3, "Puig", 600, "Superman"));
//        userActions.add(new UserAction(1, Action.TwoFive, 3, "Puig", 500, "Dupree"));
//
//
//        userActions.add(new UserAction(1, Action.Three, 4, "LaSalle", 100, "Night"));
//        userActions.add(new UserAction(1, Action.Three, 4, "LaSalle", 200, "Lady"));
//        userActions.add(new UserAction(1, Action.Download, 4, "LaSalle", 300, "Luck"));
//        userActions.add(new UserAction(1, Action.Four, 4, "LaSalle", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.Three, 4, "LaSalle", 600, "Superman"));
//        userActions.add(new UserAction(1, Action.Download, 4, "LaSalle", 500, "Dupree"));
//
//        userActions.add(new UserAction(1, Action.Three, 5, "Matthews", 100, "Night"));
//        userActions.add(new UserAction(1, Action.Three, 5, "Matthews", 200, "Lady"));
//        userActions.add(new UserAction(1, Action.Four, 5, "Matthews", 400, "Snakes"));
//        userActions.add(new UserAction(1, Action.ThreeFive, 5, "Matthews", 500, "Dupree"));
//        userActions.add(new UserAction(1, Action.Five, 5, "Matthews", 600, "Superman"));
//
//
//        when(userActionsRepoMock.getUserActions()).thenReturn(userActions);
//
//        //Act
//        final List<Map.Entry<Integer, Double>> recommendations = target.getRecommendations(6);
//
//        //Assert
//        Assert.assertTrue(true);
//    }
//}