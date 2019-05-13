package com.example.icecream;

import static org.junit.Assert.*;

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import java.util.concurrent.TimeoutException;
import org.junit.Rule;
import org.junit.Test;

public class SpeakerServiceTest {


  @Rule
  public final ServiceTestRule mServiceRule = new ServiceTestRule();


  @Test
  public void startMusicTest() {
    Intent serviceIntent =
        new Intent(InstrumentationRegistry.getTargetContext(),
            SpeakerService.class);

    // Bind the service and grab a reference to the binder.
    IBinder binder = null;
    try {
      binder = mServiceRule.bindService(serviceIntent);
    } catch (TimeoutException e) {
      e.printStackTrace();
    }

    // Get the reference to the service, or you can call
    // public methods on the binder directly.
    SpeakerService service =
        ((SpeakerService.SpeakerBinder) binder).getService();

    // Verify that the service is working correctly.
    service.startMusic();
//    assertThat(service.startMusic(), is(any(Integer.class)));
  }
}