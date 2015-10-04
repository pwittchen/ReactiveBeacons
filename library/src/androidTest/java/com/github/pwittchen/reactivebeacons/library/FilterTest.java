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
}
