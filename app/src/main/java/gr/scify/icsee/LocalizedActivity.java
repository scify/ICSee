package gr.scify.icsee;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LocalizedActivity extends AppCompatActivity {

    private String initialLocale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialLocale = LocaleManager.getPersistedLocale(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initialLocale != null && !initialLocale.equals(LocaleManager.getPersistedLocale(this))) {
            recreate();
        }
    }

}
