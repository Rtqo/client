package org.kamiblue.client.module.modules.player

import net.minecraft.item.ItemPickaxe
import net.minecraft.util.math.RayTraceResult
import org.kamiblue.client.mixin.client.MixinMinecraft
import org.kamiblue.client.mixin.client.render.MixinEntityRenderer
import org.kamiblue.client.mixin.client.world.MixinBlockLiquid
import org.kamiblue.client.module.Category
import org.kamiblue.client.module.Module
import org.kamiblue.client.util.and
import org.kamiblue.client.util.atTrue

/**
 * @see MixinBlockLiquid Liquid Interact
 * @see MixinMinecraft Multi Task
 * @see MixinEntityRenderer No Entity Trace
 */
internal object BlockInteraction : Module(
    name = "BlockInteraction",
    alias = arrayOf("LiquidInteract", "MultiTask", "NoEntityTrace", "NoMiningTrace"),
    category = Category.PLAYER,
    description = "Modifies block interaction"
) {
    private val liquidInteract by setting("Liquid Interact", false, description = "Place block on liquid")
    private val multiTask by setting("Multi Task", true, description = "Breaks block and uses item at the same time")
    private val noEntityTrace0 = setting("No Entity Trace", true, description = "Interact with blocks through entity")
    private val noEntityTrace by noEntityTrace0
    private val checkBlocks by setting("Check Blocks", true, noEntityTrace0.atTrue(), description = "Only ignores entity when there is block behind")
    private val checkPickaxe0 = setting("Check Pickaxe", true, noEntityTrace0.atTrue(), description = "Only ignores entity when holding pickaxe")
    private val checkPickaxe by checkPickaxe0
    private val sneakOverrides by setting("Sneak Override", true, noEntityTrace0.atTrue() and checkPickaxe0.atTrue(), description = "Overrides pickaxe check if sneaking")

    @JvmStatic
    val isLiquidInteractEnabled
        get() = isEnabled && liquidInteract

    @JvmStatic
    val isMultiTaskEnabled
        get() = isEnabled && multiTask

    @JvmStatic
    fun isNoEntityTraceEnabled() : Boolean {
        if (isDisabled || !noEntityTrace) return false

        val objectMouseOver = mc.objectMouseOver
        val holdingPickAxe = mc.player?.heldItemMainhand?.item is ItemPickaxe
        val sneaking = mc.gameSettings.keyBindSneak.isKeyDown

        return (!checkBlocks || objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) // Blocks
            && (!checkPickaxe || holdingPickAxe // Pickaxe
            || sneakOverrides && sneaking) // Override
    }
}