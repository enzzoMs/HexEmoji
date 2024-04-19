package sarueh.hexemoji.ui.fragments.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import sarueh.hexemoji.R
import sarueh.hexemoji.data.entities.Emoji
import sarueh.hexemoji.databinding.FragmentMainBinding
import sarueh.hexemoji.models.EmojiCategory
import sarueh.hexemoji.utils.AppPurchasesHelper

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val _toolbarTitle = MutableLiveData("")
    private lateinit var appPurchasesHelper: AppPurchasesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appPurchasesHelper = AppPurchasesHelper(
            onConnectionFail = {
                Toast.makeText(
                    context, resources.getString(R.string.app_connection_fail_toast_msg), Toast.LENGTH_SHORT
                ).show()
            },
            onPurchaseFlowError = {
                Toast.makeText(
                    context, resources.getString(R.string.app_action_fail_toast_msg), Toast.LENGTH_SHORT
                ).show()
            },
            getActivity = { requireActivity() }
        ).apply {
            handlePendingPurchases()
        }

        val binding = FragmentMainBinding.inflate(inflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark_color)

        val navController = (
            childFragmentManager.findFragmentById(R.id.main_nav_host_fragment
        ) as NavHostFragment).findNavController()

        // Manually wiring up the toolbar and screen navigation to prevent the navigation
        // component from applying a slide animation to the toolbar title when navigating

        _toolbarTitle.observe(viewLifecycleOwner) {
            binding.mainToolbar.title = it
        }

        binding.apply {
            bottomNav?.setupWithNavController(navController)
            navRail?.setupWithNavController(navController)
            navView?.setupWithNavController(navController)

            mainToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.info -> {
                        showInfoMenu(mainToolbar, R.menu.app_info_menu)
                        true
                    }
                    R.id.purchase -> {
                        showAppPurchasesMenu(mainToolbar, R.menu.app_purchases_menu)
                        true
                    }
                    else -> false
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        appPurchasesHelper.endConnection()

        super.onDestroy()
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun navigateToCategorySelection() {
        findNavController().navigate(R.id.action_main_screen_to_category_selection)
    }

    fun navigateToCollectionScreen(category: EmojiCategory, categoryEmojis: List<Emoji>) {
        findNavController().navigate(
            MainFragmentDirections.actionMainScreenToEmojiCollection(
                category, categoryEmojis.toTypedArray()
            )
        )
    }

    private fun showInfoMenu(view: View, @MenuRes menuRes: Int) {
        PopupMenu(requireContext(), view, Gravity.END).apply {
            menuInflater.inflate(menuRes, menu)

            setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.app_privacy_policy) {
                    val browserIntent = Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse(resources.getString(R.string.app_privacy_policy_link))
                    )
                    startActivity(browserIntent)
                    true
                } else {
                    false
                }
            }
        }.show()
    }

    private fun showAppPurchasesMenu(view: View, @MenuRes menuRes: Int) {
        PopupMenu(requireContext(), view, Gravity.END).apply {
            menuInflater.inflate(menuRes, menu)

            setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.app_purchase_unlock_all_emojis) {
                    appPurchasesHelper.launchPurchaseFlowForProduct(
                        AppPurchasesHelper.PRODUCT_ID_UNLOCK_ALL_EMOJIS
                    )
                    true
                } else {
                    false
                }
            }
        }.show()
    }
}