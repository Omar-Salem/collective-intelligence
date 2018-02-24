package org.omarsalem.gameel.birds;

import java.util.HashMap;
import java.util.Map;

public class UserAction {
    private final int day;
    private final Action action;
    private final int userId;
    private final String userName;
    private final int articleId;
    private final String articleName;


    public UserAction(int day, Action action, int userId, String userName, int articleId, String articleName) {
        this.day = day;
        this.action = action;
        this.userId = userId;
        this.userName = userName;
        this.articleId = articleId;
        this.articleName = articleName;
    }

    public int getDay() {
        return day;
    }

    public Action getAction() {
        return action;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    @Override
    public String toString() {
        return "{" +
                "day=" + day +
                ", action='" + action + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", articleId=" + articleId +
                ", articleName='" + articleName + '\'' +
                '}';
    }
}
