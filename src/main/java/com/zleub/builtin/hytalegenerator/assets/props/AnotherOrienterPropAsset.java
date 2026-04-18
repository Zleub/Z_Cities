package com.zleub.builtin.hytalegenerator.assets.props;

import com.hypixel.hytale.builtin.hytalegenerator.assets.material.OrthogonalRotationAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.EmptyPropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.DirectScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.OrienterProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.OrienterProp.SelectionMode;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class AnotherOrienterPropAsset extends PropAsset {
    @Nonnull
    public static final BuilderCodec<AnotherOrienterPropAsset> CODEC;
    @Nonnull
    private PropAsset propAsset = new EmptyPropAsset();
    @Nonnull
    private PatternAsset patternAsset = new ConstantPatternAsset();
    @Nonnull
    private ScannerAsset scannerAsset = new DirectScannerAsset();
    @Nonnull
    private OrthogonalRotationAsset[] rotationAssets = new OrthogonalRotationAsset[0];
    @Nonnull
    private OrienterProp.SelectionMode selectionMode;
    @Nonnull
    private String seed;

    public AnotherOrienterPropAsset() {
        this.selectionMode = SelectionMode.FIRST_VALID;
        this.seed = "";
    }

    @Nonnull
    public Prop build(@Nonnull PropAsset.Argument argument) {
        if (super.skip()) {
            return EmptyProp.INSTANCE;
        } else {
            SeedBox seedBox = argument.parentSeed.child(this.seed);
            Prop prop = this.propAsset.build(argument);
            Pattern pattern = this.patternAsset.build(PatternAsset.argumentFrom(argument));
            Scanner scanner = this.scannerAsset.build(ScannerAsset.argumentFrom(argument));
            List<RotationTuple> rotations = new ArrayList<>(this.rotationAssets.length);

            for(int i = 0; i < this.rotationAssets.length; ++i) {
                RotationTuple rotation = this.rotationAssets[i].build();
                rotations.add(rotation);
            }

            return new OrienterProp(rotations, prop, pattern, scanner, argument.materialCache, this.selectionMode, (Integer)seedBox.createSupplier().get());
        }
    }

    public void cleanUp() {
        this.propAsset.cleanUp();
    }

    static {
        BuilderCodec.Builder<AnotherOrienterPropAsset> builder = BuilderCodec.builder(
                AnotherOrienterPropAsset.class,
                AnotherOrienterPropAsset::new,
                PropAsset.ABSTRACT_CODEC
        );

        builder.append(new KeyedCodec<>("Prop", PropAsset.CODEC, true),
                        (asset, val) -> asset.propAsset = val,
                        asset -> asset.propAsset)
                .add();

        builder.append(new KeyedCodec<>("Rotations",
                                new ArrayCodec<>(OrthogonalRotationAsset.CODEC, OrthogonalRotationAsset[]::new), true),
                        (asset, val) -> asset.rotationAssets = val,
                        asset -> asset.rotationAssets)
                .add();

        builder.append(new KeyedCodec<>("Pattern", PatternAsset.CODEC, true),
                        (asset, val) -> asset.patternAsset = val,
                        asset -> asset.patternAsset)
                .add();

        builder.append(new KeyedCodec<>("Scanner", ScannerAsset.CODEC, true),
                        (asset, val) -> asset.scannerAsset = val,
                        asset -> asset.scannerAsset)
                .add();

        builder.append(new KeyedCodec<>("SelectionMode",
                                new EnumCodec<>(OrienterProp.SelectionMode.class), true),
                        (asset, val) -> asset.selectionMode = val,
                        asset -> asset.selectionMode)
                .add();

        builder.append(new KeyedCodec<>("Seed", Codec.STRING, false),
                        (asset, val) -> asset.seed = val,
                        asset -> asset.seed)
                .add();

        CODEC = builder.build();
    }
}
