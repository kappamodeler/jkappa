package com.plectix.simulator.gui.lib;

import java.awt.Color;

public class ColorMap {
    private static final int DEFAULT_TRANSPARENCY = 196;

    private static final int HISTOGRAM_COLOR_INDEX = 12;
    
    private static int colorCount = 0;

    private static final Color[] colors = {
            new Color(0, 0, 255, DEFAULT_TRANSPARENCY),          // BLUE
            new Color(255, 0, 0, DEFAULT_TRANSPARENCY),          // RED
            new Color(0, 255, 0, DEFAULT_TRANSPARENCY),          // GREEN
            new Color(255, 0, 255, DEFAULT_TRANSPARENCY),        // MAGENTA
            new Color(0, 255, 255, DEFAULT_TRANSPARENCY),        // CYAN
            new Color(255, 175, 175, DEFAULT_TRANSPARENCY),      // PINK
            new Color(0xFF, 0x63, 0x47, DEFAULT_TRANSPARENCY),          // Tomato
            new Color(255, 200, 0, DEFAULT_TRANSPARENCY),               // ORANGE
            new Color(0xFF, 0x45, 0x00, DEFAULT_TRANSPARENCY),          // Orangered
            new Color(0x9A, 0xCD, 0x32, DEFAULT_TRANSPARENCY),          // Yellowgreen
            new Color(0xDA, 0xA5, 0x20, DEFAULT_TRANSPARENCY),          // Goldenrod
            new Color(0x7B, 0x68, 0xEE, DEFAULT_TRANSPARENCY),          // Mediumslateblue
            new Color(0x8A, 0x2B, 0xE2, DEFAULT_TRANSPARENCY),          // BlueViolet
            new Color(0xFA, 0xEB, 0xD7, DEFAULT_TRANSPARENCY),          // AntiqueWhite
            new Color(0xF0, 0xF8, 0xFF, DEFAULT_TRANSPARENCY),          // AliceBlue
            new Color(0x7F, 0xFF, 0xD4, DEFAULT_TRANSPARENCY),          // Aquamarine
            new Color(0xA5, 0x2A, 0x2A, DEFAULT_TRANSPARENCY),          // Brown
            new Color(128, 128, 128, DEFAULT_TRANSPARENCY),             // GRAY
            new Color(0, 0, 0, DEFAULT_TRANSPARENCY),                   // BLACK
            new Color(255, 255, 0, DEFAULT_TRANSPARENCY)                    // YELLOW
    };

    public static int getNumberOfColor(){
    	return colors.length;
    }

    public static Color getColor(){
    	Color c = colors[colorCount%colors.length];
    	colorCount++;
    	return c;
    }
    
    public static Color getColor(int index){
    	return colors[index%colors.length];
    }

    public static Color getHistogramColor(){
            return colors[HISTOGRAM_COLOR_INDEX];
    }

}
