package com.github.pwittchen.reactivebeacons.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacAddress {
  private static final String MAC_PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
  public final String address;

  public MacAddress(String address) {
    if (!isAddressValid(address)) {
      throw new IllegalArgumentException("MAC address is invalid");
    }
    this.address = address;
  }

  private boolean isAddressValid(final String mac) {
    Pattern pattern = Pattern.compile(MAC_PATTERN);
    Matcher matcher = pattern.matcher(mac);
    return matcher.matches();
  }
}
