package taxi.eskar.eskartaxi.ui.profileedit.passenger

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_profile_passenger_edit.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.adapters.SexAdapter
import taxi.eskar.eskartaxi.util.filters.LetterFilter
import toothpick.Toothpick


class ProfilePassengerEditFragment : BaseFragment(), ProfilePassengerEditView {

    companion object {
        fun newInstance(): ProfilePassengerEditFragment {
            val args = Bundle()
            val fragment = ProfilePassengerEditFragment()
            fragment.arguments = args
            return fragment
        }
    }


    @InjectPresenter lateinit var presenter: ProfilePassengerEditPresenter

    private val adapter = SexAdapter({ pos, paymentType ->
        presenter.onSexClicked(pos, paymentType)
    })


    // Android
    override val layoutResId: Int = R.layout.fragment_profile_passenger_edit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_profile_edit)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        et_profile_edit_name_last.filters = arrayOf(LetterFilter())
        et_profile_edit_name_first.filters = arrayOf(LetterFilter())

        rv_sex.layoutManager = LinearLayoutManager(this.context)
        rv_sex.adapter = adapter
    }

    override fun onPause() {
        this.view?.findFocus()?.let { this.hideKeyboard(it) }
        super.onPause()
    }


    // View
    override fun bind() {
        et_profile_edit_name_last.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onLastNameChanged(it) }
        et_profile_edit_name_first.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onFirstNameChanged(it) }
        btn_profile_edit_save.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSaveClicked() }
    }

    override fun showAllSex(sexList: List<Sex>) {
        adapter.replaceItems(sexList)
    }

    override fun showProfile(passenger: Passenger) {
        et_profile_edit_name_last.setText(passenger.surname)
        et_profile_edit_name_first.setText(passenger.name)
        adapter.setSelected(passenger.sex().id)
    }

    override fun setSelectedSex(pos: Int, sex: Sex) {
        adapter.setSelected(pos)
    }


    @ProvidePresenter fun providePresenter(): ProfilePassengerEditPresenter {
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return ProfilePassengerEditPresenter(profileRepository, router)
    }
}