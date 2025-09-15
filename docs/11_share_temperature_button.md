# Chapter 11 – Share Temperature Button

## Overview
In this update, the **Settings button (gear icon)** was replaced by a new **Share button**.  
The purpose of this feature is to allow users to quickly share the current temperature of the selected city with other apps (e.g., WhatsApp, Messages, Email).

---

## UI Changes
- The `@+id/btnSettings` was refactored to `@+id/btnShared`.
- The drawable resource was updated to use a new icon: `@drawable/ic_shared`.
- The button is placed in the same position as the previous Settings button in the header layout.

```xml
<ImageButton
    android:id="@+id/btnShared"
    android:layout_width="40dp"
    android:layout_height="40dp"
    android:background="?attr/selectableItemBackgroundBorderless"
    android:contentDescription="@string/share_temperature"
    android:src="@drawable/ic_shared"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintTop_toTopOf="parent" />

Logic in MainActivity

A click listener was added to handle the share action.
When pressed, the button retrieves the current city and temperature from the TextViews and launches an Android Share Intent.

binding.btnShared.setOnClickListener {
    val city = binding.tvCity.text.toString()
    val temp = binding.tvTemp.text.toString()

    val shareText = "Current temperature in $city: $temp"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    startActivity(Intent.createChooser(intent, "Share via"))
}

Benefits

    User-Friendly: Easy way for users to share live weather updates.

    Cross-App Integration: Works with any app that supports text sharing.

    Replacement: Reuses the previous button’s position for a seamless UI transition.

