package me.hollow.sputnik.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

@ModuleManifest(label = "SelfFill", category = Module.Category.COMBAT, color = 0xff0030ff)
public class Burrow extends Module {

    private final Setting<Float> height = register(new Setting<>("Height", 5F, -5F, 5F));
    private final Setting<Boolean> preferEChests = register(new Setting<>("Prefer EChests", true));

    private static Burrow INSTANCE;

    public BlockPos startPos;

    private int obbySlot = -1;

    public Burrow() {
        INSTANCE = this;
    }

    public static Burrow getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (isNull()) {
            disable();
            return;
        }

        obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int eChestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if ((preferEChests.getValue() || obbySlot == -1) && eChestSlot != -1) {
            obbySlot = eChestSlot;
        } else {
            obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (obbySlot == -1) {
                MessageUtil.sendClientMessage(ChatFormatting.RED + "<Burrow> Toggling, No obsidian.", -551);
                disable();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (isNull()) {
            return;
        }

        int startSlot = mc.player.inventory.currentItem;

        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        startPos = new BlockPos(mc.player.getPositionVector());

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16, mc.player.posZ, true));

        final boolean onEChest = mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (BlockUtil.placeBlock(onEChest ? startPos.up() : startPos) && !mc.player.capabilities.isCreativeMode)
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, false));

        if (startSlot != -1)
            mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));

        disable();
    }

}
