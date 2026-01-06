package com.macuguita.island.client.job.gui

import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.macuguita.island.common.network.cs2.JobZoneUpdateC2SPacket
import com.macuguita.island.common.reg.IslandJobs
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class JobZoneMasterScreen(
    private val blockEntity: JobZoneMasterBlockEntity,
    private val parent: Screen? = null,
) : Screen(Component.translatable("job_zone_master.title")) {

    companion object {
        private val JOB_LABEL = Component.translatable("job_zone_master.job")
        private val POSITION_LABEL = Component.translatable("structure_block.position")
        private val SIZE_LABEL = Component.translatable("structure_block.size")
        private val SHOW_BOUNDING_BOX_LABEL = Component.translatable("structure_block.show_boundingbox")
    }

    private lateinit var jobCycleButton: CycleButton<Identifier>

    private lateinit var posXEdit: EditBox
    private lateinit var posYEdit: EditBox
    private lateinit var posZEdit: EditBox

    private lateinit var sizeXEdit: EditBox
    private lateinit var sizeYEdit: EditBox
    private lateinit var sizeZEdit: EditBox

    private lateinit var toggleBoundingBox: CycleButton<Boolean>

    override fun init() {
        addRenderableWidget(
            Button.builder(CommonComponents.GUI_DONE) { onDone() }
                .bounds(width / 2 - 4 - 150, 210, 150, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(CommonComponents.GUI_CANCEL) { onCancel() }
                .bounds(width / 2 + 4, 210, 150, 20)
                .build()
        )

        val availableJobs = IslandJobs.JOBS.keys.toList()
        if (availableJobs.isNotEmpty()) {
            val initialJob = blockEntity.jobId
            jobCycleButton = addRenderableWidget(
                CycleButton.builder(
                    { jobId: Identifier ->
                        Component.literal(jobId.path.replace("_", " ").replaceFirstChar { it.uppercaseChar() })
                    },
                    { initialJob }
                )
                    .withValues(availableJobs)
                    .create(
                        width / 2 - 152,
                        40,
                        300,
                        20,
                        JOB_LABEL
                    ) { _, _ -> }
            )
        }

        val pos = blockEntity.zonePos
        posXEdit = createEditBox(pos.x, width / 2 - 152, 80)
        posYEdit = createEditBox(pos.y, width / 2 - 72, 80)
        posZEdit = createEditBox(pos.z, width / 2 + 8, 80)
        addWidget(posXEdit)
        addWidget(posYEdit)
        addWidget(posZEdit)

        val size = blockEntity.zoneSize
        sizeXEdit = createEditBox(size.x, width / 2 - 152, 120)
        sizeYEdit = createEditBox(size.y, width / 2 - 72, 120)
        sizeZEdit = createEditBox(size.z, width / 2 + 8, 120)
        addWidget(sizeXEdit)
        addWidget(sizeYEdit)
        addWidget(sizeZEdit)

        toggleBoundingBox = addRenderableWidget(
            CycleButton.onOffBuilder(blockEntity.showBoundingBox)
                .displayOnlyValue()
                .create(
                    width / 2 + 4 + 100,
                    80,
                    50,
                    20,
                    SHOW_BOUNDING_BOX_LABEL
                ) { _, _ -> }
        )
    }

    private fun createEditBox(initial: Int, x: Int, y: Int): EditBox {
        val editBox = EditBox(font, x, y, 80, 20, Component.empty())
        editBox.setMaxLength(15)
        editBox.value = initial.toString()
        return editBox
    }

    override fun setInitialFocus() {
        setInitialFocus(posXEdit)
    }

    override fun onClose() {
        onCancel()
    }

    private fun onDone() {
        val pos = BlockPos(
            parseCoordinate(posXEdit.value),
            parseCoordinate(posYEdit.value),
            parseCoordinate(posZEdit.value)
        )
        val size = Vec3i(
            parseCoordinate(sizeXEdit.value),
            parseCoordinate(sizeYEdit.value),
            parseCoordinate(sizeZEdit.value)
        )
        val jobId = jobCycleButton.value
        val showBoundingBox = toggleBoundingBox.value

        ClientPlayNetworking.send(
            JobZoneUpdateC2SPacket(blockEntity.blockPos, jobId, pos, size, showBoundingBox)
        )

        minecraft.setScreen(parent)
    }

    private fun onCancel() {
        minecraft.setScreen(parent)
    }

    private fun parseCoordinate(value: String): Int {
        return try {
            value.toInt()
        } catch (_: NumberFormatException) {
            0
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.drawCenteredString(font, title, width / 2, 10, -1)

        guiGraphics.drawString(font, JOB_LABEL, width / 2 - 153, 30, -6250336)

        guiGraphics.drawString(font, POSITION_LABEL, width / 2 - 153, 70, -6250336)
        posXEdit.render(guiGraphics, mouseX, mouseY, partialTick)
        posYEdit.render(guiGraphics, mouseX, mouseY, partialTick)
        posZEdit.render(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.drawString(font, SIZE_LABEL, width / 2 - 153, 110, -6250336)
        sizeXEdit.render(guiGraphics, mouseX, mouseY, partialTick)
        sizeYEdit.render(guiGraphics, mouseX, mouseY, partialTick)
        sizeZEdit.render(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.drawString(
            font,
            SHOW_BOUNDING_BOX_LABEL,
            width / 2 + 154 - font.width(SHOW_BOUNDING_BOX_LABEL),
            70,
            -6250336
        )
    }

    override fun isPauseScreen(): Boolean = false
    override fun isInGameUi(): Boolean = true
}
