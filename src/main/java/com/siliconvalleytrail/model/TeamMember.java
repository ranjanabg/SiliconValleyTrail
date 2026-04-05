package com.siliconvalleytrail.model;

public class TeamMember {

    private final String memberId;
    private final String name;
    private final String jobTitle;

    public TeamMember(String memberId, String name, String jobTitle) {
        this.memberId = memberId;
        this.name = name;
        this.jobTitle = jobTitle;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getJobTitle() { return jobTitle; }
}
