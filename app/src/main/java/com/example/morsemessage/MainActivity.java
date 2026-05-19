package com.example.morsemessage;

import com.example.morsemessage.R;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonVibrate;
    private TextView textViewMorseResult;
    private HashMap<Character, String> morseAlphabet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inițializare componente UI
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonVibrate = findViewById(R.id.buttonVibrate);
        textViewMorseResult = findViewById(R.id.textViewMorseResult);

        // 2. Construim dicționarul Morse (Alfabetul de bază)
        initMorseAlphabet();

        // 3. Acțiunea la apăsarea butonului
        buttonVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editTextMessage.getText().toString().trim().toUpperCase();

                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Introdu un text mai întâi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traducem textul în cod vizual (puncte și linii)
                String morseCode = translateToMorse(input);
                textViewMorseResult.setText(morseCode);

                // Trimitem impulsurile către motorul de vibrație
                vibrateMorse(morseCode);
            }
        });
    }

    private void initMorseAlphabet() {
        morseAlphabet = new HashMap<>();
        morseAlphabet.put('A', ".-");    morseAlphabet.put('B', "-...");
        morseAlphabet.put('C', "-.-.");  morseAlphabet.put('D', "-..");
        morseAlphabet.put('E', ".");     morseAlphabet.put('F', "..-.");
        morseAlphabet.put('G', "--.");   morseAlphabet.put('H', "....");
        morseAlphabet.put('I', "..");    morseAlphabet.put('J', ".---");
        morseAlphabet.put('K', "-.-");   morseAlphabet.put('L', ".-..");
        morseAlphabet.put('M', "--");    morseAlphabet.put('N', "-.");
        morseAlphabet.put('O', "---");   morseAlphabet.put('P', ".--.");
        morseAlphabet.put('Q', "--.-");  morseAlphabet.put('R', ".-.");
        morseAlphabet.put('S', "...");   morseAlphabet.put('T', "-");
        morseAlphabet.put('U', "..-");   morseAlphabet.put('V', "...-");
        morseAlphabet.put('W', ".--");   morseAlphabet.put('X', "-..-");
        morseAlphabet.put('Y', "-.--");  morseAlphabet.put('Z', "--..");
        morseAlphabet.put(' ', " ");     // Spațiu între cuvinte
    }

    private String translateToMorse(String text) {
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (morseAlphabet.containsKey(c)) {
                builder.append(morseAlphabet.get(c)).append(" ");
            }
        }
        return builder.toString().trim();
    }

    private void vibrateMorse(String morseCode) {
        // Durate în milisecunde
        int dotDuration = 150;    // Vibrație scurtă
        int dashDuration = 450;   // Vibrație lungă
        int pauseDuration = 150;  // Pauză între semne

        ArrayList<Long> patternList = new ArrayList<>();

        // Primul element din pattern este pauza de dinaintea pornirii (0 înseamnă pornește instant)
        patternList.add(0L);

        for (char c : morseCode.toCharArray()) {
            if (c == '.') {
                patternList.add((long) dotDuration);   // Timp cât vibrează
                patternList.add((long) pauseDuration); // Timp cât stă oprit după
            } else if (c == '-') {
                patternList.add((long) dashDuration);
                patternList.add((long) pauseDuration);
            } else if (c == ' ') {
                // Spațiu suplimentar între litere/cuvinte
                patternList.add((long) pauseDuration * 2);
            }
        }

        // Convertim ArrayList în array primitiv long[] cerut de Android
        long[] pattern = new long[patternList.size()];
        for (int i = 0; i < patternList.size(); i++) {
            pattern[i] = patternList.get(i);
        }

        // METODA CLASICĂ ȘI SIGURĂ: Protejează aplicația de crash pe Huawei P20 Lite
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    vibrator.vibrate(pattern, -1);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Eroare hardware la vibrație.", Toast.LENGTH_SHORT).show();
        }
    }
}
