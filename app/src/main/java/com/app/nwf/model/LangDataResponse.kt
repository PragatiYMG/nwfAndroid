package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LangDataResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("data")
    @Expose
    var data: LangData? = null

}

class LangData : Serializable{
    @SerializedName("login")
    @Expose
    var login: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("user")
    @Expose
    var user: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("email_valid")
    @Expose
    var emailValid: String? = null

    @SerializedName("dashboard")
    @Expose
    var dashboard: String? = null

    @SerializedName("important_phone")
    @Expose
    var importantPhone: String? = null

    @SerializedName("important_contact")
    @Expose
    var importantContact: String? = null

    @SerializedName("consultation")
    @Expose
    var consultation: String? = null

    @SerializedName("children_handbook")
    @Expose
    var childrenHandbook: String? = null

    @SerializedName("interesting_stuff")
    @Expose
    var interestingStuff: String? = null

    @SerializedName("Watch_Learn")
    @Expose
    var watchLearn: String? = null

    @SerializedName("cypc")
    @Expose
    var cypc: String? = null

    @SerializedName("publication")
    @Expose
    var publication: String? = null

    @SerializedName("contact_us")
    @Expose
    var contactUs: String? = null

    @SerializedName("cypc_full")
    @Expose
    var cypcFull: String? = null

    @SerializedName("type_here")
    @Expose
    var typeHere: String? = null

    @SerializedName("videos")
    @Expose
    var videos: String? = null

    @SerializedName("handbook")
    @Expose
    var handbook: String? = null

    @SerializedName("publications")
    @Expose
    var publications: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("your_email")
    @Expose
    var yourEmail: String? = null

    @SerializedName("subject")
    @Expose
    var subject: String? = null

    @SerializedName("enter_message")
    @Expose
    var enterMessage: String? = null

    @SerializedName("enter_captcha")
    @Expose
    var enterCaptcha: String? = null

    @SerializedName("invalid_captcha")
    @Expose
    var invalidCaptcha: String? = null

    @SerializedName("submit_details")
    @Expose
    var submitDetails: String? = null

    @SerializedName("contact_message")
    @Expose
    var contactMessage: String? = null

    @SerializedName("contact_submit")
    @Expose
    var contactSubmit: String? = null

    @SerializedName("mail")
    @Expose
    var mail: String? = null

    @SerializedName("yes")
    @Expose
    var yes: String? = null

    @SerializedName("no")
    @Expose
    var no: String? = null

    @SerializedName("got_it")
    @Expose
    var gotIt: String? = null

    @SerializedName("thank_you")
    @Expose
    var thankYou: String? = null

    @SerializedName("submit")
    @Expose
    var submit: String? = null

    @SerializedName("consultation_msg")
    @Expose
    var consultationMsg: String? = null

    @SerializedName("all_the_question")
    @Expose
    var allTheQuestion: String? = null

    @SerializedName("know_more")
    @Expose
    var knowMore: String? = null

    @SerializedName("continue")
    @Expose
    var _continue: String? = null

    @SerializedName("enter_your_name")
    @Expose
    var enterYourName: String? = null

    @SerializedName("enter_your_email")
    @Expose
    var enterYourEmail: String? = null

    @SerializedName("enter_subject")
    @Expose
    var enterSubject: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("skip_now")
    @Expose
    var skipNow: String? = null

    @SerializedName("choose_app_language")
    @Expose
    var chooseAppLanguage: String? = null

    @SerializedName("ok")
    @Expose
    var ok: String? = null

    @SerializedName("Google_Translator")
    @Expose
    var googleTranslator: String? = null

}