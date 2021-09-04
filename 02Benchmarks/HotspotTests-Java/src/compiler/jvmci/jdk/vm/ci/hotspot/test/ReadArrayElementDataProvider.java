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
import java.util.stream.Stream;

public class ReadArrayElementDataProvider {

    @DataProvider(name = "readArrayElementDataProvider")
    public static Object[][] readArrayElementDataProvider() {
        LinkedList<Object[]> cfgSet = new LinkedList<>();
        for (int i : new int[]{0, 1}) {
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayWithValues),
                            i, JavaConstant.forBoolean(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.byteArrayWithValues),
                            i, JavaConstant.forByte(TestHelper.DUMMY_CLASS_INSTANCE.byteArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.shortArrayWithValues),
                            i, JavaConstant.forShort(TestHelper.DUMMY_CLASS_INSTANCE.shortArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.charArrayWithValues),
                            i, JavaConstant.forChar(TestHelper.DUMMY_CLASS_INSTANCE.charArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.intArrayWithValues),
                            i, JavaConstant.forInt(TestHelper.DUMMY_CLASS_INSTANCE.intArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.longArrayWithValues),
                            i, JavaConstant.forLong(TestHelper.DUMMY_CLASS_INSTANCE.longArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.floatArrayWithValues),
                            i, JavaConstant.forFloat(TestHelper.DUMMY_CLASS_INSTANCE.floatArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.doubleArrayWithValues),
                            i, JavaConstant.forDouble(TestHelper.DUMMY_CLASS_INSTANCE.doubleArrayWithValues[i])});
            cfgSet.add(new Object[]{TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectArrayWithValues),
                            i, TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.byteArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.byteArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.shortArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.shortArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.charArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.charArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.intArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.intArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.longArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.longArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.floatArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.floatArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.doubleArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.doubleArrayArrayWithValues[i])});
            cfgSet.add(new Object[]{
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectArrayArrayWithValues), i,
                            TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectArrayArrayWithValues[i])});
        }
        Stream.concat(TestHelper.ARRAYS_MAP.values().stream(), TestHelper.ARRAY_ARRAYS_MAP.values().stream()).forEach((array) -> {
            for (int i : new int[]{-1, 2}) {
                cfgSet.add(new Object[]{array, i, null});
            }
        });
        cfgSet.add(new Object[]{null, 0, null});
        cfgSet.add(new Object[]{JavaConstant.NULL_POINTER, 0, null});
        TestHelper.INSTANCE_FIELDS_MAP.values().forEach((constant) -> {
            cfgSet.add(new Object[]{constant, 0, null});
        });
        return cfgSet.toArray(new Object[0][0]);
    }
}
