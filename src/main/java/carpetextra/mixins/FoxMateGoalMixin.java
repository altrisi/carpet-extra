package carpetextra.mixins;

import carpetextra.CarpetExtraSettings;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.entity.passive.FoxEntity$MateGoal")
public abstract class FoxMateGoalMixin extends AnimalMateGoal {


	public FoxMateGoalMixin(AnimalEntity fox, double speed) {
        super(fox, speed);
    }

    @Inject(
        method = "breed()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
            ordinal = 0
        ),
        cancellable = true
    )
    protected void preventDispenserSpawningExperience(CallbackInfo ci) {
        ServerPlayerEntity player = super.animal.getLovingPlayer();
        if (player == null && super.mate != null) {
            player = super.mate.getLovingPlayer();
        }
        if (player == null && CarpetExtraSettings.dispensersFeedAnimals) {
            ci.cancel();
        }
    }
}