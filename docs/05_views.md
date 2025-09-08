# 05_views.md  

## Views no Projeto Tempo_WT  

Neste capítulo detalhamos as **Views** da arquitetura MVVM: os arquivos de layout XML e a `MainActivity`. Essas camadas são responsáveis por exibir os dados do clima ao usuário, observando as mudanças enviadas pelo `ViewModel`.

---

### 1. `activity_main.xml` (Layout Principal)

📂 Arquivo:  
`app/src/main/res/layout/activity_main.xml`

```xml
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

