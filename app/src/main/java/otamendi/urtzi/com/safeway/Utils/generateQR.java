package otamendi.urtzi.com.safeway.Utils;

import android.graphics.Bitmap;

import net.glxn.qrgen.android.QRCode;

/**
 * Created by urtzi on 07/05/2018.
 */

public class generateQR {

    public Bitmap generateFromString(String text){
         Bitmap bitmap= QRCode.from(text).withSize(1000,1000).bitmap();
        return bitmap;
    }
}
