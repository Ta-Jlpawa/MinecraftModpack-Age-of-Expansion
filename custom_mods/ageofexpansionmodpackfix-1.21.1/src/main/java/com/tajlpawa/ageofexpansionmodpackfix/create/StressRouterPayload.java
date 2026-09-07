package com.tajlpawa.ageofexpansionmodpackfix.create;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record StressRouterPayload(int menuId, int inputA, int inputB, int ratio, int speedA, int speedB) implements CustomPacketPayload {
    public static final Type<StressRouterPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(StressRouterContent.MOD_ID, "configure_stress_router"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StressRouterPayload> CODEC = new StreamCodec<>() {
        @Override public StressRouterPayload decode(RegistryFriendlyByteBuf buf) {
            return new StressRouterPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
        }
        @Override public void encode(RegistryFriendlyByteBuf buf, StressRouterPayload value) {
            buf.writeVarInt(value.menuId); buf.writeVarInt(value.inputA); buf.writeVarInt(value.inputB);
            buf.writeVarInt(value.ratio); buf.writeVarInt(value.speedA); buf.writeVarInt(value.speedB);
        }
    };
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (packet, context) -> context.enqueueWork(() -> {
            var player = context.player();
            if (player.containerMenu instanceof StressRouterMenu menu && menu.containerId == packet.menuId
                    && menu.router != null && menu.stillValid(player) && player.mayBuild()) {
                menu.router.configure(packet.inputA, packet.inputB, packet.ratio, packet.speedA, packet.speedB);
                menu.broadcastChanges();
            }
        }));
    }
}
