package hibi.boathud.config;

public enum SpeedUnits {
    METERS_PER_SECOND("m/s", "%03.0f m/s", 1d),
    KILOMETERS_PER_HOUR("km/h", "%03.0f km/h", 3.6d),
    MILES_PER_HOUR("mph", "%03.0f mph", 2.236936d),
    KNOTS("kt", "%03.0f kt", 1.943844d),
    FEET_PER_SECOND("ft/s", "%03.0f ft/s", 3.28084d),
    MACH("Ma", "%01.3f Ma", 0.0030303d),
    C("C", "%01.3f×10⁻⁶ C", 0.003336d);

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
        for(SpeedUnits unit : SpeedUnits.values()) {
            if(unit.ordinal() == id) return unit;
        }
        return SpeedUnits.METERS_PER_SECOND;
    }

    public static SpeedUnits currentlySelected() {
        return Config.speedUnit;
    }
}
