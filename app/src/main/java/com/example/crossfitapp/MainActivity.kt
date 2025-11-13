package com.example.crossfitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import com.example.crossfitapp.R
import com.example.crossfitapp.ui.theme.CrossfitAppTheme
import kotlin.collections.MutableMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrossfitAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CrossfitApp()
                }
            }
        }
    }
}

private enum class WorkoutCategory(@StringRes val titleRes: Int) {
    WITH_BAR(titleRes = R.string.category_exercises_with_bar),
    WITHOUT_BAR(titleRes = R.string.category_exercises_without_bar),
    HERO_WODS(titleRes = R.string.category_hero_wods),
    GIRL_WODS(titleRes = R.string.category_girl_wods)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrossfitApp() {
    val scoreState = remember { mutableStateMapOf<String, String>() }
    var selectedTab by remember { mutableStateOf(WorkoutCategory.WITH_BAR) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            WorkoutCategoryTabs(
                selected = selectedTab,
                onCategorySelected = { selectedTab = it }
            )
            WorkoutList(
                modifier = Modifier.fillMaxSize(),
                category = selectedTab,
                scoreState = scoreState
            )
        }
    }
}

@Composable
private fun WorkoutCategoryTabs(
    selected: WorkoutCategory,
    onCategorySelected: (WorkoutCategory) -> Unit
) {
    val categories = WorkoutCategory.entries
    TabRow(selectedTabIndex = categories.indexOf(selected)) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = category == selected,
                onClick = { onCategorySelected(category) },
                text = { Text(text = stringResource(id = category.titleRes)) }
            )
        }
    }
}

@Composable
private fun WorkoutList(
    modifier: Modifier = Modifier,
    category: WorkoutCategory,
    scoreState: MutableMap<String, String>
) {
    val items = when (category) {
        WorkoutCategory.WITH_BAR -> WorkoutData.exercisesWithBar
        WorkoutCategory.WITHOUT_BAR -> WorkoutData.exercisesWithoutBar
        WorkoutCategory.HERO_WODS -> WorkoutData.heroWods
        WorkoutCategory.GIRL_WODS -> WorkoutData.girlWods
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.last_updated) + ": ${WorkoutData.lastUpdated}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(items) { item ->
            WorkoutCard(
                item = item,
                currentScore = scoreState[item.id].orEmpty(),
                onScoreChanged = { scoreState[item.id] = it }
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    item: WorkoutItem,
    currentScore: String,
    onScoreChanged: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (!item.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = currentScore,
                onValueChange = onScoreChanged,
                label = { Text(text = stringResource(id = R.string.score_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
        }
    }
}

data class WorkoutItem(
    val id: String,
    val name: String,
    val description: String? = null
)

private object WorkoutData {
    val lastUpdated: String = "Abril/2024"

    val exercisesWithBar = listOf(
        WorkoutItem(
            id = "front_squat",
            name = "Front Squat",
            description = "3-5 séries de 5 repetições focando na postura e profundidade."
        ),
        WorkoutItem(
            id = "back_squat",
            name = "Back Squat",
            description = "5 séries de 5 repetições aumentando a carga progressivamente."
        ),
        WorkoutItem(
            id = "clean",
            name = "Clean & Jerk",
            description = "Trabalhe técnica com cargas moderadas antes de subir o peso."
        ),
        WorkoutItem(
            id = "snatch",
            name = "Snatch",
            description = "4 séries de 3 repetições focando na explosão do quadril."
        ),
        WorkoutItem(
            id = "deadlift",
            name = "Deadlift",
            description = "5x5 mantendo a postura neutra e o core firme."
        )
    )

    val exercisesWithoutBar = listOf(
        WorkoutItem(
            id = "burpee",
            name = "Burpees",
            description = "3 minutos de máximo de repetições."
        ),
        WorkoutItem(
            id = "air_squat",
            name = "Air Squat",
            description = "5 séries de 25 repetições com ritmo controlado."
        ),
        WorkoutItem(
            id = "push_up",
            name = "Push-ups",
            description = "4 séries de 20 repetições ou até a falha."
        ),
        WorkoutItem(
            id = "sit_up",
            name = "Sit-ups",
            description = "Abmat sit-ups em séries de 30 repetições."
        ),
        WorkoutItem(
            id = "double_under",
            name = "Double Unders",
            description = "Pratique séries de 50 repetições buscando consistência."
        )
    )

    val heroWods = listOf(
        WorkoutItem(
            id = "murph",
            name = "Murph",
            description = "1 mile run, 100 pull-ups, 200 push-ups, 300 air squats, 1 mile run (com colete de 9 kg opcional)."
        ),
        WorkoutItem(
            id = "dt",
            name = "DT",
            description = "5 rodadas: 12 deadlifts, 9 hang power cleans, 6 push jerks (70/47,5 kg)."
        ),
        WorkoutItem(
            id = "holleyman",
            name = "Holleyman",
            description = "30 rodadas: 5 wall balls, 3 handstand push-ups, 1 power clean pesado (102/70 kg)."
        ),
        WorkoutItem(
            id = "linda",
            name = "Linda",
            description = "10-9-8-7-6-5-4-3-2-1 reps de deadlift, bench press e clean com porcentagens do peso corporal."
        )
    )

    val girlWods = listOf(
        WorkoutItem(
            id = "fran",
            name = "Fran",
            description = "21-15-9 thrusters (43/29 kg) e pull-ups."
        ),
        WorkoutItem(
            id = "helen",
            name = "Helen",
            description = "3 rodadas: corrida 400m, 21 kettlebell swings (24/16 kg), 12 pull-ups."
        ),
        WorkoutItem(
            id = "grace",
            name = "Grace",
            description = "30 clean & jerks (61/43 kg) no menor tempo possível."
        ),
        WorkoutItem(
            id = "annie",
            name = "Annie",
            description = "50-40-30-20-10 double unders e sit-ups."
        ),
        WorkoutItem(
            id = "diane",
            name = "Diane",
            description = "21-15-9 deadlifts (102/70 kg) e handstand push-ups."
        )
    )
}
