package com.peter.compose.demo.level3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DependencyInjectionActivity - 依赖注入
 *
 * 学习目标：
 * 1. Koin 集成
 * 2. ViewModel 注入
 * 3. Repository 注入
 */

// ========== 领域层 ==========

/**
 * 用户数据类
 */
data class User(
    val id: Int,
    val name: String,
    val email: String
)

/**
 * 用户仓库接口
 */
interface UserRepository {
    fun getUser(): User
    fun updateUser(user: User)
}

/**
 * 用户仓库实现（模拟）
 */
class UserRepositoryImpl : UserRepository {
    private var currentUser = User(1, "张三", "zhangsan@example.com")

    override fun getUser(): User = currentUser

    override fun updateUser(user: User) {
        currentUser = user
    }
}

// ========== ViewModel ==========

/**
 * 用户 ViewModel
 */
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(repository.getUser())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _updateCount = MutableStateFlow(0)
    val updateCount: StateFlow<Int> = _updateCount.asStateFlow()

    fun updateUserName(name: String) {
        val updatedUser = _user.value.copy(name = name)
        repository.updateUser(updatedUser)
        _user.value = updatedUser
        _updateCount.update { it + 1 }
    }

    fun updateUserEmail(email: String) {
        val updatedUser = _user.value.copy(email = email)
        repository.updateUser(updatedUser)
        _user.value = updatedUser
        _updateCount.update { it + 1 }
    }
}

// ========== Koin 模块定义 ==========

val userModule = module {
    // Repository 单例
    single<UserRepository> { UserRepositoryImpl() }

    // ViewModel
    viewModel { UserViewModel(get()) }
}

// ========== Activity ==========

class DependencyInjectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    DependencyInjectionScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DependencyInjectionScreen(
    modifier: Modifier = Modifier
) {
    // 使用 Koin 注入 ViewModel
    val viewModel: UserViewModel = koinViewModel()

    val user by viewModel.user.collectAsStateWithLifecycle()
    val updateCount by viewModel.updateCount.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 依赖注入说明
        DiExplanation()

        // 2. 用户信息展示
        UserInfoCard(
            user = user,
            updateCount = updateCount
        )

        // 3. 操作按钮
        UserActionsCard(
            user = user,
            onUpdateName = { viewModel.updateUserName(it) },
            onUpdateEmail = { viewModel.updateUserEmail(it) }
        )

        // 4. Koin 配置说明
        KoinConfigurationCard()
    }
}

@Composable
fun DiExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "依赖注入 (DI)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Koin 是一个轻量级的 Kotlin 依赖注入框架，与 Compose 完美集成。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 依赖关系图
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "依赖关系",
                    style = MaterialTheme.typography.labelMedium
                )

                // UI
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("UI (Composable)", color = MaterialTheme.colorScheme.onPrimary)
                }

                Text("↓ 通过 koinViewModel()", style = MaterialTheme.typography.bodySmall)

                // ViewModel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("ViewModel", color = MaterialTheme.colorScheme.onSecondary)
                }

                Text("↓ 通过 get()", style = MaterialTheme.typography.bodySmall)

                // Repository
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Repository", color = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(
    user: User,
    updateCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "用户信息 (通过 DI 注入)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 用户头像
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.first().toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 用户信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${user.id}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column {
                    Text(
                        text = "姓名",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column {
                    Text(
                        text = "更新次数",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$updateCount",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                text = "邮箱: ${user.email}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun UserActionsCard(
    user: User,
    onUpdateName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "更新用户信息",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 更新姓名按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateName("李四") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("改名为李四")
                }

                Button(
                    onClick = { onUpdateName("王五") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("改名为王五")
                }
            }

            // 更新邮箱按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateEmail("lisi@example.com") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("更新邮箱1")
                }

                Button(
                    onClick = { onUpdateEmail("wangwu@example.com") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("更新邮箱2")
                }
            }
        }
    }
}

@Composable
fun KoinConfigurationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Koin 配置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "1. 定义 Koin 模块:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """val userModule = module {
    single<UserRepository> { 
        UserRepositoryImpl() 
    }
    viewModel { UserViewModel(get()) }
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            Text(
                text = "2. 在 Application 中初始化:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """startKoin {
    androidContext(this@MyApp)
    modules(userModule)
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            Text(
                text = "3. 在 Composable 中使用:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """// 注入 ViewModel
val viewModel: UserViewModel = koinViewModel()

// 注入其他依赖
val repository: UserRepository = get()""",
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
fun DependencyInjectionPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserInfoCard(
                user = User(1, "张三", "zhangsan@example.com"),
                updateCount = 3
            )
        }
    }
}
