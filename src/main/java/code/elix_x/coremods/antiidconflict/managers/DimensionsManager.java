package code.elix_x.coremods.antiidconflict.managers;

import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import code.elix_x.coremods.antiidconflict.AntiIdConflictBase;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class DimensionsManager {
	
	public static String availableIDs = "";
	public static String occupiedIDs = "";
	public static String conflictedIDs = "";
	
	public static int lowerLimit = -100;
	public static int upperLimit = 100;
	
	public static void preinit(FMLPreInitializationEvent event) throws Exception
	{ 
		AntiIdConflictBase.dimensionsFolder = new File(AntiIdConflictBase.mainFolder, "/dimensions");
		AntiIdConflictBase.dimensionsFolder.mkdir();
		
		setUpDimensionsFolder();
	}

	public static void init(FMLInitializationEvent event)
	{ 

	}

	public static void postinit(FMLPostInitializationEvent event) throws Exception
	{ 
		updateDimensionsFolder();
	}
	
	public static void setUpDimensionsFolder() throws Exception {
		File conf = new File(AntiIdConflictBase.dimensionsFolder, "/main.cfg");
		conf.createNewFile();
		Configuration config = new Configuration(conf);
		config.load();
		lowerLimit = config.getInt("lowerLimit", "scanning", -100, Integer.MIN_VALUE, Integer.MAX_VALUE, "Lower limit for free id scanning");
		upperLimit = config.getInt("upperLimit", "scanning", 100, Integer.MIN_VALUE, Integer.MAX_VALUE, "Upper limit for free id scanning");
		config.save();
	}

	public static void updateDimensionsFolder() throws Exception {
		{
			Hashtable<Integer, Class<? extends WorldProvider>> providers = ReflectionHelper.getPrivateValue(DimensionManager.class, null, "providers");
			for(int i = lowerLimit; i <= upperLimit; i++){
				if(DimensionManager.isDimensionRegistered(i)){
					try{
						occupiedIDs += i + " : " + DimensionManager.getProvider(i).getDimensionName() + ", Provider: (id: " + DimensionManager.getProviderType(i) + ", class: " + DimensionManager.getProvider(i).getClass() + ")" + "\n";
					} catch(Exception e){
						int providerId = DimensionManager.getProviderType(i);
						Class<? extends WorldProvider> clas = providers.get(providerId);
						String name = null;
						try{
							WorldProvider pr = clas.newInstance();
							name = pr.getDimensionName();
						} catch(Exception ee){
							
						}
						occupiedIDs += i + " : " + (name == null ? "" : name + ", ") + "Provider: (id: " + DimensionManager.getProviderType(i) + ", class: " + clas + ")" + "\n";
					}
				} else {
					availableIDs += i + "\n";
				}
			}
		}
		{
			File freeIds = new File(AntiIdConflictBase.dimensionsFolder, "/availableIDs.txt");
			if(freeIds.exists()){
				freeIds.delete();
			}
			freeIds.createNewFile();
			PrintWriter writer = new PrintWriter(freeIds);
			writer.println("List of available dimension ids:");
			for(String s : availableIDs.split("\n")){
				writer.println(s);
			}
			writer.close();
		}
		{
			File occupiedIds = new File(AntiIdConflictBase.dimensionsFolder, "/occupiedIDs.txt");
			if(occupiedIds.exists()){
				occupiedIds.delete();
			}
			occupiedIds.createNewFile();
			PrintWriter writer = new PrintWriter(occupiedIds);
			writer.println("Table of occupied dimension ids and their owners\nid:name(class)");
			for(String s : occupiedIDs.split("\n")){
				writer.println(s);
			}
			writer.close();
		}
		{
			File all = new File(AntiIdConflictBase.dimensionsFolder, "/AllIDs.txt");
			PrintWriter writer = new PrintWriter(all);

			Hashtable<Integer, Class<? extends WorldProvider>> providers = ReflectionHelper.getPrivateValue(DimensionManager.class, null, "providers");
			
			for(int i = lowerLimit; i <= upperLimit; i++){
				if(DimensionManager.isDimensionRegistered(i)){
					try{
						writer.println(i + " is Occupied by: " + DimensionManager.getProvider(i).getDimensionName() + ", Provider: (id: " + DimensionManager.getProviderType(i) + ", class: " + DimensionManager.getProvider(i).getClass() + ")");
					} catch(Exception e){
						int providerId = DimensionManager.getProviderType(i);
						Class<? extends WorldProvider> clas = providers.get(providerId);
						String name = null;
						try{
							WorldProvider pr = clas.newInstance();
							name = pr.getDimensionName();
						} catch(Exception ee){
							
						}
						writer.println(i + " is Occupied by: " + (name == null ? "" : name + ", ") + "Provider: (id: " + DimensionManager.getProviderType(i) + ", class: " + clas + ")");
					}
				} else {
					writer.println(i + " is Available");
				}
			}

			writer.close();
		}
	}
	
}
