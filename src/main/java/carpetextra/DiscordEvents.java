package carpetextra;

import java.util.List;

import carpet.CarpetServer;
import carpet.script.CarpetEventServer.Event;
import carpet.script.value.ValueConversions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
public class DiscordEvents extends Event
{
    public static void noop() {} //to load events before scripts do
    public DiscordEvents(String name, int reqArgs, boolean isGlobalOnly) {
        super(name, reqArgs, isGlobalOnly);
    }
    public void onDiscordMessage(BlockPos pos, ItemStack item, ItemStack originalItem) {}
    public static DiscordEvents DISPENSER_PLAYS_RECORD = new DiscordEvents("dispenser_plays_record", 3, true) {
        @Override
        public void onDiscordMessage(BlockPos pos, ItemStack item, ItemStack originalItem) {
            handler.call(() -> List.of(ValueConversions.of(pos), ValueConversions.of(item), ValueConversions.of(originalItem)),
                    () ->  CarpetServer.minecraft_server.getCommandSource().withWorld(CarpetServer.minecraft_server.getWorld(World.OVERWORLD))
            );
        }
    };
}
