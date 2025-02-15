package org.teamvoided.template

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.item.TooltipData
import net.minecraft.client.texture.StatusEffectSpriteManager
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Holder
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.teamvoided.template.Template.log
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
object TemplateClient {
    fun init() {
        log.info("Hello from Client")
        TooltipComponentCallback.EVENT.register { if (it is IconRendererData) IconRendererTooltip(it) else null }
    }
}

class IconRendererData(val langs: List<String>) : TooltipData
data class IconRendererTooltip(val potions: List<Holder<StatusEffect>?>) : TooltipComponent {
    constructor(data: IconRendererData) : this(data.langs.map { it.parse() })

    override fun getHeight(): Int {
        if (potions.isEmpty()) return if (Screen.hasShiftDown()) 10 else 0
        return if (Screen.hasShiftDown()) potions.size * bigSize() else smallSize()
    }

    override fun getWidth(textRenderer: TextRenderer): Int =
        if (Screen.hasShiftDown()) getBigWith(textRenderer) else potions.size * smallSize()

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, graphics: GuiGraphics) {
        super.drawItems(textRenderer, x, y, graphics)
        val sprites: StatusEffectSpriteManager = MinecraftClient.getInstance().statusEffectSpriteManager
        if (Screen.hasShiftDown()) {
            if (potions.isEmpty()) graphics.drawText(textRenderer, noPotions, x, y, -1, true)
            else potions.forEachIndexed { index, holder ->
                if (holder != null) {
                    graphics.drawSprite(
                        x - 2, y - 2 + (bigSize() * index), 0, bigSize(), bigSize(), sprites.getSprite(holder)
                    )
                    graphics.drawText(
                        textRenderer,
                        holder.value().name.copy().styled { it.withFormatting(holder.value().type.formatting) },
                        x + bigSize(), y + 3 + (bigSize() * index), -1, false
                    )
                }
            }
        } else {
            potions.forEachIndexed { index, holder ->
                if (holder != null)
                    graphics.drawSprite(
                        x - 2 + (smallSize() * index), y - 2, 0,
                        smallSize(), smallSize(), sprites.getSprite(holder)
                    )
                else graphics.drawText(textRenderer, "0", x + (smallSize() * index), y, -1, false)
            }
        }
    }

    fun smallSize(): Int = 12
    fun bigSize(): Int = 18
    fun getBigWith(textRenderer: TextRenderer): Int {
        if (potions.isEmpty()) return textRenderer.getWidth(noPotions)
        val width = bigSize()
        var max = 0
        potions.mapNotNull { it?.value()?.translationKey }
            .map { textRenderer.getWidth(Text.translatable(it)) }
            .forEach { if (it > max) max = it }
        return width + max
    }

    val noPotions = Text.literal("No known effects...").styled { it.withFormatting(Formatting.GRAY, Formatting.ITALIC) }
}

fun String.parse(): Holder<StatusEffect>? {
    if (!this.contains(".")) error("String inst a valid id [$this]")
    val arr = this.split(".")
    if (arr.size < 3) error("String inst a valid id [$this]")
    val id = Identifier.parse("${arr[1]}:${arr[2]}")!!

    return Registries.STATUS_EFFECT.getHolder(id).getOrNull()
}