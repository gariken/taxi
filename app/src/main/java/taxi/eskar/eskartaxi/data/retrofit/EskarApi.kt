package taxi.eskar.eskartaxi.data.retrofit

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import taxi.eskar.eskartaxi.data.model.AddressResponse
import taxi.eskar.eskartaxi.data.model.responses.*

interface EskarApi {

    companion object {
        private const val PATH_ID_USER = "user_id"
    }

    // =============================================================================================
    // Addresses
    // =============================================================================================
    @GET("/api/v1/addresses") fun getAddressFor(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double): Single<AddressResponse>

    @GET("/api/v1/coordinates") fun getLatLonFor(
            @Query("address") address: String): Single<CoordinatesResponse>


    // =============================================================================================
    // Core
    // =============================================================================================

    @GET("/api/v1/order_options") fun getOrderOptions(): Single<OrderOptionsResponse>

    @GET("/api/v1/tariffs") fun getTariffs(): Single<TariffsResponse>


    // =============================================================================================
    // Passenger
    // =============================================================================================

    @POST("/api/v1/users/send_password") fun sendPasswordPassenger(
            @Query("phone_number") phoneNumber: String): Single<SendPasswordResponse>

    @POST("/api/v1/users/auth") fun authPassenger(
            @Query("phone_number") phoneNumber: String,
            @Query("password") password: String): Single<AuthResponsePassenger>

    @GET("/api/v1/users/{id}") fun getPassenger(
            @Path("id") id: Int?): Single<Response<PassengerResponse>>

    @PUT("/api/v1/users/{id}") fun updatePassenger(
            @Path("id") id: Int,
            @Query("name") name: String?,
            @Query("surname") surname: String?,
            @Query("sex") sex: String?,
            @Query("favorite_addresses") addresses: String?): Single<PassengerResponse>

    @POST("/api/v1/users/{userId}/initialization") fun initPassenger(
            @Path("userId") userId: Int,
            @Query("lat") lat: Double,
            @Query("lon") lon: Double): Single<Response<PassengerResponse>>

    @FormUrlEncoded @PUT("/api/v1/users/{userId}") fun uploadPhotoPassenger(
            @Path("userId") userId: Int,
            @Field("photo") photo: String): Single<PassengerResponse>

    @PUT("/api/v1/users/{userId}/remove_image") fun deletePhotoPassenger(
            @Path("userId") userId: Int,
            @Query("photo") delete: String): Single<PassengerResponse>

    @GET("/api/v1/orders") fun getOrdersPassenger(
            @Query("user_id") userId: Int): Single<OrdersResponse>

    @GET("/api/v1/orders/preliminary") fun getPreliminaryOrder(
            @Query("lat_from") latFrom: Double?,
            @Query("lon_from") lonFrom: Double?,
            @Query("lat_to") latTo: Double?,
            @Query("lon_to") lonTo: Double?,
            @Query("order_option_ids[]") ids: List<Int>): Single<PreliminaryResponse>

    @POST("/api/v1/orders") fun createOrder(
            @Query("lat_from") latFrom: Double?,
            @Query("lon_from") lonFrom: Double?,
            @Query("lat_to") latTo: Double?,
            @Query("lon_to") lonTo: Double?,
            @Query("comment") comment: String?,
            @Query("tariff_id") tariffId: Int?,
            @Query("payment_method") paymentType: String?,
            @Query("order_option_ids[]") ids: List<Int>,
            @Query("user_id") userId: Int,
            @Query("card_id") cardId: Int?): Single<Response<OrderResponse>>

    @DELETE("api/v1/orders/{order_id}") fun deleteOrder(
            @Path("order_id") orderId: Int): Single<OrderResponse>


    // =============================================================================================
    // Driver
    // =============================================================================================

    @POST("/api/v1/drivers/send_password") fun sendPasswordDriver(
            @Query("phone_number") phoneNumber: String): Single<SendPasswordResponse>

    @POST("/api/v1/drivers/auth") fun authDriver(
            @Query("phone_number") phoneNumber: String,
            @Query("password") password: String): Single<AuthResponseDriver>

    @GET("/api/v1/drivers/{id}") fun getDriver(
            @Path("id") id: Int?): Single<Response<DriverResponse>>

    @PUT("/api/v1/drivers/{id}") fun updateDriver(
            @Path("id") id: Int,
            @Query("name") name: String?,
            @Query("surname") surname: String?,
            @Query("car_model") carModel: String?,
            @Query("licence_plate") licensePlate: String?,
            @Query("car_color") carColor: String?): Single<DriverResponse>

    @POST("/api/v1/drivers/{driverId}/initialization") fun initDriver(
            @Path("driverId") driverId: Int,
            @Query("lat") lat: Double,
            @Query("lon") lon: Double): Single<Response<DriverResponse>>

    @FormUrlEncoded @PUT("/api/v1/drivers/{driverId}") fun uploadPhotoDriver(
            @Path("driverId") driverId: Int,
            @Field("photo") photo: String): Single<DriverResponse>

    @FormUrlEncoded @PUT("/api/v1/drivers/{driverId}") fun uploadLicenseDriver(
            @Path("driverId") driverId: Int,
            @Field("license") license: String): Single<DriverResponse>

    @PUT("/api/v1/drivers/{driverId}/remove_image") fun deletePhotoDriver(
            @Path("driverId") driverId: Int,
            @Query("photo") delete: String): Single<DriverResponse>


    @GET("/api/v1/orders") fun getOrdersDriver(
            @Query("driver_id") driverId: Int): Single<OrdersResponse>

    @GET("/api/v1/orders") fun getOrdersNewDriver(): Single<Response<OrdersResponse>>

    @PUT("/api/v1/orders/{orderId}/take") fun takeOrder(
            @Path("orderId") orderId: Int,
            @Query("driver_id") driverId: Int): Single<Response<OrderResponse>>

    @PUT("/api/v1/orders/{orderId}/wait") fun waitOrder(
            @Path("orderId") orderId: Int): Single<OrderResponse>

    @PUT("/api/v1/orders/{orderId}/start") fun startOrder(
            @Path("orderId") orderId: Int): Single<OrderResponse>

    @PUT("/api/v1/orders/{orderId}/close") fun closeOrder(
            @Path("orderId") orderId: Int,
            @Query("payed") payed: Boolean?): Single<OrderResponse>

    @PUT("/api/v1/orders/{orderId}/estimate") fun rateOrder(
            @Path("orderId") orderId: Int,
            @Query("rating") rating: Double?,
            @Query("review") review: String?): Single<OrderResponse>


    // region cards

    @GET("/api/v1/users/{$PATH_ID_USER}/cards") fun getCards(
            @Path(PATH_ID_USER) userId: Int): Single<CardsResponse>

    @POST("/api/v1/users/{$PATH_ID_USER}/bind_card") fun bindCard(
            @Path(PATH_ID_USER) userId: Int,
            @Query("name") owner: String,
            @Query("card_cryptogram_packet") cryptogram: String): Single<BindCardResponse>

    @DELETE("/api/v1/users/{$PATH_ID_USER}/remove_card") fun unbindCard(
            @Path(PATH_ID_USER) userId: Int,
            @Query("card_id") cardId: Int): Single<Response<StatusResponse>>

    // endregion


    @GET("/api/v1/users/{$PATH_ID_USER}/debt") fun getDebt(
            @Path(PATH_ID_USER) userId: Int): Single<DebtResponse>

    @POST("/api/v1/users/{$PATH_ID_USER}/close_debt") fun closeDebt(
            @Path(PATH_ID_USER) userId: Int,
            @Query("card_id") cardId: Int): Single<CloseDebtResponse>

}