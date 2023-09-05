package hibi.boathud.config;

public enum SpeedUnits {
    METERS_PER_SECOND("Meters per Second (m/s)", "%02.0f m/s", 1d),
    KILOMETERS_PER_HOUR("Kilometers per Hour (km/h)", "%02.0f km/h", 3.6d),
    MILES_PER_HOUR("Miles per Hour (mph)", "%02.0f mph", 2.236936d),
    KNOTS("Knots (kt)", "%02.0f kt", 1.943844d),
    FEET_PER_SECOND("Feet per Second (ft/s)", "%02.0f ft/s", 3.28084d),
    MACH("Mach (M)", "%01.3f M", 0.0030303d),
    SPEED_OF_LIGHT("Speed of Light (c)", "%01.3f×10⁻⁶ c", 0.003336d);

    private final String displayName;
    private final String format;
    private final double speedRate;

    SpeedUnits(String displayName, String format, double speedRate) {
        this.displayName = displayName;
        this.format = format;
        this.speedRate = speedRate;
    }

    public String displayName() {
        return displayName;
    }

    public String speedFormat() {
        return format;
    }

    public double speedRate() {
        return speedRate;
    }

    public static SpeedUnits idOf(int id) {
        for(SpeedUnits unit : values()) {
            if(unit.ordinal() == id) return unit;
        }
        return METERS_PER_SECOND;
    }

    public static SpeedUnits currentlySelected() {
        return Config.speedUnit;
    }
}
