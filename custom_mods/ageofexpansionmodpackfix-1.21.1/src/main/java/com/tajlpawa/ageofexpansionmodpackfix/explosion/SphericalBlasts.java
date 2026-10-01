package com.tajlpawa.ageofexpansionmodpackfix.explosion;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ChunkPos;

// 爆炸只触发一次，球体内的方块按半径逐层拆除并限制每刻工作量。
@EventBusSubscriber(modid = AgeofExpansionModpackFix.MODID)
public final class SphericalBlasts extends SavedData {
    private static final int MAX_VISITED_PER_TICK = 16_384;
    private static final int MAX_CHANGED_PER_TICK = 1_024;
    private static final String DATA_NAME = "ageofexpansion_spherical_blasts";

    public enum Kind { FISSION, ORITECH }

    private final Deque<Job> jobs = new ArrayDeque<>();

    public static void start(ServerLevel level, BlockPos center, int radius, Kind kind) {
        int limitedRadius = Math.clamp(radius, 1, 128);
        SphericalBlasts data = get(level);
        for (Job job : data.jobs) if (job.center.equals(center) && job.kind == kind && job.createdTick == level.getGameTime()) return;
        data.jobs.addLast(new Job(center.immutable(), limitedRadius, kind, level.getGameTime()));
        data.setDirty();
        playInitialEffect(level, center, limitedRadius, kind);
    }

    public static int radiusForAssemblies(int assemblies) {
        return (int) Math.min(128L, Math.max(1L, (long) assemblies * 2L));
    }

    public static int randomDiameterInChunks(ServerLevel level, boolean small) {
        return small ? level.random.nextIntBetweenInclusive(4, 6) : level.random.nextIntBetweenInclusive(7, 13);
    }

    private static SphericalBlasts get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SphericalBlasts::new, SphericalBlasts::load, null), DATA_NAME);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        SphericalBlasts data = get(level);
        if (data.jobs.isEmpty()) return;

        Job job = data.jobs.removeFirst();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int visited = 0;
        int changed = 0;
        while (visited < MAX_VISITED_PER_TICK && changed < MAX_CHANGED_PER_TICK && job.next(level, cursor)) {
            visited++;
            if (cursor.getY() < level.getMinBuildHeight() || cursor.getY() >= level.getMaxBuildHeight()) continue;
            if (!level.hasChunkAt(cursor)) {
                job.defer(cursor);
                continue;
            }
            BlockState state = level.getBlockState(cursor);
            if (job.outside(cursor)) {
                changed += BlastBoundaryEffects.apply(level, cursor, state);
                continue;
            }
            if (state.isAir() || state.is(Blocks.BEDROCK)) continue;
            if (level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3)) {
                changed++;
                int fireChance = job.kind == Kind.FISSION ? 3 : 33;
                if (job.onSurface(cursor) && level.random.nextInt(fireChance) == 0
                        && Blocks.FIRE.defaultBlockState().canSurvive(level, cursor)) {
                    if (level.setBlock(cursor, Blocks.FIRE.defaultBlockState(), 3)) changed++;
                }
            }
        }
        if (!job.finished()) data.jobs.addLast(job);
        data.setDirty();
    }

    private static void playInitialEffect(ServerLevel level, BlockPos center, int radius, Kind kind) {
        ResourceLocation nukeSound = ResourceLocation.fromNamespaceAndPath("oritech", "nuke_explosion");
        SoundEvent sound = kind == Kind.ORITECH && BuiltInRegistries.SOUND_EVENT.containsKey(nukeSound)
                ? BuiltInRegistries.SOUND_EVENT.get(nukeSound) : SoundEvents.GENERIC_EXPLODE.value();
        level.playSound(null, center, sound, SoundSource.BLOCKS, kind == Kind.ORITECH ? 30 : 4, 1);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5, 1, 0, 0, 0, 0);

        // 生物伤害与击退仅计算一次，避免随逐层方块更新重复触发。
        Vec3 origin = center.getCenter();
        AABB bounds = new AABB(center).inflate(radius);
        DamageSource damage = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION));
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, bounds)) {
            double distance = entity.position().distanceTo(origin);
            if (distance > radius) continue;
            double strength = 1 - distance / radius;
            entity.hurt(damage, (float) (12 + 68 * strength));
            Vec3 direction = entity.position().subtract(origin);
            if (direction.lengthSqr() > 0.0001) entity.push(direction.x / distance * strength, Math.max(0.2, strength), direction.z / distance * strength);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Job job : jobs) list.add(job.save());
        tag.put("jobs", list);
        return tag;
    }

    public static SphericalBlasts load(CompoundTag tag, HolderLookup.Provider registries) {
        SphericalBlasts data = new SphericalBlasts();
        ListTag list = tag.getList("jobs", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            Job job = Job.load(list.getCompound(i));
            if (job != null) data.jobs.addLast(job);
        }
        return data;
    }

    private static final class Job {
        private final BlockPos center;
        private final int radius;
        private final Kind kind;
        private final long createdTick;
        private int shell;
        private int x;
        private int y;
        private int z = Integer.MIN_VALUE;
        private int phase;
        private final Set<Long> waitingChunks = new LinkedHashSet<>();
        private long activeChunk = Long.MIN_VALUE;
        private int localX;
        private int localZ;
        private int localY = Integer.MIN_VALUE;

        private Job(BlockPos center, int radius, Kind kind, long createdTick) {
            this.center = center;
            this.radius = radius;
            this.kind = kind;
            this.createdTick = createdTick;
        }

        // 每个整数球壳的 z 轴只访问其内外半径之间的两段，避免重复扫描内部方块。
        private boolean next(ServerLevel level, BlockPos.MutableBlockPos target) {
            while (shell <= radius + 1) {
                if (shell == 0) {
                    shell = 1;
                    x = -1;
                    y = -1;
                    target.set(center);
                    return true;
                }
                if (x > shell) {
                    shell++;
                    x = -shell;
                    y = -shell;
                    z = Integer.MIN_VALUE;
                    phase = 0;
                    continue;
                }
                if (y > shell) {
                    x++;
                    y = -shell;
                    z = Integer.MIN_VALUE;
                    phase = 0;
                    continue;
                }
                int outer = shell * shell - x * x - y * y;
                if (outer < 0) {
                    nextPair();
                    continue;
                }
                int maxZ = (int) Math.sqrt(outer);
                int inner = (shell - 1) * (shell - 1) - x * x - y * y;
                int minZ = inner < 0 ? 0 : (int) Math.sqrt(inner) + 1;
                if (minZ > maxZ) {
                    nextPair();
                    continue;
                }
                if (z == Integer.MIN_VALUE) {
                    phase = 0;
                    z = -maxZ;
                }
                if (phase == 0) {
                    if (z <= -minZ) {
                        target.set(center.getX() + x, center.getY() + y, center.getZ() + z++);
                        return true;
                    }
                    phase = 1;
                    z = Math.max(1, minZ);
                }
                if (z <= maxZ) {
                    target.set(center.getX() + x, center.getY() + y, center.getZ() + z++);
                    return true;
                }
                nextPair();
            }
            return nextDeferred(level, target);
        }

        private boolean nextDeferred(ServerLevel level, BlockPos.MutableBlockPos target) {
            while (!waitingChunks.isEmpty()) {
                if (activeChunk == Long.MIN_VALUE) {
                    for (long chunk : waitingChunks) {
                        ChunkPos pos = new ChunkPos(chunk);
                        if (level.hasChunk(pos.x, pos.z)) {
                            activeChunk = chunk;
                            localX = 0;
                            localZ = 0;
                            localY = Integer.MIN_VALUE;
                            break;
                        }
                    }
                    if (activeChunk == Long.MIN_VALUE) return false;
                }
                ChunkPos chunk = new ChunkPos(activeChunk);
                if (!level.hasChunk(chunk.x, chunk.z)) {
                    activeChunk = Long.MIN_VALUE;
                    continue;
                }
                while (localX < 16) {
                    int worldX = chunk.getMinBlockX() + localX;
                    int worldZ = chunk.getMinBlockZ() + localZ;
                    int dx = worldX - center.getX();
                    int dz = worldZ - center.getZ();
                    int outerRadius = radius + 1;
                    int heightSquared = outerRadius * outerRadius - dx * dx - dz * dz;
                    if (heightSquared >= 0) {
                        int height = (int) Math.sqrt(heightSquared);
                        int low = Math.max(level.getMinBuildHeight(), center.getY() - height);
                        int high = Math.min(level.getMaxBuildHeight() - 1, center.getY() + height);
                        if (localY == Integer.MIN_VALUE) localY = low;
                        if (localY <= high) {
                            target.set(worldX, localY++, worldZ);
                            return true;
                        }
                    }
                    localY = Integer.MIN_VALUE;
                    if (++localZ == 16) {
                        localZ = 0;
                        localX++;
                    }
                }
                waitingChunks.remove(activeChunk);
                activeChunk = Long.MIN_VALUE;
            }
            return false;
        }

        private void defer(BlockPos pos) {
            long chunk = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
            waitingChunks.add(chunk);
            if (activeChunk == chunk) activeChunk = Long.MIN_VALUE;
        }

        private boolean onSurface(BlockPos pos) {
            int dx = pos.getX() - center.getX();
            int dy = pos.getY() - center.getY();
            int dz = pos.getZ() - center.getZ();
            int inner = radius - 1;
            return dx * dx + dy * dy + dz * dz > inner * inner;
        }

        private boolean outside(BlockPos pos) {
            int dx = pos.getX() - center.getX();
            int dy = pos.getY() - center.getY();
            int dz = pos.getZ() - center.getZ();
            return dx * dx + dy * dy + dz * dz > radius * radius;
        }

        private void nextPair() {
            y++;
            z = Integer.MIN_VALUE;
            phase = 0;
        }

        private boolean finished() { return shell > radius + 1 && waitingChunks.isEmpty(); }

        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("center", center.asLong());
            tag.putInt("radius", radius);
            tag.putInt("kind", kind.ordinal());
            tag.putLong("createdTick", createdTick);
            tag.putInt("shell", shell);
            tag.putInt("x", x);
            tag.putInt("y", y);
            tag.putInt("z", z);
            tag.putInt("phase", phase);
            tag.putLongArray("waitingChunks", waitingChunks.stream().mapToLong(Long::longValue).toArray());
            tag.putLong("activeChunk", activeChunk);
            tag.putInt("localX", localX);
            tag.putInt("localZ", localZ);
            tag.putInt("localY", localY);
            return tag;
        }

        private static Job load(CompoundTag tag) {
            int radius = tag.getInt("radius");
            int kind = tag.getInt("kind");
            if (radius < 1 || radius > 128 || kind < 0 || kind >= Kind.values().length) return null;
            Job job = new Job(BlockPos.of(tag.getLong("center")), radius, Kind.values()[kind], tag.getLong("createdTick"));
            job.shell = Math.clamp(tag.getInt("shell"), 0, radius + 2);
            job.x = Math.clamp(tag.getInt("x"), -radius - 1, radius + 2);
            job.y = Math.clamp(tag.getInt("y"), -radius - 1, radius + 2);
            job.z = tag.getInt("z");
            job.phase = tag.getInt("phase") == 1 ? 1 : 0;
            for (long chunk : tag.getLongArray("waitingChunks")) job.waitingChunks.add(chunk);
            job.activeChunk = tag.contains("activeChunk") ? tag.getLong("activeChunk") : Long.MIN_VALUE;
            if (!job.waitingChunks.contains(job.activeChunk)) job.activeChunk = Long.MIN_VALUE;
            job.localX = Math.clamp(tag.getInt("localX"), 0, 16);
            job.localZ = Math.clamp(tag.getInt("localZ"), 0, 15);
            job.localY = tag.contains("localY") ? tag.getInt("localY") : Integer.MIN_VALUE;
            return job;
        }
    }
}
