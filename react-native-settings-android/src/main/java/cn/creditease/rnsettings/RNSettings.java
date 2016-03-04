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
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
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
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String aKey)
        {
            if (mIgnoringUpdates)
            {
                return;
            }

//            Map<String, Object> map = MapBuilder.newHashMap();
//            mReactContext
//                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                    .emit("settingsUpdated", map);
        }
    };

    @Nullable
    @Override
    public Map<String, Object> getConstants()
    {
        Map<String, Object> map = MapBuilder.<String, Object>of(
                "settings", mSharedP.getAll());
        return map;
    }

    public RNSettings(ReactApplicationContext aReactContext)
    {
        super(aReactContext);
        mReactContext = aReactContext;
        mSharedP = aReactContext.getSharedPreferences(NAME, 0);

        mReactContext.addLifecycleEventListener(this);
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
//            mIgnoringUpdates = true;

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
            }

//            mIgnoringUpdates = false;
            editor.commit();

            WritableMap map = new WritableNativeMap();

            Map<String, ?> tmp = mSharedP.getAll();

            for (String key : tmp.keySet())
            {
                Object val = tmp.get(key);

                if (val instanceof Integer) {
                    int value = ((Integer) val).intValue();

                    map.putInt(key, value);
                } else if (val instanceof String) {
                    String s = (String) val;
                    map.putString(key, s);
                } else if (val instanceof Double) {
                    double d = ((Double) val).doubleValue();
                    map.putDouble(key, d);
                } else if (val instanceof Float) {
                    float f = ((Float) val).floatValue();
                    map.putDouble(key, f);
                } else if (val instanceof Long) {
                    long l = ((Long) val).longValue();
                    map.putDouble(key, l);
                } else if (val instanceof Boolean) {
                    boolean b = ((Boolean) val).booleanValue();
                    map.putBoolean(key, b);
                }
            }

            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("settingsUpdated", map);
        }
    }

    @ReactMethod
    public void deleteValues(ReadableArray aKeys)
    {
        if (aKeys != null && aKeys.size() > 0)
        {
            int size = aKeys.size();
//            mIgnoringUpdates = true;

            //TODO: delete data
//            mIgnoringUpdates = false;
        }
    }
}
