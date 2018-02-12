package taxi.eskar.eskartaxi.ui.registration.passenger

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex
import taxi.eskar.eskartaxi.data.model.results.AllSexResult
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class RegPassengerPresenter(
        private val profileRepository: ProfileRepository,
        private val passenger: Passenger, router: Router
) : BasePresenter<RegPassengerView>(router) {

    private var uploadPhotoDisposable: Disposable? = null
    private var updatePassengerDisposable: Disposable? = null

    init {
        this.fetchAllSex()
        viewState.showPassenger(passenger)
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    fun onFirstNameChanged(firstName: String) {
        passenger.name = firstName
    }

    fun onLastNameChanged(lastName: String) {
        passenger.surname = lastName
    }

    fun onSexClicked(pos: Int, sex: Sex) {
        passenger.sex = sex.value
        viewState.setSelectedSex(pos, sex)
    }

    fun onUploadPhotoClicked() {
        if (updatePassengerDisposable != null || uploadPhotoDisposable != null) {
            return
        }

        viewState.showPhotoPicker()
    }

    fun onPhotoResult(uri: Uri) {
        if (updatePassengerDisposable != null || uploadPhotoDisposable != null) {
            return
        }

        profileRepository.uploadPhotoPassenger(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe {
                    uploadPhotoDisposable = it
                    viewState.showLoading(true)
                }
                .doOnEvent { _, _ ->
                    uploadPhotoDisposable = null
                    viewState.showLoading(false)
                }
                .subscribe({
                    when (it) {
                        is PassengerResult.Success -> {
                            viewState.showPhotoUploaded()
                        }
                        is PassengerResult.Failure -> {
                            router.showSystemMessage("Ошибка загрузки фотографии")
                            this.processError(it.throwable)
                        }
                    }
                }, this::processError)
    }

    fun onSaveClicked() {
        if (updatePassengerDisposable != null || uploadPhotoDisposable != null) {
            return
        }

        if (passenger.isNotRegistered()) {
            router.showSystemMessage("Необходимо заполнить все поля")
            return
        }

        profileRepository.updatePassenger(passenger)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { updatePassengerDisposable = it }
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> updatePassengerDisposable = null }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processUpdateProfileResult, this::processError)
    }

    fun onSignOutClicked() {
        profileRepository.signOutPassenger()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe({ router.newRootScreen(Screens.AUTH_PHONE) }, this::processError)
    }

    // =============================================================================================
    //   Private
    // =============================================================================================
    private fun fetchAllSex() {
        profileRepository.getAllSex()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processAllSexResult)
    }

    private fun processAllSexResult(result: AllSexResult) {
        when (result) {
            is AllSexResult.Success -> {
                viewState.showAllSex(result.sexList)
            }
            is AllSexResult.Fail -> {
                this.processError(result.throwable)
            }
        }
    }

    private fun processUpdateProfileResult(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                router.newRootScreen(Screens.SPLASH)
            }
            is PassengerResult.Failure -> {
                this.processError(result.throwable)
            }
        }
    }
}