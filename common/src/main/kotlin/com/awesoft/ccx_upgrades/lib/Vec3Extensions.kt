package com.awesoft.ccx_upgrades.lib

import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f

fun Vec3.rotate(quat: Quaternionf, pivot: Vec3 = Vec3(0.5, 0.5, 0.5)): Vec3 {
    val local = this.subtract(pivot)
    val rotated = quat.transform(Vector3f(local.x.toFloat(), local.y.toFloat(), local.z.toFloat()))

    return Vec3(rotated.x + pivot.x, rotated.y + pivot.y, rotated.z + pivot.z)
}