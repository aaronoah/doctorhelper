package edu.sabien.aaronzhao.doctorhelper.helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AaronZhao on 29/04/16.
 */
public class AuthCallback implements Callback<String> {

    private Choreographer choreographer;
    private static final String TAG = "AuthCallback";
    public AuthCallback(Choreographer choreographer){
        this.choreographer = choreographer;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()){
            Log.d(TAG, "Status Code = " + response.code());
            String authString = response.body().toString();
            Log.d(TAG, authString);
            try{
                JSONObject auth = new JSONObject(authString);
                String authResult= auth.getJSONObject("Contenido")
                        .getJSONObject("parametros").getJSONObject("authenticateResult")
                        .get("value").toString();
                if (authResult.equals("not accepted")){
                    Log.e(TAG, "Auth refused with code: not accepted");
                }else {
                    Log.d(TAG, "Auth success!");
                    choreographer.setChStatus(true);
                    choreographer.setAuthStatus(true);
                    choreographer.setAuthSession(authResult);
                }
            }catch (JSONException ex){
                ex.printStackTrace();
                Log.e(TAG, "Parsing error!");
                choreographer.setAuthStatus(false);
            }

        }else {
            Log.e(TAG, "Auth failed!");
            choreographer.setChStatus(false);
            choreographer.setAuthStatus(false);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Log.e(TAG, "Auth Connection failed!");
        Log.e(TAG, t.toString());
        choreographer.setChStatus(false);
        choreographer.setAuthStatus(false);
    }

}
