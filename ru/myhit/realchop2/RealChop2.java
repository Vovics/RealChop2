package ru.myhit.realchop2;

import net.minecraft.client.renderer.entity.RenderFallingSand;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import ru.myhit.realchop2.EntityFallingTree;
@Mod (modid = "realchop2", name = "RealChop2", version = "1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)


public class RealChop2 {
	@SidedProxy(clientSide = "ru.myhit.realchop2.ClientProxy", serverSide = "ru.myhit.realchop2.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void Load(FMLInitializationEvent event)
	{
		EntityRegistry.registerModEntity(EntityFallingTree.class, "FallingTree", 1, this, 64, 100, false);
		MinecraftForge.EVENT_BUS.register(new RealchopListener());
		proxy.registerRenderThings();
	}   
}
