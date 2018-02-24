package org.omarsalem.gameel.birds;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class Application {
    private static Map<Action, Integer> ACTION_WEIGHTS;

    static {
        ACTION_WEIGHTS = new HashMap<>();
        ACTION_WEIGHTS.put(Action.View, 1);
        ACTION_WEIGHTS.put(Action.UpVote, 1);
        ACTION_WEIGHTS.put(Action.DownVote, -1);
        ACTION_WEIGHTS.put(Action.Download, 2);
    }

    public static void main(String[] args) throws IOException {
        Map<Integer, Map<Integer, Integer>> usersArticlesRatings = getUsersArticlesRatings();

        System.out.println(usersArticlesRatings.size());
    }

    private static Map<Integer, Map<Integer, Integer>> getUsersArticlesRatings() throws IOException {
        //group by users
        Map<Integer, List<UserAction>> usersActions = getUserActions()
                .stream()
                .collect(groupingBy(UserAction::getUserId));
        Map<Integer, Map<Integer, Integer>> usersArticlesRatings = new HashMap<>(usersActions.size());
        for (Map.Entry<Integer, List<UserAction>> entry : usersActions.entrySet()) {
            final Map<Integer, Integer> articlesRatings = entry
                    .getValue()
                    .stream()
                    .collect(groupingBy(UserAction::getArticleId, summingInt(value -> ACTION_WEIGHTS.get(value.getAction()))));
            usersArticlesRatings.put(entry.getKey(), articlesRatings);

        }
        return usersArticlesRatings;
    }

    private static List<UserAction> getUserActions() throws IOException {
        Class clazz = Application.class;
        InputStream inputStream = clazz.getResourceAsStream("/training.txt");
        String[] rows = IOUtils.toString(inputStream, "UTF-8").split(System.getProperty("line.separator"));
        boolean found = false;
        List<UserAction> userActions = new ArrayList<UserAction>();
        for (String l : rows) {
            if (l.startsWith("# Day, Action, UserID, UserName, ArticleID, ArticleName")) {
                found = true;
                continue;
            }
            if (found) {
                String[] columns = l.split(",");
                UserAction userAction = new UserAction(Integer.parseInt(columns[0]),
                        Action.valueOf(columns[1]),
                        Integer.parseInt(columns[2]),
                        columns[3],
                        Integer.parseInt(columns[4]), columns[5]);
                userActions.add(userAction);
            }
        }
        return userActions;
    }
}