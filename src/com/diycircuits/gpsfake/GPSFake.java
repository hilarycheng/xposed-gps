package com.diycircuits.gpsfake;

import java.lang.reflect.Method;
import java.util.HashSet;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XC_MethodHook;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class GPSFake implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private boolean mLocationManagerHooked = false;

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
	XposedBridge.log("Loaded app: " + lpparam.packageName);
	// if (lpparam.packageName.equals("com.diycircuits.gpsfake"))
    }

    private void handleGetSystemService(String name, Object instance) {
	if (name.equals(Context.LOCATION_SERVICE)) {
	    if (!mLocationManagerHooked) {
		XposedBridge.log("Hook Location Manager");
		// hookAll(XLocationManager.getInstances(instance), mSecret);
		mLocationManagerHooked = true;
	    }
	}
    }

    private void hookSystemService(String context) {
	try {
	    XC_MethodHook methodHook = new XC_MethodHook() {
		    @Override
		    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		    }

		    @Override
		    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
			if (!param.hasThrowable())
			    try {
				if (param.args.length > 0 && param.args[0] != null) {
				    String name = (String) param.args[0];
				    Object instance = param.getResult();
				    if (name != null && instance != null) {
					handleGetSystemService(name, instance);
				    }
				}
			    } catch (Throwable ex) {
				throw ex;
			    }
		    }
		};

	    Set<XC_MethodHook.Unhook> hookSet = new HashSet<XC_MethodHook.Unhook>();
	    
	    Class<?> hookClass = null;
	    try {
		hookClass = findClass(context, null);
		if (hookClass == null)
		    throw new ClassNotFoundException(context);
		XposedBridge.log("Zygote Context Find Class Done");
	    } catch (Exception ex) {
		XposedBridge.log("Zygote Context Impl Exception " + ex);
	    }

	    XposedBridge.log("Zygote Context Find Class " + hookClass);
	    Class<?> clazz = hookClass;
	    while (clazz != null) {
		for (Method method : clazz.getDeclaredMethods()) {
		    if (method != null && method.getName().equals("getSystemService")) {
			hookSet.add(XposedBridge.hookMethod(method, methodHook));
		    }
		}
		clazz = (hookSet.isEmpty() ? clazz.getSuperclass() : null);
	    }
	} catch (Exception ex) {
	    XposedBridge.log("Zygote Context Hook Exception " + ex);
	}
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
	hookSystemService("android.app.ContextImpl");
	hookSystemService("android.app.Activity");
    }

}
