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
import com.application.tm_application_for_tsd.network.request_response.Status
import com.application.tm_application_for_tsd.network.request_response.Task
import com.application.tm_application_for_tsd.network.request_response.Universal
import com.application.tm_application_for_tsd.network.request_response.UpdateShk
import com.application.tm_application_for_tsd.network.request_response.UpdateSrokGodnosti
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxRequest
import com.application.tm_application_for_tsd.network.request_response.ValidateBoxResponse
import com.application.tm_application_for_tsd.network.request_response.WBBox
import com.application.tm_application_for_tsd.network.request_response.WBPrivyazka
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @POST("/api/tm/validateBox")
    suspend fun checkValidateBox(@Body barcodeData: ValidateBoxRequest): Response<ValidateBoxResponse>

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

    @GET("/market/tasks/getLDU")
    suspend fun getLDU(@Query("artikul") articul: Int , @Query("name") name: String): ChooseOp

    @DELETE("/market/tasks/deleteRecord")
    suspend fun deleteRecord(@Query("id") id: Long , @Query("task") name: String): ChooseOp

    @POST("market/tasks/updateTasksNew")
    suspend fun updateCheckBox(@Query("taskName") name: String, @Query("artikul") articul: Int, @Body data: MutableMap<String, String>): Universal

    @PUT("/market/tasks/updateStatus")
    suspend fun changeStatus(@Body data: Status): Universal

    @POST("/privyazka/addSrokGodnosti")
    suspend fun addSrokGodnosti(@Body data: SrokGodnosti): Universal

    @POST("/srok")
    suspend fun sendSrokGodnosti(@Body data: UpdateSrokGodnosti) : Universal

    @POST("/market/tasks/recordNewShk")
    suspend fun updateShk(@Body data: UpdateShk): Universal

    @POST("/send/update")
    suspend fun finishedSend(@Body data: FinishOzon): Universal

    @POST("/market/tasks/duplicate")
    suspend fun getDuplicate(@Body data: Duplicate): Universal

    @POST("/market/tasks/cancel")
    suspend fun excludeArticle(@Query("taskName") name: String, @Query("articul") articule: Int, @Query("comment") comment: String, @Query("reason") reason: String): Universal

    @GET("/article")
    suspend fun searchInDbForArticule(@Query("articul") articule: String): ShkInDb

    @GET("/privyazka/getZapis")
    suspend fun getBoxList(@Query("name") name: String, @Query("artikul") artikul: Int): WBBox

    @POST("/privyazka/add")
    suspend fun addBox(@Body data: AddBox): WBPrivyazka

    @GET("/privyazka/checkSHKWps")
    suspend fun checkWps(@Query("name") taskName: String, @Query("shk") shk: String): Universal

    @POST("/privyazka/endStatus")
    suspend fun endStatusWb(@Query("name") name: String, @Query("artikul") artikul: Int): WBBox

    @POST("/privyazka/addSrokGodnosti")
    suspend fun addSrokGodnostiForWb(@Body data: SrokGodnosti): Universal

}