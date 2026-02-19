package com.infected.ar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.infected.ar.ui.screens.home.HomeScreen
import com.infected.ar.ui.screens.legal.LegalScreen
import com.infected.ar.ui.screens.library.InfectionDetailScreen
import com.infected.ar.ui.screens.library.MyInfectionsScreen
import com.infected.ar.ui.screens.live.LiveInfectScreen
import com.infected.ar.ui.screens.onboarding.OnboardingScreen
import com.infected.ar.ui.screens.settings.SettingsScreen
import com.infected.ar.ui.screens.skins.SkinsStoreScreen
import com.infected.ar.ui.screens.splash.SplashScreen
import com.infected.ar.ui.screens.survival.SurvivalMiniGameScreen
import com.infected.ar.ui.screens.upload.ExportShareScreen
import com.infected.ar.ui.screens.upload.FaceSelectCropScreen
import com.infected.ar.ui.screens.upload.UploadPickerScreen
import com.infected.ar.ui.screens.upload.ZombifyEditorScreen

@Composable
fun InfectedNavHost(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(onNext = {
                nav.navigate(if (vm.settings.value.onboardingCompleted) Routes.Home else Routes.Onboarding) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            })
        }
        composable(Routes.Onboarding) {
            OnboardingScreen(onFinish = {
                vm.completeOnboarding()
                nav.navigate(Routes.Home) { popUpTo(Routes.Onboarding) { inclusive = true } }
            })
        }
        composable(Routes.Home) {
            HomeScreen(
                vm = vm,
                onLive = { nav.navigate(Routes.LiveInfect) },
                onUpload = { nav.navigate(Routes.UploadPicker) },
                onSurvival = { nav.navigate(Routes.Survival) },
                onLibrary = { nav.navigate(Routes.MyInfections) },
                onSkins = { nav.navigate(Routes.Skins) },
                onSettings = { nav.navigate(Routes.Settings) }
            )
        }
        composable(Routes.UploadPicker) { UploadPickerScreen(nav) }
        composable(Routes.FaceSelect) { FaceSelectCropScreen(nav) }
        composable(Routes.ZombifyEditor) { ZombifyEditorScreen(nav, vm) }
        composable(Routes.ExportShare) { ExportShareScreen(nav) }
        composable(Routes.LiveInfect) { LiveInfectScreen(nav, vm) }
        composable(Routes.Survival) { SurvivalMiniGameScreen(nav) }
        composable(Routes.MyInfections) {
            MyInfectionsScreen(vm, onDetail = { nav.navigate("${Routes.InfectionDetail}/$it") })
        }
        composable("${Routes.InfectionDetail}/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
            InfectionDetailScreen(id = it.arguments?.getString("id") ?: "", vm = vm)
        }
        composable(Routes.Skins) { SkinsStoreScreen(vm) }
        composable(Routes.Settings) {
            SettingsScreen(
                vm,
                onPrivacy = { nav.navigate(Routes.Privacy) },
                onTerms = { nav.navigate(Routes.Terms) }
            )
        }
        composable(Routes.Privacy) { LegalScreen(title = "Privacy Policy") }
        composable(Routes.Terms) { LegalScreen(title = "Terms") }
    }
}
