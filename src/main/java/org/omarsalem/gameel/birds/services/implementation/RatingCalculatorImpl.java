package org.omarsalem.gameel.birds.services.implementation;

import org.omarsalem.gameel.birds.models.Action;
import org.omarsalem.gameel.birds.models.UserAction;
import org.omarsalem.gameel.birds.services.contract.RatingCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

import static java.util.stream.Collectors.summingDouble;

public class RatingCalculatorImpl implements RatingCalculator {
    private static Map<Action, Double> ACTION_WEIGHTS;

    static {
        ACTION_WEIGHTS = new HashMap<>();
        ACTION_WEIGHTS.put(Action.View, 1d);
        ACTION_WEIGHTS.put(Action.UpVote, 1d);
        ACTION_WEIGHTS.put(Action.DownVote, -1d);
        ACTION_WEIGHTS.put(Action.Download, 2d);
    }

    @Override
    public Collector<UserAction, ?, Double> getCollector() {
        return summingDouble(value -> ACTION_WEIGHTS.get(value.getAction()));
    }
}
