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

public enum Proximity {
  IMMEDIATE("IMMEDIATE", 0, 1),
  NEAR("NEAR", 1, 3),
  FAR("FAR", 3);

  public final String description;
  public final int minDistance;
  public final int maxDistance;

  Proximity(String description, int minDistance) {
    this(description, minDistance, -1); // maxDistance is not defined (is infinite)
  }

  Proximity(String description, int minDistance, int maxDistance) {
    this.description = description;
    this.minDistance = minDistance;
    this.maxDistance = maxDistance;
  }

  @Override public String toString() {
    return description;
  }
}
