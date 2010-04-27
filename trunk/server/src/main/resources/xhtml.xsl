<?xml version="1.0" encoding="UTF-8"?>
<?xchain-transformer-factory name="{http://www.xchain.org/core}saxon"?>

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xchain="http://www.xchain.org/core/1.0"
  xmlns:servlet="http://www.xchain.org/servlet/1.0"
  xmlns:sax="http://www.xchain.org/sax/1.0"
  xmlns:jsl="http://www.xchain.org/jsl/1.0"
  version="2.0">

  <xsl:template match="@*|node()" mode="copy">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="copy"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/">
    <xchain:catalog>
      <xchain:chain xchain:name="servlet:get">
        <sax:pipeline>
          <sax:command-source>
            <jsl:template exclude-result-prefixes="xchain servlet sax jsl">
              <xsl:apply-templates select="*" mode="copy"/>
            </jsl:template>
          </sax:command-source>
          <sax:serializer method="'xhtml'"/>
          <servlet:result media-type="'text/html'"/>
        </sax:pipeline>
      </xchain:chain>
    </xchain:catalog>
  </xsl:template>

</xsl:stylesheet>

