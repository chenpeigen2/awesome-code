package com.peter.compose.demo.level3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StateRestoreActivity - 状态恢复
 *
 * 学习目标：
 * 1. remember vs rememberSaveable 的区别
 * 2. 自定义 Saver 对象
 * 3. mapSaver / listSaver 快捷方式
 * 4. 进程死亡后的状态恢复
 */
class StateRestoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StateRestoreScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class UserProfile(
    val name: String,
    val age: Int,
    val city: String
)

@Composable
fun StateRestoreScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "状态恢复 (rememberSaveable)",
            style = MaterialTheme.typography.headlineMedium
        )

        RememberVsRememberSaveableSection()
        CustomSaverSection()
        MapSaverSection()
        ListSaverSection()
    }
}

@Composable
fun RememberVsRememberSaveableSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "remember vs rememberSaveable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "旋转设备观察：remember 的计数器会重置，rememberSaveable 不会",
                style = MaterialTheme.typography.bodyMedium
            )

            var rememberCount by androidx.compose.runtime.remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("remember: $rememberCount", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { rememberCount++ }) { Text("+1") }
            }

            var saveableCount by rememberSaveable { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("rememberSaveable: $saveableCount", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { saveableCount++ }) { Text("+1") }
            }

            Text(
                text = """// remember — 不保存到 Bundle
var count by remember { mutableIntStateOf(0) }

// rememberSaveable — 保存到 Bundle
var count by rememberSaveable { mutableIntStateOf(0) }""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun CustomSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "自定义 Saver",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "对于非基本类型，需要自定义 Saver 来定义序列化逻辑",
                style = MaterialTheme.typography.bodyMedium
            )

            val userProfileSaver = Saver<UserProfile, android.os.Bundle>(
                save = { profile ->
                    android.os.Bundle().apply {
                        putString("name", profile.name)
                        putInt("age", profile.age)
                        putString("city", profile.city)
                    }
                },
                restore = { bundle ->
                    UserProfile(
                        name = bundle.getString("name", ""),
                        age = bundle.getInt("age"),
                        city = bundle.getString("city", "")
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = userProfileSaver) {
                mutableStateOf(UserProfile("张三", 25, "北京"))
            }

            Text("姓名: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
            Text("年龄: ${profile.age}", style = MaterialTheme.typography.bodyLarge)
            Text("城市: ${profile.city}", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = {
                profile = UserProfile("李四", 30, "上海")
            }) { Text("切换用户") }

            Text(
                text = """val saver = Saver<UserProfile, Bundle>(
    save = { bundle -> ... },
    restore = { bundle -> UserProfile(...) }
)
var profile by rememberSaveable(stateSaver = saver) {
    mutableStateOf(UserProfile("张三", 25, "北京"))
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun MapSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "mapSaver 快捷方式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val profileMapSaver = mapSaver<UserProfile>(
                save = { mapOf("name" to it.name, "age" to it.age, "city" to it.city) },
                restore = { map ->
                    UserProfile(
                        name = map["name"] as String,
                        age = map["age"] as Int,
                        city = map["city"] as String
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = profileMapSaver) {
                mutableStateOf(UserProfile("王五", 28, "深圳"))
            }

            Text("${profile.name} · ${profile.age}岁 · ${profile.city}")

            Button(onClick = {
                profile = UserProfile("赵六", 35, "杭州")
            }) { Text("切换用户") }

            Text(
                text = """val saver = mapSaver<UserProfile>(
    save = { mapOf("name" to it.name, ...) },
    restore = { map -> UserProfile(...) }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ListSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "listSaver 快捷方式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val profileListSaver = listSaver<UserProfile, Any>(
                save = { listOf(it.name, it.age, it.city) },
                restore = { list ->
                    UserProfile(
                        name = list[0] as String,
                        age = list[1] as Int,
                        city = list[2] as String
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = profileListSaver) {
                mutableStateOf(UserProfile("孙七", 22, "成都"))
            }

            Text("${profile.name} · ${profile.age}岁 · ${profile.city}")

            Button(onClick = {
                profile = UserProfile("周八", 40, "广州")
            }) { Text("切换用户") }

            Text(
                text = """val saver = listSaver<UserProfile, Any>(
    save = { listOf(it.name, it.age, it.city) },
    restore = { list -> UserProfile(...) }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateRestoreScreenPreview() {
    MaterialTheme {
        StateRestoreScreen()
    }
}
