package org.peter.shell;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class Downloader {

    private boolean connected = false;

    @ShellMethod("Connect server")
    public void connect() {
        connected = true;
    }

    @ShellMethod("Download file")
    @ShellMethodAvailability({"connectCheck"})
    public void download() {
        System.out.println("Downloaded.");
    }

    @ShellMethod("Upload")
    @ShellMethodAvailability({"connectCheck"})
    public void upload() {
        System.out.println("Uploaded.");
    }

    // 为命令download提供可用行支持
    public Availability downloadAvailability() {
        return connected ? Availability.available() : Availability.unavailable("you are not connected");
    }

    public Availability connectCheck() {
        return connected ? Availability.available() : Availability.unavailable("you are not connected");
    }


}
