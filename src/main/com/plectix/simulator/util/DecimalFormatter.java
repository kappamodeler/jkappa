package com.plectix.simulator.util;

import java.text.DecimalFormat;

public class DecimalFormatter {
	
    private static final DecimalFormat[] FORMATTERS = new DecimalFormat[] {
        new DecimalFormat("0"),
        new DecimalFormat("0.#"),
        new DecimalFormat("0.##"),
        new DecimalFormat("0.###"),
        new DecimalFormat("0.####"),
        new DecimalFormat("0.#####"),
        new DecimalFormat("0.######"),
        new DecimalFormat("0.#######"),
        new DecimalFormat("0.########")
    };

    /**
     *
     */
    public static final String format(double d, int i) {
            if (i < 0) {
                    i = 0;
            } else if (i >= FORMATTERS.length) {
                    i = FORMATTERS.length - 1;
            }
            return FORMATTERS[i].format(d);
    }
}
