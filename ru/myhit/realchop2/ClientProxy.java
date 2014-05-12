package ru.myhit.realchop2;

import ru.myhit.realchop2.CommonProxy;
import ru.myhit.realchop2.EntityFallingTree;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	 public void registerRenderThings() {
			RenderingRegistry.registerEntityRenderingHandler(EntityFallingTree.class, new RenderFallingTree());
	 }
}
