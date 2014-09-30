/* ------------------------------------------------------------------
 * ColorUtils.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.visualization;

import java.awt.Color;
import java.util.ArrayList;

public class ColorUtils {
	public static Color ramp(double value, ArrayList<Color> colors) {   
		return Color.black;
	}
	public static Color rampCat(double value, ArrayList<Color> colors) {
		int index = (int) Math.floor(value * colors.size());
		if (index < 0) index = 0;
		if (index >= colors.size()) index = colors.size() - 1;
		return colors.get(index);
	}
	public static Color HSVtoRGB(double h, double s, double v) {
		double r, g, b;
		if (s == 0) {
			r = v;
			g = v;
			b = v;
		}
		else {
			double var_h = h * 6;
			double var_i = Math.floor(var_h);
			double var_1 = v * ( 1 - s );
			double var_2 = v * ( 1 - s * ( var_h - var_i ) );
			double var_3 = v * ( 1 - s * ( 1 - ( var_h - var_i ) ) );
			if (var_i == 0) {
				r = v;
				g = var_3;
				b = var_1;
			}
			else if (var_i == 1) {
				r = var_2;
				g = v;
				b = var_1;
			}
			else if (var_i == 2) {
				r = var_1;
				g = v;
				b = var_3;
			}
			else if (var_i == 3) {
				r = var_1;
				g = var_2;
				b = v;
			}
			else if (var_i == 4) {
				r = var_3;
				g = var_1;
				b = v;
			}
			else {
				r = v;
				g = var_1;
				b = var_2;
			}
		}
		return new Color((float) r, (float) g, (float) b);
	}
}
