package id.co.imastudio.drawphoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView tampil;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    private static final String TAG = "Touch";
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    float x1, y1, x2, y2;
    Bitmap bmp;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tampil = (ImageView) findViewById(R.id.imgView);
        tampil.setOnTouchListener(this);
        Drawable d = getResources().getDrawable(R.drawable.user);
        bmp = ((BitmapDrawable) d).getBitmap();
        tampil.setImageBitmap(bmp);
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }



    public void onDraw() {
        bmp = Bitmap.createBitmap(tampil.getWidth(), tampil.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        tampil.draw(c);

        Paint pnt = new Paint();
        pnt.setColor(Color.RED);
        c.drawLine(x1, y1, x2, y2, pnt);
        tampil.setImageBitmap(bmp);
        i = 1;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                Log.i(TAG, "(" + String.valueOf((int) event.getX()) + "," + String.valueOf((int) event.getY()) + ")");
                if (i == 1) {
                    x1 = event.getX();
                    y1 = event.getY();
                    i = 2;
                    Log.i(TAG, "coordinate x1 : " + String.valueOf(x1) + " y1 : " + String.valueOf(y1));
                } else if (i == 2) {
                    x2 = event.getX();
                    y2 = event.getY();
                    i = 3;
                    Log.i(TAG, "coordinate x2 : " + String.valueOf(x2) + " y2 : " + String.valueOf(y2));
                    onDraw();
                }
                break;
        }
        return true;
    }
}