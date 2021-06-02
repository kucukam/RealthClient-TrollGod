package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.mixin.mixins.network.AccessorSPacketPlayerPosLook;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "NoRotate", category = Module.Category.MISC, color = 0xff4AE0AE)
public class NoRotate extends Module {

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (isNull()) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            AccessorSPacketPlayerPosLook packet = (AccessorSPacketPlayerPosLook) event.getPacket();
            packet.setPitch(mc.player.rotationPitch);
            packet.setYaw(mc.player.rotationYaw);
        }
    }

}
