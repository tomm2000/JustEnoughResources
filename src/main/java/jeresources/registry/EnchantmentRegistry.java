package jeresources.registry;

import jeresources.entries.EnchantmentEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EnchantmentRegistry
{
    private Set<EnchantmentEntry> enchantments;
    private static EnchantmentRegistry instance;

    public static EnchantmentRegistry getInstance()
    {
        if (instance == null)
            return instance = new EnchantmentRegistry();
        return instance;
    }

    public EnchantmentRegistry()
    {
        enchantments = new HashSet<>();
        for (Enchantment enchantment : getEnchants())
            if (enchantment != null) enchantments.add(new EnchantmentEntry(enchantment));
    }

    public Set<EnchantmentEntry> getEnchantments(ItemStack itemStack)
    {
        Set<EnchantmentEntry> set = new HashSet<>();
        for (EnchantmentEntry enchantment : enchantments)
        {
            if (itemStack.getItem() == Items.book && enchantment.getEnchantment().isAllowedOnBooks())
                set.add(enchantment);
            else if (enchantment.getEnchantment().canApply(itemStack)) set.add(enchantment);
        }
        return set;
    }

    private void excludeFormRegistry(Enchantment enchantment)
    {
        for (Iterator<EnchantmentEntry> itr = enchantments.iterator(); itr.hasNext(); )
            if (itr.next().getEnchantment().effectId == enchantment.effectId) itr.remove();
    }

    private void excludeFormRegistry(String sEnchantment)
    {
        for (Enchantment enchantment : getEnchants())
            if (enchantment != null && enchantment.getName().toLowerCase().contains(sEnchantment.toLowerCase()))
                excludeFormRegistry(enchantment);
    }

    public void removeAll(String[] excludedEnchants)
    {
        for (String enchant : excludedEnchants)
            excludeFormRegistry(enchant);
    }

    private static Enchantment[] getEnchants()
    {
        return ReflectionHelper.getPrivateValue(Enchantment.class, null, "field_180311_a", "enchantmentsList");
    }
}
