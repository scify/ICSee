package gr.scify.icsee.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;

import gr.scify.icsee.R;
import gr.scify.icsee.data.LoginRepository;
import gr.scify.icsee.data.VolleyCallback;
import gr.scify.icsee.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, new VolleyCallback() {
            @Override
            public void onSuccess(LoggedInUser user) {
                loginResult.setValue(new LoginResult(new LoggedInUserView(user)));
            }

            @Override
            public void onError(VolleyError data) {
                assert data.networkResponse.headers != null;
                boolean shouldShowError = data.networkResponse.headers.containsKey("SHOULD_SHOW_ERROR");
                if (shouldShowError)
                    loginResult.setValue(new LoginResult(R.string.login_failed_wrong_credentials));
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordLengthValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password_num_chars));
        } else if (!isPasswordUppercaseValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password_uppercase));
        } else if (!isPasswordSpecialCharactersValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password_special_chars));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordLengthValid(String password) {
        return password != null && password.trim().length() >= 8;
    }

    private boolean isPasswordUppercaseValid(String password) {
        if (password == null)
            return false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordSpecialCharactersValid(String password) {
        if (password == null)
            return false;
        String[] chars = {"!", "@", "#", "$", "%", "+", "="};
        for (String ch : chars) {
            if (password.contains(ch))
                return true;
        }
        return false;
    }
}