package org.peter.shell;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.stereotype.Component;

/**
 * 演示命令可用性（availability）控制。
 *
 * <p>Spring Shell 4 去掉了 {@code @ShellMethodAvailability}，改为在 {@code @Command}
 * 的 {@code availabilityProvider} 里指定一个 {@link AvailabilityProvider} bean 的名字。
 */
@Component
@CommandGroup(name = "downloader", description = "上传下载示例命令")
public class Downloader {

    private boolean connected = false;

    @Command(name = "connect", description = "Connect server")
    public String connect() {
        connected = true;
        return "connected";
    }

    @Command(name = "download", description = "Download file", availabilityProvider = "connectCheck")
    public String download() {
        return "Downloaded.";
    }

    @Command(name = "upload", description = "Upload", availabilityProvider = "connectCheck")
    public String upload() {
        return "Uploaded.";
    }

    /** 为 download/upload 提供可用性支持。 */
    @org.springframework.stereotype.Component("connectCheck")
    public static class ConnectCheck implements AvailabilityProvider {
        private final Downloader downloader;

        public ConnectCheck(Downloader downloader) {
            this.downloader = downloader;
        }

        @Override
        public Availability get() {
            return downloader.connected
                    ? Availability.available()
                    : Availability.unavailable("you are not connected");
        }
    }
}
