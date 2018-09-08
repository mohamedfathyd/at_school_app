/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.atschoolPioneerSchool.fragmentimages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.data.Constant;

import com.atschoolPioneerSchool.model.TouchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class ImagePagerFragment extends BaseFragment {

    public static final int INDEX = 2;
    ViewPager pager;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter(getActivity()));
        pager.setCurrentItem(getArguments().getInt(Constant.Extra.IMAGE_POSITION, 0));

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (Constant.IMAGES[position].contains("drawable")) {
                    //this mean this url of video
                    //save video URL
               /*     SharedPreferences sharedpref = getContext().getSharedPreferences("picars", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor edtPref = sharedpref.edit();
                    edtPref.putString("VideoURL", Constant.VideosURL[position]);
                    edtPref.commit();

                    Intent i = new Intent(getActivity(), play_video.class);
                    startActivity(i);
                    getActivity().finish();*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return rootView;
    }


    private class ImageAdapter extends PagerAdapter {

        private final String[] IMAGE_URLS = Constant.IMAGES;

        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(false)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new SimpleBitmapDisplayer())
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;


            TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            Log.d("dd", "pager: " + String.valueOf(pager.getCurrentItem()));
            Log.d("dd", "posistion: " + String.valueOf(position));

            if (Constant.IMAGES[position].contains("drawable")) {
               /* if (pager.getCurrentItem() ==( position)) {
                    SharedPreferences sharedpref = getContext().getSharedPreferences("picars", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor edtPref = sharedpref.edit();
                    edtPref.putString("VideoURL", Constant.VideosURL[position]);
                    edtPref.commit();

                    Intent i = new Intent(getActivity(), play_video.class);
                    startActivity(i);
                    getActivity().finish();
                }*/

                return new View(getContext());
            } else {

                ImageLoader.getInstance().displayImage(Constant.IMAGES[position], imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                        }
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });
                view.addView(imageLayout, 0);
                return imageLayout;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


        private void dumpEvent(MotionEvent event) {
            String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                    "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
            StringBuilder sb = new StringBuilder();
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            sb.append("event ACTION_").append(names[actionCode]);
            if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                    || actionCode == MotionEvent.ACTION_POINTER_UP) {
                sb.append("(pid ").append(
                        action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                sb.append(")");
            }
            sb.append("[");
            for (int i = 0; i < event.getPointerCount(); i++) {
                sb.append("#").append(i);
                sb.append("(pid ").append(event.getPointerId(i));
                sb.append(")=").append((int) event.getX(i));
                sb.append(",").append((int) event.getY(i));
                if (i + 1 < event.getPointerCount())
                    sb.append(";");
            }
            sb.append("]");
        }

        /**
         * Determine the space between the first two fingers
         */
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        /**
         * Calculate the mid point of the first two fingers
         */
        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    }
}