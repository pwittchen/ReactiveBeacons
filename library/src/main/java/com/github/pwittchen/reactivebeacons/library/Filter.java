/*
 * Copyright (C) 2015 Piotr Wittchen
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
package com.github.pwittchen.reactivebeacons.library;

import rx.functions.Func1;

/**
 * Provides static filtering methods,
 * which can be used with RxJava filter(...) method inside specific subscription.
 * These methods can be used for filtering Proximity, distance, device names and MAC addresses.
 */
public class Filter {

  public static Func1<Beacon, Boolean> proximityIsEqualTo(final Proximity... proximities) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        boolean proximitiesAreEqual = false;

        for (Proximity proximity : proximities) {
          proximitiesAreEqual = beacon.getProximity() == proximity;
        }

        return proximitiesAreEqual;
      }
    };
  }

  public static Func1<Beacon, Boolean> distanceIsEqualTo(final double distance) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        return beacon.getDistance() == distance;
      }
    };
  }

  public static Func1<Beacon, Boolean> distanceIsGreaterThan(final double distance) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        return beacon.getDistance() > distance;
      }
    };
  }

  public static Func1<Beacon, Boolean> distanceIsLowerThan(final double distance) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        return beacon.getDistance() < distance;
      }
    };
  }

  public static Func1<Beacon, Boolean> hasName(final String... names) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        boolean namesAreEqual = false;

        for (String name : names) {
          namesAreEqual = beacon.device.getName().equals(name);
        }

        return namesAreEqual;
      }
    };
  }

  public static Func1<Beacon, Boolean> hasMacAddress(final String... macs) {
    return new Func1<Beacon, Boolean>() {
      @Override public Boolean call(Beacon beacon) {
        boolean macsAreEqual = false;

        for (String mac : macs) {
          macsAreEqual = beacon.device.getAddress().equals(mac);
        }

        return macsAreEqual;
      }
    };
  }
}
