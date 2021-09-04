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

public class AsJavaTypeDataProvider {

    @DataProvider(name = "asJavaTypeDataProvider")
    public static Object[][] asJavaTypeDataProvider() {
        return new Object[][]{
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(DummyClass.class),
                                        "jdk.vm.ci.hotspot.test.DummyClass"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(boolean.class), "boolean"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(byte.class), "byte"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(short.class), "short"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(char.class), "char"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(int.class), "int"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(long.class), "long"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(float.class), "float"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(double.class), "double"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(Object.class), "java.lang.Object"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(boolean[].class), "boolean[]"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(boolean[][].class), "boolean[][]"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(Object[].class), "java.lang.Object[]"},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(Object[][].class), "java.lang.Object[][]"},
                        {JavaConstant.forBoolean(TestHelper.DUMMY_CLASS_INSTANCE.booleanField), null},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectField), null},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE), null},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayWithValues), null},
                        {TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.booleanArrayArrayWithValues), null},
                        {JavaConstant.NULL_POINTER, null}, {null, null}};
    }
}
