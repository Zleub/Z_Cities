package com.zleub;

import com.hypixel.hytale.builtin.hytalegenerator.assets.material.MaterialAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.material.OrthogonalRotationAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.event.PluginSetupEvent;

import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.zleub.builtin.hytalegenerator.assets.material.FieldFunctionRotationAsset;
import com.zleub.builtin.hytalegenerator.assets.props.AnotherOrienterPropAsset;
import com.zleub.events.ExampleEvent;


import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.logging.Logger;

import static com.zleub.PrefabGenerator.*;


public class ExamplePlugin extends JavaPlugin {
    static public Logger LOGGER = Logger.getLogger("ExamplePlugin");

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }
//    public static final NodeBuilder NODE_WEIGHTED_PATH = Renode.node("PrefabPropAsset.WeightedPathAsset", "Weighted Path Asset")
//                    .addCategory(HytaleGeneratorNodes.CATEGORY_PROPS);

//    public static final



//    static {
//        NodeVariantClass variant = Renode.variant("PrefabPropVariant", "Orange");
////        Renode.registerVariant(Renode.variant());
//
//        NodeBuilder[] nodes = new NodeBuilder[] {
//                HytaleGeneratorNodes.VARIANT_PROPS.variantNode("CartesianPermutation", "CartesianPermutation")
//                        .clearInputs()
//                        .addInput(HytaleGeneratorNodes.NODE_PROP_PREFAB_WEIGHTED_PATH.getInputs().getFirst())
//                        .addContent(Renode.stringContent("SubstitutionPath", "SubstitutionPath"))
//                        .addNodeOutput("Path", "Path", false, HytaleGeneratorNodes.NODE_PROP_PREFAB_WEIGHTED_PATH),
////                HytaleGeneratorNodes.NODE_PROP_PREFAB_WEIGHTED_PATH.withVariant(variant).getVariant().variantNode("Anothertest", "AnotherTitle").addNodeOutput("Path", "Path", false, HytaleGeneratorNodes.NODE_PROP_PREFAB_WEIGHTED_PATH)
////                HytaleGeneratorNodes.NODE_ORTHOGONAL_ROTATION.withVariant(new NodeVariantClass("aaa", "Yellow")),
////                HytaleGeneratorNodes.NODE_PROP_WEIGHTED_ENTRY.withVariant(new NodeVariantClass("bbb", "Blue"))
//        };
//
//        Arrays.stream(nodes).forEach(Renode::registerNode);
//    }

    @Override
    protected void setup() {
//        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
//        this.getEventRegistry().registerGlobal(PluginSetupEvent.class, e -> ExampleEvent.onPluginSetup(e, this.getCodecRegistry(PropAsset.CODEC)));
//        this.getEventRegistry().registerGlobal(StartWorldEvent.class, ExampleEvent::onStartWorldEvent);

//        getCodecRegistry()
//        getCodecRegistry(PropAsset.CODEC).register("Anothertest", AnotherOrienterPropAsset.class, AnotherOrienterPropAsset.CODEC);
//        getCodecRegistry(OrthogonalRotationAsset.CODEC).register("Anothertest", FieldFunctionRotationAsset.class, FieldFunctionRotationAsset.CODEC);

    }

    @Override
    protected void start() {
//        super.start();
//
//        LOGGER.info("Permutations size: " + getPermutationsSize());
//        LOGGER.info("Cartesian size: " + cartesianList.stream().reduce(0, (acc, v) -> acc + v.size(), Integer::sum ));
////
//        generateFromAssetMap(buildAssetMap());
//        LOGGER.info(String.format("Saved: %d | Skipped: %d", saved, skipped));
    }
}