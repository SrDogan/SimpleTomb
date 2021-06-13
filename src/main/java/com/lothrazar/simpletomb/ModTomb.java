package com.lothrazar.simpletomb;

import com.lothrazar.simpletomb.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModTomb.MODID, updateJSON = "https://raw.githubusercontent.com/Lothrazar/tombstone-fork/trunk/1.12/update.json", certificateFingerprint = ModTomb.certificateFingerprint)
public class ModTomb {

  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final String MODID = "simpletomb";
  @Instance(MODID)
  public static ModTomb instance;
  @SidedProxy(clientSide = "com.lothrazar.simpletomb.proxy.ClientProxy", serverSide = "com.lothrazar.simpletomb.proxy.ServerProxy")
  public static IProxy PROXY;
  public static org.apache.logging.log4j.Logger LOGGER;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    LOGGER = event.getModLog();
    PROXY.preInit();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    PROXY.init();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    PROXY.postInit();
  }
}
