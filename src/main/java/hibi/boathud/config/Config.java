package hibi.boathud.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import net.fabricmc.loader.api.FabricLoader;

public class Config {
	public static SpeedUnits speedUnit = SpeedUnits.METERS_PER_SECOND;
	public static DistanceUnits distanceUnit = DistanceUnits.METERS;
	public static boolean showHotbar = true;

	/** Format string for the drift angle display on the HUD. */
	public static final  String angleFormat = "%03.0f °";

	/** Format string for the acceleration display on the HUD. */
	public static final String gFormat = "%+.1f g";

	public static int yOffset = 36;
	public static boolean experimentalHud = false;
	public static boolean smallHud = false;

	/** Controls whether the HUD should be displayed. */
	public static boolean enabled = true;

	// The speed bar type is one of three values:
	// 0: (Pack) Water and Packed Ice speeds (0 ~ 40 m/s)
	// 1: (Mix) Packed and Blue Ice speeds (10 ~ 70 m/s)
	// 2: (Blue) Blue Ice type speeds (40 ~ 70 m/s)
	/** Setting a value that's not between 0 and 2 *will* cause an IndexOutOfBounds */
	public static int barType = 0;

	private static File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "boathud.properties");

	private Config() {}

	/**
	 * Load the config from disk and into memory. Ideally should be run only once. Wrong and missing settings are silently reset to defaults.
	 */
	public static void load() {
		try {
			if(configFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(configFile));
				String line = br.readLine();
				do {
					if(line.startsWith("enabled "))
						enabled = Boolean.parseBoolean(line.substring(8));
					if(line.startsWith("yoffset "))
						yOffset = Integer.parseInt(line.substring(8));
					if(line.startsWith("barType "))
						barType = Integer.parseInt(line.substring(8));
					if(line.startsWith("speedUnit "))
						speedUnit = SpeedUnits.idOf(Integer.parseInt(line.substring(10)));
					if(line.startsWith("expHud "))
						experimentalHud = Boolean.parseBoolean(line.substring(7));
					if(line.startsWith("smallHud "))
						smallHud = Boolean.parseBoolean(line.substring(9));
					if(line.startsWith("showHotbar "))
						showHotbar = Boolean.parseBoolean(line.substring(11));
					if(line.startsWith("distanceUnit "))
						distanceUnit = DistanceUnits.idOf(Integer.parseInt(line.substring(13)));
					line = br.readLine();
				} while (line != null);
				br.close();
			}
		}
		catch (Exception e) {
		}
		// Sanity check
		if(barType > 2 || barType < 0) {
			barType = 0;
		}
	}

	/**
	 * Save the config from memory and onto disk. Ideally, should only be run when the settings are changed.
	 */
	public static void save() {
		try {
			FileWriter writer = new FileWriter(configFile);
			writer.write("enabled " + enabled + "\n");
			writer.write("yoffset " + yOffset + "\n");
			writer.write("barType " + barType + "\n");
			writer.write("speedUnit " + speedUnit.ordinal() + "\n");
			writer.write("expHud " + experimentalHud + "\n");
			writer.write("smallHud " + smallHud + "\n");
			writer.write("showHotbar " + showHotbar + "\n");
			writer.write("distanceUnit " + distanceUnit.ordinal() + "\n");
			writer.close();
		}
		catch (Exception e) {
		}
	}
}
