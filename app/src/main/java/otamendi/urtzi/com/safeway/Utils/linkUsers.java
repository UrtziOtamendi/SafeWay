package otamendi.urtzi.com.safeway.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;

public class linkUsers {

    public static void saveLinker(linkedID linker){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
         mDatabase.child("linkedID").child(userF.getUid()).setValue(linker.toMap());

    }

    public static void linkReceptor(String receptorUID, String name){
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(receptorUID).child("linkedID").child(name).setValue(userF.getUid());
    }

}

