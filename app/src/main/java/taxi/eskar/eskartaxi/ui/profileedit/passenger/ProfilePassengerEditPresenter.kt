package taxi.eskar.eskartaxi.ui.profileedit.passenger

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
import taxi.eskar.eskartaxi.ui.Results
import javax.inject.Inject

@InjectViewState
class ProfilePassengerEditPresenter @Inject constructor(
        private val profileRepository: ProfileRepository, router: Router
) : BasePresenter<ProfilePassengerEditView>(router) {

    private var updateProfileDisposable: Disposable? = null

    private lateinit var passenger: Passenger

    init {
        profileRepository.getPassengerMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnSubscribe(this::unsubscribeOnDetach)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe({
                    if (it is PassengerResult.Success) {
                        passenger = it.passenger
                        viewState.showProfile(passenger)
                    }
                }, this::processError)
        this.fetchAllSex()
    }

    private fun fetchAllSex() {
        profileRepository.getAllSex()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processAllSexResult)
    }

    fun onSexClicked(pos: Int, sex: Sex) {
        passenger.sex = sex.value
        viewState.setSelectedSex(pos, sex)
    }

    fun onSaveClicked() {
        if (updateProfileDisposable != null) {
            router.showSystemMessage("Профиль обновляется, ждите!")
            return
        }

        if (passenger.isNotRegistered()) {
            router.showSystemMessage("Все поля пользователя должны быть заполнены!")
            return
        }

        profileRepository.updatePassenger(passenger)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { updateProfileDisposable = it }
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> updateProfileDisposable = null }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processUpdateProfileResult)
    }

    private fun processAllSexResult(result: AllSexResult) {
        when (result) {
            is AllSexResult.Success -> {
                viewState.showAllSex(result.sexList)
            }
            is AllSexResult.Fail -> {
                // todo process errors
            }
        }
    }

    private fun processUpdateProfileResult(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                router.exitWithResult(Results.PROFILE_EDIT_SAVED_PASSENGER, passenger)
            }
            is PassengerResult.Failure -> {
                // todo process errors
            }
        }
    }

    fun onLastNameChanged(nameLast: String) {
        passenger.surname = nameLast
    }

    fun onFirstNameChanged(nameFirst: String) {
        passenger.name = nameFirst
    }
}