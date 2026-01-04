/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import com.macuguita.island.common.api.Job
import com.macuguita.island.common.job.ice_cream.IceCreamJob
import net.minecraft.resources.Identifier


object IslandJobs {

    val JOBS: MutableMap<Identifier, Job> = emptyMap<Identifier, Job>().toMutableMap()

    val ICE_CREAM_ID = Island.id("ice_cream_job")
    val ICE_CREAM_JOB = registerJob(ICE_CREAM_ID, IceCreamJob(ICE_CREAM_ID))

    fun registerJob(identifier: Identifier, job: Job): Job {
        JOBS[identifier] = job
        return job
    }
}
