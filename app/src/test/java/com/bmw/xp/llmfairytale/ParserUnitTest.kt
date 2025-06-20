package com.bmw.xp.llmfairytale

import org.junit.Test

import org.junit.Assert.*

const val llmInput = "Es war einmal ein kleiner Schneeflocke namens Luna, die in einem dichten Schneesturm verloren ging & 40:40:80 &\n" +
        "Sie irrte durch den dunklen Wald, bis sie plötzlich ein warmes Licht sah & 220:180:100 &\n" +
        "Das Licht kam von einem kleinen Häuschen, aus dem die duftenden Weihnachtskekse und die Wärme eines Kaminfeuers herausströmten & 240:200:120 &\n" +
        "Luna trat ein und fand sich in einer gemütlichen Stube wieder, in der ein Weihnachtsbaum mit leuchtenden Kerzen stand & 150:250:150 &\n" +
        "Plötzlich hörte sie die Stimme von Santa Claus, der ihr anbot, ihre Wünsche zu erfüllen, wenn sie brav war & 255:0:50 &\n" +
        "Luna war so aufgeregt, dass sie fast ihre Wünsche vergessen hätte & 100:200:255 &\n" +
        "Santa half ihr, ihre Wünsche zu formulieren, und Luna bekam alles, was sie sich gewünscht hatte & 0:255:0 &\n" +
        "Als Dank schenkte Luna Santa ein besonderes Geschenk: eine Schneeflocke, die leuchtete wie ein kleiner Stern & 255:255:200 &\n" +
        "Luna war so glücklich, dass sie beschloss, ihre neue Freundschaft mit Santa für immer zu bewahren & 150:150:255 &\n" +
        "Von diesem Tag an war Luna jedes Jahr bei Santa Claus und half ihm bei der Verteilung der Geschenke & 200:100:150"

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ParserUnitTest {
    @Test
    fun testParseLLM() {
        val result = FairyTaleParser.parse(llmInput)
        assertTrue(result)
        val firstPhrase = FairyTaleParser.getPhrase(0).trim()
        assertEquals("Es war einmal ein kleiner Schneeflocke namens Luna, die in einem dichten Schneesturm verloren ging", firstPhrase)

        val firstRGB = FairyTaleParser.getRGB(0)
        assertEquals(40.toByte(), firstRGB.r)
        assertEquals(40.toByte(), firstRGB.g)
        assertEquals(80.toByte(), firstRGB.b)
    }
}