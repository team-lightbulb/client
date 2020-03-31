package edu.cnm.deepdive.lightbulb.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;
import edu.cnm.deepdive.lightbulb.R;

public class SettingsFragment extends PreferenceFragmentCompat {


  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
  }
}