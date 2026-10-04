package net.craftoriya.adaptersLib.adapter.mapper

import net.craftoriya.adaptersLib.model.RecipeContainer
import org.bukkit.entity.Villager

object ProfessionMapper {
    fun map(p: Villager.Profession): RecipeContainer.TradeProfession = when (p) {
        Villager.Profession.ARMORER -> RecipeContainer.TradeProfession.ARMORER
        Villager.Profession.BUTCHER -> RecipeContainer.TradeProfession.BUTCHER
        Villager.Profession.CARTOGRAPHER -> RecipeContainer.TradeProfession.CARTOGRAPHER
        Villager.Profession.CLERIC -> RecipeContainer.TradeProfession.CLERIC
        Villager.Profession.FARMER -> RecipeContainer.TradeProfession.FARMER
        Villager.Profession.FISHERMAN -> RecipeContainer.TradeProfession.FISHERMAN
        Villager.Profession.FLETCHER -> RecipeContainer.TradeProfession.FLETCHER
        Villager.Profession.LEATHERWORKER -> RecipeContainer.TradeProfession.LEATHERWORKER
        Villager.Profession.LIBRARIAN -> RecipeContainer.TradeProfession.LIBRARIAN
        Villager.Profession.MASON -> RecipeContainer.TradeProfession.MASON
        Villager.Profession.SHEPHERD -> RecipeContainer.TradeProfession.SHEPHERD
        Villager.Profession.TOOLSMITH -> RecipeContainer.TradeProfession.TOOLSMITH
        Villager.Profession.WEAPONSMITH -> RecipeContainer.TradeProfession.WEAPONSMITH
        else -> RecipeContainer.TradeProfession.NONE
    }
}