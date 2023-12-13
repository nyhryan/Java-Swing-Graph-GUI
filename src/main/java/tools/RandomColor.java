package tools;

public class RandomColor {
    public static java.awt.Color getRandomColor() {
        // get random color that is not too dark nor not too bright
        float[] hsb = java.awt.Color.RGBtoHSB((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), null);
        return java.awt.Color.getHSBColor(hsb[0], hsb[1], 0.5f + 0.5f * (float) Math.random());
    }

    public static java.awt.Color getCorrectTextColor(java.awt.Color randomColor) {
        // get correct text color for given background color
        int d;
        // Counting the perceptive luminance - human eye favors green color...
        double luminance = (0.299 * randomColor.getRed() + 0.587 * randomColor.getGreen() + 0.114 * randomColor.getBlue()) / 255;
        if (luminance > 0.5)
            d = 0; // bright colors - black font
        else
            d = 255; // dark colors - white font
        return new java.awt.Color(d, d, d);
    }
}
