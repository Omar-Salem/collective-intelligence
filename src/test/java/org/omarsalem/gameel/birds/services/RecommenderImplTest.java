package org.omarsalem.gameel.birds.services;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.omarsalem.gameel.birds.dal.UserActionsRepo;
import org.omarsalem.gameel.birds.models.Action;
import org.omarsalem.gameel.birds.models.UserAction;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

class RecommenderImplTest {
    @Mock
    UserActionsRepo userActionsRepoMock;

    private SimilarityCalculator similarityCalculator = new SimilarityCalculatorPearsonCorrelation();

    private Recommender target;

    @Before
    public void init() {
        target = new RecommenderImpl(userActionsRepoMock, similarityCalculator);
    }

    @Test
    void getRecommendations() {
        //Arrange
        final ArrayList<UserAction> userActions = new ArrayList<>();
        userActions.add(new UserAction(1, Action.Three, 1, "Rose", 100, "Night"));


        when(userActionsRepoMock.getUserActions()).thenReturn(userActions);
        //Act
        target.getRecommendations(1);

        //Assert
    }
}