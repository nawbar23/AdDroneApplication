package com.addrone.controller;

/**
 * Created by nbar on 2016-09-12.
 * Class for applying low pass filtering for controller inputs.
 * It should be highly refactored for future usage be cause of lots of hardcode.
 */

public class IirLowpassFilter {
    private final double a1, a2;
    private final double b0, b1, b2;

    private double x1, x2;
    private double y1, y2;

    // actual filter output
    public double y;

    public IirLowpassFilter(double startVal)
    {
        // IIR Butterworth discreet low pass filter coefficients for:
        // Sampling frequency: 20Hz, cutoff frequency: 0.5Hz
        a1 = -1.142980502539901;
        a2 = 0.412801598096189;
        b0 = 0.0674552738890719;
        b1 = 0.1349105477781438;
        b2 = 0.0674552738890719;
        reset(startVal);
    }

    public void reset(final double startVal)
    {
        x1 = x2 = y = y1 = y2 = startVal;
    }

    public double update(final double x)
    {
        y = x*b0 + x1*b1 + x2*b2 - y1*a1 - y2*a2;

        x2 = x1;
        x1 = x;

        y2 = y1;
        y1 = y;

        return y;
    }
}
