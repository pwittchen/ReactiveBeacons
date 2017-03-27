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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureAdapter implements Future<Beacon> {
  private boolean done;
  private Beacon beacon;

  public void setBeacon(Beacon beacon) {
    synchronized (this) {
      this.beacon = beacon;
      this.done = true;
      this.notify();
    }
  }

  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override public boolean isCancelled() {
    return false;
  }

  @Override public boolean isDone() {
    return done;
  }

  @Override public Beacon get() throws InterruptedException {
    synchronized (this) {
      while (beacon == null) {
        this.wait();
      }
    }
    return beacon;
  }

  @Override public Beacon get(long t, TimeUnit u) throws InterruptedException {
    return get();
  }
}

