package com.siliconvalleytrail.cli.commands;

public interface Command {
    String getOptionLabel();
    void execute();
}
