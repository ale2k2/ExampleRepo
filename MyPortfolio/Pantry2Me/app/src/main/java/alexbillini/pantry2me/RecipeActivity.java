package alexbillini.pantry2me;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RecipeActivity extends AppCompatActivity {

    private Button recipeButton;

    private TextView firstRecipe, secondRecipe, firstRecipeTitle, secondRecipeTitle;

    private Spinner spinner, spinner2;

    private ImageView firstImage, secondImage;

    private String example = null;

    private String firstIngredient = null;
    private String secondIngredient = null;
    private String thirdIngredient = null;

    private final static String TAG = "Recipe Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeButton = findViewById(R.id.recipe_btn);
        spinner = findViewById(R.id.food_options);
        spinner2 = findViewById(R.id.food_options2);
        firstRecipe = findViewById(R.id.first_recipe);
        secondRecipe = findViewById(R.id.second_recipe);
        firstRecipeTitle = findViewById(R.id.first_recipe_title);
        secondRecipeTitle = findViewById(R.id.second_recipe_title);
        secondImage = findViewById(R.id.second_recipe_image_view);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ingredients_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    firstIngredient = null;
                    Log.d(TAG, "First Ingredient");
                } else if(position == 1) {
                    firstIngredient = "apples";
                    Log.d(TAG, "First Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 2) {
                    firstIngredient = "oranges";
                    Log.d(TAG, "Second Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 3) {
                    firstIngredient = "strawberries";
                    Log.d(TAG, "Third Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 4) {
                    firstIngredient = "bananas";
                    Log.d(TAG, "Fourth Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.ingredients_array2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    secondIngredient = null;
                    Log.d(TAG, "Second Ingredient");
                } else if(position == 1) {
                    secondIngredient = "flour";
                    Log.d(TAG, "First Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 2) {
                    secondIngredient = "eggs";
                    Log.d(TAG, "Second Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 3) {
                    secondIngredient = "milk";
                    Log.d(TAG, "Third Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                } else if(position == 4) {
                    secondIngredient = "butter";
                    Log.d(TAG, "Fourth Item");
                    example = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=3b0bd63a6ce84b33a416eb3c67db9432&ingredients=" + firstIngredient + ",+" + secondIngredient + ",+sugar&number=2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage=BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void onRecipeClick(View view) {
        // Create a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        if(example != null) {
            // Create a new JsonArrayRequest that requests available subjects
            JsonArrayRequest requestObj = new JsonArrayRequest
                    (Request.Method.GET, example, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                Log.d(TAG, "JSON Response:" + response);
                                firstRecipeTitle.setText("Title: " + response.getJSONObject(0).getString("title"));
                                secondRecipeTitle.setText("Title: " + response.getJSONObject(1).getString("title"));
                                new DownloadImageFromInternet((ImageView) findViewById(R.id.first_recipe_image_view)).execute(response.getJSONObject(0).getString("image"));
                                new DownloadImageFromInternet((ImageView) findViewById(R.id.second_recipe_image_view)).execute(response.getJSONObject(1 ).getString("image"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            // Add the request to the RequestQueue
            queue.add(requestObj);
        } else {
            Log.d(TAG, "Must Input an Ingredient");
        }
    }
}