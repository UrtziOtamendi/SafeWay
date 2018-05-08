package otamendi.urtzi.com.safeway.Utils;

        import android.graphics.Bitmap;
        import android.util.Log;

        import com.google.firebase.auth.FirebaseAuth;

        import net.glxn.qrgen.android.QRCode;

        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONStringer;

/**
 * Created by urtzi on 07/05/2018.
 */

public class generateQR {

    public static Bitmap generateFromString(String text){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bitmap bitmap= QRCode.from(text+" "+uid).withSize(1000,1000).bitmap();
        return bitmap;
    }
}
