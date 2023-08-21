package de.softcouture.socrates.badgepreparation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MainKtTest {
    @Test
    fun initializeFromCSVRecord() {
        val attendeeData = generateTestCsvRecord("https://www.linkedin.com/in/fred-feuerstein")

        val record = AttendeeRecord(attendeeData)
        Assertions.assertEquals("FF@stoneage.org", record.email)
        Assertions.assertEquals("Fred", record.nickname)
        Assertions.assertEquals("Fred Feuerstein", record.badgename)
        Assertions.assertEquals("https://www.linkedin.com/in/fred-feuerstein", record.social)
        Assertions.assertEquals("2023-08-23", record.arrivalDate)
    }

    private fun generateTestCsvRecord(social: String?): HashMap<String, String?> {
        var attendeeData = HashMap<String, String?>()
        attendeeData.put("nickname", "Fred")
        attendeeData.put("badgename", "Fred Feuerstein")
        attendeeData.put("email", "FF@stoneage.org")
        attendeeData.put("pronouns", "he/him")
        attendeeData.put("social", social)
        attendeeData.put("arrivalDate", "2023-08-23")
        return attendeeData
    }

    @Test
    fun printNameProvidedAsBadgeNickPersonalFirstOrBillingFirst() {
        var attendeeRecord = generateTestRecord("badge", "nick", "personal", "billing")
        assertEquals("badge", attendeeRecord.name)
        attendeeRecord = generateTestRecord("", "nick", "personal", "billing")
        assertEquals("nick", attendeeRecord.name)
        attendeeRecord = generateTestRecord("", "", "personal", "billing")
        assertEquals("personal", attendeeRecord.name)
        attendeeRecord = generateTestRecord("", "", "", "billing")
        assertEquals("billing", attendeeRecord.name)
    }

    private fun generateTestRecord(badge: String, nick: String, personalFirst: String,
                                   billingFirst: String): AttendeeRecord {

        var attendeeData = HashMap<String, String?>()
        attendeeData.put("nickname", nick)
        attendeeData.put("badgename", badge)
        attendeeData.put("billingAddress_firstname", billingFirst)
        attendeeData.put("personalAddress_firstname", personalFirst)
        attendeeData.put("email", "FF@stoneage.org")
        attendeeData.put("pronouns", "he/him")
        attendeeData.put("social", "")
        attendeeData.put("arrivalDate", "2023-08-23")
        return AttendeeRecord(attendeeData)
    }

    @Test
    fun splitMultiple() {
        val attendeeData = generateTestCsvRecord("https://eineUrl @UndEinNick")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals(2, socialList.size)
        assertEquals("@UndEinNick", socialList[1].first)
    }

    @Test
    fun removeDelimeterFromSocialListElement() {
        val attendeeData = generateTestCsvRecord("https://eineUrl/, @UndEinNick")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals("https://eineUrl/", socialList[0].first)
    }

    @Test
    fun qrCodeNamesDependingOnEmail() {
        val attendeeData = generateTestCsvRecord("https://eineUrl/, @UndEinNick")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals("FF@stoneage.org_0.png", socialList[0].second)
        assertEquals("FF@stoneage.org_1.png", socialList[1].second)
    }

    @Test
    fun noQrCodeUrlWhenSocialIsEmpty(){
        val attendeeData = generateTestCsvRecord("")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals(0, socialList.size)
    }

    @Test
    fun dropProviderNameFromSocialLinks(){
        val attendeeData = generateTestCsvRecord("Twitter: @nasebohren")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals(1, socialList.size)
        assertEquals("@nasebohren", socialList[0].first)
    }
    @Test
    fun dropTooShortToBeRealSocialLinks(){
        val attendeeData = generateTestCsvRecord("@no @slartibartfass")
        val testRecord = AttendeeRecord(attendeeData)
        val socialList = testRecord.getSocialList()
        assertEquals(1, socialList.size)
        assertEquals("@slartibartfass", socialList[0].first)
    }
    @Test
    fun qrCodeGeneratorUrlBySocialUrl() {
        val url = "https://softcouture.de/"
        val expectedUrl = QR_CODE_GEN_URL + "https%3A%2F%2Fsoftcouture.de%2F"
        assertEquals(expectedUrl, createQrCodeUrl(url))
    }

    @Test
    fun csvColumnHeaders() {
        val expectedData = listOf("name","pronouns", "email", "arrivalDate", "social_0",
            "qrFile_0", "social_1", "qrFile_1", "social_2", "qrFile_2", "social_3", "qrFile_3");
        assertEquals(expectedData, AttendeeRecord.getCsvColumnHeaders())
    }
}
