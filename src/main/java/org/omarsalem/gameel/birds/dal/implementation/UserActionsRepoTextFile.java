package org.omarsalem.gameel.birds.dal.implementation;

import org.apache.commons.io.IOUtils;
import org.omarsalem.gameel.birds.dal.contract.UserActionsRepo;
import org.omarsalem.gameel.birds.models.Action;
import org.omarsalem.gameel.birds.models.UserAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserActionsRepoTextFile implements UserActionsRepo {

    private final String fileName;

    public UserActionsRepoTextFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<UserAction> getUserActions() {
        try {
            return tryGetUserActions();
        } catch (IOException e) {
            throw new RuntimeException("cant read file;" + e.getMessage());
        }
    }

    private List<UserAction> tryGetUserActions() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        String[] rows = IOUtils
                .toString(inputStream, "UTF-8")
                .split(System.getProperty("line.separator"));
        boolean found = false;
        List<UserAction> userActions = new ArrayList<>();
        for (String l : rows) {
            if (!found) {
                if (l.startsWith("# Day, Action, UserID, UserName, ArticleID, ArticleName")) {
                    found = true;
                }
            } else {
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
