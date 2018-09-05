package com.freshlancers.sriambalcrusher.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.adapters.SectionsStatePagerAdapter;
import com.freshlancers.sriambalcrusher.fragments.CollectionsFragment;
import com.freshlancers.sriambalcrusher.fragments.ExpenseFragment;
import com.freshlancers.sriambalcrusher.fragments.ExplosiveFragment;
import com.freshlancers.sriambalcrusher.fragments.PurchaseFragment;
import com.freshlancers.sriambalcrusher.fragments.SalesFragment;
import com.freshlancers.sriambalcrusher.fragments.FuelFragment;
import com.freshlancers.sriambalcrusher.fragments.MaterialTransitFragment;
import com.freshlancers.sriambalcrusher.utils.Prefs;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";

    public static String selected;

    //constants
    private static final Integer FRAGMENT_NUMBER_FREIGHT = 0;
    private static final Integer FRAGMENT_NUMBER_EXPENSE = 1;
    private static final Integer FRAGMENT_NUMBER_COLLECTIONS = 2;
    private static final Integer FRAGMENT_NUMBER_FUEL = 3;
    private static final Integer FRAGMENT_NUMBER_MATERIAL_TRANSIT = 4;
    private static final Integer FRAGMENT_NUMBER_EXPLOSIVES = 5;
    private static final Integer FRAGMENT_NUMBER_PURCHASE = 6;

    //context
    private Context mContext = HomeActivity.this;

    //view pager
    public SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    //image view
    private ImageView mSalesImageView, mExpenseImageView, mCollectionsImageView, mFuelImageView,
            mMaterialTransitImageView , mExplosiveImageView , mPurchaseImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        setupFragments();

        initListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.activityPaused();
    }

    private void initListeners() {
        mSalesImageView.setOnClickListener(this);
        mExpenseImageView.setOnClickListener(this);
        mCollectionsImageView.setOnClickListener(this);
        mFuelImageView.setOnClickListener(this);
        mMaterialTransitImageView.setOnClickListener(this);
        mExplosiveImageView.setOnClickListener(this);
        mPurchaseImageView.setOnClickListener(this);
    }

    private void initViews() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mSalesImageView = (ImageView) findViewById(R.id.sales);
        mExpenseImageView = (ImageView) findViewById(R.id.expense);
        mCollectionsImageView = (ImageView) findViewById(R.id.collections);
        mFuelImageView = (ImageView) findViewById(R.id.fuel);
        mMaterialTransitImageView = (ImageView) findViewById(R.id.material_transit);
        mExplosiveImageView = (ImageView) findViewById(R.id.explosive);
        mPurchaseImageView = (ImageView) findViewById(R.id.purchase);
    }

    private void setupFragments() {

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mSectionsStatePagerAdapter.addFragment(new SalesFragment(), getString(R.string.sales)); //fragment 0
        mSectionsStatePagerAdapter.addFragment(new ExpenseFragment(), getString(R.string.expense)); //fragment 1
        mSectionsStatePagerAdapter.addFragment(new CollectionsFragment(), getString(R.string.collections)); //fragment 2
        mSectionsStatePagerAdapter.addFragment(new FuelFragment(), getString(R.string.fuel)); //fragment 3
        mSectionsStatePagerAdapter.addFragment(new MaterialTransitFragment(), getString(R.string.material_transit)); //fragment 4
        mSectionsStatePagerAdapter.addFragment(new ExplosiveFragment(), getString(R.string.explosive)); //fragment 5
        mSectionsStatePagerAdapter.addFragment(new PurchaseFragment(), getString(R.string.purchase)); //fragment 6

        selected = getString(R.string.sales);
        setupViewPager(FRAGMENT_NUMBER_FREIGHT, mSalesImageView);

    }

    public void setupViewPager(Integer fragmentNumber, ImageView mImageView) {

        final float MIN_SCALE = 0.85f;
        final float MIN_ALPHA = 0.5f;

        mViewPager.setAdapter(mSectionsStatePagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                openSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        view.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });

        mViewPager.setCurrentItem(fragmentNumber,true);
        openSelected(fragmentNumber);
        mImageView.setBackground(getDrawable(R.drawable.circle_border_selected));

    }

    private void openSelected(Integer fragmentNumber) {
        if (fragmentNumber == 0) {

            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        } else if (fragmentNumber == 1) {

            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        } else if (fragmentNumber == 2) {

            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        } else if (fragmentNumber == 3) {

            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        } else if (fragmentNumber == 4) {

            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        }else if (fragmentNumber == 5) {

            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border_selected));

        }else if (fragmentNumber == 6) {

            mMaterialTransitImageView.setBackground(getDrawable(R.drawable.circle_border));
            mExpenseImageView.setBackground(getDrawable(R.drawable.circle_border));
            mCollectionsImageView.setBackground(getDrawable(R.drawable.circle_border));
            mFuelImageView.setBackground(getDrawable(R.drawable.circle_border));
            mSalesImageView.setBackground(getDrawable(R.drawable.circle_border));
            mPurchaseImageView.setBackground(getDrawable(R.drawable.circle_border_selected));
            mExplosiveImageView.setBackground(getDrawable(R.drawable.circle_border));

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sales:
                selected = getString(R.string.sales);
                setupViewPager(FRAGMENT_NUMBER_FREIGHT, mSalesImageView);
                break;
            case R.id.expense:
                selected = getString(R.string.expense);
                setupViewPager(FRAGMENT_NUMBER_EXPENSE, mExpenseImageView);
                break;
            case R.id.collections:
                selected = getString(R.string.collections);
                setupViewPager(FRAGMENT_NUMBER_COLLECTIONS, mCollectionsImageView);
                break;
            case R.id.fuel:
                selected = getString(R.string.fuel);
                setupViewPager(FRAGMENT_NUMBER_FUEL, mFuelImageView);
                break;
            case R.id.material_transit:
                selected = getString(R.string.material_transit);
                setupViewPager(FRAGMENT_NUMBER_MATERIAL_TRANSIT, mMaterialTransitImageView);
                break;
            case R.id.explosive:
                selected = getString(R.string.material_transit);
                setupViewPager(FRAGMENT_NUMBER_EXPLOSIVES, mExplosiveImageView);
                break;
            case R.id.purchase:
                selected = getString(R.string.material_transit);
                setupViewPager(FRAGMENT_NUMBER_PURCHASE, mPurchaseImageView);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Prefs.setAccessTokenInPrefs(null);
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
