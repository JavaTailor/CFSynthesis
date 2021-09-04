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

import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.JavaConstant;
import org.testng.annotations.DataProvider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class ConstantEqualsDataProvider {
    @DataProvider(name = "constantEqualsDataProvider")
    public static Object[][] constantEqualsDataProvider() {
        HashMap<Object, Constant> constMap = new HashMap<>();
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.booleanField, JavaConstant.forBoolean(TestHelper.DUMMY_CLASS_INSTANCE.booleanField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultBooleanField,
                        JavaConstant.forBoolean(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultBooleanField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.byteField, JavaConstant.forByte(TestHelper.DUMMY_CLASS_INSTANCE.byteField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalByteField, JavaConstant.forByte(TestHelper.DUMMY_CLASS_INSTANCE.finalByteField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultByteField,
                        JavaConstant.forByte(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultByteField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.shortField, JavaConstant.forShort(TestHelper.DUMMY_CLASS_INSTANCE.shortField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalShortField, JavaConstant.forShort(TestHelper.DUMMY_CLASS_INSTANCE.finalShortField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultShortField,
                        JavaConstant.forShort(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultShortField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.intField, JavaConstant.forInt(TestHelper.DUMMY_CLASS_INSTANCE.intField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalIntField, JavaConstant.forInt(TestHelper.DUMMY_CLASS_INSTANCE.finalIntField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultIntField,
                        JavaConstant.forInt(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultIntField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.longField, JavaConstant.forLong(TestHelper.DUMMY_CLASS_INSTANCE.longField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalLongField, JavaConstant.forLong(TestHelper.DUMMY_CLASS_INSTANCE.finalLongField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultLongField,
                        JavaConstant.forLong(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultLongField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.doubleField, JavaConstant.forDouble(TestHelper.DUMMY_CLASS_INSTANCE.doubleField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalDoubleField,
                        JavaConstant.forDouble(TestHelper.DUMMY_CLASS_INSTANCE.finalDoubleField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultDoubleField,
                        JavaConstant.forDouble(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultDoubleField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.floatField, JavaConstant.forFloat(TestHelper.DUMMY_CLASS_INSTANCE.floatField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalFloatField, JavaConstant.forFloat(TestHelper.DUMMY_CLASS_INSTANCE.finalFloatField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultFloatField,
                        JavaConstant.forFloat(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultFloatField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.charField, JavaConstant.forChar(TestHelper.DUMMY_CLASS_INSTANCE.charField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalCharField, JavaConstant.forChar(TestHelper.DUMMY_CLASS_INSTANCE.finalCharField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultCharField,
                        JavaConstant.forChar(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultCharField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stringField,
                        TestHelper.CONSTANT_REFLECTION_PROVIDER.forString(TestHelper.DUMMY_CLASS_INSTANCE.stringField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stringField2,
                        TestHelper.CONSTANT_REFLECTION_PROVIDER.forString(TestHelper.DUMMY_CLASS_INSTANCE.stringField2));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.objectField,
                        TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.objectField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.finalObjectField,
                        TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.finalObjectField));
        constMap.put(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultObjectField,
                        TestHelper.CONSTANT_REFLECTION_PROVIDER.forObject(TestHelper.DUMMY_CLASS_INSTANCE.stableDefaultObjectField));
        constMap.put(null, null);
        constMap.put(JavaConstant.NULL_POINTER, JavaConstant.NULL_POINTER);
        LinkedList<Object[]> cfgSet = new LinkedList<>();
        constMap.entrySet().stream().forEach((obj1) -> {
            constMap.entrySet().stream().forEach((obj2) -> {
                cfgSet.add(new Object[]{obj1.getValue(), obj2.getValue(),
                                Objects.equals(obj1.getKey(), obj2.getKey())});
            });
        });
        return cfgSet.toArray(new Object[0][0]);
    }
}
