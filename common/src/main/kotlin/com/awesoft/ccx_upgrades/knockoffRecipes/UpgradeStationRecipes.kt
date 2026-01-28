package com.awesoft.ccx_upgrades.knockoffRecipes

import com.awesoft.ccx.registry.CCXItems
import dan200.computercraft.shared.ModRegistry
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

private val inv_card: List<Item> = listOf(Items.CHEST, CCXItems.FAKE_CARD.get(), CCXItems.INVENTORY_CARD.get())
private val command_server: List<Item> = listOf(ModRegistry.Items.COMPUTER_COMMAND.get(), CCXItems.SERVER_ADVANCED.get(), CCXItems.SERVER_COMMAND.get())

val RECIPES: List<List<Item>> = listOf(inv_card, command_server)
