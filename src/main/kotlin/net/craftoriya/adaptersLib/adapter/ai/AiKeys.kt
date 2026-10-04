package net.craftoriya.adaptersLib.adapter.ai

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

object AiKeys {
    lateinit var plugin: Plugin
    val CONTROLLED by lazy { NamespacedKey(plugin, "ai_controlled") }
    val HARD by lazy { NamespacedKey(plugin, "ai_hard") }
    val TARGET_X by lazy { NamespacedKey(plugin, "ai_target_x") }
    val TARGET_Y by lazy { NamespacedKey(plugin, "ai_target_y") }
    val TARGET_Z by lazy { NamespacedKey(plugin, "ai_target_z") }
    val SPEED by lazy { NamespacedKey(plugin, "ai_speed") }
}