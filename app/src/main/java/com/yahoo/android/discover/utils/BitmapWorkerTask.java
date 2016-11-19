package com.yahoo.android.discover.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Created by yboini on 11/18/16.
 */

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    Context context;
    public BitmapWorkerTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Integer... voids) {
        return BitmapFactory.decodeResource(this.context.getResources(), voids[0]);
    }
}
