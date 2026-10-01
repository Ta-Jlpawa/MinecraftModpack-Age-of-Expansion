package com.tajlpawa.ageofexpansionmodpackfix.ftbquests;

import com.mojang.authlib.GameProfile;
import dev.architectury.event.EventActor;
import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.translation.TranslationKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;

public final class QuestAnnouncementTests {
    public static Collection<TestFunction> tests() {
        if (!ModList.get().isLoaded("ftbquests")) return List.of();
        var tests = new ArrayList<TestFunction>();
        add(tests, "quest_announcement_localized_title_and_hover", QuestAnnouncementTests::text);
        add(tests, "quest_announcement_all_players_and_notification_roster", QuestAnnouncementTests::broadcast);
        add(tests, "quest_announcement_completion_event_once", QuestAnnouncementTests::transition);
        add(tests, "architectury_listener_mutation_snapshot", QuestAnnouncementTests::listenerMutation);
        return tests;
    }
    public interface TickProbe { void tick(); }
    public interface ResultProbe { EventResult fire(); }
    private static void listenerMutation(GameTestHelper h) {
        var event = dev.architectury.event.EventFactory.createLoop(TickProbe.class);
        var calls = new ArrayList<String>();
        TickProbe late = () -> calls.add("late");
        TickProbe second = () -> calls.add("second");
        event.register(() -> { calls.add("first"); if (!event.isRegistered(late)) event.register(late); });
        event.register(second);
        event.invoker().tick();
        h.assertTrue(calls.equals(List.of("first", "second")), "Added listener waits until next dispatch");
        calls.clear();
        event.invoker().tick();
        h.assertTrue(calls.equals(List.of("first", "second", "late")), "New invoker includes added listener");
        event.clearListeners();
        calls.clear();
        event.register(() -> { calls.add("remove"); event.unregister(second); });
        event.register(second);
        event.invoker().tick();
        h.assertTrue(calls.equals(List.of("remove", "second")), "Removal does not mutate active dispatch");
        calls.clear();
        event.invoker().tick();
        h.assertTrue(calls.equals(List.of("remove")), "Removal applies to subsequent dispatch");
        event.clearListeners();
        calls.clear();
        event.register(() -> { calls.add("clear"); event.clearListeners(); });
        event.register(second);
        event.invoker().tick();
        h.assertTrue(calls.equals(List.of("clear", "second")), "Clearing does not mutate active dispatch");
        calls.clear();
        event.invoker().tick();
        h.assertTrue(calls.isEmpty(), "Clear applies to next dispatch");
        var results = dev.architectury.event.EventFactory.createEventResult(ResultProbe.class);
        results.register(() -> { calls.add("interrupt"); return EventResult.interruptTrue(); });
        results.register(() -> { calls.add("unexpected"); return EventResult.pass(); });
        h.assertTrue(results.invoker().fire().isTrue() && calls.equals(List.of("interrupt")), "Result short-circuit order preserved");
        results.clearListeners();
        h.succeed();
    }
    private static void add(Collection<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> run) {
        tests.add(new TestFunction("ageofexpansionmodpackfix:quest_announcements", "ageofexpansionmodpackfix:" + name,
                "ageofexpansionmodpackfix:router_empty", 100, 0, true, run));
    }
    private static Quest quest(GameTestHelper h) {
        var file = new ServerQuestFile(h.getLevel().getServer());
        var group = new ChapterGroup(910001, file);
        var chapter = new Chapter(910002, file, group);
        var quest = new Quest(910003, chapter);
        chapter.addQuest(quest);
        file.getTranslationManager().addTranslation(quest, "zh_cn", TranslationKey.TITLE, "机械动力入门");
        file.getTranslationManager().addTranslation(quest, "zh_cn", TranslationKey.QUEST_SUBTITLE, "制作你的第一个齿轮\n开始传递动力");
        file.getTranslationManager().addTranslation(quest, "en_us", TranslationKey.TITLE, "First gears");
        file.getTranslationManager().addTranslation(quest, "en_us", TranslationKey.QUEST_SUBTITLE, "Craft your first cogwheel");
        return quest;
    }
    private static Component label(Component message) { return (Component) ((TranslatableContents) message.getContents()).getArgs()[1]; }
    private static void text(GameTestHelper h) {
        var quest = quest(h);
        var message = QuestCompletionAnnouncements.message(Component.literal("Alex"), quest, "zh_cn");
        var title = label(message);
        h.assertTrue(title.getString().equals("[机械动力入门]"), "Localized title with brackets");
        h.assertTrue(title.getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT).getString()
                .equals("制作你的第一个齿轮\n开始传递动力"), "Exact multiline subtitle hover");
        h.assertTrue(((TranslatableContents) message.getContents()).getFallback().equals("%s完成了任务%s"), "Exact fallback chat format");
        h.assertTrue(label(QuestCompletionAnnouncements.message(Component.literal("Alex"), quest, "en_us")).getString().equals("[First gears]"), "English recipient translation");
        quest.getQuestFile().getTranslationManager().addTranslation(quest, "zh_cn", TranslationKey.QUEST_SUBTITLE, "");
        h.assertTrue(label(QuestCompletionAnnouncements.message(Component.literal("Alex"), quest, "zh_cn")).getStyle().getHoverEvent() == null, "No blank hover box");
        h.succeed();
    }
    private static void broadcast(GameTestHelper h) {
        var quest = quest(h);
        var a = new RecordingPlayer(h.getLevel(), "TeamA");
        var b = new RecordingPlayer(h.getLevel(), "TeamB");
        var observer = new RecordingPlayer(h.getLevel(), "OtherTeam");
        var offline = new RecordingPlayer(h.getLevel(), "Offline");
        var team = new TeamData(UUID.randomUUID(), quest.getQuestFile());
        var progress = new QuestProgressEventData<>(new Date(), team, quest, List.of(a, b), List.of(a, b, a, offline));
        var event = new ObjectCompletedEvent.QuestEvent(progress);
        var audience = List.of(a, b, observer);
        QuestCompletionAnnouncements.announce(event, audience);
        for (var recipient : audience) {
            h.assertTrue(recipient.messages.size() == 2, "Everyone including another team receives exactly two announcements");
            h.assertTrue(((Component) ((TranslatableContents) recipient.messages.getFirst().getContents()).getArgs()[0]).getString().equals("TeamA"), "First completer attribution");
            h.assertTrue(((Component) ((TranslatableContents) recipient.messages.get(1).getContents()).getArgs()[0]).getString().equals("TeamB"), "Second completer attribution");
        }
        var silent = new ObjectCompletedEvent.QuestEvent(new QuestProgressEventData<>(new Date(), team, quest, List.of(a), List.of()));
        QuestCompletionAnnouncements.announce(silent, audience);
        h.assertTrue(observer.messages.size() == 2, "No announcement for an empty notification roster");
        h.succeed();
    }
    private static void transition(GameTestHelper h) {
        var quest = quest(h);
        var team = new TeamData(UUID.randomUUID(), quest.getQuestFile());
        var count = new AtomicInteger();
        EventActor<ObjectCompletedEvent.QuestEvent> listener = event -> {
            if (event.getQuest() == quest) count.incrementAndGet();
            return EventResult.pass();
        };
        ObjectCompletedEvent.QUEST.register(listener);
        try {
            var progress = new QuestProgressEventData<>(new Date(), team, quest, List.of(), List.of());
            quest.onCompleted(progress);
            quest.onCompleted(progress);
            h.assertTrue(count.get() == 1, "Real FTB completion event fires once, not once per repeated check");
        } finally { ObjectCompletedEvent.QUEST.unregister(listener); }
        h.succeed();
    }
    private static final class RecordingPlayer extends FakePlayer {
        final List<Component> messages = new ArrayList<>();
        RecordingPlayer(ServerLevel level, String name) { super(level, new GameProfile(UUID.randomUUID(), name)); }
        @Override public void sendSystemMessage(Component message) { messages.add(message); }
    }
}
