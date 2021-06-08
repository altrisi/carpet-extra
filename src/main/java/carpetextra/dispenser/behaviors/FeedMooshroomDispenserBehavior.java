package carpetextra.dispenser.behaviors;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import carpetextra.mixins.MooshroomEntity_StatusEffectAccessorMixin;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class FeedMooshroomDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(true);
        ServerWorld world = pointer.getWorld();
        BlockPos frontBlockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

        // check if item is in SMALL_FLOWERS item tag
        if(ItemTags.SMALL_FLOWERS.contains(stack.getItem())) {
            // get brown mooshrooms in front of dispenser
            List<MooshroomEntity> mooshrooms = world.getEntitiesByType(EntityType.MOOSHROOM, new Box(frontBlockPos), EntityPredicates.VALID_LIVING_ENTITY.and((mooshroomEntity) -> {
                return ((MooshroomEntity) mooshroomEntity).getMooshroomType() == MooshroomEntity.Type.BROWN;
            }));

            // check all mooshrooms
            for(MooshroomEntity mooshroom : mooshrooms) {
                MooshroomEntity_StatusEffectAccessorMixin mooshroomAccessor = (MooshroomEntity_StatusEffectAccessorMixin) mooshroom;

                // check if mooshroom has no stew effect
                if(mooshroomAccessor.getStewEffect() == null) {
                    // get stew effect and length for flower
                    Optional<Pair<StatusEffect, Integer>> effect = mooshroomAccessor.invokeGetStewEffectFrom(stack);

                    // check if effect is present
                    if(effect.isPresent()) {
                        Pair<StatusEffect, Integer> pair = effect.get();

                        // set stew effect for mooshroom
                        mooshroomAccessor.setStatusEffect(pair.getLeft());
                        mooshroomAccessor.setStewEffectDuration(pair.getRight());

                        // play sound effect and show particles
                        world.playSound(null, frontBlockPos, SoundEvents.ENTITY_MOOSHROOM_EAT, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                        world.spawnParticles(ParticleTypes.EFFECT, mooshroom.getX() + mooshroom.getRandom().nextDouble() / 2.0D, mooshroom.getBodyY(0.5D), mooshroom.getZ() + mooshroom.getRandom().nextDouble() / 2.0D, 1, 0.0D, mooshroom.getRandom().nextDouble() / 5.0D, 0.0D, 0.01D);

                        // remove a flower and return stack
                        stack.decrement(1);
                        return stack;
                    }
                }
            }
        }

        // fail to dispense
        this.setSuccess(false);
        return stack;
    }
}
