package com.david.camara;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends AppCompatActivity {

    private Uri uriFoto;
    private String sDirectorioActualFoto;
    private ImageView imageView;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        imageView = (ImageView) findViewById(R.id.foto);

        Button bCamara = (Button) findViewById(R.id.botonCamara);
        bCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tomarFotoBitmapIntent();
                tomarFotoFullSizeIntent();
            }
        });
        Button bGaleria = (Button) findViewById(R.id.botonGaleria);
        bGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFotoDeGaleriaIntent();
            }
        });
        print("App iniciada");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == RESULTADO_EDITAR && resultCode == RESULT_OK){

        } else if (requestCode == RESULTADO_GALERIA && resultCode == RESULT_OK){
            imageView.setImageURI(Uri.parse(data.getDataString()));
        } else if(requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK
                && uriFoto != null) {
            //ponerFotoBitmap(data);
            ponerFotoFullSize(imageView, uriFoto.toString());
        }

    }

    public void tomarFotoBitmapIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULTADO_FOTO);

    }

    public void ponerFotoBitmap(Intent data){

        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageView.setImageBitmap(imageBitmap);
        print("Cargada foto en modo Bitmap");

    }

    public void seleccionarFotoDeGaleriaIntent(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RESULTADO_GALERIA);

    }

    public void tomarFotoFullSizeIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = crearArchivoImagen();
            } catch (IOException ex) {
                // Error occurred while creating the File
                print("Error al generar el archivo");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                uriFoto = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(takePictureIntent, RESULTADO_FOTO);
            }
        }


    }

    protected void ponerFotoFullSize(ImageView imageView, String uri) {

        if (uri != null) {
            imageView.setImageURI(Uri.parse(uri));
        } else{
            imageView.setImageBitmap(null);
        }

    }


    private File crearArchivoImagen() throws IOException {

        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "JPEG_" + fecha + "_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(
                nombreFichero,  /* prefix */
                ".jpg",         /* suffix */
                directorio      /* directory */
        );

        // guardar el fichero: dirección para usar en los ACTION_VIEW intents
        sDirectorioActualFoto = imagen.getAbsolutePath();
        return imagen;
    }

    private void print (String texto){

        Toast toast = Toast.makeText(this, texto , Toast.LENGTH_LONG);
        toast.show();

    }

}
