package net.clementraynaud.altinspector.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.clementraynaud.altinspector.common.YamlFile;
import net.clementraynaud.altinspector.velocity.commands.altinspector.AltinspectorCommand;
import net.clementraynaud.altinspector.velocity.listeners.PlayerListener;

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

    @Inject
    private ProxyServer proxy;
    @DataDirectory
    @Inject
    private Path dataDirectory;
    private YamlFile data;
    private YamlFile usernames;

    public YamlFile data() {
        return this.data;
    }

    public YamlFile usernames() {
        return this.usernames;
    }

    public ProxyServer proxy() {
        return this.proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.data = new YamlFile("data", this.dataDirectory);
        this.usernames = new YamlFile("usernames", this.dataDirectory);
        this.proxy.getEventManager().register(this, new PlayerListener(this.data, this.usernames));

        CommandManager commandManager = this.proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("altinspector")
                .aliases("alti")
                .plugin(this)
                .build();

        SimpleCommand simpleCommand = new AltinspectorCommand(this);

        commandManager.register(commandMeta, simpleCommand);
    }

}
