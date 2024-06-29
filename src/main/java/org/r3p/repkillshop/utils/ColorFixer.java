package org.r3p.repkillshop.utils;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class ColorFixer {
    static Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static List<String> addColors(List<String> input) {
        if (input == null || input.isEmpty())
            return input;
        for (int i = 0; i < input.size(); i++)
            input.set(i, addColors(input.get(i)));
        return input;
    }

    public static String addColors(String text) {
        if (text == null) {
            return "";
        }
        // Apply gradients for hex colors
        text = applyHexColors(text);
        // Translate alternate color codes for traditional Minecraft colors
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    private static String applyHexColors(String text) {
        Matcher matcher = hexPattern.matcher(text);
        while (matcher.find()) {
            String startColor = matcher.group();
            int start = matcher.start();
            int end = text.indexOf("#", start + 7);
            if (end == -1) {
                break; // No closing color found
            }
            String endColor = text.substring(end, end + 7);
            String content = text.substring(start + 7, end);
            String gradientText = hsvGradient(content, Color.decode(startColor), Color.decode(endColor));
            text = text.substring(0, start) + gradientText + text.substring(end + 7);
            matcher = hexPattern.matcher(text);
        }
        return text;
    }

    public static String hsvGradient(String str, Color from, Color to) {
        float[] hsvFrom = Color.RGBtoHSB(from.getRed(), from.getGreen(), from.getBlue(), null);
        float[] hsvTo = Color.RGBtoHSB(to.getRed(), to.getGreen(), to.getBlue(), null);
        double[] h = linear(hsvFrom[0], hsvTo[0], str.length());
        double[] s = linear(hsvFrom[1], hsvTo[1], str.length());
        double[] v = linear(hsvFrom[2], hsvTo[2], str.length());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            builder.append(ChatColor.of(Color.getHSBColor((float) h[i], (float) s[i], (float) v[i]))).append(str.charAt(i));
        }
        return builder.toString();
    }

    private static double[] linear(double from, double to, int max) {
        double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * (to - from) / (max - 1);
        }
        return res;
    }
}
