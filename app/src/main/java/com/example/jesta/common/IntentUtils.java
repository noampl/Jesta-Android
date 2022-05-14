package com.example.jesta.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    /**
     * Gets the gmail intent, to let the user send an email.
     *
     * @return Returns the Gmail intent.
     */
    public static Intent gmail(Context context, String mailTo) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + mailTo));
    }

    /**
     * Gets the SMS intent, to let the user send an SMS.
     *
     * @return Returns the SMS intent.
     */
    public static Intent sms(String toNumber) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(toNumber)));
        return smsIntent;
    }

    /**
     * Gets the WhatsApp intent, to let the user send a WhatsApp message.
     *
     * @return Returns the WhatsApp intent.
     */
    public static Intent whatsApp(String toNumber) {
        String url = ("https://api.whatsapp.com/send?phone=" + toNumber);
        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
        whatsappIntent.setData(Uri.parse(url));
        return whatsappIntent;
    }

}
