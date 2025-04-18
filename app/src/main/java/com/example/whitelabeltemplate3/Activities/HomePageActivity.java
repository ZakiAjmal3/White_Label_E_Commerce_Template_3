package com.example.whitelabeltemplate3.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.whitelabeltemplate3.Fragment.CartItemFragment;
import com.example.whitelabeltemplate3.Fragment.HomeFragment;
import com.example.whitelabeltemplate3.Fragment.ProfileFragment;
import com.example.whitelabeltemplate3.Fragment.SearchFragment;
import com.example.whitelabeltemplate3.Fragment.WishListFragment;
import com.example.whitelabeltemplate3.R;
import com.example.whitelabeltemplate3.Utils.SessionManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomePageActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    static Fragment currentFragment;
    BottomNavigationView bottomNavigationView;
    RelativeLayout topBar;
    SessionManager sessionManager;
    String storeId,authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(HomePageActivity.this, R.color.white));

        sessionManager = new SessionManager(this);
        storeId = sessionManager.getStoreId();
        authToken = sessionManager.getUserData().get("authToken");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frameLayout);
        topBar = findViewById(R.id.topBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home){
                    topBar.setVisibility(View.VISIBLE);
                    loadFragment(new HomeFragment());
                }else if (item.getItemId() == R.id.search){
                    topBar.setVisibility(View.GONE);
                    loadFragment(new SearchFragment());
                }else if (item.getItemId() == R.id.wishlist){
                    topBar.setVisibility(View.VISIBLE);
                    loadFragment(new WishListFragment());
                }else if (item.getItemId() == R.id.cart){
                    topBar.setVisibility(View.VISIBLE);
                    loadFragment(new CartItemFragment());
                }else if (item.getItemId() == R.id.profile){
                    if (sessionManager.isLoggedIn()) {
                    topBar.setVisibility(View.GONE);
                        loadFragment(new ProfileFragment());
                    }else {
                        startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
                    }
                }
                return true;
            }
        });

        if (getIntent().getStringExtra("frag") != null){
            if (getIntent().getStringExtra("frag").equals("search")){
                loadFragment(new SearchFragment());
                bottomNavigationView.setSelectedItemId(R.id.search);
            }if (getIntent().getStringExtra("frag").equals("wish")){
                loadFragment(new WishListFragment());
                bottomNavigationView.setSelectedItemId(R.id.wishlist);
            }if (getIntent().getStringExtra("frag").equals("cart")){
                loadFragment(new CartItemFragment());
                bottomNavigationView.setSelectedItemId(R.id.cart);
            }
        }else {
            loadFragment(new HomeFragment());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment instanceof HomeFragment) {
                    // If on HomeFragment, use the default behavior
                    setEnabled(false); // Disable this callback
                } else {
                    // If on another fragment, navigate back to HomeFragment
                    loadFragment(new HomeFragment());
                    bottomNavigationView.setSelectedItemId(R.id.home);
                }
            }
        });
    }
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
        currentFragment = fragment;
    }
    public static Fragment getCurrentFragment(){
        return currentFragment;
    }
    public void setSearchFragmentSelected() {
        bottomNavigationView.setSelectedItemId(R.id.search);
    }
    private final int MAX_RETRY = 5;
    private final long RETRY_DELAY = 200; // in milliseconds
    public void setWishlistCount() {
        setWishlistCountWithRetry(0);
    }
    private void setWishlistCountWithRetry(int attempt) {
        if (attempt >= MAX_RETRY) return;

        int count = sessionManager.getWishListCount();
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.wishlist);

        if (badge == null && attempt < MAX_RETRY) {
            // Retry after delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                setWishlistCountWithRetry(attempt + 1);
            }, RETRY_DELAY);
            return;
        }

        if (count == 0) {
            badge.setVisible(false);
        } else {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(HomePageActivity.this, R.color.red));
            badge.setBadgeTextColor(ContextCompat.getColor(HomePageActivity.this, R.color.white));
        }
    }
    public void setCartCount() {
        setCartCountWithRetry(0);
    }

    private void setCartCountWithRetry(int attempt) {
        if (attempt >= MAX_RETRY) return;

        int count = sessionManager.getCartCount();
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.cart);

        if (badge == null && attempt < MAX_RETRY) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                setCartCountWithRetry(attempt + 1);
            }, RETRY_DELAY);
            return;
        }

        if (count == 0) {
            badge.setVisible(false);
        } else {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(HomePageActivity.this, R.color.red));
            badge.setBadgeTextColor(ContextCompat.getColor(HomePageActivity.this, R.color.white));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment2 = getCurrentFragment();
//        Log.e("tabFrag",fragment2.toString());
//        if (fragment2 instanceof AllProductFragment) {
//            ((AllProductFragment) fragment2).onTabVisible();
//        }
        setWishlistCount();
        setCartCount();
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof WishListFragment) {
            bottomNavigationView.setSelectedItemId(R.id.wishlist);
        }else if (fragment instanceof CartItemFragment){
            bottomNavigationView.setSelectedItemId(R.id.cart);
        }else if (fragment instanceof ProfileFragment){
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }else if (fragment instanceof HomeFragment){
            bottomNavigationView.setSelectedItemId(R.id.home);
        }else if (fragment instanceof SearchFragment){
            bottomNavigationView.setSelectedItemId(R.id.search);
        }
    }
}