<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- $Id: image2fo.xsl 611278 2008-01-11 19:50:53Z jeremias $ -->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
  version="1.0">

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template name="masters">
    <fo:layout-master-set>
      <!-- not really used but mandatory -->
      <fo:simple-page-master master-name="dummy" page-height="29.7cm" page-width="21cm">
        <fo:region-body/>
      </fo:simple-page-master>
    </fo:layout-master-set>
  </xsl:template>
  
  <xsl:template match="/image">
    <fo:root>
      <xsl:call-template name="masters"/>
      <fox:external-document>
        <xsl:attribute name="src"><xsl:value-of select="."/></xsl:attribute>
      </fox:external-document>
    </fo:root>
  </xsl:template>
  
</xsl:stylesheet>
