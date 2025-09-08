# Model e Repository no Projeto Tempo_WT

Neste capítulo detalhamos as camadas **Model** e **Repository** da arquitetura MVVM, explicando cada linha de código.

---

## 1. Weather.kt (Model)

Arquivo: `app/src/main/java/com/carlosaoribeiro/tempo_wt/data/model/Weather.kt`

```kotlin
package com.carlosaoribeiro.tempo_wt.data.model

// Classe que representa os dados do clima
data class Weather(
    val city: String,           // Nome da cidade
    val temperatureC: Double,   // Temperatura em graus Celsius
    val description: String     // Descrição (Ex: "Ensolarado", "Nublado")
)

package com.carlosaoribeiro.tempo_wt.data.repository

import com.carlosaoribeiro.tempo_wt.data.model.Weather
import kotlin.random.Random

// Camada responsável por fornecer os dados do clima
class WeatherRepository {

    // Função que retorna um Weather "fake" (simulado)
    fun getWeather(city: String): Weather {
        // Lista de descrições possíveis
        val descriptions = listOf("Ensolarado", "Nublado", "Chuvoso", "Ventando")

        return Weather(
            city = city,
            temperatureC = Random.nextDouble(15.0, 35.0),   // gera valor entre 15ºC e 35ºC
            description = descriptions.random()             // escolhe uma descrição aleatória
        )
    }
}
Explicação

WeatherRepository → classe que representa a “fonte de dados”.

Aqui está fake, gerando dados aleatórios com Random.

getWeather(city) → recebe o nome da cidade e devolve um Weather.

Futuramente, será substituído por uma chamada real de API (Retrofit).

3. Fluxo até aqui

O usuário digita o nome de uma cidade.

O ViewModel chama WeatherRepository.getWeather(city).

O Repository gera um Weather com valores simulados.

O ViewModel expõe esse resultado via LiveData para a View.

✅ Benefícios da separação Model/Repository

O Model mantém os dados organizados.

O Repository centraliza o acesso (fake ou real) → facilita a manutenção.

Trocar de fake para API real exige mudar só o Repository, sem quebrar o restante da app.
