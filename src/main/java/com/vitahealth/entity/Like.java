package com.vitahealth.entity;

public class Like {
    private int id;
    private User user;
    private Forum forum;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Forum getForum() { return forum; }
    public void setForum(Forum forum) { this.forum = forum; }
}
