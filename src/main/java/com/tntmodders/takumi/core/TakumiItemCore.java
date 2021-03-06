package com.tntmodders.takumi.core;

import com.tntmodders.takumi.TakumiCraftCore;
import com.tntmodders.takumi.item.ItemTakumiShield;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TakumiItemCore {
    public static final TakumiItemCore INSTANCE = new TakumiItemCore();
    public static List<Item> itemBlocks = new ArrayList<Item>();
    public static final Item TAKUMI_SHIELD= new ItemTakumiShield();

    public static void register(IForgeRegistry<Item> registry) {
        Class clazz = TakumiItemCore.class;
        for (Field field : clazz.getFields()) {
            try {
                if (field.get(TakumiItemCore.INSTANCE) instanceof Item) {
                    Item item = ((Item) field.get(TakumiItemCore.INSTANCE));
                    registry.register(item);
                    TakumiCraftCore.LOGGER.info("Registered Item : " + item.getUnlocalizedName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        clazz = TakumiBlockCore.class;
        for (Field field : clazz.getFields()) {
            try {
                if (field.get(TakumiItemCore.INSTANCE) instanceof Block) {
                    Block block = ((Block) field.get(TakumiBlockCore.INSTANCE));
                    Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
                    registry.register(item);
                    TakumiItemCore.itemBlocks.add(item);
                    TakumiCraftCore.LOGGER.info("Registered Item : " + block.getUnlocalizedName());

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
