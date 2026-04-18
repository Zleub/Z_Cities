package com.zleub.builtin.hytalegenerator.assets.props;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;

import javax.annotation.Nonnull;

public class CartesianPropAsset extends PrefabPropAsset.WeightedPathAsset {
    @Nonnull
    public static final AssetBuilderCodec<String, CartesianPropAsset> CODEC = AssetBuilderCodec.builder(
                    CartesianPropAsset.class,
                    CartesianPropAsset::new,
                    Codec.STRING,
                    (asset, id) -> asset.id = id,
                    config -> config.id,
                    (config, data) -> config.data = data,
                    config -> config.data
            )
            .append(new KeyedCodec<>("Weight", Codec.DOUBLE, true), (t, y) -> t.weight = y, t -> t.weight)
            .addValidator(Validators.greaterThanOrEqual(0.0))
            .add()
            .append(new KeyedCodec<>("Path", Codec.STRING, true), (t, out) -> t.path = out, t -> t.path)
            .add()
            .build();

    private String id;
    private AssetExtraInfo.Data data;
    private double weight = 1.0;
    private String path = "";

    public CartesianPropAsset() {
    }

    public String getId() {
        return this.id;
    }
}
