package gr.scify.icsee;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LocalizedActivity extends AppCompatActivity {

    protected String initialLocale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialLocale = LocaleManager.getPersistedLocale(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLocale = LocaleManager.getPersistedLocale(getApplicationContext());
        if (initialLocale != null && !initialLocale.equals(currentLocale)) {
            recreate();
        }
    }

}
