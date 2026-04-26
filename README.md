# Z-Cities

This project is my contribution to the Hytale Modjam 2026, worldgen category.

## HowTo

As of today, i dont have any way to incorporate the biome file into a new world. You will have to make a creative world and run `/instance spawn Cities` to get it running.

## Contributing

First off, start by forking the project and importing it into your IDE.

Its assumed you have your own version of the hytale server decompiled and locally installed under
the groupID `com.hypixel.hytale` and version matching the gradle.properties (eg `2026.03.26-89796e57b`)

Take a look at the gradle.properties file and adjust the `hytaleInstallPath` to your own install path

You should find a few gradle tasks ready to be used.

- `devMode` is the main task you should use to take advantage of Hytale's hot reloading of changed assets. It execute sequentially `copyDevAssetpack`, `generateAssets` and `installDevMode`
- `installDevMode` will copy the content of `assetpack` into `<hytaleInstallPath>/UserData/Mods/zcities_devmode`
- `generateAssets` will execute the code under the `assetGen` source set to generate prefab assets from the `assetpack/Server/Prefabs/base` directory.
- `copyDevAssetpack` will copy the `<hytaleInstallPath>/UserData/Mods/zcities_devmode` folder into the project's local `assetpack`

### Workflow

Upon starting, you should run the `installDevMode` command once to get setup.

After that, you should focus your asset editing to the `<hytaleInstallPath>/UserData/Mods/zcities_devmode` folder, as it will get copied back to the project by the `installDevMode` task.
This is leverage Hytale's assets reloading when they are changed so every editor shipped with the game is still valuable.

## Thanks
The hytale dev team<br>
hytalemodding.dev team<br>
hytale-docs.com team<br>
chatgpt dev team (code analysis, ci/cd workflow, image generation)<br>
ItsVerday for its Renode plugin (even if i dont have time to properly dig it, that was very instructive)<br>
NoCube for its base file for light emissive blocks<br>
Darkknight4303 for brainstorming and feedback
pdimagearchive.org for public domain images
