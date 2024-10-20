package settingdust.resource_condition.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.Reader;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @ModifyExpressionValue(
        method = "loadRegistryContents",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/gson/JsonParser;parseReader(Ljava/io/Reader;)Lcom/google/gson/JsonElement;"
        )
    )
    private static JsonElement resource_condition$apply(
        final JsonElement original,
        RegistryOps.RegistryInfoLookup lookup,
        @Cancellable CallbackInfo ci,
        @Local Reader reader
    ) throws IOException {
        if (original.isJsonObject() && !ResourceConditions.objectMatchesConditions(original.getAsJsonObject())) {
            reader.close();
            ci.cancel();
        }
        return original;
    }
}
