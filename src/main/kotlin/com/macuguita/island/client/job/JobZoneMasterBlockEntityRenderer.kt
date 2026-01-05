package com.macuguita.island.client.job

import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.world.phys.Vec3

class JobZoneMasterBlockEntityRenderer(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<JobZoneMasterBlockEntity, JobZoneMasterBlockEntityRenderState> {

    override fun extractRenderState(
        blockEntity: JobZoneMasterBlockEntity,
        blockEntityRenderState: JobZoneMasterBlockEntityRenderState,
        f: Float,
        vec3: Vec3,
        crumblingOverlay: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay)
        blockEntityRenderState.posX = blockEntity.zonePos.x
        blockEntityRenderState.posY = blockEntity.zonePos.y
        blockEntityRenderState.posZ = blockEntity.zonePos.z
        blockEntityRenderState.sizeX = blockEntity.zoneSize.x
        blockEntityRenderState.sizeY = blockEntity.zoneSize.y
        blockEntityRenderState.sizeZ = blockEntity.zoneSize.z
        blockEntityRenderState.shouldRender = blockEntity.showBoundingBox
    }

    override fun createRenderState(): JobZoneMasterBlockEntityRenderState = JobZoneMasterBlockEntityRenderState()

    override fun submit(
        blockEntityRenderState: JobZoneMasterBlockEntityRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        cameraRenderState: CameraRenderState
    ) {
        if (!blockEntityRenderState.shouldRender) return
        val x = blockEntityRenderState.posX.toFloat()
        val y = blockEntityRenderState.posY.toFloat()
        val z = blockEntityRenderState.posZ.toFloat()

        val sx = blockEntityRenderState.sizeX.toFloat()
        val sy = blockEntityRenderState.sizeY.toFloat()
        val sz = blockEntityRenderState.sizeZ.toFloat()

        poseStack.pushPose()

        poseStack.translate(x, y, z)

        submitNodeCollector.order(0).submitCustomGeometry(poseStack, RenderTypes.lines()) { pose, vertexConsumer ->
            val r = 0f
            val g = 1f
            val b = 0f
            val a = 0.5f

            fun line(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float) {
                vertexConsumer.addVertex(pose, x1, y1, z1)
                    .setColor(r, g, b, a)
                    .setNormal(0f, 1f, 0f)
                    .setLineWidth(1f)
                vertexConsumer.addVertex(pose, x2, y2, z2)
                    .setColor(r, g, b, a)
                    .setNormal(0f, 1f, 0f)
                    .setLineWidth(1f)
            }

            // Bottom face
            line(0f, 0f, 0f, sx, 0f, 0f)
            line(sx, 0f, 0f, sx, 0f, sz)
            line(sx, 0f, sz, 0f, 0f, sz)
            line(0f, 0f, sz, 0f, 0f, 0f)

            // Top face
            line(0f, sy, 0f, sx, sy, 0f)
            line(sx, sy, 0f, sx, sy, sz)
            line(sx, sy, sz, 0f, sy, sz)
            line(0f, sy, sz, 0f, sy, 0f)

            // Vertical edges
            line(0f, 0f, 0f, 0f, sy, 0f)
            line(sx, 0f, 0f, sx, sy, 0f)
            line(sx, 0f, sz, sx, sy, sz)
            line(0f, 0f, sz, 0f, sy, sz)
        }

        poseStack.popPose()
    }
}
