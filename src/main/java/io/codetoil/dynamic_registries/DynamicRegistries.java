package io.codetoil.dynamic_registries;

public class DynamicRegistries {

    public static final DynamicRegistriesObjectHelper.DynamicRegistriesDynamicClassLoader
            dynamicRegistriesDynamicClassLoader = new DynamicRegistriesObjectHelper.
            DynamicRegistriesDynamicClassLoader(DynamicRegistries.class.getClassLoader());
}
