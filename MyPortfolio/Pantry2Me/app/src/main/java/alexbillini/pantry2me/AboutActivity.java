package alexbillini.pantry2me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {

    private Button missionButton, helpedButton, faqButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        missionButton = findViewById(R.id.our_mission_btn);
        helpedButton = findViewById(R.id.helped_btn);
        faqButton = findViewById(R.id.faq_btn);
    }

    public void onMissionClick(View view) {
        Intent intent = new Intent(this, MissionActivity.class);
        startActivity(intent);
    }

    public void onHelpedClick(View view) {
        Intent intent = new Intent(this, HelpedActivity.class);
        startActivity(intent);
    }

    public void onFaqClick(View view) {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }
}