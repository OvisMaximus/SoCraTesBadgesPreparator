package de.softcouture.socrates.badgepreparation

val SourceFields = """
        nickname
        badgename
        email
        billingAddress_lastname
        billingAddress_firstname
        billingAddress_company
        billingAddress_vat
        billingAddress_address1
        billingAddress_address2
        billingAddress_postal
        billingAddress_city
        billingAddress_country
        personalAddress_lastname 
        personalAddress_firstname 
        personalAddress_address1
        personalAddress_address2
        personalAddress_postal
        personalAddress_city
        personalAddress_country
        pronouns
        social
        diversitySelected
        reason 
        room_roomType
        arrivalDate
        departureDate
        lineItems
        room_roommate
        room_diversity
        room_family
        packages
        dietary
        swagSize
        swagCut
        childcare_children
        childcare_details
        """

private const val NICKNAME = "nickname"
private const val EMAIL = "email"
private const val ARRIVAL_DATE = "arrivalDate"
private const val BADGENAME = "badgename"
private const val SOCIAL = "social"
private const val PRONOUNS = "pronouns"
private const val PERSONAL_FIRST_NAME = "personalAddress_firstname"
private const val PERSONAL_LAST_NAME = "personalAddress_lastname"
private const val BILLING_FIRST_NAME = "billingAddress_firstname"
private const val BILLING_LAST_NAME = "billingAddress_lastname"
private const val SWAG_SIZE = "swagSize"
private const val SWAG_CUT = "swagCut"

class AttendeeRecord(attendeeData: Map<String, String?>) {

    val nickname: String = attendeeData.get(NICKNAME)!!
    val email: String = attendeeData.get(EMAIL)!!
    val arrivalDate: String = attendeeData.get(ARRIVAL_DATE)!!
    val personalFirstName: String = attendeeData.get(PERSONAL_FIRST_NAME) ?: ""
    val personalLastName: String = attendeeData.get(PERSONAL_LAST_NAME) ?: ""
    val billingFirstName: String = attendeeData.get(BILLING_FIRST_NAME) ?: ""
    val billingLastName: String = attendeeData.get(BILLING_LAST_NAME) ?: ""
    val social: String = attendeeData.get(SOCIAL) ?: ""
    val badgename: String  = attendeeData.get(BADGENAME) ?: ""
    val pronouns: String = attendeeData.get(PRONOUNS) ?: ""
    val shirtSize: String = attendeeData.get(SWAG_SIZE) ?: ""
    val shirtCut: String = attendeeData.get(SWAG_CUT) ?: ""
    val name: String
    val firstName: String
    val lastName: String

    init {
        firstName = asStringOrNull(personalFirstName)
            ?: asStringOrNull(billingFirstName)
            ?: asStringOrNull(extractFirstNameFromEMailAddress(email))
            ?: ""
        lastName = asStringOrNull(personalLastName)
            ?: asStringOrNull(billingLastName)
            ?: asStringOrNull(extractLastNameFromEMailAddress(email))
            ?: ""
        name = asStringOrNull(badgename)
            ?: asStringOrNull(nickname)
            ?: firstName
    }

    private fun extractFirstNameFromEMailAddress(email: String): String? {
        val (name, nameSeparatorIdx) = getNameFromEmail(email)
        return if (nameSeparatorIdx > 0) name.substring(0, nameSeparatorIdx) else null
    }

    private fun extractLastNameFromEMailAddress(email: String): String? {
        val (name, nameSeparatorIdx) = getNameFromEmail(email)
        return if (nameSeparatorIdx > 0) name.substring(nameSeparatorIdx + 1) else null
    }

    private fun getNameFromEmail(email: String): Pair<String, Int> {
        val name = email.trim().takeWhile { it != '@' }
        val nameSeparatorIdx = name.lastIndexOf('.')
        return Pair(name, nameSeparatorIdx)
    }

    private fun asStringOrNull(text: String?): String? {
        return if (text?.trim()?.isEmpty() != false)
            null
        else
            text
    }
    fun removeDelimeter(text:String) : String {
        if(text.endsWith(","))
            return text.substring(0, text.length - 1)
        return text
    }

    fun getSocialList(): List<Pair<String, String>>{
        val result = ArrayList<Pair<String, String>>()
        val segments = social.split(" ")
        segments.forEachIndexed { index, element ->
            var cleanElement = removeDelimeter(element.trim())
            cleanElement = truncateLinkedInUrlIfContainsTrailingArguments(cleanElement)
            if(cleanElement.length > 3 && isNotAProviderName(cleanElement)) {
                result.add(Pair(cleanElement, email + '_' + index + ".png"))
            }
        }

        return result
    }

    private fun truncateLinkedInUrlIfContainsTrailingArguments(urlString: String): String {
        if( urlString.startsWith("https://www.linkedin.com/in/")
            || urlString.startsWith("http://www.linkedin.com/in/"))
        {
            val indexOfLastSlash = urlString.indexOfLast {'/' == it}
            val numberOfSlashes = urlString.count { '/' == it }
            if(numberOfSlashes > 4)
                return urlString.substring(0, indexOfLastSlash + 1)
        }
        return urlString
    }

    private fun isNotAProviderName(socialContact: String): Boolean {
        if (socialContact.startsWith("Twitter", true))
            return false
        if (socialContact.startsWith("Mastodon", true))
            return false
        if (socialContact.startsWith("Linkedin", true))
            return false
        if (socialContact.startsWith("Facebook", true))
            return false
        return !socialContact.startsWith("Instagram", true)
    }

    override fun toString(): String {
        val socialLinks = StringBuilder()
        val socialList = getSocialList()
        socialList.forEach() {
            socialLinks.append(it.first)
            socialLinks.append("=>")
            socialLinks.append(it.second)
            socialLinks.append(";")
        }
        return "AttendeeRecord(nickname='$nickname', badgename=$badgename, pronouns=$pronouns, email='$email', social=$social, asLinks=$socialLinks)"
    }

    fun getCsvLineData(): List<String> {
        val result = mutableListOf<String>(name, pronouns, email, arrivalDate, firstName, lastName,
            shirtSize, shirtCut)
        val socialList = getSocialList()
        var lastSocial = -1
        socialList.forEach() { item: Pair<String, String> ->
            result += item.first
            result += item.second
            lastSocial += 1
        }
        for( i in lastSocial + 1 .. 3){
            result.add("")
            result.add("")
        }
        return result
    }

    companion object {
        fun getCsvColumnHeaders(): List<Any?> {
            return listOf("name","pronouns", "email", "arrivalDate", "firstName", "lastName", "shirtSize", "shirtCut",
                "social_0", "qrFile_0", "social_1", "qrFile_1", "social_2", "qrFile_2", "social_3", "qrFile_3")
        }
    }
}
