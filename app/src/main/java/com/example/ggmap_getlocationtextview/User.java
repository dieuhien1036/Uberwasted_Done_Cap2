package com.example.ggmap_getlocationtextview;

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String name;
    private String job;
    private int score;
    private String avatar;

    public User(String user_lastName, String user_job, int user_score, String avatar) {
        this.name = user_lastName;
        this.job = user_job;
        this.score = user_score;
        this.avatar = avatar;
    }

}
