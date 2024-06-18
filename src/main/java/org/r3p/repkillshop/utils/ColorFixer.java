package org.r3p.repkillshop.utils;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class ColorFixer {
    static Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

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
        for (Matcher matcher = pattern.matcher(text); matcher.find(); matcher = pattern.matcher(text)) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, "" + ChatColor.of(color));
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String hsvGradient(String str, Color from, Color to) {
        float[] hsvFrom = Color.RGBtoHSB(from.getRed(), from.getGreen(), from.getBlue(), null);
        float[] hsvTo = Color.RGBtoHSB(to.getRed(), to.getGreen(), to.getBlue(), null);
        double[] h = linear(hsvFrom[0], hsvTo[0], str.length());
        double[] s = linear(hsvFrom[1], hsvTo[1], str.length());
        double[] v = linear(hsvFrom[2], hsvTo[2], str.length());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
            builder.append(ChatColor.of(Color.getHSBColor((float) h[i], (float) s[i], (float) v[i]))).append(str.charAt(i));
        return builder.toString();
    }

    private static double[] linear(double from, double to, int max) {
        double[] res = new double[max];
        for (int i = 0; i < max; i++)
            res[i] = from + i * (to - from) / (max - 1);
        return res;
    }
}
