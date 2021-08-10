package com.kunalbadole.coviddetectorusingx_rays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kunalbadole.coviddetectorusingx_rays.ml.ModelTF;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

   private Button btn,predict_btn;
    private ImageView imageView;
    private int SELECT_IMAGE_CODE = 1;
    private TextView textView;
    private Bitmap bitmap;

    //handing exceptions
    //name for tag
    private String name="Kunaldo_07";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn_select);
        imageView =  findViewById(R.id.Xray_Image);
        predict_btn= findViewById(R.id.btn_predict);
        textView = findViewById(R.id.result);

        //new View.OnClickListener()
        //these setOnClickListener functions are replaced with lambda
        btn.setOnClickListener( (View view) -> {
         //   @Override
         //   public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),100);
                //whatever we pick image here we will get the data at on activity result

                Log.d(name,"Chooser button has been Clicked");
          //  }
        });

        //(View view) ->
        //new View.OnClickListener()
        predict_btn.setOnClickListener( (View view) -> {
         //   @Override
          //  public void onClick(View view) {
                bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true);

            try {
                ModelTF model = ModelTF.newInstance(getApplicationContext());

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(bitmap);
                ByteBuffer byteBuffer = tensorImage.getBuffer();

                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                ModelTF.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                // Releases model resources if no longer used.
                model.close();

                Log.d(name,"Output has been Generated");

                    Log.d(name,"Output="+outputFeature0.getFloatArray()[0]+","+outputFeature0.getFloatArray().length);

                    //  Toast.makeText(getApplicationContext(), (CharSequence) outputFeature0,Toast.LENGTH_LONG).show();

                    if (outputFeature0.getFloatArray()[0] >= 0.5){
                        textView.setText("The Patient is Covid Positive");
                        Log.d(name,"It is Covid Positive");
                    }else if (outputFeature0.getFloatArray()[0] < 0.5){
                        textView.setText("The Patient is Covid Negative");
                        Log.d(name,"It is Covid Negative");
                    }



            } catch (IOException e) {
                // TODO Handle the exception
            }

        });

    }

    //getting the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request code = select image code

        if(requestCode == 100)
        {
            imageView.setImageURI(data.getData());
            Log.d(name,"Image has been set");

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Log.d(name,"Bitmap Initiated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}