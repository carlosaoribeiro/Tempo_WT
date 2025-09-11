# 06 - Funcionalidades: Troca de Temperatura e Localização Atual

## 🔁 Botão de Troca de Temperatura (°C / °F)

- Componente: `btnTemperatureToggle`
- Tipo: `MaterialButton` circular
- Posição: canto superior direito da tela principal
- Ação:
  - Alterna entre unidades `"metric"` (Celsius) e `"imperial"` (Fahrenheit)
  - Atualiza o botão com o texto `"C"` ou `"F"`
  - Recarrega a tela com os dados atualizados da cidade visível ou da geolocalização

### Lógica:

- Estado controlado via `ViewModel` usando métodos:
  - `getCurrentUnits()`
  - `setCurrentUnits(units: String)`
- Observers atualizam os textos de:
  - Temperatura atual
  - Sensação térmica
  - Unidade do vento

---

## 📍 Botão de Localização Atual

- Componente: `btnLocation`
- Permissão: `ACCESS_FINE_LOCATION`
- Ação:
  - Solicita a localização do usuário
  - Ao obter sucesso, carrega os dados de clima atual e previsão
  - Caso não tenha permissão, solicita via `ActivityCompat.requestPermissions`

### Lógica:

- Usa `FusedLocationProviderClient` do Google
- Integração com o ViewModel via:
  - `viewModel.loadWeatherByCoordinates(lat, lon)`

---

## 🧪 Observações Técnicas

- O botão de localização pode ser usado **independentemente** da unidade de medida escolhida
- A mudança de unidade de temperatura **afeta tanto os dados da cidade buscada quanto da localização**

---

## 🖼️ Estética

- Botões com `backgroundTint` transparente
- Cores ajustadas via:
  - `drawable/bg_circle_button.xml`
  - `android:textColor` e `android:textStyle="bold"`

---

## ✅ Arquivos Envolvidos

- `MainActivity.kt`
- `activity_main.xml`
- `WeatherViewModel.kt`
- `WeatherRepository.kt`





