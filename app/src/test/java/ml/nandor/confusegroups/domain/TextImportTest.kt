package ml.nandor.confusegroups.domain

import ml.nandor.confusegroups.domain.model.AtomicNote
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TextImportTest {

    @Nested
    inner class TextToQAPair {
        @Test
        @Disabled("This test should fail")
        fun failingTest() {
            assertNotEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("dogs-DOGS"))
        }


        @Nested
        @DisplayName("Simple")
        inner class Simple {
            @Test
            @DisplayName("One dash")
            fun textToNote() {
                assertEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("dogs-DOGS"))

            }

            @Test
            @DisplayName("No false equalities")
            fun noFalseEquals() {
                assertNotEquals(Pair("dogs", "CATS"), TextImport.textToQAPair("dogs-DOGS"))

            }

            @Test
            @DisplayName("One Side Can Be Empty")
            fun emptyOneSide() {
                assertEquals(Pair("dogs", ""), TextImport.textToQAPair("dogs-"))

            }

            @Test
            @DisplayName("Both sides empty")
            fun bothSidesEmpty() {
                assertEquals(Pair("", ""), TextImport.textToQAPair("-"))

            }

            @Test
            @DisplayName("No dashes")
            fun noDashes() {
                assertNull(TextImport.textToQAPair("Hello!"))

            }

            @Test
            @DisplayName("Empty String")
            fun emptyString() {
                assertNull(TextImport.textToQAPair(""))

            }
        }


        @Nested
        @DisplayName("Multi Dashes")
        inner class DoubleDashes {
            @Test
            @DisplayName("Double Dashes should work")
            fun doubleDashes() {
                assertEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("dogs--DOGS"))
            }

            @Test
            @DisplayName("Two Double Dashes should not work")
            fun twoDoubleDashes() {
                assertNull(TextImport.textToQAPair("dogs--DOGS--cats"))
            }

            @Test
            @DisplayName("Double Dashes can contain single dashes")
            fun dashesAndDoubleDashes() {
                assertEquals(
                    Pair("cat-dog", "CAT-DOG"),
                    TextImport.textToQAPair("cat-dog--CAT-DOG")
                )
            }

            @Test
            @DisplayName("Triple Dashes put the dash on one side")
            fun tripleDashes() {
                val res = TextImport.textToQAPair("dogs---DOGS")

                val one = Pair("dogs", "-DOGS")
                val two = Pair("dogs-", "DOGS")

                assert(res == one || res == two)
            }

            @Test
            @DisplayName("Four Dashes break")
            fun fourDashes() {
                assertNull(TextImport.textToQAPair("dogs----DOGS"))
            }
        }

        @Nested
        @DisplayName("Trimming")
        inner class Trimming {
            @Test
            @DisplayName("Spaces around dash")
            fun spacedDash() {
                assertEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("dogs - DOGS"))
            }

            @Test
            @DisplayName("Newline handled")
            fun newlineHandled() {
                assertEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("\ndogs - DOGS\n"))
            }

            @Test
            @DisplayName("Itnernal Newline Allowed")
            fun internalNewlineAllowed() {
                assertEquals(
                    Pair("dogs", "DOGS\nAND CATS"),
                    TextImport.textToQAPair("dogs - DOGS\nAND CATS")
                )
            }

            @Test
            @DisplayName("Different Whitespace Trimmed")
            fun differentWhitespaceTrimmed() {
                assertEquals(Pair("dogs", "DOGS"), TextImport.textToQAPair("dogs - DOGS\n   "))
            }
        }

        @Test
        @DisplayName("Ignore Group Headers")
        fun ignoreGroupHeaders() {
            assertNull(TextImport.textToQAPair("#dogs-Cats"))
        }
    }

    @Nested
    inner class NoteToExportableText{
        @Test
        @DisplayName("Works")
        fun works(){
            val atomicNote = AtomicNote("", "cats", "", "dogs", "")

            val expected = "dogs--cats"

            assertEquals(expected, TextImport.noteToExportableText(atomicNote))

        }
    }
}