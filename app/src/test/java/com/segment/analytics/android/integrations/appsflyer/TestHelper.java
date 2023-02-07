package com.segment.analytics.android.integrations.appsflyer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.appsflyer.AppsFlyerLib;

import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestHelper {
    MockedStatic<AppsFlyerLib> staticAppsFlyerLib;
    public AppsFlyerLib mockAppsflyerLib(){
        this.staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        this.staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        return appsFlyerLib;
    }
    public void closeMockAppsflyerLib(){
        this.staticAppsFlyerLib.close();
    }

    public static Object getPrivateFieldForObject(String fieldName, Class classObject, Object objToGetValueFrom) throws Exception{
        Field field = classObject.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(classObject.cast(objToGetValueFrom));
    }

    public static void setPrivateFieldForObject(String fieldName, Class classObject, Object objToGetValueFrom, Class valueClass, Object value) throws Exception{
        Field field = classObject.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(objToGetValueFrom,valueClass.cast(value));
    }

    public static Method getPrivateMethodForObjectReadyToInvoke(String funcName,Class<?>... parameterTypesForMethod) throws Exception{
        Method getFlagMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod(funcName,parameterTypesForMethod);
        getFlagMethod.setAccessible(true);
        return getFlagMethod;
    }
}
