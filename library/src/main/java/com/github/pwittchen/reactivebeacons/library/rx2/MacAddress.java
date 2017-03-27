/*
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivebeacons.library.rx2;

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
