package hibi.boathud.config;

public enum DistanceUnits {
    METERS("Meters (m)", "%07.1f m", 1d),
    KILOMETERS("Kilometers (km)", "%06.3f km", 0.001d),
    MILES("Miles (mi)", "%07.4f mi", 0.000621371d),
    NAUTICAL_MILES("Nautical Miles (nmi)", "%07.4f nmi", 0.000539957d);

    private final String displayName, format;
    private final double rate;

    DistanceUnits(String displayName, String format, double rate) {
        this.displayName = displayName;
        this.format = format;
        this.rate = rate;
    }

    public String displayName() {
        return displayName;
    }

    public String format() {
        return format;
    }

    public double rate() {
        return rate;
    }

    public static DistanceUnits idOf(int id) {
        for(DistanceUnits unit : values()) {
            if(unit.ordinal() == id) return unit;
        }
        return METERS;
    }

    public static DistanceUnits currentlySelected() {
        return Config.distanceUnit;
    }
}
