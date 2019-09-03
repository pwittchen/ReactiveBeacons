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
package com.github.pwittchen.reactivebeacons.library.rx2;


import io.reactivex.functions.Function;

/**
 * Provides static filtering methods,
 * which can be used with RxJava filter(...) method inside specific subscription.
 * These methods can be used for filtering Proximity, distance, device names and MAC addresses.
 */
@SuppressWarnings("PMD")
public class Filter {

    public static Function<Beacon, Boolean> proximityIsEqualTo(final Proximity... proximities) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (Proximity proximity : proximities) {
                    if (beacon.getProximity() == proximity) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> proximityIsNotEqualTo(final Proximity... proximities) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (Proximity proximity : proximities) {
                    if (beacon.getProximity() != proximity) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> distanceIsEqualTo(final double distance) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                return beacon.getDistance() == distance;
            }
        };
    }

    public static Function<Beacon, Boolean> distanceIsGreaterThan(final double distance) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                return beacon.getDistance() > distance;
            }
        };
    }

    public static Function<Beacon, Boolean> distanceIsLowerThan(final double distance) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                return beacon.getDistance() < distance;
            }
        };
    }

    public static Function<Beacon, Boolean> hasName(final String... names) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (String name : names) {
                    if (beacon.device.getName().equals(name)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> exceptName(final String... names) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (String name : names) {
                    if (!beacon.device.getName().equals(name)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> hasMacAddress(final String... macs) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (String mac : macs) {
                    if (beacon.device.getAddress().equals(mac)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> exceptMacAddress(final String... macs) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (String mac : macs) {
                    if (!beacon.device.getAddress().equals(mac)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> hasMacAddress(final MacAddress... macs) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (MacAddress mac : macs) {
                    if (beacon.macAddress.equals(mac)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static Function<Beacon, Boolean> exceptMacAddress(final MacAddress... macs) {
        return new Function<Beacon, Boolean>() {
            @Override
            public Boolean apply(Beacon beacon) {
                for (MacAddress mac : macs) {
                    if (!beacon.macAddress.equals(mac)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }
}
