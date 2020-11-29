package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.BufferedReader;

public class PlaceDetailActivity extends AppCompatActivity {
    private TextView likenum, hatenum, bookname, author;
    private ImageView bookcover;
    private ImageButton likebtn, hatebtn;
    ExpandableTextView expTv1, expTv2;

    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootRef;
    BookDescrip newpage;
    String name_book;
    String bookIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        bookIdx = ""+2;
        name_book = "Cranford";
        newpage = new BookDescrip();
        bindView();
        setFirebase();
        displayNumberOfLikes(bookIdx, user.getUid());
        displayNumberOfHates(bookIdx, user.getUid());

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLikeClicked(likebtn, bookIdx, user.getUid());
                displayNumberOfLikes(bookIdx, user.getUid());
            }
        });

        hatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHateClicked(hatebtn, bookIdx, user.getUid());
                displayNumberOfHates(bookIdx, user.getUid());
            }
        });
    }

    public void displayNumberOfHates(String bookId, String currentUserId){
        DatabaseReference hatesRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates");
        hatesRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long numOfHates = 0;
                    if(dataSnapshot.hasChild("hates")){
                        numOfHates = dataSnapshot.child("hates").getValue(Long.class);
                    }

                    //Populate numOfHates on post i.e. textView.setText(""+numOfHates)
                    //This is to check if the user has liked the post or not
                    hatenum.setText(""+numOfHates);
                    Log.i("i", hatenum+"<<<<<<<<<<<<<<<<<<<");
                    hatebtn.setSelected(dataSnapshot.hasChild(currentUserId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onHateClicked(View v, String bookId, String userId){
        DatabaseReference hatesRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates").child("hates");
        DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates").child(userId);
        hatesRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numHates = 0;
                if(dataSnapshot.exists()){
                    numHates = dataSnapshot.getValue(Long.class);
                }
                if(hatebtn.isSelected()){
                    //If already liked then user wants to unlike the post
                    hatesRef.setValue(numHates-1);
                    uidRef.removeValue();
                    likebtn.setEnabled(true);

                } else {
                    //If not liked already then user wants to like the post
                    hatesRef.setValue(numHates+1);
                    uidRef.setValue(userId);
                    likebtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displayNumberOfLikes(String bookId, String currentUserId){
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes");
        likesRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long numOfLikes = 0;
                    if(dataSnapshot.hasChild("likes")){
                        numOfLikes = dataSnapshot.child("likes").getValue(Long.class);
                    }

                    //Populate numOfLikes on post i.e. textView.setText(""+numOfLikes)
                    //This is to check if the user has liked the post or not
                    likenum.setText(""+numOfLikes);
                    likebtn.setSelected(dataSnapshot.hasChild(currentUserId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onLikeClicked(View v, String bookId, String userId){
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes").child("likes");
        DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes").child(userId);
        likesRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numLikes = 0;
                if(dataSnapshot.exists()){
                    numLikes = dataSnapshot.getValue(Long.class);
                }
                if(likebtn.isSelected()){
                    //If already liked then user wants to unlike the post
                    likesRef.setValue(numLikes-1);
                    uidRef.removeValue();
                    hatebtn.setEnabled(true);

                } else {
                    //If not liked already then user wants to like the post
                    likesRef.setValue(numLikes+1);
                    uidRef.setValue(userId);
                    likebtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void gotoReview(View v) {

    }

    public void setBookcover(String Url) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Url);
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bookcover.setImageBitmap(bitmap);
            }
        });
    }

    public void setFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();
        loadBookDescrip();
        Log.i("i", user.getUid()+"<<<<<<<<<<<<"+user.getEmail());
    }

    public void loadBookDescrip() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    BookDescrip mypage = postSnapshot.getValue(BookDescrip.class);
                    if(key.equals(name_book)) {
                        setBookcover(mypage.getBookcover());
                        bookname.setText(key);
                        author.setText(mypage.getAuthor());
                        expTv1.setText(mypage.getDescription());
                        expTv2.setText(mypage.getAuthor_descrip());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("book_descrip").addValueEventListener(valueEventListener);
    }

    @IgnoreExtraProperties
    static public class BookDescrip {
        public String author;
        public String author_descrip;
        public String bookcover;
        public String description;

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setAuthor_descrip(String author_descrip) {
            this.author_descrip = author_descrip;
        }

        public void setBookcover(String bookcover) {
            this.bookcover = bookcover;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAuthor() {
            return author;
        }

        public String getAuthor_descrip() {
            return author_descrip;
        }

        public String getBookcover() {
            return bookcover;
        }

        public String getDescription() {
            return description;
        }

        public BookDescrip() {
        }

        public BookDescrip(String author, String author_descrip, String bookcover, String description) {
            this.author = author;
            this.author_descrip = author_descrip;
            this.bookcover = bookcover;
            this.description = description;
        }
    }


    public void bindView() {
        likenum = (TextView)findViewById(R.id.likenum);
        hatenum = (TextView)findViewById(R.id.hatenum);
        bookname = (TextView)findViewById(R.id.bookname_descrip);
        author = (TextView)findViewById(R.id.author_descrip);
        bookcover = (ImageView)findViewById(R.id.bookcover_descrip);
        likebtn = (ImageButton)findViewById(R.id.like_descrip);
        hatebtn = (ImageButton)findViewById(R.id.hate_descrip);

        ((TextView)findViewById(R.id.sample1).findViewById(R.id.title)).setText("책 소개");
        ((TextView)findViewById(R.id.sample2).findViewById(R.id.title)).setText("저자 소개");
        expTv1 = (ExpandableTextView) findViewById(R.id.sample1)
                .findViewById(R.id.expand_text_view);
        expTv2 = (ExpandableTextView) findViewById(R.id.sample2)
                .findViewById(R.id.expand_text_view);
    }
}