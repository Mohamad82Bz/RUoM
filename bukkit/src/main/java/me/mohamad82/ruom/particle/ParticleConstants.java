/*
 * MIT License
 *
 * Copyright (c) 2021 ByteZ1337
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.mohamad82.ruom.particle;

import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static me.mohamad82.ruom.particle.ParticleMappings.*;
import static me.mohamad82.ruom.particle.utils.ReflectionUtils.*;

/**
 * Constants for particles.
 *
 * @author ByteZ
 * @since 10.06.2019
 */
public final class ParticleConstants {
    
    /* ---------------- Classes ---------------- */
    
    /**
     * Represents the ItemStack class.
     */
    public static final Class ITEM_STACK_CLASS;
    /**
     * Represents the Packet class.
     */
    public static final Class PACKET_CLASS;
    /**
     * Represents the PacketPlayOutWorldParticles class.
     */
    public static final Class PACKET_PLAY_OUT_WORLD_PARTICLES_CLASS;
    /**
     * Represents the EnumParticle enum.
     */
    public static final Class PARTICLE_ENUM;
    /**
     * Represents the Particle class.
     */
    public static final Class PARTICLE_CLASS;
    /**
     * Represents the MiencraftKey class.
     */
    public static final Class MINECRAFT_KEY_CLASS;
    /**
     * Represents the Vector3f class.
     */
    public static final Class VECTOR_3FA_CLASS;
    /**
     * Represents the abstract IRegistry class.
     */
    public static final Class REGISTRY_CLASS;
    /**
     * Represents the BuiltInRegistries class.
     */
    public static final Class BUILT_IN_REGISTRIES_CLASS;
    /**
     * Represents the Block class.
     */
    public static final Class BLOCK_CLASS;
    /**
     * Represents the BlockPosition class.
     */
    public static final Class BLOCK_POSITION_CLASS;
    /**
     * Represents the IBLockData interface.
     */
    public static final Class BLOCK_DATA_INTERFACE;
    /**
     * Represents the Blocks class.
     */
    public static final Class BLOCKS_CLASS;
    /**
     * Represents the PositionSource class.
     */
    public static final Class POSITION_SOURCE_CLASS;
    /**
     * Represents the BlockPositionSource class.
     */
    public static final Class BLOCK_POSITION_SOURCE_CLASS;
    /**
     * Represents the EntityPositionSource class.
     */
    public static final Class ENTITY_POSITION_SOURCE_CLASS;
    /**
     * Represents the VibrationPath class.
     */
    public static final Class VIBRATION_PATH_CLASS;
    /**
     * Represents the Entity class.
     */
    public static final Class ENTITY_CLASS;
    /**
     * Represents the EntityPlayer class.
     */
    public static final Class ENTITY_PLAYER_CLASS;
    /**
     * Represents the PlayerConnection class.
     */
    public static final Class PLAYER_CONNECTION_CLASS;
    /**
     * Represents the CraftEntity class.
     */
    public static final Class CRAFT_ENTITY_CLASS;
    /**
     * Represents the CraftPlayer class.
     */
    public static final Class CRAFT_PLAYER_CLASS;
    /**
     * Represents the CraftItemStack class.
     */
    public static final Class CRAFT_ITEM_STACK_CLASS;
    /**
     * Represents the ParticleParam class.
     */
    public static final Class PARTICLE_PARAM_CLASS;
    /**
     * Represents the ParticleParamRedstone class.
     */
    public static final Class PARTICLE_PARAM_REDSTONE_CLASS;
    /**
     * Represents the DustColorTransitionOptions class.
     */
    public static final Class PARTICLE_PARAM_DUST_COLOR_TRANSITION_CLASS;
    /**
     * Represents the ParticleParamBlock class.
     */
    public static final Class PARTICLE_PARAM_BLOCK_CLASS;
    /**
     * Represents the ParticleParamItem class.
     */
    public static final Class PARTICLE_PARAM_ITEM_CLASS;
    /**
     * Represents the VibrationParticleOption class.
     */
    public static final Class PARTICLE_PARAM_VIBRATION_CLASS;
    /**
     * Represents the ParticleParamShriek class.
     */
    public static final Class PARTICLE_PARAM_SHRIEK_CLASS;
    /**
     * Represents the ParticleParamSculkCharge class.
     */
    public static final Class PARTICLE_PARAM_SCULK_CHARGE_CLASS;
    
    /* ---------------- Methods ---------------- */
    
    /**
     * Represents the IRegistry#get(MinecraftKey) method.
     */
    public static final Method REGISTRY_GET_METHOD;
    /**
     * Represents the PlayerConnection#sendPacket(); method.
     */
    public static final Method PLAYER_CONNECTION_SEND_PACKET_METHOD;
    /**
     * Represents the CraftEntity#getHandle(); method.
     */
    public static final Method CRAFT_ENTITY_GET_HANDLE_METHOD;
    /**
     * Represents the CraftPlayer#getHandle(); method.
     */
    public static final Method CRAFT_PLAYER_GET_HANDLE_METHOD;
    /**
     * Represents the Block#getBlockData(); method.
     */
    public static final Method BLOCK_GET_BLOCK_DATA_METHOD;
    /**
     * Represents the CraftItemStack#asNMSCopy(); method.
     */
    public static final Method CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD;
    
    /* ---------------- Fields ---------------- */
    
    /**
     * Represents the EntityPlayer#playerConnection field.
     */
    public static final Field ENTITY_PLAYER_PLAYER_CONNECTION_FIELD;
    
    /* ---------------- Constructor ---------------- */
    
    /**
     * Represents the PacketPlayOutWorldParticles constructor.
     */
    public static final Constructor PACKET_PLAY_OUT_WORLD_PARTICLES_CONSTRUCTOR;
    /**
     * Represents the MinecraftKey constructor.
     */
    public static final Constructor MINECRAFT_KEY_CONSTRUCTOR;
    /**
     * Represents the Vector3fa constructor.
     */
    public static final Constructor VECTOR_3FA_CONSTRUCTOR;
    /**
     * Represents the BlockPosition constructor.
     */
    public static final Constructor BLOCK_POSITION_CONSTRUCTOR;
    /**
     * Represents the BlockPositionSource constructor.
     */
    public static final Constructor BLOCK_POSITION_SOURCE_CONSTRUCTOR;
    /**
     * Represents the EntityPositionSource constructor.
     */
    public static final Constructor ENTITY_POSITION_SOURCE_CONSTRUCTOR;
    /**
     * Represents the VibrationPath constructor.
     */
    public static final Constructor VIBRATION_PATH_CONSTRUCTOR;
    /**
     * Represents the ParticleParamRedstone constructor.
     */
    public static final Constructor PARTICLE_PARAM_REDSTONE_CONSTRUCTOR;
    /**
     * Represents the DustColorTransitionOptions constructor.
     */
    public static final Constructor PARTICLE_PARAM_DUST_COLOR_TRANSITION_CONSTRUCTOR;
    /**
     * Represents the ParticleParamBlock constructor.
     */
    public static final Constructor PARTICLE_PARAM_BLOCK_CONSTRUCTOR;
    /**
     * Represents the ParticleParamItem constructor.
     */
    public static final Constructor PARTICLE_PARAM_ITEM_CONSTRUCTOR;
    /**
     * Represents the VibrationParticleOption constructor.
     */
    public static final Constructor PARTICLE_PARAM_VIBRATION_CONSTRUCTOR;
    /**
     * Represents the ParticleParamShriek constructor.
     */
    public static final Constructor PARTICLE_PARAM_SHRIEK_CONSTRUCTOR;
    /**
     * Represents the ParticleParamSculkCharge constructor.
     */
    public static final Constructor PARTICLE_PARAM_SCULK_CHARGE_CONSTRUCTOR;
    
    
    /* ---------------- Object constants ---------------- */
    
    /**
     * Represents the ParticleType Registry.
     */
    public static final Object PARTICLE_TYPE_REGISTRY;
    /**
     * Represents the Block Registry.
     */
    public static final Object BLOCK_REGISTRY;
    
    /* ---------------- INIT ---------------- */
    
    static {
        int version = ServerVersion.getVersion();
        int patch = ServerVersion.getPatchNumber();
        
        // Classes
        ITEM_STACK_CLASS = ItemStackAccessor.getType();
        PACKET_CLASS = PacketAccessor.getType();
        PACKET_PLAY_OUT_WORLD_PARTICLES_CLASS = ClientboundLevelParticlesPacketAccessor.getType();
        PARTICLE_ENUM = EnumParticleAccessor.getType();
        PARTICLE_CLASS = ParticleTypeAccessor.getType();
        MINECRAFT_KEY_CLASS = ResourceLocationAccessor.getType();
        VECTOR_3FA_CLASS = version < 17 ? RotationsAccessor.getType() : (version < 19.3 ? getClassSafe("com.mojang.math.Vector3fa") : getClassSafe("org.joml.Vector3f"));
        REGISTRY_CLASS = RegistryAccessor.getType();
        BUILT_IN_REGISTRIES_CLASS = BuiltInRegistriesAccessor_2.getType();
        BLOCK_CLASS = BlockAccessor.getType();
        BLOCK_POSITION_CLASS = BlockPosAccessor.getType();
        BLOCK_DATA_INTERFACE = BlockStateAccessor.getType();
        BLOCKS_CLASS = BlocksAccessor.getType();
        POSITION_SOURCE_CLASS = PositionSourceAccessor.getType();
        BLOCK_POSITION_SOURCE_CLASS = BlockPositionSourceAccessor.getType();
        ENTITY_POSITION_SOURCE_CLASS = EntityPositionSourceAccessor.getType();
        VIBRATION_PATH_CLASS = VibrationPathAccessor.getType();
        ENTITY_CLASS = EntityAccessor.getType();
        ENTITY_PLAYER_CLASS = ServerPlayerAccessor.getType();
        PLAYER_CONNECTION_CLASS = ServerGamePacketListenerImplAccessor.getType();
        CRAFT_ENTITY_CLASS = getCraftBukkitClass("entity.CraftEntity");
        CRAFT_PLAYER_CLASS = getCraftBukkitClass("entity.CraftPlayer");
        CRAFT_ITEM_STACK_CLASS = getCraftBukkitClass("inventory.CraftItemStack");
        PARTICLE_PARAM_CLASS = ParticleOptionsAccessor.getType();
        PARTICLE_PARAM_REDSTONE_CLASS = DustParticleOptionsAccessor.getType();
        PARTICLE_PARAM_DUST_COLOR_TRANSITION_CLASS = DustColorTransitionOptionsAccessor.getType();
        PARTICLE_PARAM_BLOCK_CLASS = BlockParticleOptionAccessor.getType();
        PARTICLE_PARAM_ITEM_CLASS = ItemParticleOptionAccessor.getType();
        PARTICLE_PARAM_VIBRATION_CLASS = VibrationParticleOptionAccessor.getType();
        PARTICLE_PARAM_SHRIEK_CLASS = ShriekParticleOptionAccessor.getType();
        PARTICLE_PARAM_SCULK_CHARGE_CLASS = SculkChargeParticleOptionsAccessor.getType();
        
        // Methods
        REGISTRY_GET_METHOD = RegistryAccessor.getMethodGet2();
        PLAYER_CONNECTION_SEND_PACKET_METHOD = ServerGamePacketListenerImplAccessor.getMethodSend1();
        CRAFT_ENTITY_GET_HANDLE_METHOD = getMethodOrNull(CRAFT_ENTITY_CLASS, "getHandle");
        CRAFT_PLAYER_GET_HANDLE_METHOD = getMethodOrNull(CRAFT_PLAYER_CLASS, "getHandle");
        BLOCK_GET_BLOCK_DATA_METHOD = BlockAccessor.getMethodDefaultBlockState1();
        CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD = getMethodOrNull(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", ItemStack.class);
        
        // Fields
        ENTITY_PLAYER_PLAYER_CONNECTION_FIELD = ServerPlayerAccessor.getFieldConnection();
        
        // Constructors
        if (version < 13)
            PACKET_PLAY_OUT_WORLD_PARTICLES_CONSTRUCTOR = ClientboundLevelParticlesPacketAccessor.getConstructor0();
        else if (version < 15)
            PACKET_PLAY_OUT_WORLD_PARTICLES_CONSTRUCTOR = ClientboundLevelParticlesPacketAccessor.getConstructor1();
        else
            PACKET_PLAY_OUT_WORLD_PARTICLES_CONSTRUCTOR = ClientboundLevelParticlesPacketAccessor.getConstructor2();

        MINECRAFT_KEY_CONSTRUCTOR = ResourceLocationAccessor.getConstructor0();
        VECTOR_3FA_CONSTRUCTOR = getConstructorOrNull(VECTOR_3FA_CLASS, float.class, float.class, float.class);
        BLOCK_POSITION_CONSTRUCTOR = BlockPosAccessor.getConstructor1();
        BLOCK_POSITION_SOURCE_CONSTRUCTOR = version < 17 ? null : BlockPositionSourceAccessor.getConstructor0();
        if (version < 17)
            ENTITY_POSITION_SOURCE_CONSTRUCTOR = null;
        else if (version < 19)
            ENTITY_POSITION_SOURCE_CONSTRUCTOR = EntityPositionSourceAccessor.getConstructor0();
        else
            ENTITY_POSITION_SOURCE_CONSTRUCTOR = EntityPositionSourceAccessor.getConstructor1();
        
        VIBRATION_PATH_CONSTRUCTOR = version < 17 ? null : VibrationPathAccessor.getConstructor0();
        
        if (version < 13)
            PARTICLE_PARAM_REDSTONE_CONSTRUCTOR = null;
        else if (version < 17)
            PARTICLE_PARAM_REDSTONE_CONSTRUCTOR = DustParticleOptionsAccessor.getConstructor0();
        else {
            if (version > 19 || (version == 19 && patch >= 3)) {
                PARTICLE_PARAM_REDSTONE_CONSTRUCTOR = DustParticleOptionsAccessor.getConstructor1();
            } else {
                PARTICLE_PARAM_REDSTONE_CONSTRUCTOR = DustParticleOptionsAccessor.getConstructor2();
            }
        }

        if (version < 17)
            PARTICLE_PARAM_DUST_COLOR_TRANSITION_CONSTRUCTOR = null;
        else if (version > 19 || (version == 19 && patch >= 3))
            PARTICLE_PARAM_DUST_COLOR_TRANSITION_CONSTRUCTOR = DustColorTransitionOptionsAccessor.getConstructor0();
        else
            PARTICLE_PARAM_DUST_COLOR_TRANSITION_CONSTRUCTOR = DustColorTransitionOptionsAccessor.getConstructor1();
        PARTICLE_PARAM_BLOCK_CONSTRUCTOR = version < 13 ? null : BlockParticleOptionAccessor.getConstructor0();
        PARTICLE_PARAM_ITEM_CONSTRUCTOR = version < 13 ? null : ItemParticleOptionAccessor.getConstructor0();
        if (version < 17)
            PARTICLE_PARAM_VIBRATION_CONSTRUCTOR = null;
        else if (version < 19)
            PARTICLE_PARAM_VIBRATION_CONSTRUCTOR = VibrationParticleOptionAccessor.getConstructor0();
        else
            PARTICLE_PARAM_VIBRATION_CONSTRUCTOR = VibrationParticleOptionAccessor.getConstructor1();
        PARTICLE_PARAM_SHRIEK_CONSTRUCTOR = version < 19 ? null : ShriekParticleOptionAccessor.getConstructor0();
        PARTICLE_PARAM_SCULK_CHARGE_CONSTRUCTOR = version < 19 ? null : SculkChargeParticleOptionsAccessor.getConstructor0();

        // Constants
        PARTICLE_TYPE_REGISTRY = readField(
            version < 19.3
                ? RegistryAccessor.getFieldPARTICLE_TYPE()
                    : BuiltInRegistriesAccessor_2.getFieldPARTICLE_TYPE(),
            null);
        BLOCK_REGISTRY = readField(
            version < 19.3
                ? RegistryAccessor.getFieldBLOCK()
                : BuiltInRegistriesAccessor_2.getFieldBLOCK(),
            null);
    }
    
}
