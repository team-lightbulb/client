package edu.cnm.deepdive.lightbulb;

import android.app.Application;
import com.facebook.stetho.Stetho;
import edu.cnm.deepdive.lightbulb.service.GoogleSignInService;

public class LightBulbApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setContext(this);
    Stetho.initializeWithDefaults(this);
  }
}
