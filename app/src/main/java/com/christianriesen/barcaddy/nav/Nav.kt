package com.christianriesen.barcaddy.nav

object Routes {
    const val Home = "home"
    const val Settings = "settings"
    const val Reorder = "reorder"
    const val Scan = "scan"

    // Edit / new card. id == "new" means a fresh draft.
    const val FormPattern = "form/{id}"
    fun form(id: String) = "form/$id"
    const val FormNew = "new"

    const val DisplayPattern = "display/{id}"
    fun display(id: String) = "display/$id"
}
