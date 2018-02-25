package org.omarsalem.gameel.birds.services.contract;

import org.omarsalem.gameel.birds.models.UserAction;

import java.util.stream.Collector;

public interface RatingCalculator {
    Collector<UserAction, ?, Double> getCollector();
}
