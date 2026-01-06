package com.macuguita.island.common.job

import com.macuguita.island.shim.SavedDataTypes
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.*


class ActiveJobsSavedData : SavedData {

    private val activeJobs: MutableMap<UUID, Identifier> = mutableMapOf()

    constructor() {
        this.setDirty()
    }

    private constructor(entries: List<JobEntry>) {
        entries.forEach { entry ->
            this.activeJobs[entry.uuid] = entry.jobId
        }
    }

    fun remove(uuid: UUID): Boolean {
        val wasPresent = activeJobs.remove(uuid) != null
        if (wasPresent) setDirty()
        return wasPresent
    }

    fun contains(uuid: UUID): Boolean = activeJobs.containsKey(uuid)

    fun all(): Set<UUID> = Collections.unmodifiableSet(HashSet(activeJobs.keys))

    fun allValues(): Collection<Identifier> = activeJobs.values

    fun allEntries(): Map<UUID, Identifier> = Collections.unmodifiableMap(HashMap(activeJobs))

    operator fun get(uuid: UUID): Identifier? = activeJobs[uuid]

    operator fun set(uuid: UUID, id: Identifier): Boolean {
        val wasAbsent = activeJobs.put(uuid, id) == null
        setDirty()
        return wasAbsent
    }

    companion object {
        const val ID = "active_jobs"

        val CODEC: Codec<ActiveJobsSavedData> = RecordCodecBuilder.create { instance ->
            instance.group(
                JobEntry.CODEC
                    .listOf()
                    .fieldOf("jobs")
                    .forGetter { data ->
                        data.activeJobs.entries.map { (uuid, jobId) ->
                            JobEntry(uuid, jobId)
                        }
                    }
            ).apply(instance, ::ActiveJobsSavedData)
        }

        @JvmField
        val TYPE: SavedDataType<ActiveJobsSavedData> = SavedDataTypes.create(
            ID,
            ::ActiveJobsSavedData,
            CODEC,
            null,
        )

        fun getActiveJobs(server: MinecraftServer): ActiveJobsSavedData {
            val level =
                server.getLevel(ServerLevel.OVERWORLD) ?: return ActiveJobsSavedData()

            return level.dataStorage.computeIfAbsent(TYPE)
        }
    }

    private data class JobEntry(val uuid: UUID, val jobId: Identifier) {
        companion object {
            val CODEC: Codec<JobEntry> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(JobEntry::uuid),
                    Identifier.CODEC.fieldOf("job_id").forGetter(JobEntry::jobId)
                ).apply(instance, ::JobEntry)
            }
        }
    }
}
