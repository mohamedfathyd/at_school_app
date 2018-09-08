
package com.atschoolPioneerSchool.activityimages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.fragmentimages.ImageGalleryFragment;
import com.atschoolPioneerSchool.fragmentimages.ImageGridFragment;
import com.atschoolPioneerSchool.fragmentimages.ImageListFragment;
import com.atschoolPioneerSchool.fragmentimages.ImagePagerFragment;


public class SimpleImageActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int frIndex = getIntent().getIntExtra(Constant.Extra.FRAGMENT_INDEX, 0);

        Fragment fr;
        String tag;
        int titleRes;
        switch (frIndex) {
            default:
            case ImageListFragment.INDEX:
                tag = ImageListFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageListFragment();
                }
                titleRes = R.string.ac_name_image_list;
                break;
            case ImageGridFragment.INDEX:
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                }
                titleRes = R.string.ac_name_image_grid;
                break;
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.ac_name_image_pager;
                break;
            case ImageGalleryFragment.INDEX:
                tag = ImageGalleryFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGalleryFragment();
                }
                titleRes = R.string.ac_name_image_gallery;
                break;
        }

        setTitle(titleRes);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }
}