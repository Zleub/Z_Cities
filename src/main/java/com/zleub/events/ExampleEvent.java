package com.zleub.events;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.event.GenerateAssetsEvent;
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
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.zleub.AssetPackCreator;
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

    public static void onPluginSetup(PluginSetupEvent event) {
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

        LOGGER.info("Permutations size: " + getPermutationsSize());
        LOGGER.info("Cartesian size: " + cartesianList.stream().reduce(0, (acc, v) -> acc + v.size(), Integer::sum ));

        generateFromAssetMap(buildAssetMap());

        LOGGER.info(String.format("Saved: %d | Skipped: %d", saved, skipped));

//        AssetPack generatedPack = AssetModule.get().getAssetPack("testgroup:testname");
//        LOGGER.info("generatedPack: " + generatedPack);
//        AssetPack assetPack = AssetModule.get().getAssetPack("com.zleub:ExamplePlugin");
//        LOGGER.info("assetPack: " + assetPack);
//        List<Path> results = new ArrayList<>();
//
//        PrefabStore prefabStore = PrefabStore.get();
//        Path root = prefabStore.getAssetPrefabsPathForPack(assetPack);
//        Path base = root.resolve("base");
//
//
//        try {
//            Files.walkFileTree(
//                    base,
//                    new SimpleFileVisitor<Path>() {
//                        @Nonnull
//                        public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
//                            String fileName = file.getFileName().toString();
//                            if (fileName.endsWith(".prefab.json"))
//                                results.add(base.resolve(file));
//                            return FileVisitResult.CONTINUE;
//                        }
//                    }
//            );
//        } catch (IOException var7) {
//        }



//        results.forEach(path -> {
//            LOGGER.info(path.toString());
//            BlockSelection prefab = prefabStore.getPrefab(path);
//            Map<String, Boolean> uniqueMap = new HashMap<>();
//
//            prefab.forEachBlock((x, y, z, block) -> {
//                BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
//                if (blockType != null) {
////                    LOGGER.info(blockType.getId());
//                    uniqueMap.put(blockType.getId(), true);
//                }
//            });

//            List<List<ExampleEvent.MyPair>> tmp = REPLACE_MAP.keySet().stream().filter(uniqueMap::containsKey).map(e -> Arrays.stream(REPLACE_MAP.get(e)).map(f -> new ExampleEvent.MyPair(e, BlockType.getBlockIdOrUnknown(f, "Failed to find block '%s' in chunk section!", f))).toList()).toList();
//            List<List<ExampleEvePair>> list = cartesianProduct(tmp, 0).toList();

//            list.forEach(e -> {
//                BlockSelection newPrefab = new BlockSelection();
//                newPrefab.setAnchor(prefab.getAnchorX(), prefab.getAnchorY(), prefab.getAnchorZ());
//
//
////                prefab.forEachBlock((x, y, z, block)-> {
////                    BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
////                    if (blockType != null) {
////                        e.forEach(p -> {
////                            if (Objects.equals(p.a(), blockType.getId())) {
////                BlockType.getBlockIdOrUnknown(f, "Failed to find block '%s' in chunk section!", f)
////                                newPrefab.addBlockAtLocalPos(x, y, z, p.b(), block.rotation(), block.filler(), block.supportValue());
////                            }
////                        });
//////                        uniqueMap.put(blockType.getId(), true);
////                    }
////                });
////                prefabStore.saveAssetPrefab(f);
//
//
////                Path outPath = prefabStore.getAssetRootPath().resolve(
////                        strippedPath + "_" + list.indexOf(e) + ".prefab.json");
////                LOGGER.info(String.valueOf(outPath));
////                prefabStore.savePrefab(outPath, newPrefab, false);
//
//                int lastSeparator = path.toString().indexOf(".");
//                String strippedPath = path.toString().substring(1, lastSeparator).replace("base", "generated");
//
//                Path rootPath = generatedPack.getRoot().normalize();
////                LOGGER.info("rootPath: " + rootPath);
//                Path normalized = Path.of(strippedPath + "_" + list.indexOf(e) + ".prefab.json").normalize();
////                LOGGER.info("normalized: " + normalized);
//
//                Path outPath = rootPath.resolve(normalized);
//
////                LOGGER.info("\t-> " + outPath);
//
//                AssetPack sourcePack = PrefabStore.get().findAssetPackForPrefabPath(outPath);
//
////                LOGGER.info("findAssetPackForPrefabPath -> " + (sourcePack == null ? "null" : sourcePack.getPackLocation()));
//
//
////                try {
//                HytaleServer.SCHEDULED_EXECUTOR.execute(() -> {
//                    prefabStore.savePrefab(outPath, newPrefab, true);
//
//                });
////                } catch (Exception ex) {
////                    LOGGER.info("exception thrown: " + ex.);
////                }
//
//
////                Constants.UNIVERSE_PATH.resolve("worlds").resolve(this.getWorldName(context));
//
////                LOGGER.info(Constants.UNIVERSE_PATH.resolve("worlds").toString());
//
////                Arrays.stream(ps).toList().forEach(ttt -> {
////                    LOGGER.info(String.valueOf(ttt));
////                });
//
////                prefabStore.savePrefab(outPath, newPrefab, false);
//
//            });


//            tmp.forEach();

//        tmp.stream().reduce())
//            LOGGER.info(tmp.toString());
//
//            LOGGER.info(uniqueMap.toString());
//            LOGGER.info(prefab.toString());
//        });

//        Path storePath = PrefabStoreRoot.resolvePrefabStore(PrefabStoreRoot.WORLD_GEN, event.getWorld().getSavePath());
//
//        String prefabJson = """
//        {
//          "formatVersion": 1,
//          "metadata": {
//            "name": "my_generated_prefab"
//          },
//          "size": { "x": 3, "y": 3, "z": 3 },
//          "blocks": [
//            // your prefab data here
//          ]
//        }
//        """;
//
//        try {
//            writePrefabJson(storePath, "my_generated_prefab", prefabJson);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to write prefab json", e);
//        }
    }

    public static Path writePrefabJson(Path worldSaveDir, String prefabName, String json) throws IOException {
        Path prefabsDir = worldSaveDir.resolve("prefabs");
        Files.createDirectories(prefabsDir);

        Path target = prefabsDir.resolve(prefabName + ".prefab.json");
        Path temp = prefabsDir.resolve(prefabName + ".prefab.json.tmp");

        // Write atomically so the game never sees a half-written file
        Files.writeString(temp, json, StandardCharsets.UTF_8);
        Files.move(temp, target,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);

        return target;
    }

//    public static void onBootEvent(BootEvent event) {
//        LOGGER.info("GenerateAssetsEvent");
//
//
//        Map<String, String[]> replaceMap = new HashMap<>();
//        replaceMap.put("Soil_Clay", new String[] {
//                // "Soil_Clay",
//                // "Soil_Clay_Black",
//                // "Soil_Clay_Blue",
//                // "Soil_Clay_Cyan",
//                // "Soil_Clay_Green",
//                // "Soil_Clay_Grey",
//                // "Soil_Clay_Lime",
//                // "Soil_Clay_Ocean",
//                // "Soil_Clay_Orange",
//                // "Soil_Clay_Pink",
//                // "Soil_Clay_Purple",
//                // "Soil_Clay_Red",
//                // "Soil_Clay_White",
//                // "Soil_Clay_Yellow",
//                "Rock_Chalk",
//                "Rock_Marble",
//                // "Rock_Ice",
//                // "Rock_Aqua",
//                // "Rock_Basalt",
//                "Rock_Calcite",
//                // "Rock_Ledge"
//                "Rock_Lime",
//                "Rock_Marble",
//                "Rock_Quartzite",
//                "Rock_Salt",
//                // "Rock_Sandstone",
//                // "Rock_Sandstone_Red",
//                // "Rock_Sandstone_White",
//                // "Rock_Shale",
//                // "Rock_Slate",
//                // "Rock_Stone",
//                // "Rock_Volcanic"
//
//                // "Prototype_Rock_Concrete",
//        });
//
//        replaceMap.put("Rock_Crystal_White_Block", new String[] {
//                "Rock_Crystal_Blue_Block",
//                "Rock_Crystal_Cyan_Block",
//                "Rock_Crystal_Green_Block",
//                "Rock_Crystal_Pink_Block",
//                "Rock_Crystal_Purple_Block",
//                "Rock_Crystal_Red_Block",
//                "Rock_Crystal_White_Block",
//                "Rock_Crystal_Yellow_Block",
//        });
//
//        replaceMap.put("Wood_Hardwood_Planks_Half", new String[] {
//                "Wood_Hardwood_Planks_Half",
//                "Wood_Oak_Trunk_Half"
//        });
//
//        AssetPack assetPack = AssetModule.get().getAssetPack("com.zleub:ExamplePlugin");
//        List<Path> results = new ArrayList<>();
//
//        PrefabStore prefabStore = PrefabStore.get();
//        Path root = prefabStore.getAssetPrefabsPathForPack(assetPack);
//        Path base = root.resolve("base");
//
//        AssetPack baseAssetPack = AssetModule.get().getBaseAssetPack();
//        LOGGER.info(baseAssetPack.getPackLocation().toString());
//
//        AssetModule.get().getAssetPacks().forEach(e -> {
//            LOGGER.info("[AssetPack]" + e.getName() + " : " + e.getPackLocation() );
//        });
//
//        PrefabStore.get().getAllAssetPrefabPaths().forEach(e -> {
//            LOGGER.info("[AssetPrefabPath]" + e.getDisplayName() + " : " + e.prefabsPath());
//        });
//
//        Path worldGenPath = Universe.getWorldGenPath();
//        LOGGER.info("WorldgenPath: " + worldGenPath);
//
//        AssetPack assetPack1 = AssetModule.get().getAssetPack("");
//        LOGGER.info("another trest: " + assetPack1);
//
//        try {
//            Files.walkFileTree(
//                    base,
//                    new SimpleFileVisitor<Path>() {
//                        @Nonnull
//                        public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
//                            String fileName = file.getFileName().toString();
//                            if (fileName.endsWith(".prefab.json"))
//                                results.add(base.resolve(file));
//                            return FileVisitResult.CONTINUE;
//                        }
//                    }
//            );
//        } catch (IOException var7) {
//        }
//
//        results.subList(0, 1).forEach(path -> {
//            LOGGER.info(path.toString());
//            BlockSelection prefab = prefabStore.getPrefab(path);
//            Map<String, Boolean> uniqueMap = new HashMap<>();
//
//            prefab.forEachBlock((x, y, z, block) -> {
//                BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
//                if (blockType != null) {
////                    LOGGER.info(blockType.getId());
//                    uniqueMap.put(blockType.getId(), true);
//                }
//            });
//
//            List<List<MyPair>> tmp = replaceMap.keySet().stream().filter(uniqueMap::containsKey).map(e -> Arrays.stream(replaceMap.get(e)).map(f -> new MyPair(e, f)).toList()).toList();
//            List<List<MyPair>> list = cartesianProduct(tmp, 0).toList();
//
//            list.subList(0,1).forEach(e -> {
//                BlockSelection newPrefab = new BlockSelection();
//                newPrefab.setAnchor(prefab.getAnchorX(), prefab.getAnchorY(), prefab.getAnchorZ());
//
//                prefab.forEachBlock((x, y, z, block)-> {
//                    BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
//                    if (blockType != null) {
//                        e.forEach(p -> {
//                            if (Objects.equals(p.a(), blockType.getId())) {
//                                newPrefab.addBlockAtLocalPos(x, y, z, block.blockId(), block.rotation(), block.filler(), block.supportValue());
//                            }
//                        });
////                        uniqueMap.put(blockType.getId(), true);
//                    }
//                });
////                prefabStore.saveAssetPrefab(f);
//
//
////                Path outPath = prefabStore.getAssetRootPath().resolve(
////                        strippedPath + "_" + list.indexOf(e) + ".prefab.json");
////                LOGGER.info(String.valueOf(outPath));
////                prefabStore.savePrefab(outPath, newPrefab, false);
//
//                int lastSeparator = path.toString().indexOf(".");
//                String strippedPath = path.toString().substring(0, lastSeparator).replace("base", "generated");
//                Path outPath = baseAssetPack.getPackLocation().resolve(
//                            strippedPath + "_" + list.indexOf(e) + ".prefab.json");
//
//                LOGGER.info("\t-> " + outPath);
//
//                AssetPack sourcePack = PrefabStore.get().findAssetPackForPrefabPath(outPath);
//
//                LOGGER.info("findAssetPackForPrefabPath -> " + (sourcePack == null ? "null" : sourcePack.getPackLocation()));
//
//
//                try {
//                    prefabStore.savePrefab(outPath, newPrefab, false);
//                } catch (Exception ex) {
//                    LOGGER.info("exception thrown: " + ex);
//                }
//
//
////                Constants.UNIVERSE_PATH.resolve("worlds").resolve(this.getWorldName(context));
//
//                LOGGER.info(Constants.UNIVERSE_PATH.resolve("worlds").toString());
//
////                Arrays.stream(ps).toList().forEach(ttt -> {
////                    LOGGER.info(String.valueOf(ttt));
////                });
//
////                prefabStore.savePrefab(outPath, newPrefab, false);
//
//            });
//
//
////            tmp.forEach();
//
////        tmp.stream().reduce())
////            LOGGER.info(tmp.toString());
////
////            LOGGER.info(uniqueMap.toString());
////            LOGGER.info(prefab.toString());
//        });
//    }

    public static void onAssetPackRegister(AssetPackRegisterEvent event) {
        LOGGER.info("onAssetPackRegister");
    }

//    public static void onLoadedAssets(LoadedAssetsEvent<String, PrefabPropAsset, ?> event) {
//        LOGGER.info("onLoadedAssets");
//    }
//
//    public static void onAssetPackLoaded(AssetPackRegisterEvent event) {
//        LOGGER.info("onAssetPackLoaded:" + event.getAssetPack().getName());
//    }

//    public record MyPair(String a, String b) {
//        @Override
//        public String toString() {
//            return "Pair<" + a + ", " + b + ">";
//        }
//    }
}