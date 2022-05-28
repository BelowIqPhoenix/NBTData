package zabi.minecraft.nbttooltip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(name = "NBT Tooltip", modid = "nbttooltip", version = "0.4", clientSideOnly = true, acceptedMinecraftVersions = "[1.11,1.13)", updateJSON = "http://zabi.altervista.org/minecraft/nbttooltip/update.json")
/* loaded from: nbttooltip-0.4.jar:zabi/minecraft/nbttooltip/NBTTooltip.class */
public class NBTTooltip {
    private static final String FORMAT = TextFormatting.ITALIC.toString() + TextFormatting.DARK_GRAY;
    private static int line_scrolled = 0;
    private static int time = 0;

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(this);
        Config.init(evt.getSuggestedConfigurationFile());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent evt) {
        if (!Config.requiresf3 || evt.getFlags().func_194127_a()) {
            NBTTagCompound tag = evt.getItemStack().func_77978_p();
            ArrayList<String> ttip = new ArrayList<>(Config.maxLinesShown);
            if (tag != null) {
                evt.getToolTip().add("");
                ttip.add(TextFormatting.DARK_PURPLE + " - nbt start -");
                unwrapTag(ttip, tag, FORMAT, "", "  ");
                ttip.add(TextFormatting.DARK_PURPLE + " - nbt end -");
                evt.getToolTip().addAll(transformTtip(ttip));
                return;
            }
            evt.getToolTip().add(FORMAT + "No NBT tag");
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END && !GuiScreen.func_146272_n()) {
            time++;
            if (time >= Config.ticksBeforeScroll / (GuiScreen.func_175283_s() ? 4 : 1)) {
                time = 0;
                line_scrolled++;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static ArrayList<String> transformTtip(ArrayList<String> ttip) {
        ArrayList<String> newttip = new ArrayList<>(Config.maxLinesShown);
        newttip.add("- NBTTooltip -");
        if (ttip.size() > Config.maxLinesShown) {
            if (Config.maxLinesShown + line_scrolled > ttip.size()) {
                line_scrolled = 0;
            }
            for (int i = 0; i < Config.maxLinesShown; i++) {
                newttip.add(ttip.get(i + line_scrolled));
            }
            return newttip;
        }
        line_scrolled = 0;
        newttip.addAll(ttip);
        return newttip;
    }

    @SideOnly(Side.CLIENT)
    private static void unwrapTag(List<String> tooltip, NBTBase base, String pad, @Nonnull String tagName, String padIncrement) {
        if (base.func_74732_a() == 10) {
            NBTTagCompound tag = (NBTTagCompound) base;
            tag.func_150296_c().forEach(s -> {
                boolean nested = tag.func_74781_a(s).func_74732_a() == 10 || tag.func_74781_a(s).func_74732_a() == 9;
                if (nested) {
                    tooltip.add(pad + s + ": {");
                    unwrapTag(tooltip, tag.func_74781_a(s), pad + padIncrement, s, padIncrement);
                    tooltip.add(pad + "}");
                    return;
                }
                addValueToTooltip(tooltip, tag.func_74781_a(s), s, pad);
            });
        } else if (base.func_74732_a() == 9) {
            int index = 0;
            Iterator<NBTBase> iter = ((NBTTagList) base).iterator();
            while (iter.hasNext()) {
                NBTBase nbtnext = iter.next();
                if (nbtnext.func_74732_a() == 10 || nbtnext.func_74732_a() == 9) {
                    tooltip.add(pad + "[" + index + "]: {");
                    unwrapTag(tooltip, nbtnext, pad + padIncrement, "", padIncrement);
                    tooltip.add(pad + "}");
                } else {
                    tooltip.add(pad + "[" + index + "] -> " + nbtnext.toString());
                }
                index++;
            }
        } else {
            addValueToTooltip(tooltip, base, tagName, pad);
        }
    }

    private static void addValueToTooltip(List<String> tooltip, NBTBase nbt, String name, String pad) {
        tooltip.add(pad + name + ": " + nbt.toString());
    }
}
