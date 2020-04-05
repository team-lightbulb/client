package edu.cnm.deepdive.lightbulb.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.lightbulb.R;
import edu.cnm.deepdive.lightbulb.service.GoogleSignInService;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  private AppBarConfiguration mAppBarConfiguration;
  private GoogleSignInService googleSignInService;
  private Object OnNavigationItemSelectedListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);

    //inflate header layout
    View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
//reference to views
    ImageView avatar = (ImageView) navView.findViewById(R.id.avatar);
    TextView name = (TextView) navView.findViewById(R.id.name);
    TextView email = (TextView) navView.findViewById(R.id.email);
//set views
//    imgvw.setImageResource( Picasso.get().load( googleSignInService.getAccount().getValue().getPhotoUrl().getPath(). ) );
    GoogleSignInService.getInstance().getAccount().observe(this, (account) -> {
      if (account != null) {
        if (account.getPhotoUrl() != null) {
          Picasso.get().load(account.getPhotoUrl()).into(avatar);
        } else {
          avatar.setImageResource(R.drawable.ic_lightbulb);
        }
        name.setText(account.getDisplayName());
        email.setText(account.getEmail());
      }
    });

    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    mAppBarConfiguration = new AppBarConfiguration.Builder(
        R.id.nav_recent, R.id.nav_mine, R.id.nav_search,
        R.id.nav_settings)
        .setDrawerLayout(drawer)
        .build();
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.sign_out) {
      GoogleSignInService.getInstance().signOut()
          .addOnCompleteListener((ignore) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
          });
      return true;
    } else {
      return super.onOptionsItemSelected(item);

    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
        || super.onSupportNavigateUp();
  }
}
