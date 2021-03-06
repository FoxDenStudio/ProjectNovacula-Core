/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 * *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 * *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.core.plugins;

import net.foxdenstudio.novacula.core.plugins.detector.ADetect;
import net.foxdenstudio.novacula.core.plugins.events.Event;
import net.foxdenstudio.novacula.core.plugins.events.LaunchEvent;
import net.foxdenstudio.novacula.core.plugins.events.LoadEvent;
import net.foxdenstudio.novacula.core.utils.NovaLogger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class PluginSystem {

    private static final HashMap<Class<?>, Object> registeredListeners = new HashMap<>();
    private static NovaLogger novaLogger;

    public PluginSystem(NovaLogger novaLogger) {
        PluginSystem.novaLogger = novaLogger;

        ArrayList<Class<?>> classArrayList = new ArrayList<>();

        final ADetect.TypeReporter reporter = new ADetect.TypeReporter() {
            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                try {
                    classArrayList.add(Class.forName(className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Class<? extends Annotation>[] annotations() {
                //noinspection unchecked
                return new Class[]{NovaPlugin.class};
            }
        };

        final ADetect aDetect = new ADetect(reporter);
        try {
            aDetect.detect();

            classArrayList.stream().forEach(this::register0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        callEvent(new LaunchEvent());
    }

    public synchronized static HashMap<Class<?>, Object> getRegisteredListeners() {
        return registeredListeners;
    }

    public static void callEvent(final Event event) {
        new Thread() {
            @Override
            public void run() {
                callEvent0(event);
            }
        }.start();
    }

    private static void callEventSpecClass0(Class<?> aClass, Object instance, final Event event) {
        callMethodsForClass0(aClass, instance, event);
    }

    private static void callEvent0(final Event event) {
        synchronized (registeredListeners) {
            registeredListeners.entrySet().parallelStream().forEach(clzEntry -> callMethodsForClass0(clzEntry.getKey(), clzEntry.getValue(), event));
        }
    }

    private static void callMethodsForClass0(Class<?> clz, Object instance, final Event event) {
        Arrays.asList(clz.getMethods()).parallelStream().forEach(method -> {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Class<?>[] expParams = method.getParameterTypes();
                if (expParams.length > 0) {
                    Class<?> expEvent = expParams[0];

                    if (event.getClass().equals(expEvent)) {
                        try {
                            method.invoke(instance, event);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            novaLogger.logError("ERROR: " + ex.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void register0(Class<?> aClass) {
        novaLogger.log("In Register0");
        synchronized (registeredListeners) {
            novaLogger.log("Syncd List");
            if (!registeredListeners.containsKey(aClass)) {
                try {
                    Object instance = aClass.newInstance();
                    registeredListeners.put(aClass, instance);
                    callEventSpecClass0(aClass, instance, new LoadEvent(registeredListeners.keySet()));
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
