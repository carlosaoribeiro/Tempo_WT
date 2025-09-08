# Arquitetura MVVM do Projeto Tempo_WT

O app **Tempo_WT** segue a arquitetura **MVVM (Model-View-ViewModel)**, que separa responsabilidades e facilita a manutenção do código.

---

## 1. Estrutura de pacotes

com.carlosaoribeiro.tempo_wt
├── data
│ ├── model
│ │ └── Weather.kt
│ └── repository
│ └── WeatherRepository.kt
├── ui
│ ├── view
│ │ └── MainActivity.kt
│ └── viewmodel
│ └── WeatherViewModel.kt


---

## 2. Responsabilidades

- **Model (`data/model`)**  
  Contém as classes de dados.  
  Exemplo: `Weather.kt` representa cidade, temperatura e descrição.

- **Repository (`data/repository`)**  
  Camada que fornece os dados (fake ou de uma API real).  
  Exemplo: `WeatherRepository.kt` cria dados aleatórios simulando uma API.

- **ViewModel (`ui/viewmodel`)**  
  Contém a lógica que conecta dados à interface.  
  Usa `LiveData` para expor informações e sobrevive a mudanças de configuração.  
  Exemplo: `WeatherViewModel.kt`.

- **View (`ui/view`)**  
  São as telas do app (Activities/Fragments).  
  Observam o `LiveData` exposto pelo ViewModel.  
  Exemplo: `MainActivity.kt`.

---

## 3. Fluxo de dados MVVM

1. Usuário digita uma cidade na tela e clica em **Buscar**.  
2. A `MainActivity` chama `vm.fetch(city)`.  
3. O `WeatherViewModel` pede ao `WeatherRepository` os dados dessa cidade.  
4. O `Repository` devolve um objeto `Weather`.  
5. O `ViewModel` atualiza o `LiveData`.  
6. A `MainActivity` observa o `LiveData` e atualiza a interface com a nova informação.

---

## 4. Benefícios do MVVM

- Separação clara entre **UI**, **lógica** e **dados**.  
- Fácil de evoluir: trocar o `Repository` fake por uma API real.  
- Permite testes unitários no `ViewModel` sem depender de Android.  
- Mantém o código limpo e organizado.

