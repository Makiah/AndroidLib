package dude.makiah.androidlib.threading;

public class TimeMeasure
{
    public static final TimeMeasure IMMEDIATE = new TimeMeasure(Units.NANOSECONDS, 1);

    private final long nanoseconds;

    public enum Units
    {
        NANOSECONDS(1),
        MILLISECONDS(1e-3),
        SECONDS(1e-9);

        private final double conversionToNanoseconds;
        Units(double conversionToNanoseconds)
        {
            this.conversionToNanoseconds = conversionToNanoseconds;
        }

        public long getMeasureIn(Units units, double measure)
        {
            return (long)(measure * units.conversionToNanoseconds / conversionToNanoseconds);
        }
    }

    public TimeMeasure(Units units, double duration)
    {
        this.nanoseconds = units.getMeasureIn(Units.NANOSECONDS, duration);
    }

    public long durationIn(Units units)
    {
        return Units.NANOSECONDS.getMeasureIn(units, nanoseconds);
    }
}
