package com.tajlpawa.ageofexpansionmodpackfix.ftbquests;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.translation.TranslationKey;
import dev.ftb.mods.ftblibrary.util.TextComponentParser;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

public final class QuestCompletionAnnouncements {
    private QuestCompletionAnnouncements() {}

    public static void register() {
        ObjectCompletedEvent.QUEST.register(event -> {
            if (event.getQuest().getQuestFile() instanceof ServerQuestFile file) {
                announce(event, file.server.getPlayerList().getPlayers());
            }
            return EventResult.pass();
        });
    }

    // FTB emits this once on the incomplete-to-complete transition; use its own notification roster.
    static void announce(ObjectCompletedEvent.QuestEvent event, Collection<? extends ServerPlayer> audience) {
        if (!event.getQuest().getQuestFile().isServerSide()) return;
        var online = new HashSet<UUID>();
        audience.forEach(player -> online.add(player.getUUID()));
        var announced = new HashSet<UUID>();
        for (var completedBy : event.getNotifiedPlayers()) {
            if (!online.contains(completedBy.getUUID()) || !announced.add(completedBy.getUUID())) continue;
            for (var recipient : audience) {
                recipient.sendSystemMessage(message(completedBy.getDisplayName(), event.getQuest(), recipient.clientInformation().language()));
            }
        }
    }

    // FTB's subtitle getter and rich GUI parser are client-only; use network-safe chat components.
    private static Component parseText(String text, net.minecraft.core.HolderLookup.Provider registries) {
        String trimmed = text.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                Component parsed = Component.Serializer.fromJson(trimmed, registries);
                if (parsed != null) return parsed;
            } catch (com.google.gson.JsonParseException ignored) {
                // Non-JSON bracketed task text remains ordinary formatted text.
            }
        }
        return TextComponentParser.parse(text, Component::literal);
    }

    static Component message(Component playerName, Quest quest, String locale) {
        var translations = quest.getQuestFile().getTranslationManager();
        Component title = translations.getStringTranslation(quest, locale, TranslationKey.TITLE)
                .filter(text -> !text.isBlank())
                .map(text -> parseText(text, quest.getQuestFile().holderLookup()))
                .orElseGet(quest::getTitle);
        Component subtitle = translations.getStringTranslation(quest, locale, TranslationKey.QUEST_SUBTITLE)
                .map(text -> parseText(text, quest.getQuestFile().holderLookup()))
                .orElseGet(Component::empty);
        var label = Component.literal("[").append(title.copy()).append("]").withStyle(ChatFormatting.GREEN);
        if (!subtitle.getString().isBlank()) {
            label.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, subtitle.copy())));
        }
        return Component.translatableWithFallback("chat.ageofexpansionmodpackfix.quest_completed", "%s完成了任务%s", playerName.copy(), label);
    }
}
