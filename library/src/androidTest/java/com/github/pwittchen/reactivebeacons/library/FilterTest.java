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

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.functions.Func1;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) public class FilterTest {

  @Test public void proximityShouldBeEqualToImmediate() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.IMMEDIATE;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.proximityIsEqualTo(Proximity.IMMEDIATE);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void proximityShouldNotBeEqualToImmediate() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.FAR;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.proximityIsNotEqualTo(Proximity.IMMEDIATE);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void proximityShouldBeEqualToImmediateAndNear() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.NEAR;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.proximityIsEqualTo(Proximity.IMMEDIATE, Proximity.NEAR);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void distanceShouldBeEqualToTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 10d;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.distanceIsEqualTo(10);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void distanceShouldBeLowerThanTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 5d;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.distanceIsLowerThan(10);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void distanceShouldBeGreaterThanTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 15d;
      }
    });

    Func1<Beacon, Boolean> filter = Filter.distanceIsGreaterThan(10);
    Boolean call = filter.call(beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test public void macAddressShouldBeValid() {
    // given
    String address = "00:27:0e:2a:b9:aa";

    // when
    MacAddress macAddress = new MacAddress(address);

    assertThat(macAddress).isNotNull(); // and no exception is thrown
  }

  @Test(expected = IllegalArgumentException.class) public void macAddressShouldBeInvalid() {
    // given
    String address = "invalid mac address";

    // when
    MacAddress macAddress = new MacAddress(address);

    // then throw IllegalArgumentException
  }
}
