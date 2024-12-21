package com.application.tm_application_for_tsd.network.request_response


import com.google.gson.annotations.SerializedName

data class ChooseOp(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("value")
    val value: List<LDUValue>,

    @SerializedName("errorCode")
    val errorCode: Int
)


data class LDUValue(
    @SerializedName("Op_1_Bl_1_Sht")
    val op1Bl1Sht: String? = null,

    @SerializedName("Op_2_Bl_2_Sht")
    val op2Bl2Sht: String? = null,

    @SerializedName("Op_3_Bl_3_Sht")
    val op3Bl3Sht: String? = null,

    @SerializedName("Op_4_Bl_4_Sht")
    val op4Bl4Sht: String? = null,

    @SerializedName("Op_5_Bl_5_Sht")
    val op5Bl5Sht: String? = null,

    @SerializedName("Op_6_Blis_6_10_Sht")
    val op6Blis610Sht: String? = null,

    @SerializedName("Op_7_Pereschyot")
    val op7Pereschyot: String? = null,

    @SerializedName("Op_9_Fasovka_Sborka")
    val op9FasovkaSborka: String? = null,

    @SerializedName("Op_10_Markiroвka_SHT")
    val op10MarkirovkaSHT: String? = null,

    @SerializedName("Op_11_Markiroвka_Prom")
    val op11MarkirovkaProm: String? = null,

    @SerializedName("Op_12_Markiroвka_Prom")
    val op12MarkirovkaProm: String? = null,

    @SerializedName("Op_13_Markiroвka_Fabr")
    val op13MarkirovkaFabr: String? = null,

    @SerializedName("Op_14_TU_1_Sht")
    val op14TU1Sht: String? = null,

    @SerializedName("Op_15_TU_2_Sht")
    val op15TU2Sht: String? = null,

    @SerializedName("Op_16_TU_3_5")
    val op16TU35: String? = null,

    @SerializedName("Op_17_TU_6_8")
    val op17TU68: String? = null,

    @SerializedName("Op_468_Proverka_SHK")
    val op468ProverkaSHK: String? = null,

    @SerializedName("Op_469_Spetsifikatsiya_TM")
    val op469SpetsifikatsiyaTM: String? = null,

    @SerializedName("Op_470_Dop_Upakovka")
    val op470DopUpakovka: String? = null,
    @SerializedName("Sortiruemyi_Tovar")
    val sortiruemyiTovar: String? = null,

    @SerializedName("Ne_Sortiruemyi_Tovar")
    val neSortiruemyiTovar: String? = null,

    @SerializedName("Produkty")
    val produkty: String? = null,

    @SerializedName("Opasnyi_Tovar")
    val opasnyiTovar: String? = null,

    @SerializedName("Zakrytaya_Zona")
    val zakrytayaZona: String? = null,

    @SerializedName("Krupnogabaritnyi_Tovar")
    val krupnogabaritnyiTovar: String? = null,

    @SerializedName("Yuvelirnye_Izdelia")
    val yuvelirnyeIzdelia: String? = null,

    @SerializedName("Pechat_Etiketki_s_SHK")
    val pechatEtiketkiSHK: String? = null,

    @SerializedName("Pechat_Etiketki_s_Opisaniem")
    val pechatEtiketkiOpisaniem: String? = null
)
