package fr.handipressante.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.Data.Photo;

/**
 * Created by marc on 05/03/2016.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Photo> mPhotoList = new ArrayList<>();
    private List<Photo> mCurrentlyDownloading = new ArrayList<>();

    public PhotoPagerAdapter(Context context) {
        mContext = context;
    }

    public void swapItems(ViewGroup collection, List<Photo> photoList) {
        if (mPhotoList.isEmpty() && !photoList.isEmpty()) {
            ImageView imageView = (ImageView) collection.findViewWithTag(0);
            Photo photo = photoList.get(0);

            if (imageView != null) {
                imageView.setImageResource(R.drawable.downloading);
                loadPhoto(imageView, photo);
            }
        } else {
            for (int i = 0; i < mPhotoList.size() && i < photoList.size(); ++i) {
                Photo oldPhoto = mPhotoList.get(i);
                Photo newPhoto = photoList.get(i);

                if (!oldPhoto.getId().equals(newPhoto.getId())) {
                    ImageView imageView = (ImageView) collection.findViewWithTag(i);
                    if (imageView != null) {
                        loadPhoto(imageView, newPhoto);
                    }
                }
            }
        }

        mPhotoList.clear();
        mPhotoList.addAll(photoList);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.photopager_item, collection, false);
        collection.addView(layout);

        ImageView imageView = (ImageView) layout.findViewById(R.id.picture_block);
        if (imageView != null) {
            imageView.setTag(position);

            if (mPhotoList.isEmpty()) {
                imageView.setImageResource(R.drawable.no_photos);
            } else if (mPhotoList.size() > position) {
                Photo photo = mPhotoList.get(position);
                loadPhoto(imageView, photo);
            }
        }

        return layout;
    }

    private void loadPhoto(ImageView imageView, Photo photo) {
        if (!mCurrentlyDownloading.contains(photo)) {
            File file = photoFile(photo);

            if (file.exists()) {
                new BitmapWorkerTask(imageView).execute(file.getAbsolutePath());
            } else {
                mCurrentlyDownloading.add(photo);
                new DownloadPhotoTask(imageView).execute(photo);
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        if (mPhotoList.isEmpty())
            return 1;
        else
            return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Photo";
    }

    private File photoFile(Photo photo) {

        File storageDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // TODO : using this dir (not visible from gallery) is better, but managing is then needed
            // storageDir = getExternalCacheDir();
            storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        } else {
            storageDir = mContext.getFilesDir();
        }

        return new File(storageDir, photo.getLocalPath());
    }

    private class DownloadPhotoTask extends AsyncTask<Photo, Integer, String> {
        private ImageView mView;
        private PowerManager.WakeLock mWakeLock;
        private Photo mPhoto;

        public DownloadPhotoTask(ImageView view) {
            mView = view;
        }

        @Override
        protected String doInBackground(Photo... p) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                mPhoto = p[0];
                URL url = new URL(mPhoto.getRemoteUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                File file = photoFile(mPhoto);
                File photoDir = file.getParentFile();
                if (!photoDir.exists()) photoDir.mkdir();

                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length != 1) return;
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Log.e("PhotoPageAdapter", "Download error: " + result);
            }
            else {
                File file = photoFile(mPhoto);
                if (file.exists()) {
                    new BitmapWorkerTask(mView).execute(file.getAbsolutePath());
                    mView = null;
                }
            }

            mCurrentlyDownloading.remove(mPhoto);
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String mFilepath = null;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... filepath) {
            mFilepath = filepath[0];

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int targetWidth = metrics.widthPixels;
            // 220dp to px (correlated with viewpager height in activity_toilet_sheet.xml)
            int targetHeight = 220 * metrics.densityDpi / 160;

            return decodeSampledBitmapFromFile(mFilepath, targetWidth, targetHeight);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String filepath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}