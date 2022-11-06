package code.elix_x.coremods.antiidconflict.managers;

import java.io.File;
import java.io.PrintWriter;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.Configuration;
import code.elix_x.coremods.antiidconflict.AntiIdConflictBase;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class EnchantementsManager {
	
	public static boolean translate = false;
	
	public static String availableIDs = "";
	public static String occupiedIDs = "";
	public static String conflictedIDs = "";
	
	public static void preinit(FMLPreInitializationEvent event) throws Exception
	{ 
		AntiIdConflictBase.enchantementsFolder = new File(AntiIdConflictBase.mainFolder, "/enchantements");
		AntiIdConflictBase.enchantementsFolder.mkdir();
		
		setUpEnchantementsFolder();
	}

	public static void init(FMLInitializationEvent event)
	{ 

	}

	public static void postinit(FMLPostInitializationEvent event) throws Exception
	{ 
		updateEnchantementsFolder();
	}
	
	public static void setUpEnchantementsFolder() throws Exception {
		File conf = new File(AntiIdConflictBase.enchantementsFolder, "/main.cfg");
		conf.createNewFile();
		Configuration config = new Configuration(conf);
		config.load();
		translate = config.getBoolean("translate", "translation", false, "Translate enchantement names in statistics");
		config.save();
	}

	public static void updateEnchantementsFolder() throws Exception {
		{
			for(int i = 0; i < Enchantment.enchantmentsList.length; i++){
				Enchantment e = Enchantment.enchantmentsList[i];
				if(e != null){
					occupiedIDs += i + " : " + e.getName() + " (" + e.getClass().getName() + ")\n";
				} else {
					availableIDs += i + "\n";
				}
			}
		}
		{
			File freeIds = new File(AntiIdConflictBase.enchantementsFolder, "/availableIDs.txt");
			if(freeIds.exists()){
				freeIds.delete();
			}
			freeIds.createNewFile();
			PrintWriter writer = new PrintWriter(freeIds);
			writer.println("List of available enchantement ids:");
			for(String s : availableIDs.split("\n")){
				writer.println(s);
			}
			writer.close();
		}
		{
			File occupiedIds = new File(AntiIdConflictBase.enchantementsFolder, "/occupiedIDs.txt");
			if(occupiedIds.exists()){
				occupiedIds.delete();
			}
			occupiedIds.createNewFile();
			PrintWriter writer = new PrintWriter(occupiedIds);
			writer.println("Table of occupied enchantement ids and their owners\nid:name(class)");
			for(String s : occupiedIDs.split("\n")){
				writer.println(s);
			}
			writer.close();
		}
		{
			File all = new File(AntiIdConflictBase.enchantementsFolder, "/AllIDs.txt");
			PrintWriter writer = new PrintWriter(all);

			for(int i = 0; i < Enchantment.enchantmentsList.length; i++){
				Enchantment e = Enchantment.enchantmentsList[i];
				if(e != null){
					writer.println(i + " is Occupied by: " + e.getName() + " (" + e.getClass().getName() + ")");
				} else {
					writer.println(i + " is Available");
				}
			}

			writer.close();
		}
	}
	
	
}