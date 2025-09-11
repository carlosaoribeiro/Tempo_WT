🧩 Arquivo 07_toggle_feature_details.md (conteúdo sugerido):
# 07. Temperature Unit Toggle (°C / °F)

This chapter explains the implementation of the temperature unit toggle feature in the Tempo_WT app, which allows users to switch between Celsius and Fahrenheit.

## Objective

To provide a button in the UI that toggles temperature units (C ⇄ F) dynamically for both:
- Current Weather Data
- 5-Day Forecast

## UI Implementation

- A `MaterialButton` was added to the top-right of the screen.
- The button toggles between `C` and `F` with a rounded background.
- Bold styling was applied to improve visual clarity.

```xml
<com.google.android.material.button.MaterialButton
    android:id="@+id/btnTemperatureToggle"
    android:layout_width="40dp"
    android:layout_height="40dp"
    android:text="C"
    android:textColor="@android:color/black"
    android:textStyle="bold"
    ... />

Logic

Default Unit: "metric" (Celsius).

Toggle Action:

When pressed, the unit changes between "metric" and "imperial".

The button updates the label accordingly (C or F).

The app refetches the weather using the new unit.

Affected Components:

WeatherViewModel.kt: now stores the current unit and uses it in fetch calls.

MainActivity.kt: handles the toggle button logic and updates the UI accordingly.

Code Reference

MainActivity.kt

WeatherViewModel.kt

WeatherRepository.kt

Sample Result

Temperature is shown as 23°C or 73°F.

Wind speed is also adapted: m/s or mph.

"Feels like" and humidity values are shown with appropriate symbols.

