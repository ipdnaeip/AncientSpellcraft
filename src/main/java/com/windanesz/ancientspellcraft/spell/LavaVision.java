package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class LavaVision extends SpellBuff {

	public LavaVision() {
		super(AncientSpellcraft.MODID, "lava_vision", 245, 70, 1, () -> AncientSpellcraftPotions.lava_vision);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	@SubscribeEvent
	public static void onFogDensityEvent(EntityViewRenderEvent.FogDensity event) {
		if (event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).isPotionActive(AncientSpellcraftPotions.lava_vision)) {
			if (event.getState().getMaterial() == Material.LAVA) {
				GlStateManager.setFog(GlStateManager.FogMode.EXP);
				event.setDensity(0.2f);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}