package com.zleub.builtin.hytalegenerator.assets.material;

import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.material.OrthogonalRotationAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.directionality.DirectionalityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.directionality.PatternDirectionalityAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;

import javax.annotation.Nonnull;

public class FieldFunctionRotationAsset extends OrthogonalRotationAsset {
    private String id;
    private PatternDirectionalityAsset pattern;

    @Override
    public String getId() {
        return id;
    }

    @Nonnull
    public RotationTuple build(PropAsset.Argument argument) {
        this.pattern.build(DirectionalityAsset.argumentFrom(argument));
        return RotationTuple.of(Rotation.None, Rotation.None, Rotation.None);
    }


//    static {
//        CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(OrthogonalRotationAsset.class, OrthogonalRotationAsset::new, Codec.STRING, (asset, id) -> asset.id = id, (config) -> config.id, (config, data) -> config.data = data, (config) -> config.data).append(new KeyedCodec("Yaw", Rotation.CODEC, false), (asset, value) -> asset.yaw = value, (asset) -> asset.yaw).add()).append(new KeyedCodec("Pitch", Rotation.CODEC, false), (asset, value) -> asset.pitch = value, (asset) -> asset.pitch).add()).append(new KeyedCodec("Roll", Rotation.CODEC, false), (asset, value) -> asset.roll = value, (asset) -> asset.roll).add()).build();
//    }
}
