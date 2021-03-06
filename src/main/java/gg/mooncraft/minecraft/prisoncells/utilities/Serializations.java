package gg.mooncraft.minecraft.prisoncells.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Serializations {

    public static byte[] toByteArray(ItemStack... itemStack) {
        if (itemStack == null || itemStack.length == 0) return null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {

            bukkitObjectOutputStream.writeInt(itemStack.length);
            for (ItemStack stack : itemStack) {
                if (stack != null) {
                    bukkitObjectOutputStream.writeObject(stack.serializeAsBytes());
                } else {
                    bukkitObjectOutputStream.writeObject(null);
                }
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encode ItemStack array.", e);
        }
    }

    public static ItemStack[] fromByteArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(inputStream)) {
            ItemStack[] items = new ItemStack[bukkitObjectInputStream.readInt()];

            for (int index = 0; index < items.length; index++) {
                byte[] stack = (byte[]) bukkitObjectInputStream.readObject();
                if (stack != null) {
                    items[index] = ItemStack.deserializeBytes(stack);
                } else {
                    items[index] = null;
                }
            }
            return items;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to decode ItemStack array.", e);
        }
    }
}