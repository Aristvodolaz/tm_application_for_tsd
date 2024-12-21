package com.application.tm_application_for_tsd.network

import com.application.tm_application_for_tsd.network.request_response.AddBox
import com.application.tm_application_for_tsd.network.request_response.Article
import com.application.tm_application_for_tsd.network.request_response.ChooseOp
import com.application.tm_application_for_tsd.network.request_response.Duplicate
import com.application.tm_application_for_tsd.network.request_response.EmployeeResponse
import com.application.tm_application_for_tsd.network.request_response.FinishOzon
import com.application.tm_application_for_tsd.network.request_response.Pallet
import com.application.tm_application_for_tsd.network.request_response.PalletList
import com.application.tm_application_for_tsd.network.request_response.ShkInDb
import com.application.tm_application_for_tsd.network.request_response.Sklad
import com.application.tm_application_for_tsd.network.request_response.SrokGodnosti
import com.application.tm_application_for_tsd.network.request_response.Task
import com.application.tm_application_for_tsd.network.request_response.Universal
import com.application.tm_application_for_tsd.network.request_response.WBBox
import com.application.tm_application_for_tsd.network.request_response.WBData
import com.application.tm_application_for_tsd.network.request_response.WBPrivyazka
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/api/employee/{id}")
    suspend fun getEmployeeDetails(@Path("id") id: String): EmployeeResponse

    @GET("/market/tasks/names")
    suspend fun getTasks(@Query("sk") sk: String): Task

    @GET("/privyazka/sklads")
    suspend fun getSklads(): Sklad

    @GET("/market/tasks/getDataWithStatus")
    suspend fun getTasksInWork(@Query("taskNumber") taskName: String, @Query("status") status: Int): Article

    @GET("/pallets")
    suspend fun getPallets(@Query("taskName") taskName: String): PalletList

    @GET("pallets/articles")
    suspend fun getArticleOnPallet(@Query("palletNo") pallet: String, @Query("task") taskName: String): Pallet

    @GET("/market/tasks/searchShk")
    suspend fun getShk(@Query("taskName") nameTask: String, @Query("shk") shk: String): Article

    @GET("/market/new/getLdu")
    suspend fun getLDU(@Query("id") id: Long): ChooseOp

    @DELETE("/market/tasks/deleteRecord")
    suspend fun deleteRecord(@Query("id") id: Long , @Query("task") name: String): ChooseOp

    @POST("market/new/updateLdu")
    suspend fun updateCheckBox(@Query("id") id: Long, @Body data: MutableMap<String, String>): Universal

    @PUT("/market/new/getWork")
    suspend fun changeStatus(@Query("id") id: Long, @Query("status") status: Int,
                             @Query("startTime") startTime: String, @Query("ispolnitel") ispolnitel: String): Universal

    @POST("/privyazka/addSrokGodnosti")
    suspend fun addSrokGodnosti(@Body data: SrokGodnosti): Universal

    @POST("/srok/updateNew")
    suspend fun sendSrokGodnosti(@Query("id") id: Long, @Query("srokGodnosti") srokGodnosti: String,@Query("persent") persent: String ) : Universal

    @PUT("/market/new/updateShk")
    suspend fun updateShk(@Query("id") id: Long, @Query("newSHK") newSHK: String): Universal

    @POST("/send/update")
    suspend fun finishedSend(@Body data: FinishOzon): Universal

    @POST("/market/tasks/duplicate")
    suspend fun getDuplicate(@Body data: Duplicate): Universal

    @POST("/market/new/closeTask")
    suspend fun excludeArticle(@Query("id") id: Long, @Query("reason") reason: String, @Query("comment") comment: String, @Query("count") count: Int): Universal

    @GET("/article")
    suspend fun searchInDbForArticule(@Query("articul") articule: String): ShkInDb

    @GET("/privyazka/getZapis")
    suspend fun getBoxList(@Query("name") name: String, @Query("artikul") artikul: Int): WBBox

    @POST("/privyazka/add")
    suspend fun addBox(@Body data: AddBox): WBPrivyazka

    @GET("/privyazka/checkSHKWps")
    suspend fun checkWps(@Query("name") taskName: String, @Query("shk") shk: String): Universal

    @POST("/privyazka/endStatusNew")
    suspend fun endStatusWb(@Query("id") id: Long): WBBox

    @POST("/privyazka/addSrokGodnosti")
    suspend fun addSrokGodnostiForWb(@Body data: SrokGodnosti): Universal

    @GET("/privyazka/getData")
    suspend fun getDataWb(@Query("name") name: String): WBData

    @POST("/send/updateNew")
    suspend fun updateOzon(@Query("id") id: Long, @Query("mesto") mesto: Int, @Query("vlozhennost") vlozhennost: Int,
                           @Query("palletNo") palletNo: Int): Universal

    @POST("/market/tasks/updateStatusForID")
    suspend fun updateStatus(@Query("id") id: Long, @Query("status") status: Int): Universal

    @POST("/privyazka/udpateWBNew")
    suspend fun updateWB(@Query("id") id: Long, @Query("pallet") pallet: String, @Query("kolvo") kolvo: Int): Universal

    @POST("/market/otkaz/")
    suspend fun setFactSize(@Query("id") id: Long, @Query("count") count: Int): Universal

}