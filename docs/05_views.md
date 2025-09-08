05_views.md
Views no Projeto Tempo_WT

Neste capítulo detalhamos as Views da arquitetura MVVM: os arquivos de layout XML e a MainActivity. Essas camadas são responsáveis por exibir os dados do clima ao usuário, observando as mudanças enviadas pelo ViewModel.

1. activity_main.xml (Layout Principal)

📂 Arquivo:
app/src/main/res/layout/activity_main.xml

<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_gradient">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp">

        <!-- Toolbar com título e subtítulo -->
        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="0dp"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorSurface"
            android:title="@string/tempo_wt"
            android:subtitle="Clima agora"
            app:titleCentered="true"
            app:titleTextColor="?attr/colorOnSurface"
            app:subtitleTextColor="?attr/colorOnSurfaceVariant"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Campo de busca -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilCity"
            style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:hint="Cidade"
            android:layout_marginTop="16dp"
            android:layout_marginEnd="8dp"
            app:startIconDrawable="@android:drawable/ic_menu_mylocation"
            app:layout_constraintTop_toBottomOf="@id/toolbar"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toStartOf="@id/btnLoad">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etCity"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:imeOptions="actionSearch"
                android:singleLine="true" />
        </com.google.android.material.textfield.TextInputLayout>

        <!-- Botão Buscar -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnLoad"
            android:layout_width="wrap_content"
            android:layout_height="56dp"
            android:text="Buscar"
            app:icon="@android:drawable/ic_menu_search"
            app:iconPadding="8dp"
            app:iconGravity="textStart"
            app:cornerRadius="24dp"
            app:layout_constraintTop_toTopOf="@id/tilCity"
            app:layout_constraintBottom_toBottomOf="@id/tilCity"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Card com dados do clima -->
        <com.google.android.material.card.MaterialCardView
            android:id="@+id/cardCurrent"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:padding="16dp"
            app:cardCornerRadius="20dp"
            app:layout_constraintTop_toBottomOf="@id/tilCity"
            app:layout_constraintStart_toStartOf="@id/tilCity"
            app:layout_constraintEnd_toEndOf="@id/btnLoad">

            <!-- Conteúdo do Card -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="8dp">

                <!-- Nome da cidade -->
                <TextView
                    android:id="@+id/tvCity"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Cidade"
                    android:textSize="16sp"
                    android:textStyle="bold" />

                <!-- Temperatura e ícone -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical"
                    android:paddingTop="8dp">

                    <ImageView
                        android:id="@+id/ivIcon"
                        android:layout_width="72dp"
                        android:layout_height="72dp"
                        android:contentDescription="Ícone clima" />

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:orientation="vertical"
                        android:paddingStart="12dp">

                        <TextView
                            android:id="@+id/tvTemp"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="--°C"
                            android:textSize="40sp"
                            android:textStyle="bold" />

                        <TextView
                            android:id="@+id/tvDesc"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="--" />
                    </LinearLayout>
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Indicador de Loading -->
        <com.google.android.material.progressindicator.CircularProgressIndicator
            android:id="@+id/progress"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:visibility="gone"
            app:layout_constraintTop_toBottomOf="@id/cardCurrent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</ScrollView>


🔎 Resumo:

Toolbar: exibe título e subtítulo fixos.

TextInputLayout: campo para digitar cidade.

Button: aciona a busca.

CardView: mostra dados do clima.

ProgressIndicator: aparece durante carregamento.

2. MainActivity.kt

📂 Arquivo:
app/src/main/java/com/carlosribeiro/tempo_wt/ui/MainActivity.kt

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuração da Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.tempo_wt)
        supportActionBar?.subtitle = "Clima agora"

        // Views
        val etCity = findViewById<TextInputEditText>(R.id.etCity)
        val btnLoad = findViewById<MaterialButton>(R.id.btnLoad)
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvTemp = findViewById<TextView>(R.id.tvTemp)
        val tvDesc = findViewById<TextView>(R.id.tvDesc)
        val tvFeels = findViewById<TextView>(R.id.tvFeelsLike)
        val tvHum  = findViewById<TextView>(R.id.tvHumidity)
        val tvWind = findViewById<TextView>(R.id.tvWind)
        val tvUpdated = findViewById<TextView>(R.id.tvUpdated)
        val progress = findViewById<View>(R.id.progress)

        // Função utilitária para hora
        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Ação do botão Buscar
        btnLoad.setOnClickListener {
            val city = etCity.text?.toString().orEmpty().ifBlank { "Osasco" }
            progress.visibility = View.VISIBLE
            viewModel.loadWeather(city)
        }

        // Observa o ViewModel
        viewModel.weather.observe(this) { result ->
            progress.visibility = View.GONE
            result.onSuccess { response ->
                tvCity.text = response.name
                tvTemp.text = "${response.main.temp}°C"
                tvDesc.text = response.weather.firstOrNull()?.description ?: "-"

                // Ícone via Coil
                val icon = response.weather.firstOrNull()?.icon
                if (!icon.isNullOrBlank()) {
                    val url = "https://openweathermap.org/img/wn/${icon}@4x.png"
                    ivIcon.load(url)
                } else ivIcon.setImageDrawable(null)

                // Demais dados
                tvFeels.text = "Sensação: ${response.main.feels_like}°C"
                tvHum.text   = "Umidade: ${response.main.humidity}%"
                tvWind.text  = "Vento: ${response.wind?.speed ?: 0f} m/s"
                tvUpdated.text = "Atualizado: ${now()}"
            }.onFailure { e ->
                tvDesc.text = "Erro: ${e.message}"
            }
        }
    }
}


🔎 Resumo:

Inicializa toolbar, campo de busca, botão e card.

Usa viewModel.loadWeather(cidade) para buscar dados.

Observa LiveData e atualiza a UI em tempo real.

Usa Coil para carregar ícones do clima.
