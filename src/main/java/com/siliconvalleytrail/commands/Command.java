package com.siliconvalleytrail.commands;

public interface Command {
    String getOptionLabel();
    void execute();
}
