package com.example.jesta.common;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class IntentUtils {

    /**
     * Gets the Gallery intent, to let the user choose an image.
     *
     * @return Returns the Gallery intent if found in the device, otherwise null.
     */
    public static Intent gallery(Context context) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        // Ensures there's a Gallery activity:
        if (galleryIntent.resolveActivity(context.getPackageManager()) != null) {
            galleryIntent.setType("image/*");
            return galleryIntent;
        } else {
            return null;
        }
    }

    /**
     * Gets the Camera intent, to let the user capture an image.
     *
     * @return Returns the Camera intent if found in the device, otherwise null.
     */
    public static Intent camera(Context context) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensures there's a Camera activity:
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            return cameraIntent;
        } else {
            return null;
        }
    }

}
