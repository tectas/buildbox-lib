package at.tectas.buildbox.adapters;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import at.tectas.buildbox.BuildBoxMainActivity;

public class TabsAdapter extends FragmentPagerAdapter implements
		ActionBar.TabListener, ViewPager.OnPageChangeListener {
	private static final String TAG = "TabsAdapter";
	private final BuildBoxMainActivity mContext;
	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	protected Fragment currentFragment = null;
	protected int viewPagerIndex = 0;
	
	public Fragment getCurrentFragment() {
		return this.currentFragment;
	}
	
	public int getViewPagerIndex() {
		return this.viewPagerIndex;
	}
	
	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}
	
	public TabsAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = (BuildBoxMainActivity)activity;
		mActionBar = activity.getActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this);
		mTabs.add(info);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
		
		if (this.mTabs.size() == 1) {
			this.viewPagerIndex = 0;
			this.currentFragment = (Fragment) this.instantiateItem(this.mViewPager, this.viewPagerIndex);
		}
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
		
		return fragment;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if (position < this.mTabs.size()) {
			this.viewPagerIndex = position;
			this.currentFragment = (Fragment) this.instantiateItem(this.mViewPager, this.viewPagerIndex);
			mActionBar.setSelectedNavigationItem(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Object tag = tab.getTag();
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i) == tag) {				
				mViewPager.setCurrentItem(i);
			}
		}
		
		this.mContext.invalidateOptionsMenu();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	public static String getTag() {
		return TAG;
	}
	
	public void destroy() {
		this.destroy();
	}
}