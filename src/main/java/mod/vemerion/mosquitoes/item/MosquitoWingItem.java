package mod.vemerion.mosquitoes.item;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MosquitoWingItem extends Item {
	public MosquitoWingItem() {
		setCreativeTab(Main.TAB_MOSQUITOES);
		setTranslationKey(Main.MODID + ".mosquito_wing_item");
	}
}
