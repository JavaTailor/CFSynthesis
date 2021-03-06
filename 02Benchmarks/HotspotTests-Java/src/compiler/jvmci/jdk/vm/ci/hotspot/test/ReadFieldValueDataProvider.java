/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package compiler.jvmci.jdk.vm.ci.hotspot.test;

import jdk.vm.ci.meta.JavaConstant;
import org.testng.annotations.DataProvider;

import java.util.LinkedList;

public class ReadFieldValueDataProvider {

    @DataProvider(name = "readFieldValueDataProvider")
    public static Object[][] readFieldValueDataProvider() {
        LinkedList<Object[]> cfgSet = new LinkedList<>();
        // Testing instance non-stable fields
        TestHelper.INSTANCE_FIELDS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing static non-stable fields with null as receiver
        TestHelper.STATIC_FIELDS_MAP.entrySet().stream().forEach((staticField) -> {
            cfgSet.add(new Object[]{staticField.getKey(), null, staticField.getValue()});
        });
        // Testing static non-stable fields with JavaConstant.NULL_POINTER as receiver
        TestHelper.STATIC_FIELDS_MAP.entrySet().stream().forEach((staticField) -> {
            cfgSet.add(new Object[]{staticField.getKey(),
                            JavaConstant.NULL_POINTER,
                            staticField.getValue()});
        });
        // Testing instance stable fields
        TestHelper.INSTANCE_STABLE_FIELDS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing static stable fields with null as receiver
        TestHelper.STATIC_STABLE_FIELDS_MAP.entrySet().stream().forEach((staticField) -> {
            cfgSet.add(new Object[]{staticField.getKey(), null, staticField.getValue()});
        });
        // Testing static stable fields with JavaConstant.NULL_POINTER as receiver
        TestHelper.STATIC_STABLE_FIELDS_MAP.entrySet().stream().forEach((staticField) -> {
            cfgSet.add(new Object[]{staticField.getKey(),
                            JavaConstant.NULL_POINTER,
                            staticField.getValue()});
        });
        // Testing instance non-stable array fields
        TestHelper.ARRAYS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing instance stable array fields
        TestHelper.STABLE_ARRAYS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing instance non-stable array-of-array fields
        TestHelper.ARRAY_ARRAYS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing instance stable array-of-array fields
        TestHelper.STABLE_ARRAY_ARRAYS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.DUMMY_CLASS_CONSTANT,
                            instanceField.getValue()});
        });
        // Testing instance fields with JavaConstant.NULL_POINTER as receiver
        TestHelper.INSTANCE_FIELDS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(), JavaConstant.NULL_POINTER, null});
        });
        // Testing instance fields with an object that does not have the field
        TestHelper.INSTANCE_FIELDS_MAP.entrySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField.getKey(),
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectField),
                            null});
        });
        return cfgSet.toArray(new Object[0][0]);
    }

    @DataProvider(name = "readFieldValueNegativeDataProvider")
    public static Object[][] readFieldValueNegativeDataProvider() {
        LinkedList<Object[]> cfgSet = new LinkedList<>();
        // Testing instance fields with null as receiver
        TestHelper.INSTANCE_FIELDS_MAP.keySet().stream().forEach((instanceField) -> {
            cfgSet.add(new Object[]{instanceField, null});
        });
        // Testing null as a field argument
        cfgSet.add(new Object[]{null, null});
        return cfgSet.toArray(new Object[0][0]);
    }
}
