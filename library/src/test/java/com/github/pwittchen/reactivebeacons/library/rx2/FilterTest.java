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
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilterTest {

  @Test
  public void proximityShouldBeEqualToImmediate() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override
      public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.IMMEDIATE;
      }
    });

    Function<Beacon, Boolean> filter = Filter.proximityIsEqualTo(Proximity.IMMEDIATE);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  private Boolean apply(Function<Beacon, Boolean> filter, Beacon beacon) {
    try {
      return filter.apply(beacon);
    } catch (Exception e) {
      Assert.fail();
      return null;
    }
  }

  @Test
  public void proximityShouldNotBeEqualToImmediate() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override
      public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.FAR;
      }
    });

    Function<Beacon, Boolean> filter = Filter.proximityIsNotEqualTo(Proximity.IMMEDIATE);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test
  public void proximityShouldBeEqualToImmediateAndNear() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getProximity()).then(new Answer<Proximity>() {
      @Override
      public Proximity answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Proximity.NEAR;
      }
    });

    Function<Beacon, Boolean> filter =
        Filter.proximityIsEqualTo(Proximity.IMMEDIATE, Proximity.NEAR);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test
  public void distanceShouldBeEqualToTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override
      public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 10d;
      }
    });

    Function<Beacon, Boolean> filter = Filter.distanceIsEqualTo(10);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test
  public void distanceShouldBeLowerThanTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override
      public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 5d;
      }
    });

    Function<Beacon, Boolean> filter = Filter.distanceIsLowerThan(10);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test
  public void distanceShouldBeGreaterThanTen() {
    // given
    Beacon beacon = Mockito.mock(Beacon.class);

    // when
    when(beacon.getDistance()).then(new Answer<Double>() {
      @Override
      public Double answer(InvocationOnMock invocationOnMock) throws Throwable {
        return 15d;
      }
    });

    Function<Beacon, Boolean> filter = Filter.distanceIsGreaterThan(10);
    Boolean call = apply(filter, beacon);

    // then
    assertThat(call).isTrue();
  }

  @Test
  public void macAddressShouldBeValid() {
    // given
    String address = "00:27:0e:2a:b9:aa";

    // when
    MacAddress macAddress = new MacAddress(address);

    assertThat(macAddress).isNotNull(); // and no exception is thrown
  }

  @Test(expected = IllegalArgumentException.class)
  public void macAddressShouldBeInvalid() {
    // given
    String address = "invalid mac address";

    // when
    new MacAddress(address);

    // then throw IllegalArgumentException
  }
}
