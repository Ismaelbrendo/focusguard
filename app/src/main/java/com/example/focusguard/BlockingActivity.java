package com.example.focusguard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BlockingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A única responsabilidade desta Activity é carregar o layout de bloqueio.
        // Todo o código extra que causa o erro foi removido.
        setContentView(R.layout.activity_blocking);
    }
}
