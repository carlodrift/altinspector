package net.clementraynaud.altinspector.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.clementraynaud.altinspector.common.AltManager;
import net.clementraynaud.altinspector.common.YamlFile;
import net.clementraynaud.altinspector.velocity.commands.altinspector.AltinspectorCommand;
import net.clementraynaud.altinspector.velocity.listeners.PlayerListener;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "altinspector",
        name = "Altinspector",
        version = "1.0",
        description = "Find your players' other accounts beyond IP check",
        url = "https://github.com/carlodrift/altinspector",
        authors = {"carlodrift"}
)
public class Altinspector {

    private static final int BSTATS_ID = 16269;
    private static final String VERSION = "1.0";
    @Inject
    private ProxyServer proxy;
    @DataDirectory
    @Inject
    private Path dataDirectory;
    @Inject
    private Metrics.Factory bStats;
    @Inject
    private Logger logger;
    private YamlFile data;
    private YamlFile usernames;
    private AltManager altManager;
    private boolean outdatedVersion = false;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.data = new YamlFile("data", this.dataDirectory);
        this.usernames = new YamlFile("usernames", this.dataDirectory);
        this.proxy.getEventManager().register(this, new PlayerListener(this));

        CommandManager commandManager = this.proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("altinspector")
                .aliases("alti")
                .plugin(this)
                .build();

        AltinspectorCommand altinspectorCommand = new AltinspectorCommand(this);

        commandManager.register(commandMeta, altinspectorCommand);
        this.altManager = new AltManager(this.data, altinspectorCommand);
        this.bStats.make(this, Altinspector.BSTATS_ID);
    }

    public AltManager altManager() {
        return this.altManager;
    }

    public YamlFile data() {
        return this.data;
    }

    public YamlFile usernames() {
        return this.usernames;
    }

    public ProxyServer proxy() {
        return this.proxy;
    }

    public Logger logger() {
        return this.logger;
    }

    public String version() {
        return Altinspector.VERSION;
    }

    public boolean outdatedVersion() {
        return this.outdatedVersion;
    }

    public void setOutdatedVersion(boolean outdatedVersion) {
        this.outdatedVersion = outdatedVersion;
    }
}
