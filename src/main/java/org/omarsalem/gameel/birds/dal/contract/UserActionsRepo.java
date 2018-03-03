package org.omarsalem.gameel.birds.dal.contract;

import org.omarsalem.gameel.birds.models.UserAction;

import java.util.List;

public interface UserActionsRepo {
    List<UserAction> getUserActions();
}
