package cn.creditease.rnsettings;

import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

/**
 * Created by lihui on 16/3/3.
 */
public class RNSettings extends ReactContextBaseJavaModule implements LifecycleEventListener
{
    public static final String RN_MODULE = "RNSettings";
    public static final String TAG = "RNSettings";

    private boolean mIgnoringUpdates = false;
    private static final String NAME= "RNSettings";

    private SharedPreferences mSharedP = null;
    private ReactApplicationContext mReactContext;

    private SharedPreferences.OnSharedPreferenceChangeListener myListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {

            if (mIgnoringUpdates)
            {
                return;
            }
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("settingsUpdated", mSharedP.getAll());
        }
    };

    @Nullable
    @Override
    public Map<String, Object> getConstants()
    {
        return MapBuilder.<String, Object>of(
               "settings", mSharedP.getAll());
    }

    public RNSettings(ReactApplicationContext aReactContext)
    {
        super(aReactContext);
        mReactContext = aReactContext;
        mSharedP = aReactContext.getSharedPreferences(NAME, 0);
    }

    @Override
    public String getName() {
        return RN_MODULE;
    }

    @Override
    public void onHostResume() {
        mSharedP.registerOnSharedPreferenceChangeListener(myListener);
    }

    @Override
    public void onHostPause() {
        mSharedP.unregisterOnSharedPreferenceChangeListener(myListener);
    }

    @Override
    public void onHostDestroy() {
        mSharedP.unregisterOnSharedPreferenceChangeListener(myListener);
    }


    @ReactMethod
    public void setValues(ReadableMap aValues)
    {
        if (aValues != null)
        {
            mIgnoringUpdates = true;

            ReadableMapKeySetIterator iter = aValues.keySetIterator();

            SharedPreferences.Editor editor = mSharedP.edit();

            while (iter.hasNextKey())
            {
                String key = iter.nextKey();

                ReadableType  type = aValues.getType(key);

                switch (type)
                {
                    case Null:
                    {
                        editor.putString(key, "");
                    }
                    break;
                    case Boolean:
                    {
                        boolean b = aValues.getBoolean(key);
                        editor.putBoolean(key, b);
                    }
                    break;
                    case String:
                    {
                        String s = aValues.getString(key);
                        editor.putString(key, s);
                    }
                    break;
                    case Number:
                    {
                        int value = aValues.getInt(key);
                        editor.putInt(key, value);
                    }
                    break;
                }
                editor.commit();
            }

            mIgnoringUpdates = false;
        }
    }

    @ReactMethod
    public void deleteValues(ReadableArray aKeys)
    {
        if (aKeys != null && aKeys.size() > 0)
        {
            int size = aKeys.size();
            mIgnoringUpdates = true;

            //TODO: delete data
            mIgnoringUpdates = false;
        }
    }
}
