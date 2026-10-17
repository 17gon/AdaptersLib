package net.craftoriya.adaptersLib.model.outline

import net.craftoriya.adaptersLib.model.math.Vec3I

object OutlineGeometry {
    class Part(val x: Double, val y: Double, val z: Double, val sx: Double, val sy: Double, val sz: Double)

    fun parts(min: Vec3I, max: Vec3I, style: OutlineStyle): List<Part> {
        val outset = style.outset.toDouble()
        val x0: Double = min.x - outset
        val y0: Double = min.y - outset
        val z0: Double = min.z - outset
        val x1: Double = max.x + 1.0 + outset
        val y1: Double = max.y + 1.0 + outset
        val z1: Double = max.z + 1.0 + outset
        val dx: Double = x1 - x0
        val dy: Double = y1 - y0
        val dz: Double = z1 - z0
        val thick: Double = style.thickness.toDouble()
            .coerceAtMost(minOf(dx, dy, dz) / 2)
            .coerceAtLeast(0.002)
        val out = ArrayList<Part>()

        when (style.shape) {
            OutlineShape.WIREFRAME -> {
                for (y in doubleArrayOf(y0, y1 - thick)) {
                    for (z in doubleArrayOf(z0, z1 - thick)) {
                        out += Part(x0, y, z, dx, thick, thick)
                    }
                }
                for (x in doubleArrayOf(x0, x1 - thick)) {
                    for (z in doubleArrayOf(z0, z1 - thick)) {
                        out += Part(x, y0 + thick, z, thick, dy - 2 * thick, thick)
                    }
                }
                for (x in doubleArrayOf(x0, x1 - thick)) {
                    for (y in doubleArrayOf(y0, y1 - thick)) {
                        out += Part(x, y, z0 + thick, thick, thick, dz - 2 * thick)
                    }
                }
            }
            OutlineShape.CORNERS -> {
                val lx = minOf(style.arm.toDouble(), dx / 2)
                val ly = (minOf(style.arm.toDouble(), dy / 2) - thick)
                    .coerceAtLeast(0.0)
                val lz = (minOf(style.arm.toDouble(), dz / 2) - thick)
                    .coerceAtLeast(0.0)

                for (ix in 0..1) for (iy in 0..1) for (iz in 0..1) {
                    val px = if (ix == 0) x0 else x1
                    val py = if (iy == 0) y0 else y1
                    val pz = if (iz == 0) z0 else z1
                    val ddx = if (ix == 0) 1 else -1
                    val ddy = if (iy == 0) 1 else -1
                    val ddz = if (iz == 0) 1 else -1
                    out += Part(
                        span(px, ddx, 0.0, lx),
                        span(py, ddy, 0.0, thick),
                        span(pz, ddz, 0.0, thick),
                        lx,
                        thick,
                        thick
                    )
                    out += Part(
                        span(px, ddx, 0.0, thick),
                        span(py, ddy, thick, ly),
                        span(pz, ddz, 0.0, thick),
                        thick,
                        ly,
                        thick
                    )
                    out += Part(
                        span(px, ddx, 0.0, thick),
                        span(py, ddy, 0.0, thick),
                        span(pz, ddz, thick, lz),
                        thick,
                        thick,
                        lz
                    )
                }
            }
            OutlineShape.PILLARS -> {
                for (x in doubleArrayOf(x0, x1 - thick)) {
                    for (z in doubleArrayOf(z0, z1 - thick)) {
                        out += Part(
                            x,
                            y0,
                            z,
                            thick,
                            dy,
                            thick
                        )
                    }
                }
            }
            OutlineShape.FLOOR -> {
                for (z in doubleArrayOf(z0, z1 - thick)) {
                    out += Part(
                        x0,
                        y0,
                        z,
                        dx,
                        thick,
                        thick
                    )
                }
                for (x in doubleArrayOf(x0, x1 - thick)) {
                    out += Part(
                        x,
                        y0,
                        z0 + thick,
                        thick,
                        thick,
                        dz - 2 * thick
                    )
                }
            }
            OutlineShape.FACES -> {
                for (x in doubleArrayOf(x0, x1 - thick)) {
                    out += Part(
                        x,
                        y0,
                        z0,
                        thick,
                        dy,
                        dz
                    )
                }
                for (y in doubleArrayOf(y0, y1 - thick)) {
                    out += Part(
                        x0 + thick,
                        y,
                        z0,
                        dx - 2 * thick,
                        thick,
                        dz
                    )
                }
                for (z in doubleArrayOf(z0, z1 - thick)) {
                    out += Part(
                        x0 + thick,
                        y0 + thick,
                        z,
                        dx - 2 * thick,
                        dy - 2 * thick,
                        thick
                    )
                }
            }
            OutlineShape.SOLID -> out += Part(
                x0,
                y0,
                z0,
                dx,
                dy,
                dz
            )
        }
        return out.filter { it.sx > EPS && it.sy > EPS && it.sz > EPS }
    }

    private fun span(p: Double, dir: Int, from: Double, len: Double) = if (dir > 0) p + from else p - from - len

    private const val EPS: Double = 1e-6
}