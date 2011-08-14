/*
 * This file is licensed to You under the "Simplified BSD License".
 * You may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * See the COPYRIGHT file distributed with this work for information
 * regarding copyright ownership.
 */
package ch.usi.inf.sape.util;


/**
 * A utility class for working with and converting between RGB or HSB colors.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class Colors {

    private static final int UNDEFINED_HUE = 0;
    
    
    // --- color component extraction
    public static final int extractRed(final int rgb) {
        return (rgb&0x00ff0000)>>>16;
    }

    public static final int extractGreen(final int rgb) {
        return (rgb&0x0000ff00)>>>8;
    }

    public static final int extractBlue(final int rgb) {
        return rgb&0x000000ff;
    }

    public static final int extractHue(final int hsb) {
        return (hsb&0xffff0000)>>>16;
    }

    public static final int extractSaturation(final int hsb) {
        return (hsb&0x0000ff00)>>>8;
    }

    public static final int extractBrightness(final int hsb) {
        return hsb&0x000000ff;
    }

    // --- color component replacement
    public static final int replaceRed(final int rgb, final int red) {
        return rgb&0xff00ffff | ((red&0xff)<<16);
    }

    public static final int replaceGreen(final int rgb, final int green) {
        return rgb&0xffff00ff | ((green&0xff)<<8);
    }

    public static final int replaceBlue(final int rgb, final int blue) {
        return rgb&0xffffff00 | (blue&0xff);
    }

    public static final int replaceHue(final int hsb, final int hue) {
        return hsb&0x0000ffff | ((hue&0xffff)<<16);
    }

    public static final int replaceSaturation(final int hsb, final int saturation) {
        return hsb&0xffff00ff | ((saturation&0xff)<<8);
    }

    public static final int replaceBrightness(final int hsb, final int brightness) {
        return hsb&0xffffff00 | (brightness&0xff);
    }

    // --- color creation
    public static final int createRgb(final int red, final int green, final int blue) {
        return ((red&0xff)<<16) | ((green&0xff)<<8) | (blue&0xff);
    }

    public static final int createHsb(final int hue, final int saturation, final int brightness) {
        return ((hue&0xffff)<<16) | ((saturation&0xff)<<8) | (brightness&0xff);
    }

    // --- color space conversion
    public static final int rgbToHsb(final int rgb) {
        final int red = extractRed(rgb);
        final int green = extractGreen(rgb);
        final int blue = extractBlue(rgb);

        int iMax, iMin;

        // brightness (value)
        if (red>green) {
            iMax = red;
            iMin = green;
        } else {
            iMin = red;
            iMax = green;
        }
        if (blue>iMax) {
            iMax = blue;
        }
        if (blue<iMin) {
            iMin = blue;
        }
        final int brightness = iMax;

        // saturation
        int saturation;
        if (iMax!=0) {
            saturation = 255*(iMax-iMin)/iMax;
        } else {
            saturation = 0;
        }

        // hue
        int hue;
        if (saturation==0) {
            hue = UNDEFINED_HUE;
        } else {
            double dHue;
            final double dDelta = iMax-iMin;
            if (red==iMax) {
                dHue = (double)(green-blue)/dDelta;
            } else if (green==iMax) {
                dHue = 2.0+(blue-red)/dDelta;
            } else {
                dHue = 4.0+(red-green)/dDelta;
            }
            dHue *= 60.0;
            if (dHue<0) {
                dHue += 360;
            }
            if (dHue>=360) {
                dHue -= 360;
            }
            hue = (int)dHue;
        }
        return createHsb(hue, saturation, brightness);
    }

    public static final int hsbToRgb(final int hsb) {
        final int hue = extractHue(hsb);
        final int saturation = extractSaturation(hsb);
        final int brightness = extractBrightness(hsb);

        int red;
        int green;
        int blue;

        if (saturation==0) {
            red = brightness;
            green = brightness;
            blue = brightness;
        } else {
            final double dHue = hue/60.0;
            final int i = (int)dHue;
            final double f = dHue-(double)i;
            final double dBrightness = brightness/255.0;
            final double dSaturation = saturation/255.0;

            final double p = dBrightness*(1.0-dSaturation);
            final double q = dBrightness*(1.0-(dSaturation*f));
            final double t = dBrightness*(1.0-(dSaturation*(1.0-f)));

            switch (i) {
                case 0:
                    red = (int)(dBrightness*255);
                    green = (int)(t*255);
                    blue = (int)(p*255);
                    break;
                case 1:
                    red = (int)(q*255);
                    green = (int)(dBrightness*255);
                    blue = (int)(p*255);
                    break;
                case 2:
                    red = (int)(p*255);
                    green = (int)(dBrightness*255);
                    blue = (int)(t*255);
                    break;
                case 3:
                    red = (int)(p*255);
                    green = (int)(q*255);
                    blue = (int)(dBrightness*255);
                    break;
                case 4:
                    red = (int)(t*255);
                    green = (int)(p*255);
                    blue = (int)(dBrightness*255);
                    break;
                case 5:
                    red = (int)(dBrightness*255);
                    green = (int)(p*255);
                    blue = (int)(q*255);
                    break;
                default:
                    red = 0;
                    green = 0;
                    blue = 0;
            }
        }
        return createRgb(red, green, blue);
    }

}
