package com.zleub;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import io.sentry.util.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OfflineAssetGenerator {
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

    public static List<List<Pair>> cartesianList = cartesianProduct(REPLACE_MAP.keySet().stream().map(e -> Arrays.stream(REPLACE_MAP.get(e)).map(f -> new Pair(e, f)).toList()).toList(), 0).toList();
    public static int skipped = 0;
    public static int saved = 0;

    public record Pair(String a, String b) {
        @Override
        public String toString() {
            return "Pair<" + a + ", " + b + ">";
        }
    }

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

    public static Path makeGeneratedPath(Path input, Supplier<String> generatedID) {
        // TODO: change the last separator logic as directory can contain '.' char (AssetPackSaveBrowser::INVALID_FILENAME_CHARS)
        int lastSeparator = input.toString().indexOf(".");
        String strippedPath = input.toString().substring(0, lastSeparator).replace("base", "generated");

        // TODO: find a replacement for Path::resolve since an absolute path as 2nd argument breaks the logic
        if (strippedPath.startsWith("/"))
            strippedPath = strippedPath.substring(1);

        // TODO: find a good and lightweigth way to id prefabs
        return Path.of(strippedPath + "_" + generatedID.get() + ".prefab.json");
    }

    public static void main(String[] args) {
        System.out.printf("Permutations size: %s\n", getPermutationsSize());
        System.out.printf("Cartesian size: %s\n", cartesianList.stream().reduce(0, (acc, v) -> acc + v.size(), Integer::sum ));

        List<Path> results = new ArrayList<>();
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        Path root = Path.of(args[0]).resolve("Server").resolve("Prefabs");

        FileUtils.deleteRecursively(new File(root.resolve("generated").toString()));

        try {
            Files.walkFileTree(
                    root.resolve("base"),
                    new SimpleFileVisitor<Path>() {
                        @Nonnull
                        public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                            String fileName = file.getFileName().toString();
                            if (fileName.endsWith(".prefab.json")) {
                                results.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        } catch (IOException var7) {
            System.out.printf("Error: %s\n", var7);
        }

        System.out.printf("Nb prefabs found: %s\n", results.size());

        results.forEach(path -> {
                File f = new File(path.toString());
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));

                    JsonElement jsonElement = JsonParser.parseReader(new JsonReader(reader));
                    reader.close();

                    JsonArray blocks = jsonElement.getAsJsonObject()
                            .get("blocks")
                            .getAsJsonArray();

                    cartesianList.forEach(u -> {
                        Integer reduce = u.stream()
                                .reduce(0, (acc, val) -> {
                                    int size = blocks.asList().stream().filter(b -> {
                                        String name = b.getAsJsonObject()
                                                .get("name")
                                                .getAsString();

                                        return name.equals(val.a);
                                    }).toList().size();

                                    return size > 0 ? acc + 1 : acc;
                                }, Integer::sum);

                        if (reduce == 0) {
                            skipped += 1;
                            return ;
                        }


                        JsonElement copy = jsonElement.deepCopy();
                        JsonArray blocks_copy = copy.getAsJsonObject()
                                .get("blocks")
                                .getAsJsonArray();
                        Path outPath = makeGeneratedPath(path, () -> String.valueOf(cartesianList.indexOf(u)));

                        blocks_copy.forEach(b -> {
                            String name = b.getAsJsonObject()
                                    .get("name")
                                    .getAsString();

                            Optional<Pair> pair = u.stream()
                                    .filter(p -> p.a()
                                            .equals(name))
                                    .findFirst();

                            pair.ifPresent(value -> {
                                b.getAsJsonObject().addProperty("name", value.b);
        //                                System.out.printf("%s -> %s\n", name, value.b);
                            });
                        });

                        try {
                            Files.createDirectories(outPath.getParent());
                            FileWriter fileWriter = new FileWriter("./" + outPath);
                            gson.toJson(copy, fileWriter);
                            fileWriter.flush();
                            saved += 1;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });


                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

        System.out.printf("Skipped: %s\n", skipped);
        System.out.printf("Saved: %s\n", saved);
    }
}
