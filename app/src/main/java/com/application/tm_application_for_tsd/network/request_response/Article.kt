package com.application.tm_application_for_tsd.network.request_response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Article(
    @SerializedName("success") val success: Boolean,
    @SerializedName("value") val articuls: List<Articuls>,
    @SerializedName("errorCode") val errorCode: Int
) {
    @Parcelize
    data class Articuls(
        @SerializedName("ID") val id: Long?,
        @SerializedName("Pref") val pref: String?,
        @SerializedName("Nazvanie_Zadaniya") val nazvanieZadaniya: String?,
        @SerializedName("Status_Zadaniya") val statusZadaniya: Int?,
        @SerializedName("Ispolnitel") val ispolnitel: String?,
        @SerializedName("Status") val status: Int?,
        @SerializedName("Artikul") val artikul: Int?,
        @SerializedName("Artikul_Syrya") val artikulSyrya: String?,
        @SerializedName("Nomenklatura") val nomenklatura: Long?,
        @SerializedName("Nazvanie_Tovara") val nazvanieTovara: String?,
        @SerializedName("SHK") val shk: String?,
        @SerializedName("SHK_Syrya") val shkSyrya: String?,
        @SerializedName("SHK_SPO") val shkSpo: String?,
        @SerializedName("SHK_SPO_1") val shkSpo1: String?,
        @SerializedName("Kol_vo_Syrya") val kolVoSyrya: String?,
        @SerializedName("Itog_Zakaz") val itogZakaz: Int?,
        @SerializedName("Sht_v_MP") val shtvMP: Int?,
        @SerializedName("Itog_MP") val itogMP: Int?,
        @SerializedName("SOH") val soh: String?,
        @SerializedName("Tip_Postavki") val tipPostavki: String?,
        @SerializedName("Srok_Godnosti") val srokGodnosti: String?,
        @SerializedName("Op_1_Bl_1_Sht") val op1Bl1Sht: String?,
        @SerializedName("Op_2_Bl_2_Sht") val op2Bl2Sht: String?,
        @SerializedName("Op_3_Bl_3_Sht") val op3Bl3Sht: String?,
        @SerializedName("Op_4_Bl_4_Sht") val op4Bl4Sht: String?,
        @SerializedName("Op_5_Bl_5_Sht") val op5Bl5Sht: String?,
        @SerializedName("Op_6_Blis_6_10_Sht") val op6Blis610Sht: String?,
        @SerializedName("Op_7_Pereschyot") val op7Pereschyot: String?,
        @SerializedName("Op_9_Fasovka_Sborka") val op9FasovkaSborka: String?,
        @SerializedName("Op_10_Markirovka_SHT") val op10MarkirovkaSht: String?,
        @SerializedName("Op_11_Markirovka_Prom") val op11MarkirovkaProm: String?,
        @SerializedName("Op_12_Markirovka_Prom") val op12MarkirovkaProm: String?,
        @SerializedName("Op_13_Markirovka_Fabr") val op13MarkirovkaFabr: String?,
        @SerializedName("Op_14_TU_1_Sht") val op14Tu1Sht: String?,
        @SerializedName("Op_15_TU_2_Sht") val op15Tu2Sht: String?,
        @SerializedName("Op_16_TU_3_5") val op16Tu35: String?,
        @SerializedName("Op_17_TU_6_8") val op17Tu68: String?,
        @SerializedName("Op_468_Proverka_SHK") val op468ProverkaShk: String?,
        @SerializedName("Op_469_Spetsifikatsiya_TM") val op469SpetsifikatsiyaTm: String?,
        @SerializedName("Op_470_Dop_Upakovka") val op470DopUpakovka: String?,
        @SerializedName("Mesto") val mesto: Int?,
        @SerializedName("Vlozhennost") val vlozhennost: Int?,
        @SerializedName("Pallet_No") val palletNo: Int?,
        @SerializedName("Time_Start") val timeStart: String?,
        @SerializedName("Time_Middle") val timeMiddle: String?,
        @SerializedName("Time_End") val timeEnd: String?,
        @SerializedName("Persent") val persent: String?,
        @SerializedName("SHK_WPS") val shkWps: String?,
        @SerializedName("vp")  val vp: String?,
        @SerializedName("fcat_vp")  val factVp: String?,
        @SerializedName("Plan_Otkaz") val planOtkaz: String?
    ): Parcelable
}
