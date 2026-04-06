package com.zleub;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.event.PluginSetupEvent;

import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.zleub.events.ExampleEvent;

import javax.annotation.Nonnull;

import java.util.logging.Logger;


public class ExamplePlugin extends JavaPlugin {
    static public Logger LOGGER = Logger.getLogger("ExamplePlugin");

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
        this.getEventRegistry().registerGlobal(PluginSetupEvent.class, ExampleEvent::onPluginSetup);
        this.getEventRegistry().registerGlobal(StartWorldEvent.class, ExampleEvent::onStartWorldEvent);

    }
}