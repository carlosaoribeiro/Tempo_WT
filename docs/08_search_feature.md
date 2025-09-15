# Chapter 08 - Search Feature

## Overview
The search feature allows users to quickly find the weather for any city by typing its name in the search field. It improves usability by making the app more dynamic and user-friendly.

---

## Implementation Details

### 1. Layout (XML)
The search bar was created using **Material Design Components**:

```xml
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/searchInputLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:boxCornerRadiusTopStart="12dp"
    app:boxCornerRadiusTopEnd="12dp"
    app:boxCornerRadiusBottomStart="12dp"
    app:boxCornerRadiusBottomEnd="12dp"
    app:boxStrokeColor="@color/gray_500"
    app:boxStrokeWidth="1dp"
    app:endIconDrawable="@drawable/ic_search"
    app:endIconMode="custom"
    app:endIconTint="@color/gray_500">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etSearch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="@string/search_city"
        android:imeOptions="actionSearch"
        android:inputType="text"
        android:singleLine="true"
        android:textColor="@android:color/black"
        android:textColorHint="@color/darker_gray"
        android:textSize="16sp" />
</com.google.android.material.textfield.TextInputLayout>

2. Activity/Fragment (Kotlin)

In the MainActivity, we added a search listener to handle the user input when the search icon or keyboard action is triggered:

binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        val city = binding.etSearch.text.toString().trim()
        if (city.isNotEmpty()) {
            fetchWeatherData(city)
        }
        true
    } else {
        false
    }
}

3. Search Icon

The search icon (ic_search.xml) was placed on the right side of the input field using endIconDrawable.
Benefits

    Quick access to weather data for any city.

    Clean and modern UI with Material Design.

    Optimized for keyboard actions (IME_ACTION_SEARCH).

git add docs/08_search_feature.md

