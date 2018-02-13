package id.or.jisedu;

import java.awt.*;

class OperaColor extends Color {
  
  OperaColor(int r, int g, int b) {
    super(r, g, b);
  }
  
  String getHex() {
    return toHex(getRed(), getGreen(), getBlue());
  }
  
  
  private String toHex(int r, int g, int b) {
    return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
  }
  
  private String toBrowserHexValue(int number) {
    StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
    while (builder.length() < 2) {
      builder.append("0");
    }
    return builder.toString().toUpperCase();
  }
  
}