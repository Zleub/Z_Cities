package com.zleub.events;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.event.GenerateAssetsEvent;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.AssetPackRegisterEvent;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.BootEvent;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.plugin.event.PluginSetupEvent;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.zleub.AssetPackCreator;
import com.zleub.ExamplePlugin;
import com.zleub.PrefabGenerator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

import static com.zleub.AssetPackCreator.createAssetPack;
import static com.zleub.ExamplePlugin.LOGGER;
import static com.zleub.PrefabGenerator.*;

public class ExampleEvent {

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("Welcome " + player.getDisplayName()));
        LOGGER.info("Welcome" + player.getDisplayName());
    }

    public static void onPluginSetup(PluginSetupEvent event, CodecMapRegistry.Assets<PropAsset,?> codecRegistry) {
        LOGGER.info("onPluginSetup");

        buildAssetMap().forEach((k,v) -> {
            LOGGER.info(k.getName() + " -> " + v.size());

            try {
                createAssetPack(
                        k.getManifest().getName() + "_generated",
                        k.getManifest().getGroup(),
                        "A generated pack by Z-Cities",
                        "1.0.0",
                        "",
                        "zleub",
                        String.valueOf(PluginManager.MODS_PATH)
                );
            } catch (AssetPackCreator.AssetPackCreatorError e) {
                LOGGER.info(e.getMessage());
            }
        });



    }

    public static void onStartWorldEvent(StartWorldEvent event) {
        LOGGER.info("onStartWorldEvent: " + event.getWorld().getSavePath());

    }

}