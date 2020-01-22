/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.mmserver.scheduler;

/**
 *
 * @author olivier.tatsinkou
 */
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.swing.Action;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

public class Utils {

    private static Set<Integer> tableNavigationKeys = new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(36), Integer.valueOf(35), Integer.valueOf(37), Integer.valueOf(39), Integer.valueOf(38), Integer.valueOf(40), Integer.valueOf(33), Integer.valueOf(34)}));
    private static final Set<Class<?>> primitiveWrappers = new HashSet(Arrays.asList(new Class[]{Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Boolean.class}));
    private static final Set<Class<?>> primitives = new HashSet();
    private static final Map<String, Class<?>> primitiveMap = new HashMap();
    private static final Map<String, Class<?>> primitiveToWrapper = new HashMap();
    private static final Set<String> editableTypes = new HashSet();
    private static final Set<Class<?>> extraEditableClasses = new HashSet(Arrays.asList(new Class[]{BigDecimal.class, BigInteger.class, Number.class, String.class, ObjectName.class}));
    private static final Set<String> numericalTypes = new HashSet();
    private static final Set<String> extraNumericalTypes = new HashSet(Arrays.asList(new String[]{BigDecimal.class.getName(), BigInteger.class.getName(), Number.class.getName()}));
    private static final Set<String> booleanTypes = new HashSet(Arrays.asList(new String[]{Boolean.TYPE.getName(), Boolean.class.getName()}));

    static {
        for (Class<?> c : primitiveWrappers) {
            try {
                Field f = c.getField("TYPE");
                Class<?> p = (Class) f.get(null);
                primitives.add(p);
                primitiveMap.put(p.getName(), p);
                primitiveToWrapper.put(p.getName(), c);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        for (Class<?> c : primitives) {
            editableTypes.add(c.getName());
        }
        for (Class<?> c : primitiveWrappers) {
            editableTypes.add(c.getName());
        }
        for (Class<?> c : extraEditableClasses) {
            editableTypes.add(c.getName());
        }
        for (Class<?> c : primitives) {
            String name = c.getName();
            if (!name.equals(Boolean.TYPE.getName())) {
                numericalTypes.add(name);
            }
        }
        for (Class<?> c : primitiveWrappers) {
            String name = c.getName();
            if (!name.equals(Boolean.class.getName())) {
                numericalTypes.add(name);
            }
        }
    }

    public static Class<?> getClass(String className)
            throws ClassNotFoundException {
        Class<?> c;
        if ((c = (Class) primitiveMap.get(className)) != null) {
            return c;
        }
        return Class.forName(className);
    }

    public static boolean isUniformCollection(Collection<?> c, Class<?> e) {
        if (e == null) {
            throw new IllegalArgumentException("Null reference type");
        }
        if (c == null) {
            throw new IllegalArgumentException("Null collection");
        }
        if (c.isEmpty()) {
            return false;
        }
        for (Object o : c) {
            if ((o == null) || (!e.isAssignableFrom(o.getClass()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean canBeRenderedAsArray(Object elem) {
        if (isSupportedArray(elem)) {
            return true;
        }
        if ((elem instanceof Collection)) {
            Collection<?> c = (Collection) elem;
            if (c.isEmpty()) {
                return false;
            }
            return (!isUniformCollection(c, CompositeData.class)) && (!isUniformCollection(c, TabularData.class));
        }
        if ((elem instanceof Map)) {
            return !(elem instanceof TabularData);
        }
        return false;
    }

    public static boolean isSupportedArray(Object elem) {
        if ((elem == null) || (!elem.getClass().isArray())) {
            return false;
        }
        Class<?> ct = elem.getClass().getComponentType();
        if (ct.isArray()) {
            return false;
        }
        if ((Array.getLength(elem) > 0) && ((CompositeData.class.isAssignableFrom(ct)) || (TabularData.class.isAssignableFrom(ct)))) {
            return false;
        }
        return true;
    }

    public static String getArrayClassName(String name) {
        String className = null;
        if (name.startsWith("[")) {
            int index = name.lastIndexOf("[");
            className = name.substring(index, name.length());
            if (className.startsWith("[L")) {
                className = className.substring(2, className.length() - 1);
            } else {
                try {
                    Class<?> c = Class.forName(className);
                    className = c.getComponentType().getName();
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Bad class name " + name, e);
                }
            }
        }
        return className;
    }

    public static String getReadableClassName(String name) {
        String className = getArrayClassName(name);
        if (className == null) {
            return name;
        }
        int index = name.lastIndexOf("[");
        StringBuilder brackets = new StringBuilder(className);
        for (int i = 0; i <= index; i++) {
            brackets.append("[]");
        }
        return brackets.toString();
    }

    public static boolean isEditableType(String type) {
        return editableTypes.contains(type);
    }

    public static String getDefaultValue(String type) {
        if ((numericalTypes.contains(type)) || (extraNumericalTypes.contains(type))) {
            return "0";
        }
        if (booleanTypes.contains(type)) {
            return "true";
        }
        type = getReadableClassName(type);
        int i = type.lastIndexOf('.');
        if (i > 0) {
            return type.substring(i + 1, type.length());
        }
        return type;
    }

    public static Object newStringConstructor(String type, String param)
            throws Exception {
        Constructor c = getClass(type).getConstructor(new Class[]{String.class});
        try {
            return c.newInstance(new Object[]{param});
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if ((t instanceof Exception)) {
                throw ((Exception) t);
            }
            throw e;
        }
    }

    private static Number createNumberFromStringValue(String value)
            throws NumberFormatException {
        String suffix = value.substring(value.length() - 1);
        if ("L".equalsIgnoreCase(suffix)) {
            return Long.valueOf(value.substring(0, value.length() - 1));
        }
        if ("F".equalsIgnoreCase(suffix)) {
            return Float.valueOf(value.substring(0, value.length() - 1));
        }
        if ("D".equalsIgnoreCase(suffix)) {
            return Double.valueOf(value.substring(0, value.length() - 1));
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e1) {
                try {
                    return Double.valueOf(value);
                } catch (NumberFormatException e2) {
                    throw new NumberFormatException("Cannot convert string value '" + value + "' into a numerical value");
                }
            }
        }
    }

    public static Object createObjectFromString(String type, String value)
            throws Exception {
        Object result;
        if (primitiveToWrapper.containsKey(type)) {
            if (type.equals(Character.TYPE.getName())) {
                result = new Character(value.charAt(0));
            } else {
                result = newStringConstructor(((Class) primitiveToWrapper.get(type)).getName(), value);
            }
        } else {
            if (type.equals(Character.class.getName())) {
                result = new Character(value.charAt(0));
            } else {
                if (Number.class.isAssignableFrom(getClass(type))) {
                    result = createNumberFromStringValue(value);
                } else {
                    if ((value == null) || (value.toString().equals("null"))) {
                        result = null;
                    } else {
                        result = newStringConstructor(type, value);
                    }
                }
            }
        }
        return result;
    }

    public static Throwable getActualException(Throwable e) {
        if ((e instanceof ExecutionException)) {
            e = e.getCause();
        }
        if (((e instanceof MBeanException)) || ((e instanceof RuntimeMBeanException)) || ((e instanceof RuntimeOperationsException)) || ((e instanceof ReflectionException))) {
            Throwable t = e.getCause();
            if (t != null) {
                return t;
            }
        }
        return e;
    }

//    public static class ReadOnlyTableCellEditor  extends DefaultCellEditor {
//
//        public ReadOnlyTableCellEditor(JTextField tf) {
//            super();
//            tf.addFocusListener(new Utils.EditFocusAdapter(this));
//            tf.addKeyListener(new Utils.CopyKeyAdapter());
//        }
//    }

    public static class EditFocusAdapter
            extends FocusAdapter {

        private CellEditor editor;

        public EditFocusAdapter(CellEditor editor) {
            this.editor = editor;
        }

        public void focusLost(FocusEvent e) {
            this.editor.stopCellEditing();
        }
    }

    public static class CopyKeyAdapter
            extends KeyAdapter {

        private static final String defaultEditorKitCopyActionName = "copy-to-clipboard";
        private static final String transferHandlerCopyActionName = (String) TransferHandler.getCopyAction().getValue("Name");

        public void keyPressed(KeyEvent e) {
            KeyStroke ks = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());

            JComponent comp = (JComponent) e.getSource();
            for (int i = 0; i < 3; i++) {
                InputMap im = comp.getInputMap(i);
                Object key = im.get(ks);
                if (("copy-to-clipboard".equals(key)) || (transferHandlerCopyActionName.equals(key))) {
                    return;
                }
            }
            if (!Utils.tableNavigationKeys.contains(Integer.valueOf(e.getKeyCode()))) {
                e.consume();
            }
        }

        public void keyTyped(KeyEvent e) {
            e.consume();
        }
    }
}
