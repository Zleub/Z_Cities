package com.zleub;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.zleub.events.ExampleEvent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.zleub.ExamplePlugin.LOGGER;

public class PrefabGenerator {
    public static Map<String, String[]> REPLACE_MAP = new HashMap<>();
    static {
        REPLACE_MAP.put("Soil_Clay", new String[]{
                // "Soil_Clay",
                // "Soil_Clay_Black",
                // "Soil_Clay_Blue",
                // "Soil_Clay_Cyan",
                // "Soil_Clay_Green",
                // "Soil_Clay_Grey",
                // "Soil_Clay_Lime",
                // "Soil_Clay_Ocean",
                // "Soil_Clay_Orange",
                // "Soil_Clay_Pink",
                // "Soil_Clay_Purple",
                // "Soil_Clay_Red",
                // "Soil_Clay_White",
                // "Soil_Clay_Yellow",
                "Rock_Chalk",
                "Rock_Marble",
                // "Rock_Ice",
                // "Rock_Aqua",
                // "Rock_Basalt",
                "Rock_Calcite",
                // "Rock_Ledge"
                "Rock_Lime",
                "Rock_Marble",
                "Rock_Quartzite",
                "Rock_Salt",
                // "Rock_Sandstone",
                // "Rock_Sandstone_Red",
                // "Rock_Sandstone_White",
                // "Rock_Shale",
                // "Rock_Slate",
                // "Rock_Stone",
                // "Rock_Volcanic"

                // "Prototype_Rock_Concrete",
        });

        REPLACE_MAP.put("Rock_Crystal_White_Block", new String[]{
                "Rock_Crystal_Blue_Block",
                "Rock_Crystal_Cyan_Block",
                "Rock_Crystal_Green_Block",
                "Rock_Crystal_Pink_Block",
                "Rock_Crystal_Purple_Block",
                "Rock_Crystal_Red_Block",
                "Rock_Crystal_White_Block",
                "Rock_Crystal_Yellow_Block",
        });

//        REPLACE_MAP.put("Wood_Hardwood_Planks_Half", new String[]{
//                "Wood_Hardwood_Planks_Half",
//                "Wood_Oak_Trunk_Half"
//        });
    }

    public static String SELF = "com.zleub:ExamplePlugin";
    public static List<String> searchPacks = new ArrayList<>();
    static {
        searchPacks.add(SELF);
        searchPacks.add("zleub:cities_debug");
    };

    public static List<List<Pair>> cartesianList = cartesianProduct(REPLACE_MAP.keySet().stream().map(e -> Arrays.stream(REPLACE_MAP.get(e)).map(f -> new Pair(e, f)).toList()).toList(), 0).toList();
    public static Map<AssetPack, List<Path>> assetMap;
    public static int skipped = 0;
    public static int saved = 0;

    public static <T> Stream<List<T>> cartesianProduct(List<List<T>> sets, int index) {
        if (index == sets.size()) {
            List<T> emptyList = new ArrayList<>();
            return Stream.of(emptyList);
        }
        List<T> currentSet = sets.get(index);
        return currentSet.stream().flatMap(element -> cartesianProduct(sets, index+1)
                .map(list -> {
                    List<T> newList = new ArrayList<>(list);
                    newList.add(0, element);
                    return newList;
                }));
    }

    public static int getPermutationsSize() {
        return REPLACE_MAP.values().stream().reduce(1, (acc, val) -> acc * val.length, Integer::sum);
    }

    public static Map<AssetPack, List<Path>> buildAssetMap() {
        if (assetMap != null)
            return assetMap;

        PrefabStore prefabStore = PrefabStore.get();
        assetMap = new HashMap<>();

        searchPacks.forEach(e -> {
            AssetPack assetPack = AssetModule.get().getAssetPack(e);
            if (assetPack != null) {
                List<Path> results = new ArrayList<>();
                Path root = prefabStore.getAssetPrefabsPathForPack(assetPack);
                Path base = root.resolve("base");

                try {
                    Files.walkFileTree(
                            base,
                            new SimpleFileVisitor<Path>() {
                                @Nonnull
                                public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                                    String fileName = file.getFileName().toString();
                                    if (fileName.endsWith(".prefab.json"))
                                        results.add(base.resolve(file));
                                    return FileVisitResult.CONTINUE;
                                }
                            }
                    );
                } catch (IOException var7) {
                }

                assetMap.put(assetPack, results);
            }
        });

        return assetMap;
    }

    public static Path makeGeneratedPath(Path input, AssetPack destination, Supplier<String> generatedID) {
        // TODO: change the last separator logic as directory can contain '.' char (AssetPackSaveBrowser::INVALID_FILENAME_CHARS)
        int lastSeparator = input.toString().indexOf(".");
        String strippedPath = input.toString().substring(0, lastSeparator).replace("base", "generated");

        // TODO: find a replacement for Path::resolve since an absolute path as 2nd argument breaks the logic
        if (strippedPath.startsWith("/"))
            strippedPath = strippedPath.substring(1);

        // TODO: find a good and lightweigth way to id prefabs
        return destination.getPackLocation().resolve(
                strippedPath + "_" + generatedID.get() + ".prefab.json");
    }

    public static void generateFromAssetMap(Map<AssetPack, List<Path>> assetMap) {
        PrefabStore prefabStore = PrefabStore.get();

        assetMap.forEach( (pack,paths) -> {
            AssetPack destination = AssetModule.get().getAssetPack(pack.getManifest().getGroup() + ":" + pack.getManifest().getName() + "_generated");
            LOGGER.info(destination.toString());

            paths.forEach(path -> {
                BlockSelection prefab = prefabStore.getPrefab(path);
                cartesianList.forEach(e -> {
                    Path outPath = makeGeneratedPath(path, destination, () -> String.valueOf(cartesianList.indexOf(e)));
//                    LOGGER.info("Save generated: " + outPath);

                    if (Files.exists(outPath)) {
                        skipped += 1;
                        return ;
                    }

                    BlockSelection newPrefab = new BlockSelection();
                    newPrefab.setAnchor(prefab.getAnchorX(), prefab.getAnchorY(), prefab.getAnchorZ());

                    prefab.forEachBlock((x, y, z, block)-> {
                        BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
                        if (blockType != null) {
                            e.forEach(p -> {
                                if (Objects.equals(p.a(), blockType.getId())) {
                                    int blockId = BlockType.getBlockIdOrUnknown(p.b(), "Failed to find block '%s' in chunk section!", p.b());
                                    newPrefab.addBlockAtLocalPos(x, y, z, blockId, block.rotation(), block.filler(), block.supportValue());
                                }
                            });
                        }
                    });

//                    HytaleServer.SCHEDULED_EXECUTOR.execute(() -> {

                        prefabStore.savePrefab(outPath, newPrefab, true);

                        saved += 1;

//                    });
                });
            });
        });
    }

    public record Pair(String a, String b) {
        @Override
        public String toString() {
            return "Pair<" + a + ", " + b + ">";
        }
    }
}
